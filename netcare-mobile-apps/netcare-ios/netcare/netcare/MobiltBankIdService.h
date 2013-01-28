//
//  MobiltBankIdService.h
//  netcare
//
//  Created by Johannes Carlén on 2013-01-23.
//
//

#import <Foundation/Foundation.h>

#import "HTTPAuthenticate.h"
#import "HTTPCollect.h"

@interface MobiltBankIdService : NSObject {
    NSString* crn;
}

- (MobiltBankIdService*) initWithCrn:(NSString*)crn;

- (void) authenticate;
- (void) collect:(NSString*)ref;

@end
