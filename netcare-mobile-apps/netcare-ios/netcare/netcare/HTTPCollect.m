//
//  HTTPCollect.m
//  netcare
//
//  Created by Johannes Carlén on 2013-01-24.
//
//

#import "HTTPCollect.h"

#import "Util.h"

@implementation HTTPCollect

- (HTTPCollect*)init {
    httpResponse = [[NSMutableData alloc] init];
    return self;
}

- (void)sendRequest:(NSString*)ref {
    NSLog(@"HTTPCollect.sendRequest");
    
    NSString* requestURL = [self collectUrl];
    NSLog(@"Url: %@",requestURL);
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestURL]];
    [request setHTTPMethod: @"POST"];
    [request setTimeoutInterval:15];
    [request setHTTPBody:[[NSString stringWithFormat:@"orderRef=%@", ref] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    
    [[NSURLConnection alloc] initWithRequest:request delegate:self];
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
    NSLog(@"HTTPCollect.connectionDidFinishLoading");
    
    NSString *result= [[NSString alloc] initWithData:httpResponse encoding:NSUTF8StringEncoding];
    NSLog(@"Data: %@", result);
    NSLog(@"Trigga webbvyn härnäst!");
}

- (void)connection:(NSURLConnection *)connection
  didFailWithError:(NSError *)error
{
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
