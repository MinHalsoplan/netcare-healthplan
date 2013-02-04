//
//  HTTPComplete.h
//  netcare
//
//  Created by Johannes Carlén on 2013-01-24.
//
//

#import <Foundation/Foundation.h>

#import "MainViewController.h"

@class MainViewController;

@interface HTTPComplete : NSObject {
    NSMutableData* httpResponse;
    NSString* token;
}

@property (nonatomic, assign) MainViewController *mainViewController;

- (HTTPComplete*)init;
- (void)sendRequest:(NSString*)ref;

@end
