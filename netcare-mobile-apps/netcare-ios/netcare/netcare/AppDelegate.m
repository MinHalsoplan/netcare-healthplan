//
//  AppDelegate.m
//  netcare
//
// Copyright (C) 2012 Callista Enterprise AB <info@callistaenterprise.se>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.


#import "AppDelegate.h"

#import "MainViewController.h"
#import "Util.h"
#import "MobiltBankIdService.h"

@implementation AppDelegate

@synthesize window = _window;


// push registration
- (void)application:(UIApplication*)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData*)deviceToken
{
    NSString *token = [Util toHexString:deviceToken]; 
	NSLog(@"APNS Device token: %@", token);
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    NSString *oldToken = [prefs valueForKey:@"deviceToken"];
    if (![token isEqualToString:oldToken])
    {
        NSLog(@"APNS Device token is updated");
        [prefs setValue:token forKey:@"deviceToken"];
        [prefs setBool:YES forKey:@"isDeviceTokenUpdated"];
        [prefs synchronize];
    }
}

- (BOOL)application:(UIApplication*) application handleOpenURL:(NSURL*) url{
    if (!url) return NO;
    NSString* urlString=[url absoluteString];
    NSLog(@"Received URL %@",urlString);

    NSString *token = [urlString substringFromIndex: 10];
    NSLog (@"token = %@", token);

    [((MainViewController*)self.window.rootViewController) switchToWebView:token];

//    MobiltBankIdService *bankIdService = [[MobiltBankIdService alloc] init];
//    [bankIdService collect:ref];
    
    return YES;
}

- (void)application:(UIApplication*)application didFailToRegisterForRemoteNotificationsWithError:(NSError*)error
{
	NSLog(@"Failed to get APNS Device token, error: %@", error);
}

// 
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // register for push.
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
     (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];

    return YES;
}
							
- (void)applicationWillResignActive:(UIApplication *)application
{
    /*
     Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
     Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
     */
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    /*
     Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
     If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
     */
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    /*
     Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
     */
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    /*
     Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
     */
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    /*
     Called when the application is about to terminate.
     Save data if appropriate.
     See also applicationDidEnterBackground:.
     */
}

@end
