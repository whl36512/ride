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

