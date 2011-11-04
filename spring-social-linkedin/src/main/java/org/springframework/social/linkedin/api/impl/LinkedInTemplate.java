/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.linkedin.api.impl;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInConnections;
import org.springframework.social.linkedin.api.LinkedInNetworkUpdate;
import org.springframework.social.linkedin.api.LinkedInNetworkUpdates;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.oauth1.AbstractOAuth1ApiBinding;

/**
 * This is the central class for interacting with LinkedIn.
 * <p>
 * Greenhouse operations require OAuth authentication with the server.
 * Therefore, LinkedInTemplate must be constructed with the minimal information
 * required to sign requests with and OAuth 1 Authorization header.
 * </p>
 * @author Craig Walls
 */
public class LinkedInTemplate extends AbstractOAuth1ApiBinding implements LinkedIn {

	/**
	 * Creates a new LinkedInTemplate given the minimal amount of information needed to sign requests with OAuth 1 credentials.
	 * @param consumerKey the application's API key
	 * @param consumerSecret the application's API secret
	 * @param accessToken an access token acquired through OAuth authentication with LinkedIn
	 * @param accessTokenSecret an access token secret acquired through OAuth authentication with LinkedIn
	 */
	public LinkedInTemplate(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		super(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		registerLinkedInJsonModule();
	}
	
	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getPublicProfileUrl();
	}

	public LinkedInProfile getUserProfile() {
		return getRestTemplate().getForObject(PROFILE_URL, LinkedInProfile.class);
	}
	
	public List<LinkedInNetworkUpdate> getNetworkUpdates() {
		return getNetworkUpdates(0,100);
	}
	
	public List<LinkedInNetworkUpdate> getNetworkUpdates(int start, int count) {
		return getNetworkUpdates(start, count, LinkedInNetworkUpdates.class).getUpdates();
	}
	
	public <T> T  getNetworkUpdates(int start, int count, Class<T> responseType) {
		return getRestTemplate().getForObject(UPDATES_URL, responseType, count, start);
	}
	
	public String getNetworkUpdatesJson() {
		return getNetworkUpdates(0, 100, String.class);
	}
	
	public String getNetworkUpdatesJson(int start, int count) {
		return getNetworkUpdates(start, count, String.class);
	}
	
	public String getJson(String url) {
		return getRestTemplate().getForObject(url, String.class);
	}

	public List<LinkedInProfile> getConnections() {
		LinkedInConnections connections = getRestTemplate().getForObject(CONNECTIONS_URL, LinkedInConnections.class);
		return connections.getConnections();
	}

	// private helper
	
	private void registerLinkedInJsonModule() {
		List<HttpMessageConverter<?>> converters = getRestTemplate().getMessageConverters();
		for (HttpMessageConverter<?> converter : converters) {
			if(converter instanceof MappingJacksonHttpMessageConverter) {
				MappingJacksonHttpMessageConverter jsonConverter = (MappingJacksonHttpMessageConverter) converter;
				ObjectMapper objectMapper = new ObjectMapper();				
				objectMapper.registerModule(new LinkedInModule());
				jsonConverter.setObjectMapper(objectMapper);
			}
		}
	}

	static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,industry,site-standard-profile-request,public-profile-url,picture-url,summary)?format=json";

	static final String UPDATES_URL = "https://api.linkedin.com/v1/people/~/network/updates?format=json&count={count}&start={start}";
	
	static final String CONNECTIONS_URL = "https://api.linkedin.com/v1/people/~/connections?format=json";
}
