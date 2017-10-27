/*
 * WebservicesModule.java
 *
 * Product: SCG
 * Strand Clinical Genomics
 *
 * Copyright 2007-2017, Strand Life Sciences
 * 5th Floor, Kirloskar Business Park, 
 * Bellary Road, Hebbal,
 * Bangalore 560024
 * India
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Strand Life Sciences., ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Strand Life Sciences.
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
