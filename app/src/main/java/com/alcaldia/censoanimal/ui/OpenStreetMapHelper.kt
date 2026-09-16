package com.alcaldia.censoanimal.ui

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
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
}
