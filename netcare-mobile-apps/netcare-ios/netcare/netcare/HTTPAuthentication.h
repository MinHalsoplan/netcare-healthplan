//
//  HTTPAuthentication.h
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


#import <Foundation/Foundation.h>
#import "HTTPConnection.h"

@class HTTPAuthentication;

@protocol HTTPAuthenticationDelegate
- (void)authReady:(NSInteger)code;
@end

@interface HTTPAuthentication : HTTPConnection <HTTPConnectionDelegate>

@property (weak, atomic, readonly) NSString *user;
@property (weak, atomic, readonly) NSString *password;
@property (weak, atomic, readonly) id authDelegate;



- (HTTPAuthentication*)init:(NSURL*)theUrl withDelegate:(id)theDelegate withUser:(NSString*)theUser withPassword:(NSString*)thePassword;

+ (void)cleanSession;

@end
