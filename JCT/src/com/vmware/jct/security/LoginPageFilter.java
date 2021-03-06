/**
 * 
 */
package com.vmware.jct.security;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * <p><b>Class name:</b> LoginPageFilter.java</p>
 * <p><b>Author:</b> InterraIT</p>
 * <p><b>Purpose:</b> This class acts as a filter class...
 * ActionPlanController extends BasicController  and has following  methods.
 * -doFilter()
 * <p><b>Description:</b> This class acts as a filter class... </p>
 * <p><b>Copyrights:</b> 	All rights reserved by Interra IT and should only be used for its internal application development.</p>
 * <p><b>Created Date:</b> 20/Feb/2014</p>
 * <p><b>Revision History:</b>
 * 	<li>6/May/2014: Changed the saving of action plan to dynamic.</li>
 * </p>
 */
public class LoginPageFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginPageFilter.class);
	
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info(">>>> LoginPageFilter.init");
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
	    LOGGER.info(">>>> LoginPageFilter.doFilter");
	    HttpServletRequest request = (HttpServletRequest) servletRequest;
	    HttpServletResponse response = (HttpServletResponse) servletResponse;
	    request.getRequestURL();
	    HttpSession httpSession = request.getSession();
	    Boolean isLoggedIn = (Boolean)httpSession.getAttribute("isLoggedIn");
	       
	    if(isLoggedIn != null && isLoggedIn==true) { //If user is already authenticated
	    	LOGGER.debug("Inside Filter if");
	    	filterChain.doFilter(servletRequest, servletResponse);
	    } else if("/user/".equalsIgnoreCase(request.getRequestURI()) || "/user/view/accountRenew.jsp".equalsIgnoreCase(request.getRequestURI()) 
	    			|| "/user/signup-page.jsp".equalsIgnoreCase(request.getRequestURI()) 
	    		   || "/user/view/activateAccount.jsp".equalsIgnoreCase(request.getRequestURI())) {
	    	   LOGGER.debug("sign up page called");
	    	   filterChain.doFilter(servletRequest, servletResponse);
	    } else {
	    	    LOGGER.debug("Inside Filter else");
	    	    response.sendRedirect("/user/?cde=804");// or, forward using RequestDispatcher	          
	      }
	      LOGGER.info("<<<< LoginPageFilter.doFilter");
	 }

	   public void destroy() {
		   LOGGER.info(">>>> LoginPageFilter.destroy");
	   }
	}

