//
//  FlipsideViewController.h
//  netcare
//
//  Created by Peter Larsson on 2012-02-26.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FlipsideViewController;

@protocol FlipsideViewControllerDelegate
- (void)flipsideViewControllerDidFinish:(FlipsideViewController *)controller;
- (NSURL*)startURL;
@end

@interface FlipsideViewController : UIViewController  <UIWebViewDelegate>

@property (weak, nonatomic) IBOutlet id <FlipsideViewControllerDelegate> delegate;
@property (weak, nonatomic) IBOutlet UIWebView *webView;

- (IBAction)done:(id)sender;
- (IBAction)refresh:(id)sender;

@end
