package com.Jialiang.messenger.backend.datastore;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Jialiang on 7/16/2016.
 */
public class FriendRequest {
    private static final Logger log = Logger.getLogger(FriendRequest.class.getName());

    private User sender;
    /// Could be user name or user email.
    private String sendeeInfo;

    private Entity foundSendee;

    public FriendRequest(User user, String sendee)
    {
        sender = user;
        sendeeInfo = sendee.toLowerCase();
        foundSendee = new Entity("Member");
    }

    public Boolean sendRequest() throws EntityNotFoundException {
        if(checkIfFriendsAlready()) {return false;}
        else if(!checkIfuserExists())
        {
            log.info("User not found with username or email: " + sendeeInfo);
            return false;
        }
        else if(!checkIfRequestAlreadyExists())
        {
            log.info("This friend request is new.");
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            Entity friendRequest = new Entity("FriendRequest", foundSendee.getKey());
            friendRequest.setProperty("requesterkey",sender.getUserEntity().getKey());
            friendRequest.setProperty("acceptance", "pending");
            datastore.put(friendRequest);
        }
        return true;
    }

    // See if the user is already friends with sendee by username or by email.
    private Boolean checkIfFriendsAlready() throws EntityNotFoundException {
        sender.loadFriendsList();
        for(int i = 0; i < sender.getFriendsList().size(); i++) {
            if(sendeeInfo.contentEquals((String)(sender.getFriendsList().get(i).getProperty("username")))) {
                log.info("There is already a friend with that user name.");
                return true;}
            else if(sendeeInfo.contentEquals((String)(sender.getFriendsList().get(i).getProperty("email")))) {
                log.info("There is already a friend with that email.");
                return true;}
        }
        return false;
    }

    private Boolean checkIfuserExists()
    {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter nameFilter = new Query.FilterPredicate("username", Query.FilterOperator.EQUAL, sendeeInfo);
        Query.Filter emailFilter = new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, sendeeInfo);
        Query lookForName = new Query("Member").setFilter(nameFilter);
        Query lookForEmail = new Query("Member").setFilter(emailFilter);
        List<Entity> nameResult = datastore.prepare(lookForName).asList(FetchOptions.Builder.withDefaults());
        List<Entity> emailResult = datastore.prepare(lookForEmail).asList(FetchOptions.Builder.withDefaults());

        if(!(nameResult.size() == 0))
        {
            log.info("User found with username: " + sendeeInfo);
            foundSendee = nameResult.get(0);
            return true;
        }
        else if(!(emailResult.size() == 0))
        {
            log.info("User found with email: " + sendeeInfo);
            foundSendee = emailResult.get(0);
            return true;
        }
        return false;
    }

    private Boolean checkIfRequestAlreadyExists()
    {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter requestFilter = new Query.FilterPredicate("requesterkey", Query.FilterOperator.EQUAL, sender.getUserEntity().getKey());
        Query lookForRequest = new Query("FriendRequest").setAncestor(foundSendee.getKey());
        lookForRequest.setFilter(requestFilter);
        List<Entity> requestResult = datastore.prepare(lookForRequest).asList(FetchOptions.Builder.withDefaults());

        if(requestResult.size() == 0) {
            return false;
        }
        else {
            log.info("Request already exists from: " + requestResult.get(0).getProperty("requesterkey") + ", changing request to pending.");
            requestResult.get(0).setProperty("acceptance", "pending");
            datastore.put(requestResult.get(0));
            return true;
        }

    }

}
