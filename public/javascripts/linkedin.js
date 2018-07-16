    // Setup an event listener to make an API call once auth is complete
    function onLinkedInLoad() {
       // IN.Event.on(IN, "auth", ride.getProfileData);
	ride.checkAuth();
    }

    // Handle the successful return from the API call
    function onSuccess(data) {
        console.log("INFO 201807131656");
        console.log(data);
    }

    // Handle an error response from the API call
    function onError(error) {
        console.log("ERROR 201807131657");
        console.log(error);
    }

var profileData;

    // Use the API call wrapper to request the member's basic profile data
var ride = {

      getProfileData : function () {
        console.log("INFO 201807131627 Authorized") ;
        //IN.API.Raw("/people/~").result(onSuccess).error(onError);
        IN.API.Raw("/people/~").result(ride.getProfileDataOnSuccess).error(onError);
    }


    , getProfileDataOnSuccess : function (data) {
        console.log("INFO 2017131508 data");
        console.log(data);
	//profileJson = JSON.parse(data)
        setCookie("profile.firstName", data.firstName, 1 )
        setCookie("profile.lastName", data.lastName, 1 )
        setCookie("profile.headline", data.headline, 1 )
        setCookie("profile.id", data.id, 1 )

        console.log("INFO 2017131508 profile.id =" + getCookie("profile.id"));
        console.log("INFO 2017131508 profile.lastName =" + getCookie("profile.lastName"));
	window.location.reload(true)              // use true to reload page from server
    }


    , newoffer : function () {
	this.checkAuth();
	if (getCookie("profile.id") != "") window.location = '/newoffer';
    }

    , checkAuth: function () {
      
      console.log("INFO 201807131928 checkAuth");
      console.log("INFO 201807131928 profile.id = " + getCookie("profile.id") );
      if ( !IN.User.isAuthorized() ) IN.User.authorize(this.getProfileData, null);
      else if ( IN.User.isAuthorized() && getCookie("profile.id") == "" ) IN.User.authorize(this.getProfileData, null);
      else {
	console.log("INFO 201807131924 Already authed");
	return true;
      }
      return false;
    }

    , signout: function () {
      IN.User.logout(this.signoutCallback, null) ;
    }

    , signoutCallback: function () {
        setCookie("profile.id", "", -1 )
        setCookie("profile.firstName", "", -1 )
        setCookie("profile.lastName", "", -1 )
        setCookie("profile.headline", "", -1 )
        //setCookie("PLAY_SESSION", "", -1 )   // cannot remove PLAY_SESSION because it has "same site" attribute
        console.log("INFO 201807131556 Signed Out !") ;
    }
}


function setCookie (cname, cvalue, exhours) {
    var d = new Date();
    d.setTime(d.getTime() + (exhours*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }

function getCookie (cname) {
      var name = cname + "=";
      var decodedCookie = decodeURIComponent(document.cookie);
      var ca = decodedCookie.split(';');
      for(var i = 0; i <ca.length; i++) {
          var c = ca[i];
          while (c.charAt(0) == ' ') {
              c = c.substring(1);
          }
          if (c.indexOf(name) == 0) {
              return c.substring(name.length, c.length);
          }
      }
      return "";
    }
