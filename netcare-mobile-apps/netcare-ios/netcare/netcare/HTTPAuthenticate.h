//
//  HTTPAuthenticate.h
//  netcare
//
//  Created by Johannes Carlén on 2013-01-23.
//
//

#import <Foundation/Foundation.h>

@interface HTTPAuthenticate : NSObject {
    NSString* crn;
    NSMutableData* httpResponse;
}
- (HTTPAuthenticate*)initWithCrn:(NSString*)crn;
- (void)sendRequest;

@end
