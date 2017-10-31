/*
 * AppConfigurationManager.java
 */
package com.novice.hbdr.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * To start guice wiring
 * 
 * @author satishbabu
 */
public class AppConfigurationManager extends GuiceServletContextListener
{
    /* (non-Javadoc)
     * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
     */
    @Override
    protected Injector getInjector()
    {
        System.out.println("App configuration manager called");
        
        return Guice.createInjector(new EngineModule(), 
                new WebservicesModule());
    }

}
