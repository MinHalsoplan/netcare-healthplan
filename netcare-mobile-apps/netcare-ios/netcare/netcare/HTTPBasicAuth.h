//
//  HTTPBasicAuth.h
//  netcare
//
//  Created by Johannes Carlén on 2013-05-15.
//
//

#import <Foundation/Foundation.h>

@class HTTPBasicAuth;

@protocol HTTPBasicAuthDelegate
- (void)authenticationCompleted:(NSString *)token;
@end

@interface HTTPBasicAuth : NSObject {
    NSString* personnummer;
    NSMutableData* httpResponse;
}

@property (weak, atomic, readonly) id mainViewController;

- (HTTPBasicAuth*)init:(id)delegate withUser:(NSString*)user;
- (void)pleaseDo;

@end
