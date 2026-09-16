package com.alcaldia.censoanimal.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.ZipaquiraGeoHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class LocationPickerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_INITIAL_LAT = "EXTRA_INITIAL_LAT"
        const val EXTRA_INITIAL_LNG = "EXTRA_INITIAL_LNG"

        const val EXTRA_RESULT_LAT = "EXTRA_RESULT_LAT"
        const val EXTRA_RESULT_LNG = "EXTRA_RESULT_LNG"
        const val EXTRA_RESULT_VEREDA = "EXTRA_RESULT_VEREDA"
        const val EXTRA_RESULT_ADDRESS = "EXTRA_RESULT_ADDRESS"
    }

    private lateinit var webViewOpenStreetMap: WebView
    private lateinit var tvPickerVeredaBadge: TextView
    private lateinit var tvPickerCoords: TextView
    private lateinit var tvPickerAddress: TextView
    private lateinit var btnConfirmLocation: Button
    private lateinit var layoutVeredaChips: LinearLayout
    private lateinit var fabCurrentLocation: FloatingActionButton
    private lateinit var btnHeaderGps: Button
    private lateinit var btnBackMapPicker: ImageButton

    private var currentLat = ZipaquiraGeoHelper.DEFAULT_LAT
    private var currentLng = ZipaquiraGeoHelper.DEFAULT_LNG
    private var selectedVereda = "Vereda San Jorge"
    private var selectedAddress = "Vereda San Jorge, Sector Finca El Porvenir"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        // Read intent extras if provided, otherwise default to GPS / Zipaquirá
        val initialLat = intent.getDoubleExtra(EXTRA_INITIAL_LAT, 0.0)
        val initialLng = intent.getDoubleExtra(EXTRA_INITIAL_LNG, 0.0)

        if (initialLat != 0.0 && initialLng != 0.0) {
            currentLat = initialLat
            currentLng = initialLng
        } else {
            val gpsLocation = getDeviceGpsLocation()
            currentLat = gpsLocation.first
            currentLng = gpsLocation.second
        }

        initViews()
        setupChips()
        setupListeners()
        setupMap()

        // Resolve initial location
        updateLocationData(currentLat, currentLng)
    }

    private fun initViews() {
        webViewOpenStreetMap = findViewById(R.id.webViewOpenStreetMap)
        tvPickerVeredaBadge = findViewById(R.id.tvPickerVeredaBadge)
        tvPickerCoords = findViewById(R.id.tvPickerCoords)
        tvPickerAddress = findViewById(R.id.tvPickerAddress)
        btnConfirmLocation = findViewById(R.id.btnConfirmLocation)
        layoutVeredaChips = findViewById(R.id.layoutVeredaChips)
        fabCurrentLocation = findViewById(R.id.fabCurrentLocation)
        btnHeaderGps = findViewById(R.id.btnHeaderGps)
        btnBackMapPicker = findViewById(R.id.btnBackMapPicker)
    }

    private fun setupListeners() {
        btnBackMapPicker.setOnClickListener {
            finish()
        }

        btnHeaderGps.setOnClickListener {
            snapToGpsLocation()
        }

        fabCurrentLocation.setOnClickListener {
            snapToGpsLocation()
        }

        btnConfirmLocation.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(EXTRA_RESULT_LAT, currentLat)
                putExtra(EXTRA_RESULT_LNG, currentLng)
                putExtra(EXTRA_RESULT_VEREDA, selectedVereda)
                putExtra(EXTRA_RESULT_ADDRESS, selectedAddress)
            }
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Ubicación seleccionada: $selectedVereda", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupMap() {
        OpenStreetMapHelper.configureWebView(
            webView = webViewOpenStreetMap,
            initialLat = currentLat,
            initialLng = currentLng,
            isInteractive = true,
            listener = object : OpenStreetMapHelper.OnLocationChangeListener {
                override fun onLocationChanged(lat: Double, lng: Double) {
                    currentLat = lat
                    currentLng = lng
                    updateLocationData(lat, lng)
                }
            }
        )
    }

    private fun setupChips() {
        layoutVeredaChips.removeAllViews()

        for (zone in ZipaquiraGeoHelper.TERRITORIES) {
            val chip = TextView(this).apply {
                text = zone.sectorName
                textSize = 11f
                setPadding(28, 14, 28, 14)
                setBackgroundResource(R.drawable.bg_pill_slate)
                setTextColor(ContextCompat.getColor(context, R.color.slate_700))

                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(6, 4, 6, 4)
                layoutParams = params

                setOnClickListener {
                    currentLat = zone.lat
                    currentLng = zone.lng
                    OpenStreetMapHelper.updatePinLocation(webViewOpenStreetMap, zone.lat, zone.lng)
                    updateLocationData(zone.lat, zone.lng)
                }
            }
            layoutVeredaChips.addView(chip)
        }
    }

    private fun snapToGpsLocation() {
        val (gpsLat, gpsLng) = getDeviceGpsLocation()
        currentLat = gpsLat
        currentLng = gpsLng
        OpenStreetMapHelper.updatePinLocation(webViewOpenStreetMap, gpsLat, gpsLng)
        updateLocationData(gpsLat, gpsLng)
        Toast.makeText(this, "Centrado en coordenadas GPS", Toast.LENGTH_SHORT).show()
    }

    private fun getDeviceGpsLocation(): Pair<Double, Double> {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val fineGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val coarseGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                val loc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if (loc != null && loc.latitude != 0.0) {
                    return Pair(loc.latitude, loc.longitude)
                }
            }
        } catch (e: Exception) {
            // Fallback
        }
        return Pair(ZipaquiraGeoHelper.DEFAULT_LAT, ZipaquiraGeoHelper.DEFAULT_LNG)
    }

    private fun updateLocationData(lat: Double, lng: Double) {
        tvPickerCoords.text = String.format(Locale.US, "Lat: %.4f • Lng: %.4f", lat, lng)

        ZipaquiraGeoHelper.resolveLocationAsync(lat, lng) { geoResult ->
            selectedVereda = geoResult.vereda
            selectedAddress = geoResult.address

            tvPickerVeredaBadge.text = geoResult.vereda
            tvPickerAddress.text = geoResult.address
        }
    }
}
