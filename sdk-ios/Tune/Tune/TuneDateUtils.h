//
//  TuneDateUtils.h
//  TuneMarketingConsoleSDK
//
//  Created by Matt Gowie on 7/28/15.
//  Copyright (c) 2015 Tune. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TuneDateUtils : NSObject

/**
  Date formatter using format "yyyy-MM-dd'T'HH:mm:ss+00:00'", UTC timezone and locale "en_US_POSIX".
 */
+ (NSDateFormatter *)dateFormatterIso8601;

/**
 Date formatter using format "yyyy-MM-dd'T'HH:mm:ss'Z'", UTC timezone and locale "en_US_POSIX".
 */
+ (NSDateFormatter *)dateFormatterIso8601UTC;

+ (BOOL)date:(NSDate *)date isBetweenDate:(NSDate *)beginDate andEndDate:(NSDate *)endDate;

+ (int)daysBetween:(NSDate *)beginDate and:(NSDate *)endDate;

@end
