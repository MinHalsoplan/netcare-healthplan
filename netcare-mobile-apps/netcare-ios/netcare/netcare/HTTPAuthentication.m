//
//  HTTPAuthentication.m
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
        NSLog(@"Clean session");
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
