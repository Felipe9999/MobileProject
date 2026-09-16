package com.alcaldia.censoanimal.data

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Geographic helper and offline-first reverse-geocoding engine for Zipaquirá.
 * Matches any geographic coordinate to the official controlled territory catalog
 * (14 veredas + casco urbano) and generates an accurate municipal address.
 */
object ZipaquiraGeoHelper {

    data class TerritoryZone(
        val veredaName: String,
        val lat: Double,
        val lng: Double,
        val defaultAddress: String,
        val sectorName: String
    )

    data class GeoResult(
        val vereda: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val isExactNominatim: Boolean = false
    )

    // Central coordinates and reference points for Zipaquirá's 14 official veredas + Casco Urbano
    val TERRITORIES = listOf(
        TerritoryZone("Vereda San Jorge", 4.9842, -73.9562, "Vereda San Jorge, Sector Finca El Porvenir", "San Jorge"),
        TerritoryZone("Vereda El Rosal", 4.9920, -73.9610, "Vereda El Rosal, Sector San Antonio", "El Rosal"),
        TerritoryZone("Vereda Barroblanco", 5.0080, -73.9820, "Vereda Barroblanco, Finca La Esperanza", "Barroblanco"),
        TerritoryZone("Vereda Santa Librada", 5.0300, -73.9550, "Vereda Santa Librada, Sector Las Mercedes", "Santa Librada"),
        TerritoryZone("Vereda La Esperanza", 4.9800, -74.0150, "Vereda La Esperanza, Camino Real Lote 3", "La Esperanza"),
        TerritoryZone("Vereda San Benito", 5.0150, -74.0350, "Vereda San Benito, Sector Quebrada", "San Benito"),
        TerritoryZone("Vereda Ventalarga", 5.0550, -74.0100, "Vereda Ventalarga, Vía Represa del Neusa", "Ventalarga"),
        TerritoryZone("Vereda Portachuelo", 5.0450, -74.0200, "Vereda Portachuelo, Sector Alto", "Portachuelo"),
        TerritoryZone("Vereda Río Frío", 5.0380, -73.9700, "Vereda Río Frío, Sector El Trébol", "Río Frío"),
        TerritoryZone("Vereda El Tunal", 4.9750, -73.9850, "Vereda El Tunal, Predio Los Sauces", "El Tunal"),
        TerritoryZone("Vereda Páramo de Guerrero", 5.0600, -74.0500, "Vereda Páramo de Guerrero, Zona Alta", "Páramo de Guerrero"),
        TerritoryZone("Vereda Barandillas", 5.0120, -73.9740, "Vereda Barandillas, Vía Principal Lote 8", "Barandillas"),
        TerritoryZone("Vereda La Granja", 5.0200, -73.9900, "Vereda La Granja, Sector Finca Santa Inés", "La Granja"),
        TerritoryZone("Vereda Empalizado", 5.0400, -74.0400, "Vereda Empalizado, Camino Vecinal Lote 2", "Empalizado"),
        TerritoryZone("Casco Urbano / Barrios", 5.0260, -74.0040, "Casco Urbano, Centro Histórico / Cra 7", "Casco Urbano")
    )

    const val DEFAULT_LAT = 4.9842
    const val DEFAULT_LNG = -73.9562

    /**
     * Calculates distance between two coordinates in meters using Haversine formula.
     */
    fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    /**
     * Instantly resolves the closest official vereda and suggested address offline.
     */
    fun resolveOfflineLocation(lat: Double, lng: Double): GeoResult {
        var closestZone = TERRITORIES[0]
        var minDistance = Double.MAX_VALUE

        for (zone in TERRITORIES) {
            val dist = distanceMeters(lat, lng, zone.lat, zone.lng)
            if (dist < minDistance) {
                minDistance = dist
                closestZone = zone
            }
        }

        // Generate intelligent address description based on distance to territory center
        val address = if (minDistance < 600) {
            closestZone.defaultAddress
        } else {
            val distKm = String.format(Locale.US, "%.1f", minDistance / 1000.0)
            "${closestZone.veredaName}, Sector rural a ${distKm} km del centro veredal"
        }

        return GeoResult(
            vereda = closestZone.veredaName,
            address = address,
            latitude = lat,
            longitude = lng,
            isExactNominatim = false
        )
    }

    /**
     * Asynchronously attempts OpenStreetMap Nominatim reverse-geocoding,
     * falling back gracefully to the offline catalog resolution.
     */
    fun resolveLocationAsync(
        lat: Double,
        lng: Double,
        onResult: (GeoResult) -> Unit
    ) {
        // Immediate offline resolution first
        val offlineResult = resolveOfflineLocation(lat, lng)

        thread {
            var finalResult = offlineResult
            try {
                val urlString = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lng&zoom=17&addressdetails=1"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 2500
                connection.readTimeout = 2500
                connection.setRequestProperty("User-Agent", "ZipaquiraCensoAnimalApp/1.0 (Alcaldia de Zipaquira)")

                if (connection.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val json = JSONObject(response.toString())
                    val addressObj = json.optJSONObject("address")
                    if (addressObj != null) {
                        val road = addressObj.optString("road", "")
                        val suburb = addressObj.optString("suburb", "")
                        val hamlet = addressObj.optString("hamlet", "")
                        val neighbourhood = addressObj.optString("neighbourhood", "")

                        val placePart = when {
                            road.isNotEmpty() && hamlet.isNotEmpty() -> "$hamlet, $road"
                            road.isNotEmpty() -> road
                            hamlet.isNotEmpty() -> hamlet
                            suburb.isNotEmpty() -> suburb
                            neighbourhood.isNotEmpty() -> neighbourhood
                            else -> ""
                        }

                        if (placePart.isNotEmpty()) {
                            val combinedAddress = "${offlineResult.vereda}, $placePart"
                            finalResult = GeoResult(
                                vereda = offlineResult.vereda,
                                address = combinedAddress,
                                latitude = lat,
                                longitude = lng,
                                isExactNominatim = true
                            )
                        }
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                // Keep offlineResult
            }

            Handler(Looper.getMainLooper()).post {
                onResult(finalResult)
            }
        }
    }
}
