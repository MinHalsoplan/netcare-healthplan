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
package org.callistasoftware.netcare.web.mobile.controller;

import org.callistasoftware.netcare.core.api.ServiceResult;
import org.callistasoftware.netcare.core.api.impl.ServiceResultImpl;
import org.callistasoftware.netcare.core.api.messages.GenericSuccessMessage;
import org.callistasoftware.netcare.web.controller.ControllerSupport;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Mobile app controller.
 */
@Controller
@RequestMapping(value = "")
public class MobileController extends ControllerSupport {

    /**
     * Start page of mobile app.
     *
     * @param auth
     * @return page
     */
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public final String displayMobileStartPage(final Authentication auth) {
        return "start";
    }

    /**
     * @param activity
     * @param m
     * @return
     */
    @RequestMapping(value = "/activity/{activity}/report", method = RequestMethod.GET)
    public final String displayReportPage(@PathVariable(value = "activity") final Long activity, final Model m) {
        m.addAttribute("activityId", activity);
        return "report";
    }

    /**
     * Url for initiating authentication
     * @return
     */
    @RequestMapping(value = "/checkcredentials", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public final ServiceResult<Boolean> checkUserCredentials() {
        return ServiceResultImpl.createSuccessResult(Boolean.TRUE, new GenericSuccessMessage());
    }


    /**
     * Logs out the user from the app.
     *
     * @return JSON
     */
    @RequestMapping(value = "/logout")
    @ResponseBody
    public final String appLogout(final HttpSession sc, final HttpServletRequest request) {
        getLog().info("App Logout");
        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();
        return "{ \"loggedout\": true }";
    }
}
