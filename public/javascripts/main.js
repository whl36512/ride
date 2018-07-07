function navicon() {
    var x = document.getElementById("tn");
    if (x.className == "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
}

