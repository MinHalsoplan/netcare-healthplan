//
//  HTTPCollect.h
//  netcare
//
//  Created by Johannes Carlén on 2013-01-24.
//
//

#import <Foundation/Foundation.h>

@interface HTTPCollect : NSObject {
    NSMutableData* httpResponse;
}

- (HTTPCollect*)init;
- (void)sendRequest:(NSString*)ref;

@end
