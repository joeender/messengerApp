package com.Jialiang.messenger.backend.datastore;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

import java.util.List;
import java.util.logging.Logger;

import jdk.nashorn.internal.codegen.CompilerConstants;

/**
 * Created by Jialiang on 7/13/2016.
 */
public class SignIn {
    private static final Logger log = Logger.getLogger(SignIn.class.getName());

    private String name;
    private String password;
    private User user;

    public SignIn(String inputName, String inputPassword)
    {
        name = inputName.toLowerCase();
        password = inputPassword;
        user = new User();
    }

    public Boolean attemptToSignIn() throws EntityNotFoundException {
        log.info("Attempting to sign in with name: " + name + " and password: " + password);

        if(user.loadBasicUserInfo(name, password))
        {
            log.info("Name and password pair found in datastore.");
            return true;
        }
        else
        {
            log.info("Name not found or password mismatch in datastore.");
            return false;
        }
    }
}
