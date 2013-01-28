//
//  MobiltBankIdService.m
//  netcare
//
//  Created by Johannes Carlén on 2013-01-23.
//
//

#import "MobiltBankIdService.h"

@implementation MobiltBankIdService

- (MobiltBankIdService*) initWithCrn:(NSString*)theCrn {
    NSLog(@"MobiltBankIdService.initWithCrn:  %@", theCrn);
    crn = theCrn;
    return self;
}

- (void) authenticate {
    NSLog(@"MobiltBankIdService.authenticate");
    
    HTTPAuthenticate* httpAuthenticate = [[HTTPAuthenticate alloc] initWithCrn:crn];
    [httpAuthenticate sendRequest];
    
}

- (void) collect:(NSString*)ref {
    NSLog(@"MobiltBankIdService.collect");

    HTTPCollect* httpCollect = [[HTTPCollect alloc] init];
    [httpCollect sendRequest:ref];
    
}


@end
