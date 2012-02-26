//
//  MainViewController.m
//  netcare
//
//  Created by Peter Larsson on 2012-02-26.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "MainViewController.h"

@implementation MainViewController

@synthesize personNumberTextEdit;
@synthesize pinCodeTextEdit;
@synthesize nextPageButton;

NSURLProtectionSpace *protectionSpace = nil;

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

// Saves personal number.

- (void)savePersonalNumber {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
	[prefs setValue:[personNumberTextEdit text] forKey:@"personalNumber"];
	[prefs synchronize];
}

- (NSString*)retrievePersonalNumber {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    NSString *s = [prefs stringForKey:@"personalNumber"];
    return (s == nil) ? @"" : s;
}

- (NSString*)infoValueForKey:(NSString*)key {
    return [[[NSBundle mainBundle] infoDictionary] valueForKey:key];
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    if (protectionSpace == nil) 
    {
        protectionSpace = [[NSURLProtectionSpace alloc]
                           initWithHost: [self infoValueForKey:@"NCHost"]
                           port: [[self infoValueForKey:@"NCPort"] intValue]
                           protocol: [self infoValueForKey:@"NCProtocol"]
                           realm:[self infoValueForKey:@"NCSecurityRealm"]
                           authenticationMethod: NSURLAuthenticationMethodHTTPBasic];
    }
    
    [nextPageButton setHidden:YES];
    [personNumberTextEdit setText:[self retrievePersonalNumber]];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
}



#pragma mark - Basic Auth Stuff

//
- (NSString*)baseURLString
{
    int port = [[self infoValueForKey:@"NCPort"] intValue];
    NSString *portString = (port == 80) ? @"" : [@":" stringByAppendingFormat:@"%d",port];
    NSString *urlString = [self infoValueForKey:@"NCProtocol"];
    urlString = [urlString stringByAppendingString:@"://"];
    urlString = [urlString stringByAppendingString:[self infoValueForKey:@"NCHost"]];
    urlString = [urlString stringByAppendingString:portString];
    NSLog(@"BaseURL --> %@\n", urlString);
    
    return urlString;
}

//
- (NSURL*)checkCredentialURL 
{
    
    NSString *urlString = [self  baseURLString];
    urlString = [urlString stringByAppendingString:[self infoValueForKey:@"NCCheckCredentialsPage"]];    
    NSLog(@"Authenticate: Check Auth URL --> %@\n", urlString);
    return [NSURL URLWithString:urlString];     
}

- (void)showAuthError:(int)responseCode
{
    NSString *msg = [NSString stringWithFormat:(responseCode == -1) ? @"Felaktig personlig kod, eller så saknas inställningar för denna tjänst (%d)" : @"Tekniskt fel, försök igen lite senare (%d)", responseCode];
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Åtkomst nekad"
                                                    message:msg 
                                                   delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil];
    
    [alert show];
    
}

// starts a request
- (void)startAuthentication
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    
    NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[self checkCredentialURL]];
    // Start the connection request
    NSURLConnection *conn = [[NSURLConnection alloc] initWithRequest:urlRequest delegate:self];
        
    conn = nil;
}


//
- (void)connection:(NSURLConnection *)connection willSendRequestForAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    NSLog(@"Authenticate: willSendRequestForAuthenticationChallenge");
    
    if ([challenge previousFailureCount] > 0) {
        [[challenge sender] cancelAuthenticationChallenge:challenge];
        [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
        [self showAuthError:-1];
    } else {
        NSURLCredential *credential = [[NSURLCredential alloc]
                                       initWithUser: [personNumberTextEdit text]
                                       password: [pinCodeTextEdit text]
                                       persistence: NSURLCredentialPersistenceForSession]; 
        
        [[NSURLCredentialStorage sharedCredentialStorage] setDefaultCredential:credential forProtectionSpace:protectionSpace];
        
        [[challenge sender] useCredential:credential forAuthenticationChallenge:challenge];        
    }
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
    NSLog(@"Authenticate: Succeded\n");
    [self performSegueWithIdentifier:@"webView" sender:nextPageButton];
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

}

//
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    NSLog(@"Authenticate: Connection failure: %@\n", [error localizedDescription]);
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
}

//
- (void) connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
    int responseCode = [httpResponse statusCode];
    if (responseCode != 200) {
        [connection cancel];
        [self showAuthError:responseCode];
    }
    NSLog(@"Authenticate: HTTP Response code: %d\n", responseCode);
}

//
- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    NSLog(@"Authenticate: Received %d bytes\n", [data length]);
}

//
- (IBAction)login:(id)sender {
    NSLog(@"Personnummer: %@\n", [personNumberTextEdit text]);
    [self savePersonalNumber];
    [self startAuthentication];
}

// hide keyboard
- (IBAction)textFieldReturn:(id)sender {
    [sender resignFirstResponder];
}

//
- (IBAction)backgroundTouched:(id)sender {
    if ([personNumberTextEdit isFirstResponder])
        [personNumberTextEdit resignFirstResponder];
    if ([pinCodeTextEdit isFirstResponder]) 
        [pinCodeTextEdit resignFirstResponder];
}

// clean up stuff
// to prepare for a new session
- (void)cleanSession
{
    // remove actual credentials to force a new login
    NSURLCredential *credential = [[NSURLCredentialStorage sharedCredentialStorage]defaultCredentialForProtectionSpace:protectionSpace];
    
    if (credential)
    {
        NSLog(@"Delete credential --> %@\n", [credential description]);
        [[NSURLCredentialStorage sharedCredentialStorage] removeCredential:credential forProtectionSpace:protectionSpace];
    }
    
    // reset pinCode
    [pinCodeTextEdit setText:@""];
}

#pragma mark - Flipside View

// back to logon screen
- (void)flipsideViewControllerDidFinish:(FlipsideViewController *)controller
{
    [self dismissModalViewControllerAnimated:YES];
    [self cleanSession];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSLog(@"%@\n", [segue identifier]);
        
    if ([[segue identifier] isEqualToString:@"webView"]) {
        [[segue destinationViewController] setDelegate:self];
    }
}

- (NSURL*)startURL
{
    NSString *urlString = [self  baseURLString];
    urlString = [urlString stringByAppendingString:[self infoValueForKey:@"NCStartPage"]];
    NSLog(@"Start URL --> %@\n", urlString);
    
    return [[NSURL alloc] initWithString:urlString];
}

@end
