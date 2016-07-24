loggedIn = false;
username = "";
password = "";
requestArray = [];
friendNameKeyArray = [];
currentFriend = {};
messagePanelDivs = [];
initialize = true;

function enableRegisterClick()
{
    document.getElementById('registerButton').onclick = function() {
        document.location = "register.html";
    }
}

function enableSignInClick() {
    document.getElementById('loginButton').onclick = function() {
        this.disabled = true;
        window.username = document.getElementById('nameLogin').value;
        window.password = document.getElementById('passwordLogin').value;
        gapi.client.myApi.signin({'username': window.username, 'password': window.password}).execute(
            function(response)
            {
                if(((response.result.data).localeCompare("true")) == 0)
                {
                    window.loggedIn = true;
                    document.getElementById('signIn').style.display  = "none";
                    document.getElementById('messagesMain').style.display = 'block';
                    enableFriendRequestClick();
                    refreshLists();
                    enableSendMessage();
                    enableCheckMessagesPing();
                }
                else
                {
                    document.getElementById('loginButton').disabled = false;
                    alert("Not signedIN");
                }
            }
        );
        return false;
    }
}
//
function enableFriendRequestClick() {
    document.getElementById('sendRequest').onclick = function() {
        if(window.loggedIn)
        {
            var friendInfo = document.getElementById('friendInfo').value;

            gapi.client.myApi.sendfriendrequest({'username': window.username, 'password': window.password, 'friendinfo': friendInfo}).execute(
                function(response)
                {
                    if(((response.result.data).localeCompare("true")) == 0)
                    {
                        alert("Request Sent");
                    }
                    else
                    {
                        alert("Request Not Sent");
                    }
                }
            );
            return false;
        }
    }
}

function getFriendRequests() {
    if(window.loggedIn)
    {
        gapi.client.myApi.getrequestlist({'username': window.username, 'password': window.password}).execute(
            function(response)
            {
                var obj = JSON.parse(response.result.data);
                var requestListLength = Object.keys(obj.requests).length;

                console.log("Friend Requests: " + requestListLength);

                for(var i = 0; i < requestListLength; i++)
                {
                    var request = {};
                    request.name = obj.requests[i];
                    console.log("Checking for new requests:");
                    if(nameNotInArray(window.requestArray, request.name ))
                    {
                        window.requestArray.push(obj.requests[i]);
                        makeRequestMenu(request.name);
                        console.log("Name: " + window.requestArray[i].name);
                    }
                }
            }
        );
    }
    return false;
}

 function getFriendList() {
    if(window.loggedIn)
    {
        gapi.client.myApi.getfriendlist({'username': window.username, 'password': window.password}).execute(
            function(response)
            {
                var obj = JSON.parse(response.result.data);
                var friendListLength = Object.keys(obj.names).length;
                console.log("Friends List:" + friendListLength);
                for(var i = 0; i < friendListLength; i++)
                {
                    var friend = {};
                    friend.name = obj.names[i];
                    friend.key = obj.keys[i];

                    if(nameNotInArray(window.friendNameKeyArray, friend.name))
                    {
                        window.friendNameKeyArray.push(friend);
                        makeFriendsMenu(friend);
                        console.log("Name: "+ friend.name);
                        console.log("Key: " + friend.name);
                    }
                }

                ////
                getUnreadCount();


                if(window.initialize)
                {
                    window.currentFriend = window.friendNameKeyArray[0];
                    console.log("Current friend selected: " + window.currentFriend.name);
                    window.initialize = false;
                    showMessagePanel();
                }
            }
        );
    }
    return false;
 }

function getUnreadCount() {
    if(window.loggedIn){
     gapi.client.myApi.unreadcount({'username': window.username, 'password': window.password}).execute(
         function(response)
         {
             console.log("Unread Msgs:");
             if(((response.result.data).localeCompare("none")) !== 0)
             {
                  var obj = JSON.parse(response.result.data);
                  var friendListLength = Object.keys(obj.names).length;
                  for(var i = 0; i < friendListLength; i++)
                  {
                      console.log("Name:" + obj.names[i] + " Count: " + obj.counts[i]);
                      addCountToName(obj.names[i], obj.counts[i]);
                  }
             }
         }
     );
    }
    return false;
}



 function refreshLists()
 {
    getFriendRequests();
    getFriendList();
 }

 function acceptFriend(requester, answer) {
    if(window.loggedIn)
    {
        gapi.client.myApi.friendaccept({'username': window.username, 'password': window.password, 'requester': requester, 'answer':answer}).execute(
            function(response)
            {
                  if(((response.result.data).localeCompare("true")) == 0)
                  {
                      refreshLists();
                  }
                  else
                  {
                      alert("Friend Not Added");
                  }
            }
        );
    }
    return false;
 }

 function enableSendMessage() {
     document.getElementById('sendMessage').onclick = function() {
         this.disabled = true;
         var message = document.getElementById('sendText').value;
         document.getElementById('sendText').value = "";
         gapi.client.myApi.sendmessage({'username': window.username, 'password': window.password, 'receiverkey':window.currentFriend.key, 'message':message}).execute(
             function(response)
             {
                $(".currentPanel").append("<p>" + message + "</p>");
                document.getElementById('sendMessage').disabled = false;
             }
         );
         return false;
     }
 }


