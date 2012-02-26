//
//  MainViewController.h
//  netcare
//
//  Created by Peter Larsson on 2012-02-26.
//  Copyright (c) 2012 Callista Enterprise AB. All rights reserved.
//
#import "FlipsideViewController.h"

@interface MainViewController : UIViewController <FlipsideViewControllerDelegate, NSURLConnectionDataDelegate>

@property (weak, nonatomic) IBOutlet UITextField *personNumberTextEdit;
@property (weak, nonatomic) IBOutlet UITextField *pinCodeTextEdit;
// nextpage button is not really used, just keeps 
// a point to maintain a storyboard sequence, which really is invoked
// upon a proper authenitcation
@property (weak, nonatomic) IBOutlet UIButton *nextPageButton;

- (IBAction)login:(id)sender;
- (IBAction)textFieldReturn:(id)sender;
- (IBAction)backgroundTouched:(id)sender;

@end
