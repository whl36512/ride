// See post: http://asmaloney.com/2014/01/code/creating-an-interactive-map-with-leaflet-and-openstreetmap/

var map;
var marker;
var markerTo;
var markerFrom;

function createMap(lat, long, zoom)
{
	map = L.map('map').setView([lat, long], zoom);

	L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  	attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
  	subdomains: ['a', 'b', 'c']
	}).addTo( map )
}

function flyTo(lat,lon,name){
	console.log("flyTo");
	map.setView([lat,lon],12);
	//map.flyTo([lat,lon],12);
	if(marker) {         map.removeLayer(marker); }
	if(markerTo) {         map.removeLayer(markerTo); }
	if(markerFrom) {         map.removeLayer(markerFrom); }
	//marker= L.Marker.SVGMarker([lat,lon]).addTo(map).bindPopup(name) svg marker doesnt work
	marker= L.marker([lat,lon]).addTo(map).bindPopup(name) ;
}


function flyToBounds(start_lat, start_lon, start_display_name, end_lat, end_lon, end_display_name)
{
	console.log("flyToBounds");
	fitB= map.fitBounds([
    		[start_lat, start_lon],
    		[end_lat, end_lon]
	]);
	if(marker) {         map.removeLayer(marker); }
	if(markerTo) {         map.removeLayer(markerTo); }
	if(markerFrom) {         map.removeLayer(markerFrom); }
	map.flyToBounds(fitB);
	markerFrom= L.marker([start_lat,start_lon]).addTo(map).bindPopup(start_display_name) ;
	markerTo= L.marker([end_lat,end_lon]).addTo(map).bindPopup(end_display_name) ;
}


function routingUrl(start_lat, start_lon, end_lat, end_lon){
// curl 'http://router.project-osrm.org/route/v1/driving/13.388860,52.517037;13.397634,52.529407;13.428555,52.523219?overview=false'
	var url= "http://router.project-osrm.org/route/v1/driving/" ;
	points=start_lon+","+ start_lat + ";" + end_lon+ ","+ end_lat  ;
	var query="?overview=false"  ;
	var urlEncoded=url+points+query ;
	return urlEncoded;
}

createMap(41.889489, -87.633229, 12);

//var rb='<a href="https://regionbound.com">RegionBound</a> | ';
//var cc='<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>';
//var attr1=rb+'Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, '+cc;
//var tileLayer1Options={attribution:attr1};
//var tileLayer1=L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png',tileLayer1Options);
//var attr2=rb+'Tiles © <a target="attr" href="http://esri.com">Esri</a>';
//var tileLayer2Options={attribution:attr2};
//var tileLayer2=L.tileLayer('https://services.arcgisonline.com/ArcGIS/rest/services/world_topo_map/MapServer/tile/{z}/{y}/{x}.png',tileLayer2Options);
//var flyMap;
//function flyTo(lat,lon,name){
	//flyMap.flyTo([lat,lon],11);
	//L.marker([lat,lon]).addTo(flyMap).bindPopup(name+"<br/>International Airport<br/>["+lat+", "+lon+"]")
//}
//var mapOptions={};
//flyMap=L.map('map-with-flyto',mapOptions).locate({setView:true,maxZoom:16});
//tileLayer2.addTo(flyMap);
//var layers={'OSM':tileLayer1,'Esri Topo':tileLayer2};
//L.control.layers(layers,{}).addTo(flyMap);
//flyMap.on('locationfound',function(e){var radius=(e.accuracy/2).toFixed();L.circle(e.latlng,radius).addTo(flyMap).bindPopup("You are within this circle");L.marker(e.latlng).addTo(flyMap).bindPopup("You are within "+radius+" meters of this marker.");});
//flyMap.on('locationerror',function(e){alert(e.message);});</script></div></div></div>    </article>
//



