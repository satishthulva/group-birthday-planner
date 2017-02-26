/*
 * Mailer.java
 *
 * Product: SCG
 * Strand Clinical Genomics
 *
 * Copyright 2007-2013, Strand Life Sciences
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
package com.novice.hbdr;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 * The proxy class for javax.mail.Session
 * @author satishbabu
 */
public class Mailer
{
    private static final String REPLY_TO_KEY = "mail.reply.to";
    /**
     * javax.mail.Session configuration
     */
    private Properties mailConfiguration;
    /**
     * the actual java mail session
     */
    private Session mailSession;

    /**
     * Creates the mailer with the specified Properties
     * @param config the javax.mail.Session properties
     */
    public Mailer(Properties config)
    {
        this.mailConfiguration = config;
    }
    
    /**
     * @return The configuration for the email account we use to send reminder emails
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private static Properties getMailProperties(InputStream input)
    {
        Properties props = new Properties();
        try
        {
            props.load(input);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error while trying to read mail properties");
        }
        return props;
    }
    
    /**
     * Returns the Java Mail Session
     * @return the javax.mail.session
     */
    public Session getSession()
    {
        if(mailSession == null)
        {
            synchronized(this)
            {
                if(mailSession == null)
                {
                    mailSession = createSession();
                }
            }
        }
        
        return mailSession;
    }
        
    private Session createSession()
    {
        // Create a new Session instance inside a doPrivileged block, so that JavaMail
        // can read its default properties without throwing Security
        // exceptions.
        return AccessController.doPrivileged( new PrivilegedAction<Session>() {
            @Override
            public Session run()
            {
                String password = mailConfiguration.getProperty("password");
                Authenticator auth = null;
                
                if (password != null) 
                {
                    String user = mailConfiguration.getProperty("mail.smtp.user");
                    if(user == null) 
                    {
                        user = mailConfiguration.getProperty("mail.user");
                    }
                               
                    if(user != null) 
                    {
                        final PasswordAuthentication pa = new PasswordAuthentication(user, Crypto.decode(password));
                        auth = new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return pa;
                            }
                        };
                     }
                }
         
                // Create and return the new Session object
                Session session = Session.getInstance(mailConfiguration, auth);
                return (session);
            }
        });
    }
    
    /**
     * Get reply to address to use. <code>null</code> if not present
     * @return Get reply to address to use. <code>null</code> if not present
     */
    public String getReplyToAddress()
    {
        return mailConfiguration.getProperty(REPLY_TO_KEY);
    }
    
    /**
     * @return Properties to create session and store etc
     */
    public Properties getProperties()
    {
        return mailConfiguration;
    }

    public static Mailer createMailer()
    {
        return new Mailer(getMailProperties(Mailer.class.getResourceAsStream("/mail.config")));
    }
        
}
