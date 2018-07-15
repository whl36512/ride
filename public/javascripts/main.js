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
	sendRequestWithCallback("", "GET", urlEncoded, elem, callbackGeocode) ;
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
	var distance=document.getElementById("distance");
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
			var respon=httpRequest.responseText;
                        console.log("INFO 201807132315 callbackRouting respon.length=" + respon.length );
                        console.log("INFO 201807132315 callbackRouting respon=" + respon );
			var responseTextJson=JSON.parse(respon) ;
			console.log("callbackRouting responseTextJson.length=" +  responseTextJson.length );
		        //var distance=document.getElementById("distance");
			distance.value= Math.round(responseTextJson.routes[0].distance /160)/10;
                        console.log("INFO 201807132315 distance.value=" + distance.value );
                }
                else {
                        console.log('ERROR 201807102058: return status '+ httpRequest.status + "\n" + httpRequest.responseText.replace(/\s+/g,''));
		        //var distance=document.getElementById("distance");
                        distance.value="" ;
                        console.log("INFO 201807132315 distance.value=" + distance.value );
                }
	}
}

function sendRequestWithCallback(urlEncoded, method, action, elem, callback)
{
	// if GET, urlEncoded is empty and action has the full url
	// if POST, urlEncoded is the qury part 
	method = method.toUpperCase() ;
        console.log("INFO 201807141033 sendRequestWithCallback urlEncoded= <"+urlEncoded+">")
        console.log("INFO 201807141033 sendRequestWithCallback method= "+ method)
        console.log("INFO 201807141033 sendRequestWithCallback action= "+action)
        console.log("INFO 201807141033 sendRequestWithCallback callback= "+callback)
        var xhr = new XMLHttpRequest();

        xhr.onreadystatechange = function(){ callback(xhr, elem); } ;

        xhr.open (method, action, true);
	if ( method == "POST"  )
	{
        	console.log("INFO 201807140938 setting POST headers" );
		xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		// xhr.setRequestHeader('Content-Length', urlEncoded.length); // Refused to set unsafe header "Content-Length"
	}
        xhr.send (urlEncoded);
        console.log("INFO 201807141033 sendRequestWithCallback done");
        return false;
}

//function sendRequest(urlEncoded, method, action, elem)
//{
	//// if GET, urlEncoded is empty and action has the full url
	//// if POST, urlEncoded is the qury part 
        //console.log("sendRequest urlEncoded="+urlEncoded)
        //console.log("sendRequest method="+method)
        //console.log("sendRequest action="+action)
        //var xhr = new XMLHttpRequest();
//
        //xhr.onreadystatechange = function(){ callbackGeocode(xhr, elem); } ;
//
        //xhr.open (method, action, true);
	//if ( method == "POST" )
	//{
		//xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		//xhr.setRequestHeader('Content-Length', urlEncoded.length);
	//}
        //xhr.send (urlEncoded);
        //console.log("after sendRequest");
        //return false;
//}

function submitForm(oFormElement, validateFunc, callbackFunc)
{
        console.log("INFO 201807132227 enter submitForm");
        console.log("INFO 201807132227 oFormElement.method=" + oFormElement.method) ;
        console.log("INFO 201807132227 oFormElement.action=" + oFormElement.action) ;
	var formData = null;

        if ( validateFunc == null ) formData = getFormData(oFormElement)
	else if  (validateFunc(oFormElement) ) formData = getFormData(oFormElement)

        if (formData==null) return false ;
        var urlEncoded=urlEncodedData(formData);
        sendRequestWithCallback(urlEncoded, oFormElement.method, oFormElement.action, oFormElement, callbackFunc )
        return false;
}

function getFormData(form)
{
        var formData = new Map();
        var elemArray = [];
        function pushElement(tagName)
        {
                var elems = form.getElementsByTagName(tagName) ;
                console.log("INFO 201807140949 length="+elems.length)
                for (var i = 0; i < elems.length; i++)
                {
                     //console.log("INFO 201807140950 i="+i+" elem="+elems[i])
                         elemArray.push (elems[i]);
                }
        }
        pushElement("input") ;
        // alert (elemArray);
        pushElement("textarea") ;
        // alert ("length=" +elemArray.length);
        for (var i = 0; i < elemArray.length; i++)
        {
                var name =elemArray[i].getAttribute("name");
		var value ="" ;
		if(elemArray[i].type == "checkbox")
		{
			if (elemArray[i].checked) {value= "on"} else {value ="off"}
		}
		else value = elemArray[i].value ;
                formData.set (name, value) ;
        }
        //console.log(formData)
        formData.forEach(function (value,name){
                console.log("INFO 201807142107 formData name= "+ name + " value= " + value);

        })
        return formData;
}


