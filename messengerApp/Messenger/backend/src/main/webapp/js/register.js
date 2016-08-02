
    function enableSignUpConfirmClick() {
        document.getElementById('confirmSignUp').onclick = function() {
            this.disabled = true;
            var username = document.getElementById('userNameInput').value;
            var password = document.getElementById('passwordInput').value;
            var repeatPassword = document.getElementById('repeatPasswordInput').value;
            var email = document.getElementById('emailInput').value;

            if(email.length <= 0 || username.length <= 0 || password.length <= 0)
            {
                console.log("A field is empty.");
                document.getElementById('confirmSignUp').disabled = false;
                return false;
            }

            if((repeatPassword).localeCompare(password) != 0)
            {
                console.log("Passwords doesn't match.");
                document.getElementById('passwordInput').className += " errorBox";
                document.getElementById('repeatPasswordInput').className += " errorBox";
                document.getElementById('confirmSignUp').disabled = false;
                return false;
            }

            gapi.client.myApi.register({'username': username, 'password': password, 'email': email}).execute(
                function(response)
                {
                    if(((response.result.data).localeCompare("true")) == 0)
                    {
                        window.location = "index.html";
                    }
                    else
                    {
                        document.getElementById('userNameInput').className += " errorBox";
                        document.getElementById('emailInput').className += " errorBox";
                        document.getElementById('confirmSignUp').disabled = false;
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