package com.Jialiang.messenger.backend.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Jialiang on 7/24/2016.
 */
public class GetMessages {
    private static final Logger log = Logger.getLogger(GetMessages.class.getName());
    User user;

    public GetMessages(String userName, String userPassword) throws EntityNotFoundException {
        user = new User();
        user.loadBasicUserInfo(userName, userPassword);
    }

    public String getUnreadJSONList()
    {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query messagesQuery = new Query("Messages").setAncestor(user.getUserEntity().getKey());
        Query.Filter seenFilter = new Query.FilterPredicate("seen", Query.FilterOperator.EQUAL, "no");
        messagesQuery.setFilter(seenFilter);
        messagesQuery.addSort("sender", Query.SortDirection.DESCENDING);
        List<Entity> messagesResults = datastore.prepare(messagesQuery).asList(FetchOptions.Builder.withDefaults());

        String currentName;
        int currentCount;
        JSONArray names = new JSONArray();
        JSONArray unreadCount = new JSONArray();
        JSONObject obj = new JSONObject();

        if(messagesResults.size() > 0)
        {
            currentName = (String)(messagesResults.get(0).getProperty("sender"));
            currentCount = 0;
        }
        else
        {
            log.info("No unread messages.");
            return "none";
        }


        for(int i = 0; i < messagesResults.size(); i++)
        {
            if((messagesResults.get(i).getProperty("sender")).equals(currentName))
            {
                currentCount++;
                if(i == messagesResults.size() - 1)
                {
                    names.add(currentName);
                    unreadCount.add(currentCount);
                }
            }
            else
            {
                names.add(currentName);
                unreadCount.add(currentCount);
                currentName = (String)(messagesResults.get(i).getProperty("sender"));
                currentCount = 0;
            }
        }

        obj.put("names", names);
        obj.put("counts", unreadCount);
        log.info("JSON for unread messages notification: " + obj.toString());
        return obj.toString();
    }
}