function urlEncodedData(formData){
        console.log("in urlEncodedData");
        console.log(formData);

        var urlEncodedData = "";
        var urlEncodedDataPairs = [];
//      for (var [k, v] of formData){
//              // alert("name="+name +" value=" + value)
//          urlEncodedDataPairs.push(encodeURIComponent(k) + '=' + encodeURIComponent(v));
//
//      }
        formData.forEach(function (v,k){
                console.log("INFO 201807140914 k= "+k + " v= " +v);
                urlEncodedDataPairs.push(encodeURIComponent(k) + '=' + encodeURIComponent(v));

        })
        urlEncodedData = urlEncodedDataPairs.join('&').replace(/%20/g, '+');
        return urlEncodedData;
}

function validatenewoffer(form) {
	console.log("INFO 201807142230 validatenewoffer enter " ) ; 
        var formData 	= getFormData(form);
        distance	= formData.get("distance")
        trip_start 	= Date.parse(formData.get("trip_start"))/(24.0*60*60*1000) // days from epoch
        departure_time  = formData.get("departure_time ")
        seats   	= formData.get("seats")
        price   	= formData.get("price")
        recurring    	= formData.get("recurring")
        trip_end     	= Date.parse(formData.get("trip_end"))/(24.0*60*60*1000) // days from epoch
        day0      	= formData.get("day0")
        day1      	= formData.get("day1")
        day2      	= formData.get("day2")
        day3      	= formData.get("day3")
        day4      	= formData.get("day4")
        day5      	= formData.get("day5")
        day6      	= formData.get("day6")

	nowtime		= Math.floor(Date.now()/(24.0*60*60*1000)) // days from epoch

	console.log("INFO 201807142019 trip_start nowtime = " + trip_start + " " +  nowtime ) ; 
	console.log("INFO 201807142019 trip_start - nowtime = " + (trip_start - nowtime ) ) ; 
	if(distance =="" ) {
	  console.log("ERROR 201807142049 distance unset") ; 
	  formData = null;
	}
	else if( isNaN(trip_start)  ) {
	  console.log("ERROR 201807142049 trip_start unset") ; 
	  formData = null;
	}
        else if(trip_start - nowtime < -1) { 
	  console.log("ERROR 201807142019 trip_start -  nowtime =" + (trip_start - nowtime) +" < -1" ) ; 
	  formData = null;
	}
	else if(departure_time =="" ) 
	{
	  console.log("ERROR 201807142049 departure_time unset") ; 
	  formData = null;
	}
	else if(seats =="" )
	{
	  console.log("ERROR 201807142049 seats unset") ; 
	  formData = null;
	}
	else if ( seats <0 || seats > 6 ) 
	{
	  console.log("ERROR 201807142049 seats out of range") ; 
	  formData = null;
	}
	else if(price =="" )
	{
	  console.log("ERROR 201807142049 price unset") ; 
	  formData = null;
	}
	else if ( price <0 || price > 0.2 ) 
	{
	  console.log("ERROR 201807142049 price out of range") ; 
	  formData = null;
	}
        else if (recurring =="on" && isNaN(trip_end) ) 
	{
	  console.log("ERROR 201807142049 recurring but trip_end unset") ; 
	  formData = null;
	}
        else if (recurring =="on" && (trip_end < trip_start)   ) 
        {
	  console.log("ERROR 201807142020  trip_end < trip_start " );
	  formData = null;
 	}
        else if (recurring =="on" && (trip_end - trip_start) > 92 ) 
        {
	  console.log("ERROR 201807142301  trip_end - trip_start = " + (trip_end - trip_start) + " > 92" );
	  formData = null;
 	}
        else if (recurring =="on" && day0 != "on" && day1 != "on" && day2 != "on" && day3 != "on" && day4 != "on" && day5 != "on" && day6 != "on" ) {
	  console.log("ERROR 201807142027  recurring but no day of week is selected" );
	  formData = null;
        }
      
	if (formData == null) return false
	else return true
	//return formData;
}
