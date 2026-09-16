package com.alcaldia.censoanimal.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.view.View
import android.webkit.WebView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.data.ZipaquiraGeoHelper
import java.util.Locale

/**
 * Reusable location helper that unifies OpenStreetMap interactive rendering,
 * GPS detection, full-screen map picker navigation, and automated Zipaquirá
 * territory and address reverse-geocoding across any form in the application.
 */
class FormLocationHelper(
    private val context: Context,
    private val mapWebView: WebView,
    private val btnGps: Button,
    private val btnFullscreenMap: Button,
    private val tvCoords: TextView,
    private val tvDetectedVereda: TextView?,
    private val spinnerVereda: Spinner?,
    private val etAddress: EditText?,
    private val launcher: ActivityResultLauncher<Intent>,
    private val onLocationChangedCallback: ((lat: Double, lng: Double, vereda: String, address: String) -> Unit)? = null
) {

    var currentLat: Double = ZipaquiraGeoHelper.DEFAULT_LAT
        private set

    var currentLng: Double = ZipaquiraGeoHelper.DEFAULT_LNG
        private set

    var currentVereda: String = "Vereda San Jorge"
        private set

    var currentAddress: String = ""
        private set

    companion object {
        /**
         * Convenience factory that binds all views from an included `layout_form_location_picker`.
         */
        fun bind(
            container: View,
            launcher: ActivityResultLauncher<Intent>,
            customTitle: String? = null,
            customSubtitle: String? = null,
            onLocationChanged: ((lat: Double, lng: Double, vereda: String, address: String) -> Unit)? = null
        ): FormLocationHelper {
            val context = container.context

            customTitle?.let {
                container.findViewById<TextView>(R.id.tvLocationSectionTitle)?.text = it
            }
            customSubtitle?.let {
                container.findViewById<TextView>(R.id.tvLocationSectionSubtitle)?.text = it
            }

            val helper = FormLocationHelper(
                context = context,
                mapWebView = container.findViewById(R.id.mapLocationWebView),
                btnGps = container.findViewById(R.id.btnLocationGps),
                btnFullscreenMap = container.findViewById(R.id.btnLocationFullscreen),
                tvCoords = container.findViewById(R.id.tvLocationCoords),
                tvDetectedVereda = container.findViewById(R.id.tvLocationDetectedVereda),
                spinnerVereda = container.findViewById(R.id.spinnerLocationVereda),
                etAddress = container.findViewById(R.id.etLocationAddress),
                launcher = launcher,
                onLocationChangedCallback = onLocationChanged
            )
            helper.init()
            return helper
        }
    }

    /**
     * Initializes spinners, map webview, and defaults to device GPS or Zipaquirá central coordinates.
     */
    fun init(initialLat: Double? = null, initialLng: Double? = null) {
        // Setup spinner adapter if present
        spinnerVereda?.let { spinner ->
            val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                Microdataset.OFFICIAL_VEREDAS
            )
            spinner.adapter = adapter
        }

        // Determine starting coordinates
        if (initialLat != null && initialLng != null) {
            currentLat = initialLat
            currentLng = initialLng
        } else {
            val (gpsLat, gpsLng) = getDeviceGpsLocation()
            currentLat = gpsLat
            currentLng = gpsLng
        }

        // Configure OpenStreetMap WebView
        OpenStreetMapHelper.configureWebView(
            webView = mapWebView,
            initialLat = currentLat,
            initialLng = currentLng,
            isInteractive = true,
            listener = object : OpenStreetMapHelper.OnLocationChangeListener {
                override fun onLocationChanged(lat: Double, lng: Double) {
                    applyLocation(lat, lng, null, null, updateMap = false)
                }
            }
        )

        // GPS Button Listener
        btnGps.setOnClickListener {
            val (gpsLat, gpsLng) = getDeviceGpsLocation()
            applyLocation(gpsLat, gpsLng, null, null, updateMap = true)
            Toast.makeText(context, "Centrado en coordenadas GPS actuales", Toast.LENGTH_SHORT).show()
        }

        // Full-screen Map Button Listener
        btnFullscreenMap.setOnClickListener {
            val intent = Intent(context, LocationPickerActivity::class.java).apply {
                putExtra(LocationPickerActivity.EXTRA_INITIAL_LAT, currentLat)
                putExtra(LocationPickerActivity.EXTRA_INITIAL_LNG, currentLng)
            }
            launcher.launch(intent)
        }

        // Apply initial resolution without redrawing map yet
        applyLocation(currentLat, currentLng, null, null, updateMap = false)
    }

    /**
     * Handles returning from `LocationPickerActivity` in the activity result callback.
     */
    fun handleActivityResult(data: Intent) {
        val lat = data.getDoubleExtra(LocationPickerActivity.EXTRA_RESULT_LAT, currentLat)
        val lng = data.getDoubleExtra(LocationPickerActivity.EXTRA_RESULT_LNG, currentLng)
        val vereda = data.getStringExtra(LocationPickerActivity.EXTRA_RESULT_VEREDA)
        val address = data.getStringExtra(LocationPickerActivity.EXTRA_RESULT_ADDRESS)

        applyLocation(lat, lng, vereda, address, updateMap = true)
    }

    /**
     * Sets coordinates, updates pin, and synchronizes vereda and address fields.
     */
    fun applyLocation(
        lat: Double,
        lng: Double,
        forcedVereda: String?,
        forcedAddress: String?,
        updateMap: Boolean
    ) {
        currentLat = lat
        currentLng = lng
        tvCoords.text = String.format(Locale.US, "Lat: %.4f, Lng: %.4f", lat, lng)

        if (updateMap) {
            OpenStreetMapHelper.updatePinLocation(mapWebView, lat, lng)
        }

        if (forcedVereda != null && forcedAddress != null) {
            currentVereda = forcedVereda
            currentAddress = forcedAddress
            syncFields(forcedVereda, forcedAddress)
            onLocationChangedCallback?.invoke(currentLat, currentLng, currentVereda, currentAddress)
        } else {
            ZipaquiraGeoHelper.resolveLocationAsync(lat, lng) { geoResult ->
                currentVereda = geoResult.vereda
                currentAddress = geoResult.address
                syncFields(geoResult.vereda, geoResult.address)
                onLocationChangedCallback?.invoke(currentLat, currentLng, currentVereda, currentAddress)
            }
        }
    }

    private fun syncFields(vereda: String, address: String) {
        spinnerVereda?.let { spinner ->
            val index = Microdataset.OFFICIAL_VEREDAS.indexOfFirst {
                it.equals(vereda, ignoreCase = true)
            }
            if (index >= 0) {
                spinner.setSelection(index)
            }
        }

        tvDetectedVereda?.text = "Territorio detectado: $vereda"
        etAddress?.setText(address)
    }

    fun getDeviceGpsLocation(): Pair<Double, Double> {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                val loc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if (loc != null && loc.latitude != 0.0) {
                    return Pair(loc.latitude, loc.longitude)
                }
            }
        } catch (e: Exception) {
            // Fallback to default
        }
        return Pair(ZipaquiraGeoHelper.DEFAULT_LAT, ZipaquiraGeoHelper.DEFAULT_LNG)
    }
}
