/*
 * EndPoint.java
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

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.novice.hbdr.FriendGroupParser;
import com.novice.hbdr.Person;
import com.sun.jersey.multipart.FormDataParam;

/**
 * Entry point for all http calls
 * 
 * @author satishbabu
 */
@Path("/v1/hbdr")
public class EndPoint
{
    @Context
    protected HttpServletRequest request;
    
    @Context
    protected HttpServletResponse response;
    
    @Inject
    public EndPoint()
    {
        
    }
    
    @Path("/hello")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String sayHello()
    {
        return "Hello";
    }

    @Path("/repeater")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String repeater(@QueryParam("name") String name)
    {
        return "Hello " + name;
    }
    
    @Path("/newGroup")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void registerNewGroup(/*FormDataMultiPart multipart, */@FormDataParam("groupFile") InputStream groupFile)
    {
        try
        {
            FriendGroupParser parser = new FriendGroupParser();
            List<Person> people = parser.parsePersons(groupFile);
            
            if(people != null)
            {
                for(Person person : people)
                {
                    System.out.println(person.getName());
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
