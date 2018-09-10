package com.novice.hbdr.web.filters;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthenticationFilter implements Filter {

	private Set<String> ignorePatterns = new HashSet<>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String ignorePattern = filterConfig.getInitParameter("ignore");
		if (ignorePattern != null)
			ignorePatterns.add(ignorePattern);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String requestPath = ((HttpServletRequest)request).getRequestURI();
		for(String ignorePattern : ignorePatterns)
		{
			if(requestPath.matches(ignorePattern)) {
				chain.doFilter(request, response);
				return;
			}
		}
		
		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session == null) {
			System.out.println("No session");
			((HttpServletResponse)response).sendRedirect("/index.html");
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
	}

}
