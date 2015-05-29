/**
 * Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package netcare

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class PatientSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://46.137.134.138")
		.inferHtmlResources()
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("sv-SE,sv;q=0.8,en-US;q=0.5,en;q=0.3")
		.connection("keep-alive")
		.contentTypeHeader("application/json; charset=UTF-8")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:29.0) Gecko/20100101 Firefox/29.0")

	val headers_0 = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

	val headers_2 = Map("X-Requested-With" -> "XMLHttpRequest")

	val headers_13 = Map(
		"Accept" -> "application/json, text/javascript, */*; q=0.01",
		"Pragma" -> "no-cache",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_17 = Map("Accept" -> "text/css,*/*;q=0.1")

	val headers_20 = Map("Accept" -> "image/png,image/*;q=0.8,*/*;q=0.5")

    val uri1 = "http://46.137.134.138/v2"

	val scn = scenario("PatientSimulation")
		// Patient
		.exec(http("request_0")
			.get("/v2/netcare")
			.headers(headers_0))
		.pause(8)
		.exec(http("request_1")
			.post("/v2/j_spring_security_check")
			.headers(headers_0)
			.formParam("j_username", "191212121212")
			.resources(http("request_2")
			.get(uri1 + "/api/comments?_=1424797782401")
			.headers(headers_2),
            http("request_3")
			.get(uri1 + "/api/scheduledActivities?due=true&reported=false&start=1423588182398&end=1424797782398&_=1424797782406")
			.headers(headers_2),
            http("request_4")
			.get(uri1 + "/api/activityPlans?onlyOngoing=false&_=1424797782409")
			.headers(headers_2)))
		.pause(24)
		.exec(http("request_5")
			.get("/v2/netcare/user/report")
			.headers(headers_0)
			.resources(http("request_6")
			.get(uri1 + "/api/scheduledActivities?due=true&reported=true&start=1424732400000&end=1424732400000&_=1424797808323")
			.headers(headers_2)))
		.pause(9)
		.exec(http("request_7")
			.get("/v2/api/scheduledActivities?due=true&reported=true&start=1420066800000&end=1424732400000&_=1424797817449")
			.headers(headers_2))
		.pause(14)
		.exec(http("request_8")
			.get("/v2/api/scheduledActivities?due=true&reported=true&start=1388530800000&end=1424732400000&_=1424797832552")
			.headers(headers_2))
		.pause(3)
		.exec(http("request_9")
			.get("/v2/netcare/user/extra-report")
			.headers(headers_0)
			.resources(http("request_10")
			.get(uri1 + "/api/activityPlans?onlyOngoing=false&_=1424797835976")
			.headers(headers_2)))
		.pause(6)
		.exec(http("request_11")
			.get("/v2/netcare/user/profile")
			.headers(headers_0)
			.resources(http("request_12")
			.get(uri1 + "/api/user/38/load?_=1424797843344")
			.headers(headers_2)))
		.pause(3)
		.exec(http("request_13")
			.post("/v2/api/user/38/update")
			.headers(headers_13)
			.body(RawFileBody("PatientSimulation_0013_request.txt")))
		.pause(2)
		.exec(http("request_14")
			.get("/v2/netcare/shared/select-results")
			.headers(headers_0)
			.resources(http("request_15")
			.get(uri1 + "/api/activityPlans?patient=38&onlyOngoing=false&_=1424797849716")
			.headers(headers_2)))
		.pause(7)
		.exec(http("request_16")
			.get("/v2/netcare/shared/results?activity=166868")
			.headers(headers_0)
			.resources(http("request_17")
			.get(uri1 + "/css/print-results.css")
			.headers(headers_17),
            http("request_18")
			.get(uri1 + "/js/highstock-1.2.5/highstock.js"),
            http("request_19")
			.get(uri1 + "/api/activityPlans/166868?_=1424797859316")
			.headers(headers_2),
            http("request_20")
			.get(uri1 + "/netcare/resources/images/icons/toolbarIcons.png")
			.headers(headers_20),
            http("request_21")
			.get(uri1 + "/api/healthplans/activity/item/166869/statistics?_=1424797859449")
			.headers(headers_2),
            http("request_22")
			.get(uri1 + "/api/healthplans/activity/item/166870/statistics?_=1424797859452")
			.headers(headers_2),
            http("request_23")
			.get(uri1 + "/api/healthplans/activity/item/166871/statistics?_=1424797859453")
			.headers(headers_2)))
		.pause(9)
		.exec(http("request_24")
			.get("/v2/netcare/home")
			.headers(headers_0)
			.resources(http("request_25")
			.get(uri1 + "/api/scheduledActivities?due=true&reported=false&start=1423588269588&end=1424797869588&_=1424797869596")
			.headers(headers_2),
            http("request_26")
			.get(uri1 + "/api/comments?_=1424797869590")
			.headers(headers_2),
            http("request_27")
			.get(uri1 + "/api/activityPlans?onlyOngoing=false&_=1424797869599")
			.headers(headers_2)))
		.pause(34)
		.exec(http("request_28")
			.get("/v2/netcare/security/logout")
			.headers(headers_0))

	setUp(scn.inject(rampUsers(50) over (120 seconds))).protocols(httpProtocol)
}