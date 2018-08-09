//
//  TuneSkyhookCenterTests.m
//
//

#import <XCTest/XCTest.h>
#import <OCMock/OCMock.h>

#import "Tune+Internal.h"
#import "TuneSkyhookCenter+Testing.h"
#import "TuneUserProfile+Testing.h"

#import "TuneManager+Testing.h"

#import "SimpleObserver.h"
#import "TuneXCTestCase.h"

@interface TuneSkyhookCenterTests : TuneXCTestCase {

}

@end

@implementation TuneSkyhookCenterTests

- (void)testDeallocatedObserverIsRemovedWhenPostingSkyhook {
    TuneSkyhookCenter *center = [[TuneSkyhookCenter alloc] init];
    
    @autoreleasepool {
        SimpleObserver *simpleObserver = [[SimpleObserver alloc] init];

        XCTAssertFalse([center hasObserverForHook:@"testskyhook"], @"There should not be an observer for testskyhook");
        NSLog(@"%@", center);
        
        [center addObserver:simpleObserver selector:@selector(skyhookPosted:) name:@"testskyhook" object:nil];
        
        XCTAssertTrue([center hasObserverForHook:@"testskyhook"], @"There should be an observer for testskyhook");

        [center postSkyhook:@"testskyhook"];
        XCTAssertTrue([center hasObserverForHook:@"testskyhook"], @"There should be an observer for testskyhook");
        XCTAssertEqual(simpleObserver.skyhookPostCount, 1, @"Observer should have received a post");
        
        simpleObserver = nil;
    }

    // simpleObserver has been deallocated at this point, so it should no longer be an observer after the next post
    [center postSkyhook:@"testskyhook"];
    XCTAssertFalse([center hasObserverForHook:@"testskyhook"], @"There should not be an observer for testskyhook");
}

- (void)testQueuedSkyhook {
    TuneSkyhookCenter *center = [[TuneSkyhookCenter alloc] init];
    
    @autoreleasepool {
        SimpleObserver *simpleObserver = [[SimpleObserver alloc] init];
        
        XCTAssertFalse([center hasObserverForHook:@"testqueuedskyhook"], @"There should not be an observer for testqueuedskyhook");
        
        [center addObserver:simpleObserver selector:@selector(skyhookPosted:) name:@"testqueuedskyhook" object:nil];
        
        XCTAssertTrue([center hasObserverForHook:@"testqueuedskyhook"], @"There should be an observer for testqueuedskyhook");
        
        [center postQueuedSkyhook:@"testqueuedskyhook"];
        [center startSkyhookQueue];
        
        [NSThread sleepForTimeInterval:2.0f];
        
        XCTAssertTrue([center hasObserverForHook:@"testqueuedskyhook"], @"There should be an observer for testqueuedskyhook");
        XCTAssertEqual(simpleObserver.skyhookPostCount, 1, @"Observer should have received a post");
        
        simpleObserver = nil;
    }
}

@end
