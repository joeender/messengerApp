/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.Jialiang.messenger.backend;

import com.Jialiang.messenger.backend.datastore.FriendAccept;
import com.Jialiang.messenger.backend.datastore.FriendRequest;
import com.Jialiang.messenger.backend.datastore.GetMessages;
import com.Jialiang.messenger.backend.datastore.GetUserData;
import com.Jialiang.messenger.backend.datastore.Register;
import com.Jialiang.messenger.backend.datastore.SendMessage;
import com.Jialiang.messenger.backend.datastore.SignIn;
import com.Jialiang.messenger.backend.datastore.User;
import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.EntityNotFoundException;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.messenger.Jialiang.com",
    ownerName = "backend.messenger.Jialiang.com",
    packagePath=""
  )
)
public class MyEndpoint {

    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }

    @ApiMethod(name = "testing")
    public MyBean testing(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Testing, " + name);

        return response;
    }

    @ApiMethod(name = "register")
    public MyBean register(@Named("username") String username, @Named("password")String password, @Named("email") String email) {
        MyBean response = new MyBean();
        Register register = new Register(username, password, email);

        if( register.attemptToRegister()) {
            response.setData("true");
        }
        else {
            response.setData("false");
        }

        return response;
    }

    @ApiMethod(name = "signin")
    public MyBean signIn(@Named("username") String username, @Named("password")String password) throws EntityNotFoundException {
        MyBean response = new MyBean();
        SignIn signIn = new SignIn(username, password);

        if( signIn.attemptToSignIn()) {
            response.setData("true");
        }
        else {
            response.setData("false");
        }

        return response;
    }

    @ApiMethod(name = "checkmessages")
    public MyBean checkMessages(@Named("username") String input) throws ServiceException
    {
        MyBean response = new MyBean();
        response.setData(input);
        return response;
    }

    @ApiMethod(name = "sendfriendrequest")
    public MyBean sendFriendRequest(@Named("username") String username, @Named("password")String password, @Named("friendinfo") String friendinfo) throws ServiceException, EntityNotFoundException {
        MyBean response = new MyBean();
        User user = new User();
        user.loadBasicUserInfo(username, password);
        FriendRequest request = new FriendRequest(user,friendinfo);

        if(request.sendRequest()) {
            response.setData("true");
        }
        else {
            response.setData("false");
        }
        return response;
    }

    @ApiMethod(name = "getrequestlist")
    public MyBean getRequestList(@Named("username") String username, @Named("password")String password) throws ServiceException, EntityNotFoundException {
        MyBean response = new MyBean();
        GetUserData data = new GetUserData(username, password);
        response.setData(data.getRequestListJsonString());
        return response;
    }

    @ApiMethod(name = "getfriendlist", path="getfriendlist")
    public MyBean getFriendList(@Named("username") String username, @Named("password")String password) throws ServiceException, EntityNotFoundException {
        MyBean response = new MyBean();
        GetUserData data = new GetUserData(username, password);
        response.setData(data.getFriendsListJsonString());
        return response;
    }

    @ApiMethod(name = "friendaccept")
    public MyBean friendAccept(@Named("username") String username, @Named("password")String password,@Named("requester")String requester, @Named("answer")String answer) throws ServiceException, EntityNotFoundException {
        MyBean response = new MyBean();
        FriendAccept friendAccept = new FriendAccept(username, password, requester);
        if(friendAccept.resolveRequest(answer)) {
            response.setData("true");
        }
        else {
            response.setData("false");
        }
        return response;
    }

    @ApiMethod(name = "sendmessage")
    public MyBean sendMessage(@Named("username") String username, @Named("password")String password,@Named("receiverkey")String receiverkey, @Named("message")String message) throws ServiceException, EntityNotFoundException {
        MyBean response = new MyBean();
        SendMessage sendMessage = new SendMessage(username, password, receiverkey, message);
        sendMessage.sendToDataStore();
        response.setData("done");
        return response;
    }

    @ApiMethod(name = "unreadcount")
    public MyBean unreadCount(@Named("username") String username, @Named("password")String password) throws ServiceException, EntityNotFoundException {
        MyBean response = new MyBean();
        GetMessages counts = new GetMessages(username, password);
        response.setData(counts.getUnreadJSONList());
        return response;
    }

    @ApiMethod(name = "getmessages")
    public MyBean getMessages(@Named("username") String username, @Named("password")String password, @Named("friend") String friend) throws ServiceException, EntityNotFoundException {
        MyBean response = new MyBean();
        GetMessages messages = new GetMessages(username, password);
        response.setData(messages.getMessagesFromAFriendJSON(friend));
        return response;
    }
}
