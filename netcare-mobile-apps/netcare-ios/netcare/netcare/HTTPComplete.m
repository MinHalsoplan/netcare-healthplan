//
//  HTTPComplete.m
//  netcare
//
//  Created by Johannes Carlén on 2013-01-24.
//
//

#import "HTTPComplete.h"

#import "Util.h"

@implementation HTTPComplete

@synthesize mainViewController;

- (HTTPComplete*)initWithDelegate:(id)theDelegate {
    httpResponse = [[NSMutableData alloc] init];
    mainViewController = theDelegate;
    return self;
}

- (void)sendRequest:(NSString*)ref {
    NSLog(@"HTTPComplete.sendRequest");
    
    token = ref;
    NSString* requestURL = [self collectUrl];
    NSLog(@"Url: %@",requestURL);
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestURL]];
    [request setHTTPMethod: @"GET"];
    [request setTimeoutInterval:15];
    [request setValue:ref forHTTPHeaderField:@"X-netcare-order"];
    
    (void)[[NSURLConnection alloc] initWithRequest:request delegate:self];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [httpResponse setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [httpResponse appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

    NSLog(@"HTTPComplete.connectionDidFinishLoading");
    
    NSString *result= [[NSString alloc] initWithData:httpResponse encoding:NSUTF8StringEncoding];
    NSLog(@"Data: %@", result);
    [mainViewController authenticationCompleted:token];
}

- (void)connection:(NSURLConnection *)connection
  didFailWithError:(NSError *)error
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

    // inform the user
    NSLog(@"Connection failed! Error - %@ %@",
          [error localizedDescription],
          [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
}


- (NSString*)collectUrl{
    NSString* requestURL = [Util baseURLString];
    requestURL = [requestURL stringByAppendingString:[Util infoValueForKey:@"NCCollect"]];
    return requestURL;
}
@end
