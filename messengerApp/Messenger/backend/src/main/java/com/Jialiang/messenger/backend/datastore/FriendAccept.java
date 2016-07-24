package com.Jialiang.messenger.backend.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import java.util.logging.Logger;
import java.util.List;

/**
 * Created by Jialiang on 7/18/2016.
 */
public class FriendAccept {
    private static final Logger log = Logger.getLogger(FriendAccept.class.getName());
    private User user;
    private String requestName;

    public FriendAccept(String userInput, String userPassword, String request) throws EntityNotFoundException {
        user = new User();
        user.loadBasicUserInfo(userInput, userPassword);
        requestName = request.toLowerCase();
    }

    public Boolean resolveRequest(String answer)
    {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        // Find friend in Member kind
        Query.Filter friendNameFilter = new Query.FilterPredicate("username", Query.FilterOperator.EQUAL, requestName);
        Query memberQuery = new Query("Member").setFilter(friendNameFilter);
        List<Entity> memberResults = datastore.prepare(memberQuery).asList(FetchOptions.Builder.withDefaults());
        Entity friendEntity = memberResults.get(0);

        // Find requester in FriendRequest kind.
        Query.Filter requesterKeyFilter = new Query.FilterPredicate("requesterkey", Query.FilterOperator.EQUAL, friendEntity.getKey());
        Query friendRequestQuery = new Query("FriendRequest").setFilter(requesterKeyFilter);
        List<Entity> requesterResults = datastore.prepare(friendRequestQuery).asList(FetchOptions.Builder.withDefaults());
        Entity requestEntity = requesterResults.get(0);

        if(answer.contains("yes"))
        {
            /// Set user as friends with requester
            Entity friend = new Entity("Friends", user.getUserEntity().getKey());
            friend.setProperty("friendkey",friendEntity.getKey());
            datastore.put(friend);

            /// Set requester as friends with user
            friend = new Entity("Friends", friendEntity.getKey());
            friend.setProperty("friendkey", user.getUserEntity().getKey());
            datastore.put(friend);

            /// If user has sent the requester a friend request already, set it as yes to remove it from the friend's feed.
            Query.Filter requestFilter = new Query.FilterPredicate("requesterkey", Query.FilterOperator.EQUAL,  user.getUserEntity().getKey());
            Query lookForRequest = new Query("FriendRequest").setAncestor(friendEntity.getKey());
            lookForRequest.setFilter(requestFilter);
            List<Entity> requestResult = datastore.prepare(lookForRequest).asList(FetchOptions.Builder.withDefaults());

            if(!(requestResult.size() == 0)) {
                requestResult.get(0).setProperty("acceptance", "yes");
                datastore.put(requestResult.get(0));
            }

            /// Set request to from pending to yes
            requestEntity.setProperty("acceptance", "yes");
            datastore.put(requestEntity);
            log.info(user.getName() + " accepted " + requestName + " as a friend.");
            return true;
        }
        else
        {
            /// Rejecting friend request
            /// Set request from pending to no
            requestEntity.setProperty("acceptance", "no");
            datastore.put(requestEntity);
            log.info(user.getName() + " did not accept " + requestName + " as a friend.");
            return false;
        }
    }
}
