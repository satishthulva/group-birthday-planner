package com.novice.hbdr.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Injector;
import com.novice.hbdr.service.impl.NotificationService;

/**
 * Context listener to start a thread for Notification service
 * 
 * @author satish
 */
public class NotificationServiceContextListener implements ServletContextListener {

	private Thread notificationThread;
	
	private NotificationService notificationService;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		Injector injector = (Injector)context.getAttribute(Injector.class.getName());
		
		notificationService = injector.getInstance(NotificationService.class);
		notificationThread = new Thread(notificationService);
		
		notificationThread.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		notificationService.shutdown();
		notificationThread.interrupt();
	}

}
