//
//  HTTPAuthenticate.m
//  netcare
//
//  Created by Johannes Carlén on 2013-01-23.
//
//

#import "HTTPAuthenticate.h"

#import "Util.h"

@implementation HTTPAuthenticate

@synthesize mainViewController;

- (HTTPAuthenticate*)initWithCrn:(NSString*)theCrn {
    httpResponse = [[NSMutableData alloc] init];
    crn = theCrn;
    return self;
}

- (void)sendRequest {
    NSLog(@"HTTPAuthenticate.sendRequest");
    
    NSString* requestURL = [self authenticateUrl];
    NSLog(@"Url: %@",requestURL);
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestURL]];
    [request setHTTPMethod: @"POST"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    
   (void)[[NSURLConnection alloc] initWithRequest:request delegate:self];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [httpResponse setLength:0];
    NSHTTPURLResponse* resp = (NSHTTPURLResponse*)response;
    int responseCode = [resp statusCode];
    NSLog(@"HTTP: Response code: %d\n", responseCode);
    if (responseCode != 200) {
        if (connection) {
            [connection cancel];
        }
        [mainViewController displayAlert];
    }
    
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [httpResponse appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    NSLog(@"HTTPAuthenticate.connectionDidFinishLoading");
    
    NSString *result= [[NSString alloc] initWithData:httpResponse encoding:NSUTF8StringEncoding];
    NSLog(@"Ref: %@", result);
    
    NSString *bankidUrl = [NSString stringWithFormat: @"bankid://redirect=netcare://%@", result];
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:bankidUrl]];
}

- (void)connection:(NSURLConnection *)connection
  didFailWithError:(NSError *)error
{
    // inform the user
    NSLog(@"Connection failed! Error - %@ %@",
          [error localizedDescription],
          [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
}

- (NSString*)authenticateUrl{
    NSString* requestURL = [Util baseURLString];
    requestURL = [requestURL stringByAppendingString:[Util infoValueForKey:@"NCAuthenticate"]];
    NSString* param = [NSString stringWithFormat:@"?crn=%@", crn];
    requestURL = [requestURL stringByAppendingString: param];
    return requestURL;
}
@end
