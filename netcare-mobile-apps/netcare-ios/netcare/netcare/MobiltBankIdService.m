//
//  MobiltBankIdService.m
//  netcare
//
//  Created by Johannes Carlén on 2013-01-23.
//
//

#import "MobiltBankIdService.h"

@implementation MobiltBankIdService

- (MobiltBankIdService*) initWithCrn:(NSString*)theCrn andDelegate:(MainViewController *)theDelegate;{
    NSLog(@"MobiltBankIdService.initWithCrn:  %@", theCrn);
    crn = theCrn;
    delegate = theDelegate;
    return self;
}

- (void) authenticate {
    NSLog(@"MobiltBankIdService.authenticate");
    
    HTTPAuthenticate* httpAuthenticate = [[HTTPAuthenticate alloc] initWithCrn:crn];
    [httpAuthenticate sendRequest];
    
}

- (void) complete:(NSString*)ref {
    NSLog(@"MobiltBankIdService.complete");

    HTTPComplete* httpComplete = [[HTTPComplete alloc] initWithDelegate:delegate];
    [httpComplete sendRequest:ref];
    
}


@end
