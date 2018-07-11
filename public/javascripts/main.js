function navicon() {
    var x = document.getElementById("tn");
    if (x.className == "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
}


function toggleDisply(id) {
	var elem = findElemToBeToggled(id) ;
        elem.style.display = (elem.style.display == "block") ? "none" : "block";
        false;
}

function findElemToBeToggled(id) {
        //id comes in as either 12345 or toggleBy-12345. If it already has toggleBy- on it, do not prepend toggleBy- again
        console.log("toggleDisplay id=" + id)
        if ( id.indexOf("toggleBy-") != 0 ) id="toggleBy-" + id
        var elem = document.getElementById(id);
	return elem;
}



function toggleDisplayByCheck(checkBox) {
	var elem = findElemToBeToggled(checkBox.id) ;
	// If the checkbox is checked, display the output text
	elem.style.display = (checkBox.checked) ? "block" : "none";
	false;
}

function geocode(elem) {
	var url="https://nominatim.openstreetmap.org/search/"
//	input = 135%20pilkington%20avenue,%20birmingham
	query="?format=json&polygon=0&addressdetails=0" ;
	var urlEncoded = url+encodeURIComponent(elem.value.trim())+query ;
	console.log("geocode urlEncoded="+urlEncoded)
	sendRequest("", "GET", urlEncoded, elem) ;
	return false;
}


function alertContents(httpRequest, afterResponseAction) {

        if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
                        console.log("httpRequest.responseText=" + httpRequest.responseText );
                        window.location.reload(true);

                }
                else {
                        console.log('ERROR 20170117005623: return status '+ httpRequest.status + "\n" + httpRequest.responseText.replace(/\s+/g,''));
                }
        }
}

function parseGeocoded(responsText) {
}

//function callbackGeocode(httpRequest, elem) {
callbackGeocode = function(httpRequest, elem) {

        if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
                        console.log("callbackGeocode httpRequest.responseText=" + httpRequest.responseText );
			var responseTextJson=JSON.parse(httpRequest.responseText) ;
			console.log("callbackGeocode responseTextJson.length=" +  responseTextJson.length );
			console.log("callbackGeocode responseTextJson=\n" +  responseTextJson );
			console.log("responseTextJson[0]=\n", responseTextJson[0]) ;

			
			var start_display_name=document.getElementById("start_display_name");
			var start_lat=document.getElementById("start_lat");
			var start_lon=document.getElementById("start_lon");
			var end_display_name=document.getElementById("end_display_name");
			var end_lat=document.getElementById("end_lat");
			var end_lon=document.getElementById("end_lon");


			if(elem.id=="start_loc"){
				display_name=start_display_name ;
				lat=start_lat ;
				lon=start_lon ;
			}
			if(elem.id=="end_loc"){
				display_name=end_display_name ;
				lat=end_lat ;
				lon=end_lon ;
                        }

			if(responseTextJson[0] != null ) {	
	                	display_name.value=responseTextJson[0].display_name;
	                	lat.value=responseTextJson[0].lat;
	                	lon.value=responseTextJson[0].lon;

				//console.log("callbackGeocode "+ start_lat.name 		+ "=" + start_lat.value);
				//console.log("callbackGeocode "+ start_lon.name 		+ "=" + start_lon.value);
				//console.log("callbackGeocode "+ start_display_name.name + "=" + start_display_name.value);
				//console.log("callbackGeocode "+ end_lat.name 		+ "=" + end_lat.value);
				//console.log("callbackGeocode "+ end_lon.name 		+ "=" + end_lon.value);
				//console.log("callbackGeocode "+ end_display_name.name 	+ "=" + end_display_name.value);
				if (start_display_name.value !=null && start_display_name.value !="" && end_display_name.value != null && end_display_name.value !="")
				{
					flyToBounds(start_lat.value, start_lon.value, start_display_name.value, end_lat.value, end_lon.value, end_display_name.value);
					var routeUrl=routingUrl(start_lat.value, start_lon.value, end_lat.value, end_lon.value);
					sendRequestWithCallback("", "GET", routeUrl, null, callbackRouting ) ;

				}
				else
				{
					flyTo(lat.value, lon.value,display_name.value) ;
				}
			}
			else
			{
	                	display_name.value="" ;
	                	display_name.placeholder="location not found" ;
	                	lat.value="" ;
	                	lon.value="" ;
			}
		
                }
                else {
                        console.log('ERROR 20170117005623: return status '+ httpRequest.status + "\n" + httpRequest.responseText.replace(/\s+/g,''));
                }
        }
}

callbackRouting = function (httpRequest, elem)
{
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
			var respon=httpRequest.responseText;
                        console.log("callbackRouting respon.length=" + respon.length );
                        console.log("callbackRouting respon=" + respon );
			var responseTextJson=JSON.parse(respon) ;
			console.log("callbackRouting responseTextJson.length=" +  responseTextJson.length );
			console.log("callbackRouting responseTextJson=\n" +  responseTextJson );
		        var distnace=document.getElementById("distance");
			distance.value= Math.round(responseTextJson.routes[0].distance /160)/10;
                }
                else {
                        console.log('ERROR 201807102058: return status '+ httpRequest.status + "\n" + httpRequest.responseText.replace(/\s+/g,''));
                }
	}
}

function sendRequestWithCallback(urlEncoded, method, action, elem, callback)
{
	// if GET, urlEncoded is empty and action has the full url
	// if POST, urlEncoded is the qury part 
        console.log("sendRequestWithCallback urlEncoded="+urlEncoded)
        console.log("sendRequestWithCallback method="+ method)
        console.log("sendRequestWithCallback action="+action)
        console.log("sendRequestWithCallback callback="+callback)
        var xhr = new XMLHttpRequest();

        xhr.onreadystatechange = function(){ callback(xhr, elem); } ;

        xhr.open (method, action, true);
	if ( method == "POST" )
	{
		xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xhr.setRequestHeader('Content-Length', urlEncoded.length);
	}
        xhr.send (urlEncoded);
        console.log("sendRequestWithCallback done");
        return false;
}

function sendRequest(urlEncoded, method, action, elem)
{
	// if GET, urlEncoded is empty and action has the full url
	// if POST, urlEncoded is the qury part 
        console.log("sendRequest urlEncoded="+urlEncoded)
        console.log("sendRequest method="+method)
        console.log("sendRequest action="+action)
        var xhr = new XMLHttpRequest();

        xhr.onreadystatechange = function(){ callbackGeocode(xhr, elem); } ;

        xhr.open (method, action, true);
	if ( method == "POST" )
	{
		xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xhr.setRequestHeader('Content-Length', urlEncoded.length);
	}
        xhr.send (urlEncoded);
        console.log("after sendRequest");
        return false;
}


