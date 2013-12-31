/**
 * Copyright (C) 2010 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 */
package com.schdri.bpm;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This filter put the credentials of the remote user in session in order to be automatically logged in.
 * This filter is intended to be used with SSO solutions like CAS
 * @author Anthony Birembaut
 *
 */
public class SetCredentialsInSessionFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(SetCredentialsInSessionFilter.class.getName());

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            final HttpServletRequest httpRequest = (HttpServletRequest)request;
            final String username = httpRequest.getRemoteUser();
            if (username != null && username.length() > 0) {
                final HttpSession session = httpRequest.getSession();
                session.setAttribute("username",username);
                BPMModule.setUserID(username);
            } else {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "The HttpServletRequest remoteUser should be initialized in order for the SetCredentialsInSessionFilter to work");
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error while setting the credentials in session");
            throw new ServletException(e);
        }
        filterChain.doFilter(request, response);
    }

    public void init(final FilterConfig filterConfig) throws ServletException {

    }
    
    public void destroy() {

    }
}
