/*
 * WebservicesModule.java
 */
package com.novice.hbdr.web;

import java.util.HashMap;
import java.util.Map;

import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Web endopints and servlet bindings
 * 
 * @author satishbabu
 */
public class WebservicesModule extends JerseyServletModule
{
    /* (non-Javadoc)
     * @see com.google.inject.servlet.ServletModule#configureServlets()
     */
    @Override
    protected void configureServlets()
    {
        Map<String, String> params = new HashMap<>();
        
        params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
        
        params.put("com.sun.jersey.spi.container.ContainerRequestFilters", "com.sun.jersey.api.container.filter.GZIPContentEncodingFilter");
        
        params.put("com.sun.jersey.spi.container.ContainerResponseFilters", "com.sun.jersey.api.container.filter.GZIPContentEncodingFilter");
        
        params.put("com.sun.jersey.config.property.packages", "com.fasterxml.jackson.jaxrs.json");
        
        bind(EndPoint.class);
        
        serve("/rest/*").with(GuiceContainer.class, params);
//        serve("/").with(AppServlet.class);
        
        System.out.println("Webservices module configure servlets called");
    }

}
