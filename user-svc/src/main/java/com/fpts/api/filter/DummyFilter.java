package com.fpts.api.filter;

import jakarta.servlet.*;

import java.io.IOException;

/**
 * This class is a dummy filter to be used in runtime time at the occasion where
 * the core library is not found. <br>
 * This Filter won't be performing any operation besides the forward of the
 * requests.
 *
 * @author andre
 */
public class DummyFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, response);

    }

    public void destroy() {
        // TODO Auto-generated method stub

    }

}