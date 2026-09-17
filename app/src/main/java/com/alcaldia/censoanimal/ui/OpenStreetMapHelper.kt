package com.alcaldia.censoanimal.ui

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.alcaldia.censoanimal.model.AnimalRecord
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * OpenStreetMap helper that configures an interactive Leaflet + OpenStreetMap map in an Android WebView.
 * Allows selecting the user's current GPS location, moving the pin by dragging or tapping the map,
 * and reporting coordinates back to Kotlin.
 */
object OpenStreetMapHelper {

    interface OnLocationChangeListener {
        fun onLocationChanged(lat: Double, lng: Double)
    }

    class MapBridge(private val listener: OnLocationChangeListener) {
        private val mainHandler = Handler(Looper.getMainLooper())

        @JavascriptInterface
        fun onLocationSelected(lat: Double, lng: Double) {
            mainHandler.post {
                listener.onLocationChanged(lat, lng)
            }
        }
    }

    fun buildMapHtml(initialLat: Double, initialLng: Double, isInteractive: Boolean = true): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                * { box-sizing: border-box; -webkit-tap-highlight-color: transparent; }
                html, body, #map {
                    width: 100%;
                    height: 100%;
                    margin: 0;
                    padding: 0;
                    background: #f1f5f9;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                }
                .leaflet-container {
                    background: #e2e8f0;
                    font-size: 11px;
                }
                /* Clean custom pin wrapper without extra transforms */
                .custom-pin-wrapper {
                    background: transparent !important;
                    border: none !important;
                    box-shadow: none !important;
                }
                .custom-pin-wrapper svg {
                    display: block;
                    width: 36px;
                    height: 48px;
                    overflow: visible;
                }
                .location-badge {
                    position: absolute;
                    top: 10px;
                    right: 10px;
                    z-index: 1000;
                    background: rgba(15, 23, 42, 0.85);
                    color: #f8fafc;
                    padding: 6px 12px;
                    border-radius: 20px;
                    font-size: 10px;
                    font-weight: 600;
                    box-shadow: 0 2px 6px rgba(0,0,0,0.2);
                    pointer-events: none;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <div class="location-badge" id="coordsBadge">Lat: ${String.format(Locale.US, "%.4f", initialLat)}, Lng: ${String.format(Locale.US, "%.4f", initialLng)}</div>

            <script>
                var curLat = $initialLat;
                var curLng = $initialLng;
                var isInteractive = $isInteractive;

                var map = L.map('map', {
                    center: [curLat, curLng],
                    zoom: 14,
                    zoomControl: isInteractive,
                    attributionControl: true,
                    dragging: isInteractive,
                    touchZoom: isInteractive,
                    scrollWheelZoom: isInteractive
                });

                L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap'
                }).addTo(map);

                // Custom SVG pin with exact geometric tip at (18, 46) and matching Leaflet iconAnchor
                var pinSvg = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 36 48" width="36" height="48">' +
                             '  <defs>' +
                             '    <filter id="pinShadow" x="-30%" y="-20%" width="160%" height="150%">' +
                             '      <feDropShadow dx="0" dy="2.5" stdDeviation="2" flood-color="#0f172a" flood-opacity="0.35"/>' +
                             '    </filter>' +
                             '  </defs>' +
                             '  <ellipse cx="18" cy="46" rx="6" ry="2" fill="rgba(15, 23, 42, 0.25)"/>' +
                             '  <circle cx="18" cy="46" r="3" fill="none" stroke="#2563eb" stroke-width="2" opacity="0.6">' +
                             '    <animate attributeName="r" from="3" to="12" dur="2s" repeatCount="indefinite"/>' +
                             '    <animate attributeName="opacity" from="0.7" to="0" dur="2s" repeatCount="indefinite"/>' +
                             '  </circle>' +
                             '  <path d="M18,2 C9.16,2 2,9.16 2,18 C2,27.5 13,39.5 18,46 C23,39.5 34,27.5 34,18 C34,9.16 26.84,2 18,2 Z"' +
                             '        fill="#2563eb" stroke="#ffffff" stroke-width="2.5" stroke-linejoin="round" filter="url(#pinShadow)" />' +
                             '  <circle cx="18" cy="18" r="6" fill="#ffffff"/>' +
                             '  <circle cx="18" cy="18" r="2.5" fill="#1d4ed8"/>' +
                             '</svg>';

                var customIcon = L.divIcon({
                    className: 'custom-pin-wrapper',
                    html: pinSvg,
                    iconSize: [36, 48],
                    iconAnchor: [18, 46]
                });

                var marker = L.marker([curLat, curLng], {
                    icon: customIcon,
                    draggable: isInteractive
                }).addTo(map);

                // Ensure Leaflet recalculates dimensions if container resizes
                window.addEventListener('resize', function() {
                    map.invalidateSize();
                });
                setTimeout(function() {
                    map.invalidateSize();
                }, 250);

                function updateBadge(lat, lng) {
                    var badge = document.getElementById('coordsBadge');
                    if (badge) {
                        badge.innerText = 'Lat: ' + lat.toFixed(4) + ', Lng: ' + lng.toFixed(4);
                    }
                }

                function notifyKotlin(lat, lng) {
                    updateBadge(lat, lng);
                    if (window.AndroidMapBridge) {
                        window.AndroidMapBridge.onLocationSelected(lat, lng);
                    }
                }

                if (isInteractive) {
                    marker.on('dragend', function(e) {
                        var pos = marker.getLatLng();
                        curLat = pos.lat;
                        curLng = pos.lng;
                        notifyKotlin(curLat, curLng);
                    });

                    map.on('click', function(e) {
                        curLat = e.latlng.lat;
                        curLng = e.latlng.lng;
                        marker.setLatLng([curLat, curLng]);
                        notifyKotlin(curLat, curLng);
                    });
                }

                // Public JS function called from Kotlin
                window.setPinLocation = function(lat, lng, panTo) {
                    curLat = lat;
                    curLng = lng;
                    marker.setLatLng([lat, lng]);
                    updateBadge(lat, lng);
                    if (panTo !== false) {
                        map.setView([lat, lng], map.getZoom());
                    }
                };

                window.centerOnLocation = function(lat, lng) {
                    setPinLocation(lat, lng, true);
                };
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun configureWebView(
        webView: WebView,
        initialLat: Double,
        initialLng: Double,
        isInteractive: Boolean = true,
        listener: OnLocationChangeListener? = null
    ) {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.setSupportZoom(false)
        webView.settings.builtInZoomControls = false
        webView.settings.displayZoomControls = false
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false

        if (listener != null) {
            webView.addJavascriptInterface(MapBridge(listener), "AndroidMapBridge")
        }

        webView.webViewClient = object : WebViewClient() {}

        // Prevent parent ScrollView from stealing touch gestures when dragging the map
        webView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.parent?.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        val html = buildMapHtml(initialLat, initialLng, isInteractive)
        webView.loadDataWithBaseURL("https://www.openstreetmap.org", html, "text/html", "UTF-8", null)
    }

    fun updatePinLocation(webView: WebView, lat: Double, lng: Double) {
        val js = String.format(Locale.US, "window.setPinLocation(%.6f, %.6f, true);", lat, lng)
        webView.evaluateJavascript(js, null)
    }

    interface OnAnimalSelectedListener {
        fun onAnimalSelected(recordId: String)
    }

    class AnimalMapBridge(private val listener: OnAnimalSelectedListener) {
        private val mainHandler = Handler(Looper.getMainLooper())

        @JavascriptInterface
        fun onAnimalSelected(recordId: String) {
            mainHandler.post {
                listener.onAnimalSelected(recordId)
            }
        }
    }

    fun animalsToJson(records: List<AnimalRecord>): String {
        val array = JSONArray()
        for (r in records) {
            val obj = JSONObject()
            obj.put("id", r.registro_id)
            obj.put("name", r.animal_nombre)
            obj.put("species", r.especie)
            obj.put("breed", r.raza)
            obj.put("vereda", r.territorio)
            obj.put("owner", r.responsable_nombre)
            obj.put("lat", r.latitud)
            obj.put("lng", r.longitud)
            obj.put("isDuplicateAlert", r.alerta_duplicado)
            array.put(obj)
        }
        return array.toString()
    }

    fun buildAnimalMapHtml(records: List<AnimalRecord>, initialVereda: String = "Todas las Veredas"): String {
        val animalsJson = animalsToJson(records)
        val escapedVereda = initialVereda.replace("'", "\\'")

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                * { box-sizing: border-box; -webkit-tap-highlight-color: transparent; }
                html, body, #map {
                    width: 100%;
                    height: 100%;
                    margin: 0;
                    padding: 0;
                    background: #f1f5f9;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
                }
                .leaflet-container {
                    background: #e2e8f0;
                    font-size: 11px;
                }
                .animal-pin-marker {
                    background: transparent !important;
                    border: none !important;
                    box-shadow: none !important;
                }
                .animal-pin-marker svg {
                    display: block;
                    width: 38px;
                    height: 48px;
                    overflow: visible;
                    cursor: pointer;
                }
                .animal-tooltip {
                    background: rgba(15, 23, 42, 0.92);
                    border: none;
                    border-radius: 14px;
                    color: #ffffff;
                    font-size: 11px;
                    font-weight: 600;
                    padding: 5px 10px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.3);
                }
                .animal-tooltip:before {
                    border-top-color: rgba(15, 23, 42, 0.92);
                }
            </style>
        </head>
        <body>
            <div id="map"></div>

            <script>
                var animalsData = $animalsJson;
                var currentVereda = '$escapedVereda';
                var markers = {};
                var selectedAnimalId = null;

                var map = L.map('map', {
                    center: [5.0260, -74.0040],
                    zoom: 13,
                    zoomControl: false,
                    attributionControl: true
                });

                L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap | Censo Zipaquirá'
                }).addTo(map);

                var territoryCenters = {
                    "Vereda San Jorge": [4.9842, -73.9562],
                    "Vereda El Rosal": [4.9920, -73.9610],
                    "Vereda Barroblanco": [5.0080, -73.9820],
                    "Vereda Santa Librada": [5.0300, -73.9550],
                    "Vereda La Esperanza": [4.9800, -74.0150],
                    "Vereda San Benito": [5.0150, -74.0350],
                    "Vereda Ventalarga": [5.0550, -74.0100],
                    "Vereda Portachuelo": [5.0450, -74.0200],
                    "Vereda Río Frío": [5.0380, -73.9700],
                    "Vereda El Tunal": [4.9750, -73.9850],
                    "Vereda Páramo de Guerrero": [5.0600, -74.0500],
                    "Vereda Barandillas": [5.0120, -73.9740],
                    "Vereda La Granja": [5.0200, -73.9900],
                    "Vereda Empalizado": [5.0400, -74.0400],
                    "Casco Urbano / Barrios": [5.0260, -74.0040]
                };

                function createAnimalIcon(animal, isSelected) {
                    var isDog = (animal.species || '').toLowerCase() === 'perro';
                    var emoji = isDog ? '🐶' : '🐱';
                    var mainColor = animal.isDuplicateAlert ? '#e11d48' : (isDog ? '#2563eb' : '#059669');
                    var ring = isSelected ? '<circle cx="19" cy="18" r="16" fill="none" stroke="#f59e0b" stroke-width="3.5" opacity="0.95"/>' : '';

                    var svg = '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 38 48" width="38" height="48">' +
                              '  <defs>' +
                              '    <filter id="shadow-' + animal.id + '" x="-25%" y="-20%" width="150%" height="150%">' +
                              '      <feDropShadow dx="0" dy="2" stdDeviation="2" flood-color="#0f172a" flood-opacity="0.35"/>' +
                              '    </filter>' +
                              '  </defs>' +
                              '  <ellipse cx="19" cy="46" rx="6" ry="2" fill="rgba(15, 23, 42, 0.25)"/>' +
                              ring +
                              '  <path d="M19,2 C9.6,2 2,9.6 2,19 C2,28.5 14,39 19,46 C24,39 36,28.5 36,19 C36,9.6 28.4,2 19,2 Z"' +
                              '        fill="' + mainColor + '" stroke="#ffffff" stroke-width="2.5" stroke-linejoin="round" filter="url(#shadow-' + animal.id + ')" />' +
                              '  <circle cx="19" cy="18" r="11" fill="#ffffff"/>' +
                              '  <text x="19" y="23" text-anchor="middle" font-size="13">' + emoji + '</text>' +
                              '</svg>';

                    return L.divIcon({
                        className: 'animal-pin-marker',
                        html: svg,
                        iconSize: [38, 48],
                        iconAnchor: [19, 46]
                    });
                }

                function initMarkers() {
                    animalsData.forEach(function(animal) {
                        var icon = createAnimalIcon(animal, false);
                        var marker = L.marker([animal.lat, animal.lng], { icon: icon });
                        marker.animalData = animal;

                        marker.bindTooltip(animal.name + ' (' + animal.species + ') • ' + animal.vereda, {
                            direction: 'top',
                            offset: [0, -42],
                            className: 'animal-tooltip'
                        });

                        marker.on('click', function() {
                            selectAnimal(animal.id, false);
                            if (window.AndroidAnimalBridge) {
                                window.AndroidAnimalBridge.onAnimalSelected(animal.id);
                            }
                        });

                        markers[animal.id] = marker;
                    });

                    filterVereda(currentVereda);
                }

                function selectAnimal(id, panTo) {
                    selectedAnimalId = id;
                    for (var aId in markers) {
                        var m = markers[aId];
                        m.setIcon(createAnimalIcon(m.animalData, aId === id));
                    }
                    if (panTo && markers[id]) {
                        map.panTo(markers[id].getLatLng());
                    }
                }

                function filterVereda(vereda) {
                    currentVereda = vereda;
                    var visibleLatLngs = [];

                    for (var id in markers) {
                        var marker = markers[id];
                        var animal = marker.animalData;
                        var matches = (vereda === 'Todas las Veredas') ||
                                      (animal.vereda && animal.vereda.toLowerCase() === vereda.toLowerCase());
                        if (matches) {
                            if (!map.hasLayer(marker)) {
                                map.addLayer(marker);
                            }
                            visibleLatLngs.push(marker.getLatLng());
                        } else {
                            if (map.hasLayer(marker)) {
                                map.removeLayer(marker);
                            }
                        }
                    }

                    if (visibleLatLngs.length > 0) {
                        var bounds = L.latLngBounds(visibleLatLngs);
                        map.fitBounds(bounds, { padding: [40, 40], maxZoom: 16 });
                    } else if (territoryCenters[vereda]) {
                        map.setView(territoryCenters[vereda], 15);
                    } else {
                        map.setView([5.0260, -74.0040], 13);
                    }
                }

                window.addEventListener('resize', function() {
                    map.invalidateSize();
                });
                setTimeout(function() {
                    map.invalidateSize();
                }, 250);

                initMarkers();

                window.selectAnimal = selectAnimal;
                window.filterVereda = filterVereda;
                window.zoomIn = function() { map.zoomIn(); };
                window.zoomOut = function() { map.zoomOut(); };
                window.fitAll = function() { filterVereda(currentVereda); };
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun configureAnimalMapWebView(
        webView: WebView,
        records: List<AnimalRecord>,
        initialVereda: String = "Todas las Veredas",
        listener: OnAnimalSelectedListener? = null
    ) {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.setSupportZoom(false)
        webView.settings.builtInZoomControls = false
        webView.settings.displayZoomControls = false
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false

        if (listener != null) {
            webView.addJavascriptInterface(AnimalMapBridge(listener), "AndroidAnimalBridge")
        }

        webView.webViewClient = object : WebViewClient() {}

        webView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.parent?.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        val html = buildAnimalMapHtml(records, initialVereda)
        webView.loadDataWithBaseURL("https://www.openstreetmap.org", html, "text/html", "UTF-8", null)
    }

    fun selectAnimalOnMap(webView: WebView, recordId: String, panTo: Boolean = true) {
        val js = "window.selectAnimal('$recordId', $panTo);"
        webView.evaluateJavascript(js, null)
    }

    fun filterAnimalMapVereda(webView: WebView, vereda: String) {
        val escaped = vereda.replace("'", "\\'")
        val js = "window.filterVereda('$escaped');"
        webView.evaluateJavascript(js, null)
    }

    fun zoomInAnimalMap(webView: WebView) {
        webView.evaluateJavascript("window.zoomIn();", null)
    }

    fun zoomOutAnimalMap(webView: WebView) {
        webView.evaluateJavascript("window.zoomOut();", null)
    }

    fun fitAllAnimalMap(webView: WebView) {
        webView.evaluateJavascript("window.fitAll();", null)
    }
}
