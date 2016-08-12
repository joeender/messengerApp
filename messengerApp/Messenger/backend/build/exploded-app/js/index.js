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
    document.getElementById('registerButton').onclick = function(event) {
        event.preventDefault()
        window.location = "register.html";
    }
}

function enableSignInClick() {
    document.getElementById('loginButton').onclick = function() {
        this.disabled = true;
        window.username = document.getElementById('nameLogin').value;
        window.username = window.username.toLowerCase();
        window.password = document.getElementById('passwordLogin').value;
        gapi.client.myApi.signin({'username': window.username, 'password': window.password}).execute(
            function(response)
            {
                if(((response.result.data).localeCompare("true")) == 0)
                {
                    window.loggedIn = true;
                    $('body').css('background-color','white');
                    document.getElementById('signIn').style.display  = "none";
                    document.getElementById('wrapper').style.display = 'block';
                    enableFriendRequestClick();
                    refreshLists();
                    enableSendMessage();
                    enableCheckMessagesPing();
                }
                else
                {
                    document.getElementById('loginButton').disabled = false;
                    document.getElementById('nameLogin').className += " errorBox";
                    document.getElementById('passwordLogin').className += " errorBox";
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
            friendInfo = friendInfo.toLowerCase();

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
                    }

                    if(window.friendNameKeyArray.length === 1)
                    {
                        window.currentFriend = window.friendNameKeyArray[0];
                        console.log("Current friend selected: " + window.currentFriend.name);
                        showMessagePanel();
                        printCurrentMessages();
                        document.getElementById(window.currentFriend.name + "Select").style.backgroundColor = "white";
                        document.getElementById(window.currentFriend.name + "Select").style.color = "black";
                        document.getElementById("toFriend").innerHTML= "To: " + window.currentFriend.name;
                    }
                }

                ////
                getUnreadCount();


                if(window.initialize)
                {
                    if(window.friendNameKeyArray.length > 0)
                    {
                        window.currentFriend = window.friendNameKeyArray[0];
                        console.log("Current friend selected: " + window.currentFriend.name);
                        showMessagePanel();
                        printCurrentMessages();
                        document.getElementById(window.currentFriend.name + "Select").style.backgroundColor = "white";
                        document.getElementById(window.currentFriend.name + "Select").style.color = "black";
                        document.getElementById("toFriend").innerHTML= "To: " + window.currentFriend.name;
                    }
                    else
                    {
                        console.log("No friends");
                    }
                    window.initialize = false;
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

function printCurrentMessages()
{
    if(window.loggedIn){
     gapi.client.myApi.getmessages({'username': window.username, 'password': window.password, 'friend': window.currentFriend.name}).execute(
         function(response)
         {
            if(currentFriend !== null)
            {
                 console.log("Messages from " + window.currentFriend.name + ":");
                 if(((response.result.data).localeCompare("none")) !== 0)
                 {
                      var obj = JSON.parse(response.result.data);
                      var messagesListLength = Object.keys(obj.messages).length;
                      for(var i = 0; i < messagesListLength; i++)
                      {
                          console.log(obj.messages[i]);
                          $(".currentPanel").prepend('<p class="theirText">' + "<b class = 'theirWords'>" + obj.messages[i] + '</b></p>');
                          $(".currentPanel").prepend('<p class="theirText">' + "<b class = 'theirName'>" + window.currentFriend.name + "</b> </p>");
                      }
                 }
                 else
                 {
                    console.log("No messages.")
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
                        var p = document.createElement("p");
                        $(".currentPanel").prepend(p);
                        p.className = "yourText";
                        p.innerHTML = "<b class = 'yourWords'>" + message + "</b>";

                        var nameP = document.createElement("p");
                        $(".currentPanel").prepend(nameP);
                        nameP.className = "yourText";
                        nameP.innerHTML = "<b class = 'yourName'>You</b>";

                        document.getElementById('sendMessage').disabled = false;


             }
         );
         return false;
     }

      document.onkeydown = function(e) {
          if(e.keyCode == 13)
          {
                this.disabled = true;
                var message = document.getElementById('sendText').value;
                document.getElementById('sendText').value = "";
                gapi.client.myApi.sendmessage({'username': window.username, 'password': window.password, 'receiverkey':window.currentFriend.key, 'message':message}).execute(
                    function(response)
                    {
                        var p = document.createElement("p");
                        $(".currentPanel").prepend(p);
                        p.className = "yourText";
                        p.innerHTML = "<b class = 'yourWords'>" + message + "</b>";

                        var nameP = document.createElement("p");
                        $(".currentPanel").prepend(nameP);
                        nameP.className = "yourText";
                        nameP.innerHTML = "<b class = 'yourName'>You</b>";

                        document.getElementById('sendMessage').disabled = false;
                    }
                );
                return false;
          }

      }
 }


function enableCheckMessagesPing()
{
    if(window.loggedIn)
    {
        setInterval(printCurrentMessages, 5000);
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
    $(parent).prepend(li);
    li.appendChild(div);
    div.innerHTML = requestFrom + " wants to be friends<br>";
    div.className = "friendPrompt";


    var btnYes = document.createElement("BUTTON");
    btnYes.innerHTML = "YES";
    var btnNo = document.createElement("BUTTON");
    btnNo.innerHTML = "NO";

    div.appendChild(btnYes);
    div.appendChild(btnNo);

    btnYes.className = "btn btn-primary btn-sm friendYes";
    btnNo.className = "btn btn-primary btn-sm friendNo";

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
    div.className = "friendSelect";


    div.addEventListener("click", function() {
         $(".friendSelect").each(function( index ) {
             /// Sets the unselected friend as teal-green.
             this.style.backgroundColor = "#00D9C7";
             this.style.color = "white";
         });
         this.style.backgroundColor = "white";
         this.style.color = "black";
         console.log(friendsFrom.name + " selected!")
         window.currentFriend = friendsFrom;
         div.innerHTML = name;
         showMessagePanel();
         printCurrentMessages();
         document.getElementById("toFriend").innerHTML= "To: " + window.currentFriend.name;
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
            return false;
        }
    }
    console.log(name + " is new.");
    return true;
 }