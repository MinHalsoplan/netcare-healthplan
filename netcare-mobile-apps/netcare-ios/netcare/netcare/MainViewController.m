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

@synthesize backgroundImageView;
@synthesize personNumberTextEdit;
@synthesize nextPageButton;
@synthesize loginButton;
@synthesize shadowedLabel;

@synthesize orderrefToken;

- (IBAction)authenticate:(id)sender {
    NSLog(@"autenthicate() called");
    [loginButton setEnabled:NO];
    NSString* pnr0 = [personNumberTextEdit text];
    NSLog(@"Inmatat personnummer: %@\n", pnr0);
    NSString* pnr = [Util formatPersonnummer: pnr0];
    [personNumberTextEdit setText: pnr];
    NSLog(@"Validerar formaterat personnummer: %@\n", pnr);
    if([Util validatePersonnummer: pnr]) {
        [self savePersonalNumber: pnr];
        [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
        
        NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
        if ([prefs boolForKey:@"nc_dev_mode"]) {
            HTTPBasicAuth* basicAuth = [[HTTPBasicAuth alloc] init: self withUser:pnr];
            [basicAuth pleaseDo];
        } else {
            MobiltBankIdService *bankIdService = [[MobiltBankIdService alloc] initWithCrn:pnr andDelegate:self];
            [bankIdService authenticate];
        }
    } else {
        [Util displayAlert:@"" withMessage:@"Felaktigt personnumer"];
    }
    
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

// Saves personal number.

- (void)savePersonalNumber:(NSString*) pnr {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
	[prefs setValue:pnr forKey:@"personalNumber"];
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

- (BOOL)shouldAutorotate
{
    return NO;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    //return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
    return NO;
}


#pragma mark - Push notifications stuff

- (void)handlePushNotifications
{
    // Clear Badge
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    
    if ([prefs boolForKey:@"nc_use_notifications"]) {
//        if ([prefs boolForKey:@"isDeviceTokenUpdated"])
//        {
            NSString* token = [prefs stringForKey:@"deviceToken"];
            // Start registration
            NSURL *url = [self pushRegistrationURL];
            HTTPPushConnection *conn = [[HTTPPushConnection alloc] init:url withDelegate:self];
            [conn synchronizedPost:[NSString stringWithFormat:@"apnsRegistrationId=%@", token]];
//        }
    } else {
        NSURL *url = [self pushRegistrationURL];
        HTTPPushConnection *conn = [[HTTPPushConnection alloc] init:url withDelegate:self];
        [conn synchronizedDelete];
    }
}
- (NSURL*)pushRegistrationURL
{
    NSString *urlString = [Util  baseURLString];
    urlString = [urlString stringByAppendingString:[Util infoValueForKey:@"NCPushRegistrationPage"]]; 
    NSLog(@"Push Registration:  URL --> %@\n", urlString);
    return [NSURL URLWithString:urlString];         
}


// Authentication finished

// From HTTPCompleteDelegate:
- (void)authenticationCompleted:(NSString *)token {
    [self switchToWebView:token];
}

- (void)switchToWebView:(NSString*)ref {
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

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

#pragma mark - URLHandler

// From HTTPPushConnectionDelegate:
- (void)connReady:(NSInteger)code
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

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
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSLog(@"MainViewController.prepareForSegue: %@\n", [segue identifier]);
    
    if ([[segue identifier] isEqualToString:@"webView"]) {
        [[segue destinationViewController] setDelegate:self];
        [self handlePushNotifications];
    }
}

// Other stuff
- (NSURL*)startURL
{
    NSString *urlString = [Util baseURLString];
    urlString = [urlString stringByAppendingString:[Util infoValueForKey:@"NCStartPage"]];
    NSLog(@"MainViewController.startURL --> %@\n", urlString);
    return [[NSURL alloc] initWithString:urlString];
}

@end
