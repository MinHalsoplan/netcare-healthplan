//
//  HTTPAuthenticate.h
//  netcare
//
//  Created by Johannes Carlén on 2013-01-23.
//
//

#import <Foundation/Foundation.h>

#import "MainViewController.h"

@class MainViewController;

@interface HTTPAuthenticate : NSObject {
    NSString* crn;
    NSMutableData* httpResponse;
    MainViewController* delegate;
}

@property (nonatomic, assign) MainViewController *mainViewController;

- (HTTPAuthenticate*)initWithCrn:(NSString*)crn;
- (void)sendRequest;

@end
