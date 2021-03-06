/**
 * Licensed to ESUP-Portail under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * ESUP-Portail licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.esupsignature.web.controller.admin;

import org.apache.commons.io.FileUtils;
import org.esupportail.esupsignature.service.security.SessionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin/currentsessions")
@Controller
public class CurrentSessionsController {
	
	@ModelAttribute("adminMenu")
	String getCurrentMenu() {
		return "active";
	}

	@ModelAttribute("activeMenu")
	public String getActiveMenu() {
		return "currentSessions";
	}

	@Resource
	@Qualifier("sessionRegistry")
	private SessionRegistry sessionRegistry;

	@Resource
	private SessionService sessionService;

	@GetMapping
	public String getCurrentSessions(Model model) throws NoSuchMethodException {
		Map<String, List<Session>> allSessions = new HashMap<>();
		List<Object> principals = sessionRegistry.getAllPrincipals();
		long sessionSize = 0;
		for(Object principal: principals) {
			List<Session> sessions = new ArrayList<>();
			List<SessionInformation> sessionInformations =  sessionRegistry.getAllSessions(principal, true);
			for(SessionInformation sessionInformation : sessionInformations) {
				Session session = sessionService.getSessionById(sessionInformation.getSessionId());
				if(session != null) {
					for(String attr : session.getAttributeNames()) {
						sessionSize += session.getAttribute(attr).toString().getBytes().length;
					}
					sessions.add(session);
				}
			}
			if(sessions.size() > 0) {
				allSessions.put(((UserDetails) principal).getUsername(), sessions);
			}

		}
		model.addAttribute("currentSessions", allSessions);
		model.addAttribute("sessionSize", FileUtils.byteCountToDisplaySize(sessionSize));
		model.addAttribute("active", "sessions");
		return "admin/currentsessions";
	}

	@DeleteMapping
	public String deleteSessions(@RequestParam String sessionId) {
		Session session = sessionService.getSessionById(sessionId);
		if(session != null) {
			sessionService.deleteSessionById(session.getId());
		}
		return "redirect:/admin/currentsessions";
	}

}
