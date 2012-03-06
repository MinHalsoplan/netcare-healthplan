//
//  BasicURLConnectionDelegate.h
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

@class HTTPConnection;

@protocol HTTPConnectionDelegate
- (void)connReady:(NSInteger)code;
@end

@interface HTTPConnection : NSObject <NSURLConnectionDataDelegate>

@property (weak, atomic, readonly) NSURL *url;
@property (weak, atomic, readonly) id connDelegate;
@property (retain, readonly) NSURLConnection *conn;

- (HTTPConnection*)init:(NSURL*)theUrl withDelegate:(id)theDelegate;
- (void)get;
- (void)synchronizedPost:(NSString*)data;
- (void)ready:(NSInteger)code connection:(NSURLConnection*)connection;

@end
