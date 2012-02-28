//
//  Util.h
//  netcare
//
//  Created by Peter Larsson on 2012-02-28.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Util : NSObject

+ (NSString*)infoValueForKey:(NSString*)key;
+ (NSString*) toHexString:(NSData*) data;

@end
