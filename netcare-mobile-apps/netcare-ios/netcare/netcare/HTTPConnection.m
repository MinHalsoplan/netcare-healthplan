//
//  BasicURLConnectionDelegate.m
//  netcare
//
//  Created by Peter Larsson on 2012-02-27.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "HTTPConnection.h"

@implementation HTTPConnection

@synthesize url;
@synthesize connDelegate;
@synthesize conn;

- (HTTPConnection*)init:(NSURL*)theUrl withDelegate:(id)theDelegate
{
    url = theUrl;
    connDelegate = theDelegate;
    conn = nil;
    
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
- (void)ready:(NSInteger)code connection:(NSURLConnection*)connection
{
    [connection cancel];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    [connDelegate connReady:code];
    conn = nil;
}

#pragma mark -
#pragma mark NSURLConnectionDataDelegate


//
- (void)connection:(NSURLConnection *)connection willSendRequestForAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    NSLog(@"HTTP: willSendRequestForAuthenticationChallenge");
}

//
- (BOOL)connectionShouldUseCredentialStorage:(NSURLConnection *)connection {
    NSLog(@"Use credential storage YES\n");
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
    NSLog(@"HTTP: Succeded\n");
    [self ready:200 connection:connection];
}

//
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    NSLog(@"HTTP: Connection failure: %@ (%d)\n", [error localizedDescription], [error code]);
    [self ready:[error code] connection:connection];
}

//
- (void) connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
    
    int responseCode = [httpResponse statusCode];
    
    NSLog(@"HTTP: Response code: %d\n", responseCode);
    
    if (responseCode != 200)
    {
        [self ready:responseCode connection:connection];
    }
}

//
- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    NSLog(@"HTTP: Received %d bytes\n", [data length]);
}



@end
