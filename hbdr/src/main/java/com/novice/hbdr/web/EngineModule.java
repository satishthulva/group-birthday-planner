/*
 * EngineModule.java
 */
package com.novice.hbdr.web;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.novice.hbdr.Configuration;
import com.novice.hbdr.service.GroupService;
import com.novice.hbdr.service.impl.ConfigurationImpl;
import com.novice.hbdr.service.impl.GroupServiceImpl;
import com.novice.hbdr.service.impl.NotificationService;

/**
 * Wirings to build the whole application
 * 
 * @author satishbabu
 */
public class EngineModule extends AbstractModule
{
    /* (non-Javadoc)
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure()
    {
        bind(Configuration.class).to(ConfigurationImpl.class).in(Scopes.SINGLETON);
        bind(GroupService.class).to(GroupServiceImpl.class).in(Scopes.SINGLETON);
        bind(NotificationService.class).in(Scopes.SINGLETON);
    }
}
