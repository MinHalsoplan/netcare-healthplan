//
//  HTTPAuthentication.h
//  netcare
//
//  Created by Peter Larsson on 2012-02-28.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HTTPConnection.h"

@class HTTPAuthentication;

@protocol HTTPAuthenticationDelegate
- (void)authReady:(NSInteger)code;
@end

@interface HTTPAuthentication : HTTPConnection <HTTPConnectionDelegate>

@property (weak, atomic, readonly) NSString *user;
@property (weak, atomic, readonly) NSString *password;
@property (weak, atomic, readonly) id authDelegate;



- (HTTPAuthentication*)init:(NSURL*)theUrl withDelegate:(id)theDelegate withUser:(NSString*)theUser withPassword:(NSString*)thePassword;

+ (void)cleanSession;

@end
