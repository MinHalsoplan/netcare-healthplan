//
//  FlipsideViewController.m
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


#import "FlipsideViewController.h"

@implementation FlipsideViewController

@synthesize delegate = _delegate;
@synthesize webView;

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[_delegate startURL]];
    //[request addValue:@"X-netcare-order" forHTTPHeaderField:[_delegate orderrefToken]];
    [webView loadRequest:request];
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

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
 
    NSString *requestString = [[[request URL] absoluteString] stringByReplacingPercentEscapesUsingEncoding: NSUTF8StringEncoding];
    
    NSLog(@"FlipsideViewController.webView - %@\n", requestString);
    
    if ([requestString hasPrefix:@"ios-log:"]) {
        NSString* logString = [[requestString componentsSeparatedByString:@":#iOS#"] objectAtIndex:1];
        NSLog(@"UIWebView console: %@", logString);
        return NO;
    }
    
    return YES;
}

- (void)webViewDidStartLoad:(UIWebView *)webView
{      
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    NSLog(@"Load started");   
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;

    NSLog(@"Load finished");    
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    //NSURLErrorDomain                                                                                                       
    // load error, hide the activity indicator in the status bar                                                             
    NSLog(@"Load failed: %@\n", [error localizedDescription]);
    [self done:nil];
}


#pragma mark - Actions

- (IBAction)done:(id)sender
{
    // clean up content
    [webView stringByEvaluatingJavaScriptFromString:@"document.open();document.close();"];
    // go back to logon screen
    [self.delegate flipsideViewControllerDidFinish:self];
}

//
// on a forms page, go back to main page and reload.... 
// reloading a reporting page makes no sense
- (IBAction)refresh:(id)sender
{
    if ([webView canGoBack]) 
    {
        [webView goBack];
    } 
    [webView reload];
}

@end
