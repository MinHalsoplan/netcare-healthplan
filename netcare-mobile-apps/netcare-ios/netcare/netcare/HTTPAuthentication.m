//
//  HTTPAuthentication.m
//  netcare
//
//  Created by Peter Larsson on 2012-02-28.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "HTTPAuthentication.h"
#import "Util.h"


@implementation HTTPAuthentication

@synthesize user;
@synthesize password;
@synthesize authDelegate;

static NSURLProtectionSpace *protectionSpace = nil;

- (HTTPAuthentication*)init:(NSURL*)theUrl withDelegate:(id)theDelegate withUser:(NSString*)theUser withPassword:(NSString*)thePassword
{
    self = [super init:theUrl withDelegate:self];
    user = theUser;
    password = thePassword;
    authDelegate = theDelegate;
    if (protectionSpace == nil) 
    {
        protectionSpace = [[NSURLProtectionSpace alloc]
                           initWithHost: [Util infoValueForKey:@"NCHost"]
                           port: [[Util infoValueForKey:@"NCPort"] intValue]
                           protocol: [Util infoValueForKey:@"NCProtocol"]
                           realm:[Util infoValueForKey:@"NCSecurityRealm"]
                           authenticationMethod: NSURLAuthenticationMethodHTTPBasic];
        
    }
    return self;
}

+ (void)cleanSession
{
    if (protectionSpace != nil) 
    {
        NSURLCredential *credential = [[NSURLCredentialStorage sharedCredentialStorage]defaultCredentialForProtectionSpace:protectionSpace];
        
        if (credential)
        {
            NSLog(@"Remove credential --> %@\n", [credential description]);
            [[NSURLCredentialStorage sharedCredentialStorage] removeCredential:credential forProtectionSpace:protectionSpace];
        }
    }
}

- (void)connReady:(NSInteger)code
{
    [authDelegate authReady:code]; 
}


#pragma mark -
#pragma mark NSURLConnectionDataDelegate

//
- (BOOL)connectionShouldUseCredentialStorage:(NSURLConnection *)connection {
    NSLog(@"Use credential storage NO\n");
    return NO;
}


//
- (void)connection:(NSURLConnection *)connection willSendRequestForAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    NSLog(@"HTTP Auth: willSendRequestForAuthenticationChallenge");   
    
    if ([challenge previousFailureCount] > 0) {
        [[challenge sender] cancelAuthenticationChallenge:challenge];
    } else {
        NSURLCredential *credential = [[NSURLCredential alloc]
                                       initWithUser: user
                                       password: password
                                       persistence: NSURLCredentialPersistenceForSession]; 
        
        [[NSURLCredentialStorage sharedCredentialStorage] setDefaultCredential:credential forProtectionSpace:protectionSpace];
        
        [[challenge sender] useCredential:credential forAuthenticationChallenge:challenge];        
    }
}



@end
