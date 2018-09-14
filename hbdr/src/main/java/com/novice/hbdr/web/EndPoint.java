/*
 * EndPoint.java
 */
package com.novice.hbdr.web;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.inject.Inject;
import com.novice.hbdr.FriendGroupParser;
import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.GroupDetails;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.datamodels.UserID;
import com.novice.hbdr.service.GroupService;
import com.novice.hbdr.service.UserManagementService;
import com.novice.hbdr.service.impl.NotificationService;
import com.novice.hbdr.web.datamodels.PersonDTO;
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

	private final GroupService groupService;

	private final NotificationService notificationService;

	private final UserManagementService userService;
	
	@Inject
	public EndPoint(GroupService groupService, NotificationService notificationService, UserManagementService userService) {
		this.groupService = groupService;
		this.notificationService = notificationService;
		this.userService = userService;
	}

	@Path("/newGroup")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void registerNewGroup(@FormDataParam("name") String groupName,
			@FormDataParam("groupFile") InputStream groupFile) {
		try {
			FriendGroupParser parser = new FriendGroupParser();
			List<Person> people = parser.parsePersons(groupFile);
			List<UserID> members = new ArrayList<>();
			for(Person person : people)
			    members.add(new UserID(person.getEmail()));
			
			groupService.registerGroup(groupName, members, 4);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	@Path("/list")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Group> listGroups() {
	    UserID userID = getCurrentUser();
	    
	    List<Group> groups = new ArrayList<>();
	    for(GroupDetails groupDetails : groupService.findGroups(userID))
	    {
	        Group group = groupService.findGroup(groupDetails.getId());
	        groups.add(group);
	    }
	    
		return groups;
	}

	@Path("/upcomingBirthDays")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<PersonDTO> findPeopleWithUpcomingBirthday() {
	    UserID userID = getCurrentUser();
	    
	    Set<GroupID> userGroups = new HashSet<>();
	    for(GroupDetails groupDetails : groupService.findGroups(userID))
	        userGroups.add(groupDetails.getId());
	    
		Map<GroupDetails, List<Person>> personsMap = notificationService.findPeopleWithUpcomingBirthday();

		List<PersonDTO> persons = new ArrayList<>();

		for (Entry<GroupDetails, List<Person>> groupEntry : personsMap.entrySet()) {
			GroupDetails groupDetails = groupEntry.getKey();
			List<Person> people = groupEntry.getValue();

			if(!userGroups.contains(groupDetails.getId()))
			    continue;
			
			String groupName = groupDetails.getName();
			GroupID groupID = groupDetails.getId();

			for (Person person : people) {
				persons.add(new PersonDTO(groupID, groupName, person));
			}
		}

		return persons;
	}

	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void login(@FormParam("idToken") String idTokenString) {
		String CLIENT_ID = "832947442488-rve5ebggtkchts5d6ecr0etvc5vbda3b.apps.googleusercontent.com";

		JsonFactory jsonFactory = new JacksonFactory();
		HttpTransport httpTransport = new ApacheHttpTransport();
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
				// Specify the CLIENT_ID of the app that accesses the backend:
				.setAudience(Collections.singletonList(CLIENT_ID)).build();

		try {
			// (Receive idTokenString by HTTPS POST)
			GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken != null) {
				Payload payload = idToken.getPayload();

				// Print user identifier
				String userId = payload.getSubject();
				System.out.println("User ID: " + userId);

				// Get profile information from payload
				String email = payload.getEmail();
//				boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
				String name = (String) payload.get("name");
//				String pictureUrl = (String) payload.get("picture");
//				String locale = (String) payload.get("locale");
//				String familyName = (String) payload.get("family_name");
				String givenName = (String) payload.get("given_name");
				
				Person person = new Person(name, givenName, null, email, null);
				if(userService.findPerson(email) == null)
					userService.registerUser(person);
				else
					System.out.println("User exists already");

				HttpSession session = request.getSession(false);
				if(session != null) {
					session.invalidate();
				}
				
				session = request.getSession(true);
				Cookie cookie = new Cookie(USER_KEY, email);
				response.addCookie(cookie);
				
				session.setAttribute(USER_KEY, new UserID(email));
				
			} else {
				System.out.println("Invalid ID token.");
			}
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find the user for whom this session is created
	 * @return the user for whom this session is created
	 */
    private UserID getCurrentUser()
	{
	    return (UserID)request.getSession().getAttribute("userID");
	}
	
	/**
	 * Session attribute name to keep the loggedIn user info
	 */
	private final String USER_KEY = "userID";
	
}
