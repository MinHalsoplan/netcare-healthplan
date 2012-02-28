//
//  BasicURLConnectionDelegate.h
//  netcare
//
//  Created by Peter Larsson on 2012-02-27.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@class URLHandler;

@protocol URLHandlerDelegate
- (void)ready:(BOOL)success;
@end

@interface URLHandler : NSObject <NSURLConnectionDataDelegate>

@property (weak, atomic, readonly) NSURL *url;
@property (weak, atomic, readonly) id delegate;

- (URLHandler*)init:(NSURL*)theUrl withDelegate:(id)theDelegate;
- (void)execute;

@end
