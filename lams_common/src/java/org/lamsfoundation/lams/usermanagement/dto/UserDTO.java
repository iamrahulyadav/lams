/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0 
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
/* $$Id$$ */
package org.lamsfoundation.lams.usermanagement.dto;

import java.util.TimeZone;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.lamsfoundation.lams.themes.dto.CSSThemeBriefDTO;

/**
 * @author Manpreet Minhas
 */
public class UserDTO {

    private Integer userID;
    private String firstName;
    private String lastName;
    private String login;
    private String localeLanguage;
    private String localeCountry;
    private String fckLanguageMapping;
    private String direction;
    private String email;
    // private CSSThemeVisualElement theme;
    private CSSThemeBriefDTO flashTheme;
    private CSSThemeBriefDTO htmlTheme;
    private TimeZone timeZone;
    private Integer authenticationMethodId;
    private Boolean enableFlash;
    private String lamsCommunityToken;
    private String lamsCommunityUsername;
    private Boolean loggedIntoLamsCommunity;

    //	public UserDTO(Integer userID, String firstName, String lastName,
    //			String login, String email, CSSThemeVisualElement theme) {		
    //		this.userID = userID;
    //		this.firstName = firstName;
    //		this.lastName = lastName;
    //		this.login = login;
    //		this.email = email;
    //		this.theme = theme;
    //	}

    public UserDTO(Integer userID, String firstName, String lastName, String login, String localeLanguage,
	    String localeCountry, String direction, String email, CSSThemeBriefDTO flashTheme,
	    CSSThemeBriefDTO htmlTheme, TimeZone timezone, Integer authenticationMethodId, String fckLanguageMapping,
	    Boolean enableFlash, String lamsCommunityToken, String lamsCommunityUsername) {
	this.userID = userID;
	this.firstName = firstName;
	this.lastName = lastName;
	this.login = login;
	this.localeCountry = localeCountry;
	this.localeLanguage = localeLanguage;
	this.direction = direction;
	this.email = email;
	this.flashTheme = flashTheme;
	this.htmlTheme = htmlTheme;
	this.timeZone = timezone;
	this.authenticationMethodId = authenticationMethodId;
	this.fckLanguageMapping = fckLanguageMapping;
	this.enableFlash = enableFlash;
	this.lamsCommunityToken = lamsCommunityToken;
	this.lamsCommunityUsername = lamsCommunityUsername;
    }

    /**
     * Equality test of UserDTO objects
     */
    public boolean equals(Object o) {
	if ((o != null) && (o.getClass() == this.getClass())) {
	    return ((UserDTO) o).userID == this.userID;
	} else {
	    return false;
	}
    }

    /**
     * Returns the hash code value for this object.
     */
    public int hashCode() {
	// TODO this might be an ineffcient implementation since userIDs are likely to be sequential, 
	// hence we dont get a good spread of values
	return userID.intValue();
    }

    /**
     * @return Returns the firstName.
     */
    public String getFirstName() {
	return firstName;
    }

    /**
     * @return Returns the lastName.
     */
    public String getLastName() {
	return lastName;
    }

    /**
     * @return Returns the login.
     */
    public String getLogin() {
	return login;
    }

    /**
     * @return Returns the userID.
     */
    public Integer getUserID() {
	return userID;
    }

    public CSSThemeBriefDTO getFlashTheme() {
	return flashTheme;
    }

    public void setFlashTheme(CSSThemeBriefDTO flashTheme) {
	this.flashTheme = flashTheme;
    }

    public CSSThemeBriefDTO getHtmlTheme() {
	return htmlTheme;
    }

    public void setHtmlTheme(CSSThemeBriefDTO htmlTheme) {
	this.htmlTheme = htmlTheme;
    }

    public String getEmail() {
	return email;
    }

    public String getLocaleCountry() {
	return localeCountry;
    }

    public String getLocaleLanguage() {
	return localeLanguage;
    }

    /** Should the page be displayed left to right (LTR) or right to left (RTL) */
    public String getDirection() {
	return direction;
    }

    /** User's timezone. At the moment, this is always the server's timezone */
    public TimeZone getTimeZone() {
	return timeZone;
    }

    public Integer getAuthenticationMethodId() {
	return authenticationMethodId;
    }

    public String toString() {
	return new ToStringBuilder(this).append("userID", getUserID()).append("firstName", getFirstName()).append(
		"lastName", getLastName()).append("login", getLogin()).append("localeLanguage", getLocaleLanguage())
		.append("localeCountry", getLocaleCountry()).append("direction", getDirection()).append("email",
			getEmail()).append("flashTheme", getFlashTheme()).append("htmlTheme", getHtmlTheme()).append(
			"timeZone", getTimeZone()).append("authenticationMethodId", getAuthenticationMethodId())
		.append("fckLanguageMapping", getFckLanguageMapping()).append("enableFlash", getEnableFlash()).append(
			"lamsCommunityUser", this.getLamsCommunityUsername()).append("lamsCommunityToken",
			this.getLamsCommunityToken()).append("loggedIntoLamsCommunity",
			"" + this.getLoggedIntoLamsCommunity()).toString();
    }

    public String getFckLanguageMapping() {
	return fckLanguageMapping;
    }

    public Boolean getEnableFlash() {
	return enableFlash;
    }

    public void setEnableFlash(Boolean enableFlash) {
	this.enableFlash = enableFlash;
    }

    public String getLamsCommunityToken() {
	return lamsCommunityToken;
    }

    public void setLamsCommunityToken(String lamsCommunityToken) {
	this.lamsCommunityToken = lamsCommunityToken;
    }

    public String getLamsCommunityUsername() {
	return lamsCommunityUsername;
    }

    public void setLamsCommunityUsername(String lamsCommunityUsername) {
	this.lamsCommunityUsername = lamsCommunityUsername;
    }

    public Boolean getLoggedIntoLamsCommunity() {
	return loggedIntoLamsCommunity;
    }

    public void setLoggedIntoLamsCommunity(Boolean loggedIntoLamsCommunity) {
	this.loggedIntoLamsCommunity = loggedIntoLamsCommunity;
    }

    //	public CSSThemeVisualElement getTheme() {
    //		return theme;
    //	}
    //	
    //	public void setTheme(CSSThemeVisualElement theme) {
    //		this.theme = theme;
    //	}

}
