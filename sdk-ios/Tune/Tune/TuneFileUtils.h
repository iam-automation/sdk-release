//
//  TuneFileUtils.h
//  TuneMarketingConsoleSDK
//
//  Created by Daniel Koch on 8/4/15.
//  Copyright (c) 2015 Tune. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TuneFileUtils : NSObject

// Property List
+ (id)loadPropertyList:(NSString *)filePathName;
+ (id)loadPropertyList:(NSString *)filePathName mutabilityOption:(NSPropertyListMutabilityOptions)opt;
+ (BOOL)savePropertyList:(id)object filePathName:(NSString *)filePathName plistFormat:(int)plistFormat;

// Images
+ (UIImage *)loadImageAtPath:(NSString *)path;
+ (BOOL)saveImageData:(NSData *)data toPath:(NSString *)path;

// File System Modification
+ (BOOL)createDirectory:(NSString *)filePath;
+ (BOOL)fileExists:(NSString *)filePath;
+ (BOOL)deleteFileOrDirectory:(NSString *)fileOrDirectory;
+ (BOOL)moveFileOrDirectory:(NSString *)fileOrDirectory toFileOrDirectory:(NSString *)toFileOrDirectory;
+ (BOOL)addSkipBackupAttributeToItemAtURL: (NSURL *)url;
+ (NSString *)pathToConfiguration;

@end