function enableCheckMessagesPing()
{
    if(window.loggedIn)
    {
        setInterval(refreshLists, 15000);
    }
}


// This is called initially
function init() {
  var apiName = 'myApi';
  var apiVersion = 'v1';
  var apiRoot = 'https://' + window.location.host + '/_ah/api';
  if (window.location.hostname == 'localhost'
      || window.location.hostname == '127.0.0.1'
      || ((window.location.port != "") && (window.location.port > 1023))) {
        // We're probably running against the DevAppServer
        apiRoot = 'http://' + window.location.host + '/_ah/api';
  }

  $(document).ready(function() {
    $('[data-toggle=offcanvas]').click(function() {
      $('.row-offcanvas').toggleClass('active');
    });
  });

  var callback = function() {
    enableSignInClick();
    enableRegisterClick();
  }
  gapi.client.load(apiName, apiVersion, callback, apiRoot);
}

/// Interface Functions
function makeRequestMenu(requestFrom)
{
    var parent = document.getElementById('friendRequestSideBar');
    var li = document.createElement('li');
    var div = document.createElement('div');
    parent.appendChild(li);
    li.appendChild(div);
    div.innerHTML = requestFrom + " wants to be friends";

    var btnYes = document.createElement("BUTTON");
    btnYes.innerHTML = "YES";
    var btnNo = document.createElement("BUTTON");
    btnNo.innerHTML = "NO";

    div.appendChild(btnYes);
    div.appendChild(btnNo);

    btnYes.addEventListener ("click", function() {
        parent.removeChild(li);
        acceptFriend(requestFrom, "yes");
    });
    btnNo.addEventListener ("click", function() {
        parent.removeChild(li);
        acceptFriend(requestFrom, "no");
    });
}

function makeFriendsMenu(friendsFrom)
{
    var name = friendsFrom.name;
    var key = friendsFrom.key;
    var parent = document.getElementById('friendRequestSideBar');
    var li = document.createElement('li');
    var div = document.createElement('div');
    parent.appendChild(li);
    li.appendChild(div);
    div.innerHTML = name;
    div.setAttribute('id', name + "Select");
    console.log("Friend Select id:" + div.id);

    div.style.height = "50px";
    div.style.width = "100%";
    div.className = "friendSelect";

    div.addEventListener("click", function() {
         console.log(friendsFrom.name + " selected!")
         window.currentFriend = friendsFrom;
         div.innerHTML = name;
         showMessagePanel();
    });
}

function showMessagePanel()
{
    removeAndSaveCurrentPanel();
    ///Create the Panel if there is none
    if(nameNotInArray(window.messagePanelDivs, window.currentFriend.name))
    {
        console.log("Creating message panel for:" + window.currentFriend.name);
        var messageDiv = document.createElement('div');
        messageDiv.style.height = "100%";
        messageDiv.style.width = "100%";
        messageDiv.setAttribute('id', window.currentFriend.name);
        messageDiv.className = "currentPanel";
        var $messageDiv = $(messageDiv);
        var obj = {};
        obj.name = window.currentFriend.name;
        obj.key = window.currentFriend.key;
        obj.panel = $messageDiv;
        window.messagePanelDivs.push(obj);
        $("#messagePanel").append($messageDiv);
    }
    else
    {
        for(var i = 0; i < window.messagePanelDivs.length; i++)
        {
            if((window.messagePanelDivs[i].name).localeCompare(window.currentFriend.name) == 0)
            {
                console.log("Retrieving message panel for:" + window.messagePanelDivs[i].name);
                $("#messagePanel").append(window.messagePanelDivs[i].panel);
                return;
            }
        }
    }
}

function removeAndSaveCurrentPanel()
{
    var currentPanel = $(".currentPanel");
    var name = $(".currentPanel").attr('id');

    ///Saving the panel
    for(var i = 0; i < window.messagePanelDivs.length; i++)
    {
        if((window.messagePanelDivs[i].name).localeCompare(name) == 0)
        {
            console.log("Removing and saving panel for: " + name);
            window.messagePanelDivs[i].panel = currentPanel;
        }
    }
    //Remove the panel
    $(".currentPanel").remove();
}

function addCountToName(name, count)
{
    console.log("Marking: " + name + "Select");
    document.getElementById(name + "Select").innerHTML = count + " " + name;
    console.log(document.getElementById(name + "Select").innerHTML);
}

 function nameNotInArray(array, name)
 {
    for(var i = 0; i < array.length; i++)
    {
        if(((array[i].name).localeCompare(name)) == 0)
        {
            console.log(name + " already in array.");
            return false;
        }
    }
    console.log(name + " is new.");
    return true;
 }