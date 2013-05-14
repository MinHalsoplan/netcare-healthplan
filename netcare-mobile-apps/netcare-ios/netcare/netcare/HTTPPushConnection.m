//
//  BasicURLConnectionDelegate.m
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


#import "HTTPPushConnection.h"

@implementation HTTPPushConnection

@synthesize url;
@synthesize connDelegate;
@synthesize conn;

- (HTTPPushConnection*)init:(NSURL*)theUrl withDelegate:(id)theDelegate
{
    url = theUrl;
    connDelegate = theDelegate;
    conn = nil;
    
    return self;
}

- (void)get
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:url];
    [urlRequest setTimeoutInterval:15];
    // Start registration
    conn = [[NSURLConnection alloc] initWithRequest:urlRequest delegate:self];
}

//
- (void)ready:(NSInteger)code connection:(NSURLConnection*)connection
{
    if (connection)
    {
        [connection cancel];
    }
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    if (connDelegate)
    {
        [connDelegate connReady:code];
    }
    conn = nil;
}

- (void)synchronizedPost:(NSString*)data
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    
    NSMutableString* postData = [NSMutableString stringWithString:data];
    [postData appendString: @"\n"];
    [postData appendString: [NSString stringWithFormat: @"%@%@\n", @"os.version=", [[UIDevice currentDevice] systemVersion]]];
    [postData appendString: [NSString stringWithFormat: @"%@%@\n", @"app.version=", [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"]]];
    
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:url];
    [urlRequest setTimeoutInterval:15];
    [urlRequest setHTTPMethod:@"POST"];
    [urlRequest setHTTPBody:[postData dataUsingEncoding:NSUTF8StringEncoding]];
    [urlRequest setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField: @"Content-Type"];
    NSLog(@"synchronizedPost: %@\n", postData);
 
    NSError* error;
    NSHTTPURLResponse* response;
    [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
    int code = (error) ? [error code] : [response statusCode];
    if (code != 200)
    {
        NSLog(@"Error synchronizedPost: %d (%@)\n", code, error);
        [self ready:code connection:nil];
    }
    else
    {
        [self ready:code connection:nil];        
    }
    error = nil;
    response = nil;
}

- (void)synchronizedDelete {
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:url];
    [urlRequest setTimeoutInterval:15];
    [urlRequest setHTTPMethod:@"DELETE"];
    NSLog(@"synchronizedDelete");
    
    NSError* error;
    NSHTTPURLResponse* response;
    [NSURLConnection sendSynchronousRequest:urlRequest returningResponse:&response error:&error];
    int code = (error) ? [error code] : [response statusCode];
    if (code != 200)
    {
        NSLog(@"Error synchronizedDelete: %d (%@)\n", code, error);
        [self ready:code connection:nil];
    }
    else
    {
        [self ready:code connection:nil];
    }
    error = nil;
    response = nil;
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
