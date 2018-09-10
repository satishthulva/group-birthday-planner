/*
 * EngineModule.java
 */
package com.novice.hbdr.web;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.novice.hbdr.Configuration;
import com.novice.hbdr.service.GroupService;
import com.novice.hbdr.service.SearchService;
import com.novice.hbdr.service.UserManagementService;
import com.novice.hbdr.service.impl.ConfigurationImpl;
import com.novice.hbdr.service.impl.GroupServiceImpl;
import com.novice.hbdr.service.impl.NotificationService;
import com.novice.hbdr.service.impl.SearchServiceImpl;
import com.novice.hbdr.service.impl.UserManagementServiceImpl;

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
        bind(UserManagementService.class).to(UserManagementServiceImpl.class).in(Scopes.SINGLETON);
        bind(GroupService.class).to(GroupServiceImpl.class).in(Scopes.SINGLETON);
        bind(SearchService.class).to(SearchServiceImpl.class).in(Scopes.SINGLETON);
        bind(NotificationService.class).in(Scopes.SINGLETON);
    }
}
