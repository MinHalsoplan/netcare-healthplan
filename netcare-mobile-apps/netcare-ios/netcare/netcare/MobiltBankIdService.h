//
//  MobiltBankIdService.h
//  netcare
//
//  Created by Johannes Carlén on 2013-01-23.
//
//

#import <Foundation/Foundation.h>

#import "HTTPAuthenticate.h"
#import "HTTPComplete.h"
#import "MainViewController.h"

@class MainViewController;

@interface MobiltBankIdService : NSObject {
    NSString* crn;
    MainViewController* delegate;
}

- (MobiltBankIdService*) initWithCrn:(NSString*)crn andDelegate:(MainViewController *)delegate;

- (void) authenticate;
- (void) complete:(NSString*)ref;

@end
