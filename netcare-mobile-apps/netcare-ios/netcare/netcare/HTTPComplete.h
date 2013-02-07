//
//  HTTPComplete.h
//  netcare
//
//  Created by Johannes Carlén on 2013-01-24.
//
//

#import <Foundation/Foundation.h>

@class HTTPComplete;

@protocol HTTPCompleteDelegate
- (void)authenticationCompleted:(NSString *)token;
@end

@interface HTTPComplete : NSObject {
    NSMutableData* httpResponse;
    NSString* token;
}

@property (weak, atomic, readonly) id mainViewController;

- (HTTPComplete*)initWithDelegate:(id)theDelegate;
- (void)sendRequest:(NSString*)ref;

@end
