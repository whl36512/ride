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


function geocode(inputString, elem) {
	var url="https://nominatim.openstreetmap.org/search/"
//	input = 135%20pilkington%20avenue,%20birmingham
	query="?format=json&polygon=0&addressdetails=0" ;
	var urlEncoded = url+encodeURIComponent(inputString.trim())+query ;
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
                        alert('ERROR 20170117005623: return status '+ httpRequest.status + "\n" + httpRequest.responseText.replace(/\s+/g,''));
                }
        }
}

function parseGeocoded(responsText) {
}

function callbackGeocode(httpRequest, elem) {

        if (httpRequest.readyState === XMLHttpRequest.DONE) {
                if (httpRequest.status === 200) {
                        console.log("callbackGeocode httpRequest.responseText=" + httpRequest.responseText );
			var responseTextJson=JSON.parse(httpRequest.responseText) ;
			console.log("callbackGeocode responseTextJson.length=" +  responseTextJson.length );
			var readOnlyInput= elem.closest("dl" ).getElementsByClassName("readOnly").item(0) ;
			console.log("callbackGeocode elem.parentElement" +  elem.closest("dl" ).getElementsByClassName("readOnly").item(0));
	                readOnlyInput.value=responseTextJson[0].display_name;
			

                }
                else {
                        alert('ERROR 20170117005623: return status '+ httpRequest.status + "\n" + httpRequest.responseText.replace(/\s+/g,''));
                }
        }
}





function sendRequest(urlEncoded, method, action, elem)
{
	// if GET, urlEncoded is empty and action has the full url
	// if POST, urlEncoded is the qury part 
        console.log("sendRequest urlEncoded="+urlEncoded)
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

