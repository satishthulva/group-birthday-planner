package com.novice.hbdr.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import com.google.inject.Inject;
import com.novice.hbdr.Configuration;
import com.novice.hbdr.datamodels.Group;
import com.novice.hbdr.datamodels.GroupID;
import com.novice.hbdr.datamodels.Person;
import com.novice.hbdr.service.SearchService;

/**
 * Implements {@link SearchService}
 * 
 * @author satish
 */
public class SearchServiceImpl implements SearchService {

	/**
	 * configuration to access elastic search
	 */
	private final Configuration configuration;

	@Inject
	public SearchServiceImpl(Configuration configuration) {
		this.configuration = configuration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.novice.hbdr.service.SearchService#findPeopleWithName(java.lang.
	 * String)
	 */
	@Override
	public List<Person> findPeopleWithName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.novice.hbdr.service.SearchService#addGroup(com.novice.hbdr.
	 * datamodels.Group)
	 */
	@Override
	public void addGroup(Group group) {
		try (RestHighLevelClient client = getClient()) {
			for(Person person : group.getMembers()) {
				Map<String, Object> personObj = toPersonDocument(person, group.getId());
				IndexRequest request = new IndexRequest("group_birthday_planner", "doc", "1");
				request.source(personObj);
				
				IndexResponse response = client.index(request);
				if(response.getResult() == DocWriteResponse.Result.CREATED) {
					System.out.println("Person " + person.getName() + " added to index");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return Elastic search rest client
	 */
	private RestHighLevelClient getClient() {
		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("localhost", 9200, "http")));
		return client;
	}

	/**
	 * Create elastic-search document for person
	 * 
	 * @param person
	 *            Person to be included
	 * @param groupID
	 *            Unique id of the group
	 * @return object map to send for elastic-search
	 */
	private Map<String, Object> toPersonDocument(Person person, GroupID groupID) {
		Map<String, Object> document = new HashMap<String, Object>();

		document.put("name", person.getName());
		document.put("pet_name", person.getPetName());
		document.put("birthday_month", person.getBirthday().getMonth().name());
		document.put("birthday_day", person.getBirthday().getDayOfMonth());
		document.put("email", person.getEmail());
		document.put("gender", person.getGender().name());

		document.put("groupid", groupID.getId());

		return document;
	}

}
