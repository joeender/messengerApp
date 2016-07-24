package com.Jialiang.messenger.backend.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Jialiang on 7/21/2016.
 */
public class SendMessage {
    private static final Logger log = Logger.getLogger(SendMessage.class.getName());
    private User user;
    private Key receiverKey;
    private String message;

    public SendMessage(String name, String password, String key, String input) throws EntityNotFoundException {
        user = new User();
        user.loadBasicUserInfo(name, password);
        receiverKey = KeyFactory.stringToKey(key);
        message = input;
    }

    public void sendToDataStore()
    {
        Date date = new Date();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity messageEnt = new Entity("Messages",receiverKey);
        messageEnt.setProperty("sender", user.getName());
        messageEnt.setProperty("senderkey", KeyFactory.keyToString(user.getUserEntity().getKey()));
        messageEnt.setProperty("message", message);
        messageEnt.setProperty("seen", "no");
        messageEnt.setProperty("date", date);
        datastore.put(messageEnt);
        log.info(user.getName() + " to " + receiverKey.toString() + ": " + message + " " + date.toString());
    }
}
