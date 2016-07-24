
    function enableSignUpConfirmClick() {
        document.getElementById('confirmSignUp').onclick = function() {
            var username = document.getElementById('userNameInput').value;
            var password = document.getElementById('passwordInput').value;
            var email = document.getElementById('emailInput').value;
            gapi.client.myApi.register({'username': username, 'password': password, 'email': email}).execute(
                function(response)
                {
                    if(((response.result.data).localeCompare("true")) == 0)
                    {
                        alert("User Added");
                    }
                    else
                    {
                        alert("User Name Taken!");
                    }
                }
            );
            return false;
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
      var callback = function() {
        enableSignUpConfirmClick();
      }
      gapi.client.load(apiName, apiVersion, callback, apiRoot);
    }