//
//  BasicURLConnectionDelegate.h
//  netcare
//
//  Created by Peter Larsson on 2012-02-27.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@class HTTPConnection;

@protocol HTTPConnectionDelegate
- (void)connReady:(NSInteger)code;
@end

@interface HTTPConnection : NSObject <NSURLConnectionDataDelegate>

@property (weak, atomic, readonly) NSURL *url;
@property (weak, atomic, readonly) id connDelegate;
@property (retain, readonly) NSURLConnection *conn;

- (HTTPConnection*)init:(NSURL*)theUrl withDelegate:(id)theDelegate;
- (void)get;
- (void)synchronizedPost:(NSString*)data;
- (void)ready:(NSInteger)code connection:(NSURLConnection*)connection;

@end
