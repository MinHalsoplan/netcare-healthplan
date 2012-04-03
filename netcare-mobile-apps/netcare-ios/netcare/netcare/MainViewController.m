//
//  MainViewController.m
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


#import "MainViewController.h"
#import "Util.h"

@implementation MainViewController

@synthesize personNumberTextEdit;
@synthesize pinCodeTextEdit;
@synthesize nextPageButton;

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
    if (s != nil) 
    {
        [pinCodeTextEdit becomeFirstResponder];
    }
    return (s == nil) ? @"" : s;
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [nextPageButton setHidden:YES];
    [personNumberTextEdit setText:[self retrievePersonalNumber]];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
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
    NSString *urlString = [Util infoValueForKey:@"NCProtocol"];
    urlString = [urlString stringByAppendingString:@"://"];
    urlString = [urlString stringByAppendingString:[Util infoValueForKey:@"NCHost"]];
    
    int port = [[Util infoValueForKey:@"NCPort"] intValue];
    if (port > 0) 
    {
        urlString = [urlString stringByAppendingString:[NSString stringWithFormat:@":%d",port]];
    }
    NSLog(@"BaseURL --> %@\n", urlString);
    return urlString;
}

//
- (NSURL*)checkCredentialURL 
{
    
    NSString *urlString = [self  baseURLString];
    urlString = [urlString stringByAppendingString:[Util infoValueForKey:@"NCCheckCredentialsPage"]];    
    NSLog(@"Authenticate: Check Auth URL --> %@\n", urlString);
    return [NSURL URLWithString:urlString];     
}

//
- (NSURL*)logoutURL
{
    NSString *urlString = [self  baseURLString];
    urlString = [urlString stringByAppendingString:[Util infoValueForKey:@"NCLogoutPage"]]; 
    NSLog(@"Logout:  URL --> %@\n", urlString);
    return [NSURL URLWithString:urlString];         
}

//
- (NSURL*)pushRegistrationURL
{
    NSString *urlString = [self  baseURLString];
    urlString = [urlString stringByAppendingString:[Util infoValueForKey:@"NCPushRegistrationPage"]]; 
    NSLog(@"Push Registration:  URL --> %@\n", urlString);
    return [NSURL URLWithString:urlString];         
}

- (void)showAuthError:(int)responseCode
{
    NSString *msg = [NSString stringWithFormat:(responseCode == -1012) ? @"Felaktig personlig kod, eller så saknas inställningar för denna tjänst (%d)" : @"Tekniskt fel, försök igen lite senare (%d)", responseCode];
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Åtkomst nekad"
                                                    message:msg 
                                                   delegate:self
                                          cancelButtonTitle:@"Ok" 
                                          otherButtonTitles:nil];
    
    [alert show];
    
}

// starts a request
- (void)startAuthentication
{
    HTTPAuthentication *auth = [[HTTPAuthentication alloc] init:[self checkCredentialURL] withDelegate:self withUser:[personNumberTextEdit text] withPassword:[pinCodeTextEdit text]];
  
    [auth get];
}

- (void)authReady:(NSInteger)code
{
    if (code == 200)
    {
        [self performSegueWithIdentifier:@"webView" sender:nextPageButton];
    }
    else
    {
        [self showAuthError:code];
    }
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
    [HTTPAuthentication cleanSession];
    NSURL *url = [self logoutURL];
    HTTPConnection *conn = [[HTTPConnection alloc] init:url withDelegate:self];
    [conn get];
    // reset pinCode
    [pinCodeTextEdit setText:@""];
}

- (void)pushKeeping
{
    // Clear Badge
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    if ([prefs boolForKey:@"isDeviceTokenUpdated"])
    {
        NSString* token = [prefs stringForKey:@"deviceToken"];
        // Start registration
        NSURL *url = [self pushRegistrationURL];
        HTTPConnection *conn = [[HTTPConnection alloc] init:url withDelegate:self];
        [conn synchronizedPost:[NSString stringWithFormat:@"apnsRegistrationId=%@", token]];
    }
}

#pragma mark - URLHandler

- (void)connReady:(NSInteger)code
{
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    if (code == 200) 
    {
        [prefs setBool:NO forKey:@"isDeviceTokenUpdated"];
        [prefs synchronize];
    }
    NSLog(@"RegistrationId token update done with code: %d\n", code);
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
        [self pushKeeping];
    }
}

- (NSURL*)startURL
{
    NSString *urlString = [self  baseURLString];
    urlString = [urlString stringByAppendingString:[Util infoValueForKey:@"NCStartPage"]];
    NSLog(@"Start URL --> %@\n", urlString);
    
    return [[NSURL alloc] initWithString:urlString];
}

@end
