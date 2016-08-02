package com.Jialiang.messenger.backend.datastore;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import java.util.logging.Logger;

/**
 * Created by Jialiang on 7/17/2016.
 */
public class GetUserData {
    private static final Logger log = Logger.getLogger(GetUserData.class.getName());
    private User user;

    public GetUserData(String name, String password) throws EntityNotFoundException {
        user = new User();
        user.loadBasicUserInfo(name, password);
    }

    public String getFriendsListJsonString() throws EntityNotFoundException {
        user.loadFriendsList();
        String friendKeyString;
        JSONArray names = new JSONArray();
        JSONArray keyStrings = new JSONArray();
        JSONObject obj = new JSONObject();

        for(int i = 0; i < user.getFriendsList().size(); i++)
        {
            names.add(user.getFriendsList().get(i).getProperty("username"));
            friendKeyString = KeyFactory.keyToString(user.getFriendsList().get(i).getKey());
            keyStrings.add(friendKeyString);
        }

        obj.put("names", names);
        obj.put("keys", keyStrings);

        log.info("Friends list JSON: " + obj.toString());

        return obj.toJSONString();
    }

    public String getRequestListJsonString() throws EntityNotFoundException {
        user.loadRequestList();
        JSONArray list = new JSONArray();
        JSONObject obj = new JSONObject();

        for(int i = 0; i < user.getRequestList().size(); i++)
        {
            list.add(user.getRequestList().get(i).getProperty("username"));
        }

        obj.put("requests", list);
        log.info("Friend requests JSON: " + obj.toString());

        return obj.toJSONString();
    }
}
