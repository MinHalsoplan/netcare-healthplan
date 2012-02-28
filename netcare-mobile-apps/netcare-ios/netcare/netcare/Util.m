//
//  Util.m
//  netcare
//
//  Created by Peter Larsson on 2012-02-28.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Util.h"

@implementation Util


+ (NSString*)infoValueForKey:(NSString*)key {
    return [[[NSBundle mainBundle] infoDictionary] valueForKey:key];
}


//
+ (NSString*) toHexString:(NSData*) data
{
    NSMutableString *str = [NSMutableString stringWithCapacity:64];
    int length = [data length];
    char *bytes = malloc(sizeof(char) * length);
    
    [data getBytes:bytes length:length];
    
    for (int i = 0; i < length; i++)
    {
        [str appendFormat:@"%02.2hhx", bytes[i]];
    }
    free(bytes);
    
    return str;
}

@end
