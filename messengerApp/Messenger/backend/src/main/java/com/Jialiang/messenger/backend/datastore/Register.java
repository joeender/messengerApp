package com.Jialiang.messenger.backend.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Jialiang on 7/11/2016.
 */
public class Register {

    private static final Logger log = Logger.getLogger(Register.class.getName());
    private User user;
    public Register(String username, String password, String email)
    {
        user = new User(username, password, email);
    }

    public Boolean attemptToRegister() {
        if(checkUniqueUserName() && checkUniqueUserEmail())
        {
            addingToDataStore();
            return true;
        }
        else
        {
            return false;
        }
    }

    private Boolean checkUniqueUserName() {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter propertyFilter = new Query.FilterPredicate("username", Query.FilterOperator.EQUAL, user.getName());
        Query q = new Query("Member").setFilter(propertyFilter);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        log.info(Integer.toString(results.size()) + " name matches " + user.getName() + " found in datastore.");

        if(results.size() == 0) {
            return true;
        }
        else {
            log.info(user.getName() + " already exists in datastore.");
            return false;
        }
    }

    private Boolean checkUniqueUserEmail() {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.Filter propertyFilter = new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, user.getEmail());
        Query q = new Query("Member").setFilter(propertyFilter);

        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());

        log.info(Integer.toString(results.size()) + " email matches " + user.getEmail() + " found in datastore.");

        if(results.size() == 0) {
            return true;
        }
        else {
            log.info(user.getEmail() + " already exists in datastore.");
            return false;
        }
    }

    private void addingToDataStore()
    {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Entity member = new Entity("Member");
        member.setProperty("username", user.getName());
        member.setProperty("password", user.getPassword());
        member.setProperty("email", user.getEmail());
        log.info("User added with user name: " + user.getName() + " and email: " + user.getEmail());
        datastore.put(member);
    }
}
