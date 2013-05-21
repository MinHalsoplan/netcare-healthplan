//
//  HTTPBasicAuth.m
//  netcare
//
//  Created by Johannes Carlén on 2013-05-15.
//
//

#import "HTTPBasicAuth.h"

#import "Util.h"

@implementation HTTPBasicAuth

@synthesize mainViewController;

- (HTTPBasicAuth*)init: (id)theDelegate withUser:(NSString*)user {
    mainViewController = theDelegate;
    httpResponse = [[NSMutableData alloc] init];
    personnummer = user;
    return self;
}

- (void)pleaseDo {
    NSLog(@"HTTPBasicAuth.pleaseDo");
    
    NSString* requestURL = [self authenticateUrl];
    NSLog(@"Url: %@",requestURL);
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: requestURL]];
    [request setHTTPMethod: @"GET"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    
    (void)[[NSURLConnection alloc] initWithRequest:request delegate:self];
}

-(BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace
{
    return YES;
}

-(void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    NSURLCredential *credential = [NSURLCredential credentialWithUser:personnummer password:@""
                                                          persistence:NSURLCredentialPersistenceForSession];   
	[[challenge sender] useCredential:credential forAuthenticationChallenge:challenge];
}

-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    [httpResponse appendData:data];
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
        [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
        NSString* msg =  [NSString stringWithFormat:@"%d", responseCode];
        [Util displayAlert:@"" withMessage:msg];
    }
    
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    
    // inform the user
    NSLog(@"Connection failed! Error - %@ %@",
          [error localizedDescription],
          [[error userInfo] objectForKey:NSURLErrorFailingURLStringErrorKey]);
    
    NSString* title = [Util infoValueForKey:@"NCGenericErrorTitle"];
    NSString* msg = [Util infoValueForKey:@"NCGenericError"];
    
    [Util displayAlert:title withMessage:msg];
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    NSLog(@"HTTPBasicAuth.connectionDidFinishLoading");
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    
    NSString *result= [[NSString alloc] initWithData:httpResponse encoding:NSUTF8StringEncoding];
    NSLog(@"Ref: %@", result);
    
    [mainViewController authenticationCompleted:@"TOKEN"];
}

- (NSString*)authenticateUrl{
    NSString* urlString = [Util  baseURLString];
    urlString = [urlString stringByAppendingString:@"/mobile/start"];
    NSLog(@"Authenticate: Check Auth URL --> %@\n", urlString);
    return urlString;
/*
    NSString* requestURL = [Util baseURLString];
    requestURL = [requestURL stringByAppendingString:[Util infoValueForKey:@"NCAuthenticate"]];
    NSString* param = [NSString stringWithFormat:@"?crn=%@", personnummer];
    requestURL = [requestURL stringByAppendingString: param];
    return @"";
 */
}

@end
