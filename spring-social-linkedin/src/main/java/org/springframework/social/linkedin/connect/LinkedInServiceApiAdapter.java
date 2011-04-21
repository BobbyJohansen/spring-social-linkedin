/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.linkedin.connect;

import org.springframework.social.connect.ServiceApiAdapter;
import org.springframework.social.connect.ServiceProviderConnectionValues;
import org.springframework.social.connect.ServiceProviderUserProfile;
import org.springframework.social.linkedin.api.LinkedInApi;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.web.client.HttpClientErrorException;

public class LinkedInServiceApiAdapter implements ServiceApiAdapter<LinkedInApi> {

	public boolean test(LinkedInApi serviceApi) {
		try {
			serviceApi.getUserProfile();
			return true;
		} catch (HttpClientErrorException e) {
			// TODO: Have api throw more specific exception and trigger off of that.
			return false;
		}
	}

	public ServiceProviderConnectionValues getConnectionValues(LinkedInApi serviceApi) {
		LinkedInProfile profile = serviceApi.getUserProfile();
		String displayName = profile.getFirstName() + " " + profile.getLastName();
		return new ServiceProviderConnectionValues(profile.getId(), displayName, profile.getPublicProfileUrl(), profile.getProfilePictureUrl());
	}

	public ServiceProviderUserProfile fetchUserProfile(LinkedInApi serviceApi) {
		LinkedInProfile profile = serviceApi.getUserProfile();
		String fullName = profile.getFirstName() + " " + profile.getLastName();
		// LinkedIn doesn't expose user emails via the API and there is no concept of username
		return new ServiceProviderUserProfile(fullName, profile.getFirstName(), profile.getLastName(), null, null);
	}
	
	public void updateStatus(LinkedInApi serviceApi, String message) {
		// not supported yet
	}
	
}
