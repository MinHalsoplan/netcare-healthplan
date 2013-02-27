//
//  Util.m
//  netcare
//
// Copyright (C) 2012 Callista Enterprise AB <info@callistaenterprise.se>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.


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

+ (NSString*)baseURLString
{
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    NSString *protocol;
    NSString *server;
    int port;
    BOOL secure = YES;
    if([prefs objectForKey:@"nc_use_secure_connection"] != nil) {
        secure = [prefs boolForKey:@"nc_use_secure_connection"];
    }
    if (secure) {
        protocol = @"https";
    } else {
        protocol = @"http";
    }
    if ([prefs boolForKey:@"nc_use_server"]) {
        server = [prefs stringForKey:@"nc_host"];
        port = [prefs integerForKey:@"nc_port"];
    } else {
        server = [Util infoValueForKey:@"NCHost"];
        port = [[Util infoValueForKey:@"NCPort"] intValue];
    }

    NSString *urlString = protocol;
    urlString = [urlString stringByAppendingString:@"://"];
    urlString = [urlString stringByAppendingString:server];
    
    if (port > 0)
    {
        urlString = [urlString stringByAppendingString:[NSString stringWithFormat:@":%d",port]];
    }
    NSLog(@"Util.baseURLString --> %@\n", urlString);
    return urlString;
}

+ (void)displayAlert:(NSString*) title withMessage:(NSString *) msg{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title
                                                    message:msg
                                                   delegate:nil
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}

@end
