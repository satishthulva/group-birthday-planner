/*
 * EndPoint.java
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.google.inject.Inject;
import com.novice.hbdr.FriendGroupParser;
import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.service.GroupService;
import com.sun.jersey.multipart.FormDataParam;

/**
 * Entry point for all http calls
 * 
 * @author satishbabu
 */
@Path("/v1/hbdr")
public class EndPoint {
	@Context
	protected HttpServletRequest request;

	@Context
	protected HttpServletResponse response;

	private GroupService groupService;

	@Inject
	public EndPoint(GroupService groupService) {
		this.groupService = groupService;
	}

	@Path("/newGroup")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void registerNewGroup(@FormDataParam("name") String groupName,
			@FormDataParam("groupFile") InputStream groupFile) {
		try {
			FriendGroupParser parser = new FriendGroupParser();
			List<Person> people = parser.parsePersons(groupFile);

			groupService.registerGroup(groupName, people);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	@Path("/list")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Group> listGroups() {
		return groupService.findGroups();
	}
	
}
