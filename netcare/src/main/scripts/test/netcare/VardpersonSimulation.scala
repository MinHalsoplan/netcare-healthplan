package netcare

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://46.137.134.138")
		.inferHtmlResources()
		.acceptHeader("image/png,image/*;q=0.8,*/*;q=0.5")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("sv-SE,sv;q=0.8,en-US;q=0.5,en;q=0.3")
		.connection("keep-alive")
		.contentTypeHeader("application/x-www-form-urlencoded; charset=UTF-8")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:29.0) Gecko/20100101 Firefox/29.0")

	val headers_0 = Map("Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

	val headers_1 = Map("Accept" -> "text/css,*/*;q=0.1")

	val headers_2 = Map("Accept" -> "*/*")

	val headers_31 = Map(
		"Accept" -> "*/*",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_46 = Map(
		"Accept" -> "application/json, text/javascript, */*; q=0.01",
		"Content-Type" -> "application/json; charset=UTF-8",
		"Pragma" -> "no-cache",
		"X-Requested-With" -> "XMLHttpRequest")

	val headers_64 = Map(
		"Accept" -> "application/json, text/javascript, */*; q=0.01",
		"Pragma" -> "no-cache",
		"X-Requested-With" -> "XMLHttpRequest")

    val uri1 = "http://46.137.134.138/v2"

	val scn = scenario("RecordedSimulation")
		// Test1
		.exec(http("request_0")
			.get("/v2/netcare")
			.headers(headers_0)
			.resources(http("request_1")
			.get(uri1 + "/netcare/resources/css/netcare-ui.css")
			.headers(headers_1),
            http("request_2")
			.get(uri1 + "/netcare/resources/js/netcare-ui/Util.js")
			.headers(headers_2),
            http("request_3")
			.get(uri1 + "/netcare/resources/css/mvk-component.css")
			.headers(headers_1),
            http("request_4")
			.get(uri1 + "/netcare/resources/js/netcare-ui/Ajax.js")
			.headers(headers_2),
            http("request_5")
			.get(uri1 + "/netcare/resources/js/jquery.ui.touch-punch.min.js")
			.headers(headers_2),
            http("request_6")
			.get(uri1 + "/netcare/resources/js/underscore-1.4.2-min.js")
			.headers(headers_2),
            http("request_7")
			.get(uri1 + "/netcare/resources/js/netcare-common.js")
			.headers(headers_2),
            http("request_8")
			.get(uri1 + "/favicon.ico")
			.headers(headers_0),
            http("request_9")
			.get(uri1 + "/netcare/resources/css/mvk-styles.css")
			.headers(headers_1),
            http("request_10")
			.get(uri1 + "/netcare/resources/css/bootstrap-2.2.1.min.css")
			.headers(headers_1),
            http("request_11")
			.get(uri1 + "/js/netcare-healthplan.js")
			.headers(headers_2),
            http("request_12")
			.get(uri1 + "/netcare/resources/js/jquery-1.8.2.min.js")
			.headers(headers_2),
            http("request_13")
			.get(uri1 + "/netcare/resources/js/netcare-ui/PageMessages.js")
			.headers(headers_2),
            http("request_14")
			.get(uri1 + "/netcare/resources/js/mvk-ui.js")
			.headers(headers_2),
            http("request_15")
			.get(uri1 + "/netcare/resources/css/jquery-ui-1.8.24.custom.css")
			.headers(headers_1),
            http("request_16")
			.get(uri1 + "/netcare/resources/js/jquery.validate.min.js")
			.headers(headers_2),
            http("request_17")
			.get(uri1 + "/netcare/resources/js/bootstrap-2.2.1.min.js")
			.headers(headers_2),
            http("request_18")
			.get(uri1 + "/netcare/resources/js/jquery-ui-1.8.24.custom.min.js")
			.headers(headers_2),
            http("request_19")
			.get(uri1 + "/netcare/resources/images/baseBg.png")))
		.pause(31)
		.exec(http("request_20")
			.post("/v2/j_spring_security_check")
			.headers(headers_0)
			.formParam("j_username", "hsa-cg-1")
			.resources(http("request_21")
			.get(uri1 + "/css/netcare-healthplan.css")
			.headers(headers_1),
            http("request_22")
			.get(uri1 + "/netcare/resources/images/loaders/ajax-loader-medium.gif"),
            http("request_23")
			.get(uri1 + "/netcare/resources/images/header/headerBgBorder.png"),
            http("request_24")
			.get(uri1 + "/netcare/resources/images/backgroundLogo.png"),
            http("request_25")
			.get(uri1 + "/netcare/resources/images/header/headerBg.png"),
            http("request_26")
			.get(uri1 + "/netcare/resources/images/navigateBack.png"),
            http("request_27")
			.get(uri1 + "/netcare/resources/images/menuBg.png"),
            http("request_28")
			.get(uri1 + "/netcare/resources/images/header/headerLogo_small.png"),
            http("request_29")
			.get(uri1 + "/netcare/resources/images/menuBottomBg.png"),
            http("request_30")
			.get(uri1 + "/netcare/resources/images/menuTopBg.png"),
            http("request_31")
			.get(uri1 + "/api/healthplans/activity/reported/comments/newreplies?_=1424771472191")
			.headers(headers_31),
            http("request_32")
			.get(uri1 + "/netcare/resources/images/header/headRightSide.png"),
            http("request_33")
			.get(uri1 + "/netcare/resources/images/header/headLeftSide.png"),
            http("request_34")
			.get(uri1 + "/netcare/resources/images/icons/info_icon.png"),
            http("request_35")
			.get(uri1 + "/api/healthplans/activity/reported/latest?_=1424771472196")
			.headers(headers_31),
            http("request_36")
			.get(uri1 + "/netcare/resources/images/header/idCard.png"),
            http("request_37")
			.get(uri1 + "/netcare/resources/images/icons/mainMenuIcons.png"),
            http("request_38")
			.get(uri1 + "/api/alarm/list?_=1424771472197")
			.headers(headers_31),
            http("request_39")
			.get(uri1 + "/img/icons/menu_icons_v2.png"),
            http("request_40")
			.get(uri1 + "/img/icons/24/trash.png"),
            http("request_41")
			.get(uri1 + "/netcare/resources/images/containerBackgrounds/boxShadowBodyMain.png"),
            http("request_42")
			.get(uri1 + "/netcare/resources/images/containerBackgrounds/boxShadowTopAndBottom.png"),
            http("request_43")
			.get(uri1 + "/netcare/resources/images/containerBackgrounds/paperSlipBottomRight.png"),
            http("request_44")
			.get(uri1 + "/netcare/resources/images/containerBackgrounds/boxShadowBodySec.png"),
            http("request_45")
			.get(uri1 + "/netcare/resources/images/listItemBodyBg.png")))
		.pause(5)
		.exec(http("request_46")
			.post("/v2/api/alarm/279155/resolve")
			.headers(headers_46)
			.body(RawFileBody("RecordedSimulation_0046_request.txt"))
			.resources(http("request_47")
			.get(uri1 + "/api/alarm/list?_=1424771479122")
			.headers(headers_31)))
		.pause(9)
		.exec(http("request_48")
			.get("/v2/netcare/admin/activity/list")
			.headers(headers_0)
			.resources(http("request_49")
			.get(uri1 + "/netcare/resources/images/containerBackgrounds/paperSheetBottom.png"),
            http("request_50")
			.get(uri1 + "/api/healthplans/activity/reported/filter?personnummer=&dateFrom=2015-02-21&dateTo=2015-02-24&_=1424771489046")
			.headers(headers_31)))
		.pause(4)
		.exec(http("request_51")
			.get("/v2/api/healthplans/activity/reported/filter?personnummer=&dateFrom=2015-02-21&dateTo=2015-02-24&_=1424771493945")
			.headers(headers_31))
		.pause(2)
		.exec(http("request_52")
			.get("/v2/netcare/resources/css/images/ui-bg_highlight-soft_75_cccccc_1x100.png")
			.resources(http("request_53")
			.get(uri1 + "/netcare/resources/css/images/ui-bg_glass_75_e6e6e6_1x400.png"),
            http("request_54")
			.get(uri1 + "/netcare/resources/css/images/ui-bg_flat_75_ffffff_40x100.png"),
            http("request_55")
			.get(uri1 + "/netcare/resources/css/images/ui-icons_222222_256x240.png"),
            http("request_56")
			.get(uri1 + "/netcare/resources/css/images/ui-bg_glass_55_fbf9ee_1x400.png"),
            http("request_57")
			.get(uri1 + "/netcare/resources/css/images/ui-bg_glass_65_ffffff_1x400.png")))
		.pause(5)
		.exec(http("request_58")
			.get("/v2/api/healthplans/activity/reported/filter?personnummer=&dateFrom=2015-01-01&dateTo=2015-02-24&_=1424771501962")
			.headers(headers_31)
			.resources(http("request_59")
			.get(uri1 + "/css/images/togglerClosed.png")))
		.pause(6)
		.exec(http("request_60")
			.get("/v2/css/images/togglerClosedBlue.png")
			.resources(http("request_61")
			.get(uri1 + "/img/icons/little-read.png"),
            http("request_62")
			.get(uri1 + "/img/icons/little-like.png"),
            http("request_63")
			.get(uri1 + "/css/images/togglerOpen.png")))
		.pause(15)
		.exec(http("request_64")
			.post("/v2/api/healthplans/activity/270259/comment")
			.headers(headers_64)
			.formParam("comment", "Ja det var väl ok ??"))
		.pause(6)
		.exec(http("request_65")
			.post("/v2/api/healthplans/activity/270259/like")
			.headers(headers_64)
			.formParam("like", "true"))
		.pause(4)
		.exec(http("request_66")
			.get("/v2/netcare/home")
			.headers(headers_0)
			.resources(http("request_67")
			.get(uri1 + "/api/healthplans/activity/reported/comments/newreplies?_=1424771537807")
			.headers(headers_31),
            http("request_68")
			.get(uri1 + "/api/healthplans/activity/reported/latest?_=1424771537811")
			.headers(headers_31),
            http("request_69")
			.get(uri1 + "/api/alarm/list?_=1424771537812")
			.headers(headers_31)))
		.pause(7)
		.exec(http("request_70")
			.get("/v2/netcare/admin/patients")
			.headers(headers_0)
			.resources(http("request_71")
			.get(uri1 + "/api/user/load?_=1424771546376")
			.headers(headers_31),
            http("request_72")
			.get(uri1 + "/netcare/resources/images/listNavigationBgBlue.png")))
		.pause(4)
		.exec(http("request_73")
			.get("/v2/netcare/admin/templates")
			.headers(headers_0)
			.resources(http("request_74")
			.get(uri1 + "/api/categories?_=1424771551031")
			.headers(headers_31),
            http("request_75")
			.get(uri1 + "/api/support/accessLevels?_=1424771551116")
			.headers(headers_31),
            http("request_76")
			.get(uri1 + "/api/templates/?name=&category=all&level=all&_=1424771551194")
			.headers(headers_31),
            http("request_77")
			.get(uri1 + "/css/images/toolbarIconsBlue.png"),
            http("request_78")
			.get(uri1 + "/netcare/resources/images/shadow-btn.png"),
            http("request_79")
			.get(uri1 + "/netcare/resources/images/stateTextBg.png")))
		.pause(6)
		.exec(http("request_80")
			.get("/v2/netcare/admin/template/5853")
			.headers(headers_0)
			.resources(http("request_81")
			.get(uri1 + "/api/templates/5853?_=1424771558363")
			.headers(headers_31),
            http("request_82")
			.get(uri1 + "/api/categories?_=1424771558364")
			.headers(headers_31),
            http("request_83")
			.get(uri1 + "/css/images/listNavigationUpBgBlue.png"),
            http("request_84")
			.get(uri1 + "/api/support/accessLevels?_=1424771558486")
			.headers(headers_31),
            http("request_85")
			.get(uri1 + "/css/images/listNavigationDownBgBlue.png"),
            http("request_86")
			.get(uri1 + "/api/support/measureValueTypes?_=1424771558581")
			.headers(headers_31),
            http("request_87")
			.get(uri1 + "/api/units?_=1424771558582")
			.headers(headers_31)))
		.pause(7)
		.exec(http("request_88")
			.get("/v2/netcare/security/logout")
			.headers(headers_0))

	setUp(scn.inject(rampUsers(50) over (120 seconds))).protocols(httpProtocol)


}