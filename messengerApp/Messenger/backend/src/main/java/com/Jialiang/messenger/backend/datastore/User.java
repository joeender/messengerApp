package com.Jialiang.messenger.backend.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javafx.util.Pair;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

/**
 * Created by Jialiang on 7/10/2016.
 */
public class User {
    private static final Logger log = Logger.getLogger(User.class.getName());

    private String name;
    private String password;
    private String email;
    private List<Entity> friendList;
    private List<Entity> requestList;
    private Entity userEntity;

    public User()
    {
        name = "noone";
        password = "***";
        email = "noone";
        friendList =  new ArrayList<Entity>(0);
        requestList =  new ArrayList<Entity>(0);
        userEntity = new Entity("Member");
    }

    public User(String userName, String userPassword, String userEmail)
    {
        name = userName.toLowerCase();
        password = userPassword;
        email = userEmail.toLowerCase();
        friendList =  new ArrayList<Entity>(0);
        requestList =  new ArrayList<Entity>(0);
        userEntity = new Entity("Member");

        log.info("Creating a user with name: " + name + ", password: " + password +", email: " + email);
    }

    /// Used to load some info and friends list from data store with kind Member entity key.
    public Boolean loadBasicUserInfo(String nameInput, String passwordInput) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter nameFilter = new Query.FilterPredicate("username", Query.FilterOperator.EQUAL, nameInput);
        Query.Filter passwordFilter = new Query.FilterPredicate("password", Query.FilterOperator.EQUAL, passwordInput);
        Query.CompositeFilter namePasswordFilter = Query.CompositeFilterOperator.and(nameFilter, passwordFilter);

        Query q = new Query("Member").setFilter(namePasswordFilter);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        /// Name not found or incorrect password.
        if(results.size() == 0) {
            log.info("Name not found or incorrect password.");
            return false;
        }
        /// Name and password found.
        else {

            userEntity = results.get(0);
            name =(String)(userEntity.getProperty("username"));
            email = (String)(userEntity.getProperty("email"));
            return true;
        }
    }

    /// Get friends list
    public void loadFriendsList() throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query friendsListQuery = new Query("Friends").setAncestor(userEntity.getKey());
        List<Entity> friendsListResults = datastore.prepare(friendsListQuery).asList(FetchOptions.Builder.withDefaults());
        log.info(friendsListResults.size() + " friends found.");
        for(int i = 0; i < friendsListResults.size(); i++)
        {
            friendList.add(datastore.get((Key)(friendsListResults.get(i).getProperty("friendkey"))));
            log.info("Friend named: " + friendList.get(i).getProperty("username") + " found for user " + userEntity.getProperty("username"));
        }
    }

    public void loadRequestList() throws EntityNotFoundException {
        /// Get friends request list
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query requestListQuery = new Query("FriendRequest").setAncestor(userEntity.getKey());
        Query.Filter propertyFilter = new Query.FilterPredicate("acceptance", Query.FilterOperator.EQUAL, "pending");
        requestListQuery.setFilter(propertyFilter);

        List<Entity> requestListResults = datastore.prepare(requestListQuery).asList(FetchOptions.Builder.withDefaults());
        log.info(requestListResults.size() + " pending friend requests found.");
        for(int i = 0; i < requestListResults.size(); i++)
        {
            requestList.add(datastore.get((Key)(requestListResults.get(i).getProperty("requesterkey"))));
            log.info("Pending friend request from named: " + requestList.get(i).getProperty("username") + " found for user " + userEntity.getProperty("username"));
        }
    }



    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }

    public List<Entity> getFriendsList(){return friendList; };

    public List<Entity> getRequestList(){return requestList; };

    public Entity getUserEntity() {return userEntity;}
}
