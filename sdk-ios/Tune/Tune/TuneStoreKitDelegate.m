//
//  TuneStoreKitDelegate.m
//  Tune
//
//  Created by Harshal Ogale on 4/20/15.
//  Copyright (c) 2015 TUNE. All rights reserved.
//

#if !TARGET_OS_WATCH

#import "TuneStoreKitDelegate.h"

#import "Tune+Internal.h"
#import "TuneEvent+Internal.h"
#import "TuneEventItem+Internal.h"

@interface TuneStoreKitDelegate () <SKPaymentTransactionObserver>

@property (nonatomic, copy) NSMutableDictionary *products;
@property (nonatomic, copy) NSMutableDictionary *productRequests;

@end

static TuneStoreKitDelegate *shared;

static BOOL startedObserver;


typedef void(^RequestCompletionBlock)(SKProduct *);

@interface TuneStoreKitProductRequester : NSObject <SKProductsRequestDelegate>

@property (nonatomic, copy) RequestCompletionBlock customBlock;

- (void)requestProductWithId:(NSString *)productId completion:(RequestCompletionBlock)completionBlock;

@end

@implementation TuneStoreKitProductRequester

- (void)requestProductWithId:(NSString *)productId completion:(RequestCompletionBlock)completionBlock {
    self.customBlock = completionBlock;
    
    NSSet *setProducts = [NSSet setWithObject:productId];
    
    // retrieve the specified in-app purchase products
    SKProductsRequest *req = [[SKProductsRequest alloc] initWithProductIdentifiers:setProducts];
    req.delegate = self;
    [req start];
}


#pragma mark - SKProductsRequestDelegate Methods

- (void)productsRequest:(SKProductsRequest *)request didReceiveResponse:(SKProductsResponse *)response {
    NSArray *arrProducts = response.products;
    
    // store the downloaded in-app purchase products
    for(SKProduct *prod in arrProducts) {
        // run the completion block
        self.customBlock(prod);
    }
}

@end


@implementation TuneStoreKitDelegate


#pragma mark - Init

+ (void)initialize {
    shared = [TuneStoreKitDelegate new];
    startedObserver = NO;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _products = [NSMutableDictionary dictionary];
        _productRequests = [NSMutableDictionary dictionary];
    }
    return self;
}

+ (void)startObserver {
    if(!startedObserver) {
        [[SKPaymentQueue defaultQueue] addTransactionObserver:shared];
        startedObserver = YES;
    }
}

+ (void)stopObserver {
    if(startedObserver) {
        [[SKPaymentQueue defaultQueue] removeTransactionObserver:shared];
        startedObserver = NO;
    }
}


#pragma mark - SKPaymentTransactionObserver Methods

- (void)paymentQueue:(SKPaymentQueue *)queue updatedTransactions:(NSArray *)transactions {
    // empty
}

- (void)paymentQueue:(SKPaymentQueue *)queue removedTransactions:(NSArray *)transactions {
    // handle each transaction
    for (SKPaymentTransaction *transaction in transactions) {
        switch (transaction.transactionState) {
            case SKPaymentTransactionStatePurchased: {
                // purchase successful
                if(transaction.transactionIdentifier) {
                    // ref: Apple Receipt Validation Programming Guide
                    // https://developer.apple.com/library/ios/releasenotes/General/ValidateAppStoreReceipt/Chapters/ValidateRemotely.html#//apple_ref/doc/uid/TP40010573-CH104-SW2
                    // we do not use the receipt stored in the file [[NSBundle mainBundle] appStoreReceiptURL]
                    // because in case of consumable IAP transactions, if some other SKPaymentTransactionObserver
                    // calls finishTransaction then the receipt may be removed from the file before this code
                    // has a chance to retrieve the IAP transaction receipt
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
                    NSData *receipt = transaction.transactionReceipt;
#pragma clang diagnostic pop
                    
                    // extract the stored product with given product id
                    SKProduct *product = self.products[transaction.payment.productIdentifier];
                    
                    // cached product info is available
                    if(product) {
                        // fire a Tune purchase request
                        [self measurePurchaseEvent:transaction product:product receipt:receipt];
                    } else {
                        @synchronized(self.productRequests) {
                            if(!self.productRequests[transaction.transactionIdentifier]) {
                                // download the product info using the given product id
                                TuneStoreKitProductRequester *prodReq = [TuneStoreKitProductRequester new];
                                
                                // store a reference to the product request, so that it is available when
                                // the requestProductWithId:completion: completion block is executed async
                                self.productRequests[transaction.transactionIdentifier] = prodReq;
                                
                                // fire the product info download request
                                [prodReq requestProductWithId:transaction.payment.productIdentifier completion:^(SKProduct *pr){
                                    // if the product info is successfully downloaded
                                    if(pr) {
                                        // store the downloaded product for future reference
                                        @synchronized(self.products) {
                                            self.products[pr.productIdentifier] = pr;
                                        }
                                    }
                                    
                                    // fire a Tune purchase event
                                    [self measurePurchaseEvent:transaction product:pr receipt:receipt];
                                    
                                    @synchronized(self.productRequests) {
                                        // remove the stored reference to the product request
                                        [self.productRequests removeObjectForKey:transaction.transactionIdentifier];
                                    }
                                }];
                            }
                        }
                    }
                }
                break;
            }
            case SKPaymentTransactionStateFailed:
            case SKPaymentTransactionStateRestored:
            case SKPaymentTransactionStatePurchasing:
            default:
                // ignore
                break;
        }
    }
}

#pragma mark - Event Measurement

- (void)measurePurchaseEvent:(SKPaymentTransaction *)transaction product:(SKProduct *)product receipt:(NSData *)receipt {
    // assign the currency code extracted from the transaction
    NSString *currencyCode = [product.priceLocale objectForKey:NSLocaleCurrencyCode];
    
    // extract transaction product quantity
    NSInteger quantity = transaction.payment.quantity;
    
    // extract unit price of the product
    float unitPrice = [product.price floatValue];
    
    // assign revenue generated from the current product
    float revenue = quantity * unitPrice;
    
    // if the product info is not available, then use the product id as the event item name
    NSString *productTitle = product.localizedTitle ?: transaction.payment.productIdentifier;
    
    // create the event item, unitPrice and revenue will be zero if the product info download fails
    TuneEventItem *eventItem = [TuneEventItem eventItemWithName:productTitle unitPrice:unitPrice quantity:quantity revenue:revenue];
    eventItem.attribute1 = product.priceLocale.localeIdentifier;
    
    // Measure the in-app-purchase event
    TuneEvent *event = [TuneEvent eventWithName:@"purchase"];
    event.currencyCode = currencyCode;
    event.date1 = transaction.transactionDate;
    event.eventItems = @[eventItem];
    event.receipt = receipt;
    event.refId = transaction.transactionIdentifier;
    event.transactionState = transaction.transactionState;
    event.contentId = transaction.payment.productIdentifier;
    event.contentType = @"tune_iap_auto_event";
    
    [Tune measureEvent:event];
}

@end

#endif
