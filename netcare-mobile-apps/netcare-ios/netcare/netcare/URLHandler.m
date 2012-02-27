//
//  BasicURLConnectionDelegate.m
//  netcare
//
//  Created by Peter Larsson on 2012-02-27.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "URLHandler.h"

@implementation URLHandler

@synthesize url;
@synthesize delegate;

NSURLConnection *conn;

- (URLHandler*)init:(NSURL*)theUrl withDelegate:(id)theDelegate
{
    url = theUrl;
    delegate = theDelegate;
    return self;
}

- (void)execute
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:url];
    // Start registration
    conn = [[NSURLConnection alloc] initWithRequest:urlRequest delegate:self];
}

//
- (void)connection:(NSURLConnection *)connection willSendRequestForAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    NSLog(@"Basic: willSendRequestForAuthenticationChallenge");
}

//
- (void)ready:(BOOL)success connection:(NSURLConnection*)connection
{
    if (!success) {
        [connection cancel];
    }
    [delegate ready:success];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    conn = nil;
}

//
- (BOOL)connectionShouldUseCredentialStorage:(NSURLConnection *)connection {
    return YES;
}

//
- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace
{
    return YES;
}


- (NSCachedURLResponse *)connection:(NSURLConnection *)connection willCacheResponse:(NSCachedURLResponse *)cachedResponse {
    return nil;
}

//
- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{    
    NSLog(@"Basic: Succeded\n");
    [self ready:YES connection:connection];
}

//
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    NSLog(@"Basic: Connection failure: %@\n", [error localizedDescription]);
    [self ready:NO connection:connection];
}

//
- (void) connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
    
    int responseCode = [httpResponse statusCode];
    
    NSLog(@"Basic: HTTP Response code: %d\n", responseCode);
    
    if (responseCode != 200)
    {
        [self ready:NO connection:connection];
    }
}

//
- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    NSLog(@"Basic: Received %d bytes\n", [data length]);
}



@end
