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

#import "QuartzCore/QuartzCore.h"
#import "Util.h"

@implementation MainViewController

@synthesize personNumberTextEdit;
@synthesize nextPageButton;
@synthesize loginButton;
@synthesize shadowedLabel;

@synthesize orderrefToken;

- (IBAction)authenticate:(id)sender {
    NSLog(@"autenthicate() called");
    [loginButton setEnabled:NO];
    NSLog(@"Personnummer: %@\n", [personNumberTextEdit text]);
    [self savePersonalNumber];
    MobiltBankIdService *bankIdService = [[MobiltBankIdService alloc] initWithCrn:[personNumberTextEdit text] andDelegate:self];
    [bankIdService authenticate];
    
}


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

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    shadowedLabel.layer.cornerRadius=10;
    
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
    //return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
    return NO;
}


#pragma mark - Basic Auth Stuff

//
- (NSURL*)logoutURL
{
    NSString *urlString = [Util  baseURLString];
    urlString = [urlString stringByAppendingString:[Util infoValueForKey:@"NCLogoutPage"]]; 
    NSLog(@"Logout:  URL --> %@\n", urlString);
    return [NSURL URLWithString:urlString];         
}

//
- (NSURL*)pushRegistrationURL
{
    NSString *urlString = [Util  baseURLString];
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

- (void)switchToWebView:(NSString*)ref {
    NSLog(@"MainViewController.switchToWebView %@", ref);
    [self performSegueWithIdentifier:@"webView" sender:nextPageButton];
    [self setOrderrefToken:ref];
}

// hide keyboard
- (IBAction)textFieldReturn:(id)sender {
    [sender resignFirstResponder];
}

//
- (IBAction)backgroundTouched:(id)sender {
    if ([personNumberTextEdit isFirstResponder])
        [personNumberTextEdit resignFirstResponder];
}

// clean up stuff
// to prepare for a new session
- (void)cleanSession
{
    [HTTPAuthentication cleanSession];
    NSURL *url = [self logoutURL];
    HTTPConnection *conn = [[HTTPConnection alloc] init:url withDelegate:nil];
    [conn get];
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
    [self dismissViewControllerAnimated:YES completion:nil];
    [self cleanSession];
    
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSLog(@"MainViewController.prepareForSegue: %@\n", [segue identifier]);
    
    if ([[segue identifier] isEqualToString:@"webView"]) {
        [[segue destinationViewController] setDelegate:self];
        [self pushKeeping];
    }
}

- (NSURL*)startURL
{
    NSString *urlString = [Util  baseURLString];
    urlString = [urlString stringByAppendingString:[Util infoValueForKey:@"NCStartPage"]];
    NSLog(@"MainViewController.startURL --> %@\n", urlString);
    return [[NSURL alloc] initWithString:urlString];
}

- (void)displayAlert {
    NSString* msg = [NSString stringWithFormat:@"Det finns ingen patient med hälsoplan som har personnummer %@", [personNumberTextEdit text]];
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Ingen patient registrerad."
                                                    message:msg
                                                   delegate:nil
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}
@end
