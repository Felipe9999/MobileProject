package com.alcaldia.censoanimal.ui

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.LostFoundAlert
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportLostAnimalActivity : AppCompatActivity() {

    private lateinit var btnBackReportLost: ImageButton
    private lateinit var btnAccountReportLost: LinearLayout
    private lateinit var tvAccountReportLostLabel: TextView

    private lateinit var etLostAnimalName: EditText
    private lateinit var spinnerLostSpecies: Spinner
    private lateinit var spinnerLostSex: Spinner
    private lateinit var etLostBreed: EditText
    private lateinit var spinnerLostColor: Spinner
    private lateinit var etLostMicrochip: EditText
    private lateinit var etLostDistinctiveMarks: EditText
    private lateinit var etLostDate: EditText

    private lateinit var etLostContactName: EditText
    private lateinit var etLostContactPhone: EditText
    private lateinit var etLostDescription: EditText

    private lateinit var btnSubmitReportLost: Button
    private lateinit var btnCancelReportLost: Button

    private lateinit var locationHelper: FormLocationHelper

    private val locationPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            locationHelper.handleActivityResult(result.data!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_lost_animal)

        initViews()
        setupSpinners()
        setupLocationPicker()
        setupListeners()
        populateDefaults()
    }

    private fun initViews() {
        btnBackReportLost = findViewById(R.id.btnBackReportLost)
        btnAccountReportLost = findViewById(R.id.btnAccountReportLost)
        tvAccountReportLostLabel = findViewById(R.id.tvAccountReportLostLabel)

        etLostAnimalName = findViewById(R.id.etLostAnimalName)
        spinnerLostSpecies = findViewById(R.id.spinnerLostSpecies)
        spinnerLostSex = findViewById(R.id.spinnerLostSex)
        etLostBreed = findViewById(R.id.etLostBreed)
        spinnerLostColor = findViewById(R.id.spinnerLostColor)
        etLostMicrochip = findViewById(R.id.etLostMicrochip)
        etLostDistinctiveMarks = findViewById(R.id.etLostDistinctiveMarks)
        etLostDate = findViewById(R.id.etLostDate)

        etLostContactName = findViewById(R.id.etLostContactName)
        etLostContactPhone = findViewById(R.id.etLostContactPhone)
        etLostDescription = findViewById(R.id.etLostDescription)

        btnSubmitReportLost = findViewById(R.id.btnSubmitReportLost)
        btnCancelReportLost = findViewById(R.id.btnCancelReportLost)

        TopBarAccountHelper.setupAccountButton(this, btnAccountReportLost, tvAccountReportLostLabel)
    }

    private fun setupSpinners() {
        spinnerLostSpecies.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Perro", "Gato", "Conejo / Pequeña Especie", "Otro")
        )

        spinnerLostSex.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Macho", "Hembra", "Desconocido")
        )

        spinnerLostColor.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Microdataset.COLOR_OPTIONS
        )
    }

    private fun setupLocationPicker() {
        val locationContainer = findViewById<android.view.View>(R.id.locationPickerSectionLost)
        locationHelper = FormLocationHelper.bind(
            container = locationContainer,
            launcher = locationPickerLauncher,
            customTitle = "2. Lugar de Pérdida / Última Vez Visto *",
            customSubtitle = "Mueve el pin o pulsa GPS. La vereda y dirección se completan automáticamente."
        )
    }

    private fun populateDefaults() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        etLostDate.setText(today)

        val profile = AppSessionManager.currentProfile
        if (profile.name.isNotEmpty() && profile.documentNumber != "No Registrado") {
            etLostContactName.setText(profile.name)
            etLostContactPhone.setText(profile.phone.ifEmpty { "3100000000" })
        }
    }

    private fun setupListeners() {
        btnBackReportLost.setOnClickListener { finish() }
        btnCancelReportLost.setOnClickListener { finish() }

        btnSubmitReportLost.setOnClickListener {
            submitReport()
        }
    }

    private fun submitReport() {
        val animalName = etLostAnimalName.text.toString().trim()
        val breed = etLostBreed.text.toString().trim()
        val contactName = etLostContactName.text.toString().trim()
        val contactPhone = etLostContactPhone.text.toString().trim()
        val dateStr = etLostDate.text.toString().trim()
        val microchip = etLostMicrochip.text.toString().trim()
        val marks = etLostDistinctiveMarks.text.toString().trim()
        val narrative = etLostDescription.text.toString().trim()

        if (animalName.isEmpty()) {
            etLostAnimalName.error = "El nombre del animal de compañía es obligatorio"
            etLostAnimalName.requestFocus()
            return
        }

        if (breed.isEmpty()) {
            etLostBreed.error = "Ingresa la raza o mestizaje"
            etLostBreed.requestFocus()
            return
        }

        if (contactName.isEmpty()) {
            etLostContactName.error = "Ingresa el nombre del guardián o contacto"
            etLostContactName.requestFocus()
            return
        }

        if (contactPhone.isEmpty()) {
            etLostContactPhone.error = "Ingresa el teléfono de contacto"
            etLostContactPhone.requestFocus()
            return
        }

        val species = spinnerLostSpecies.selectedItem.toString()
        val sex = spinnerLostSex.selectedItem.toString()
        val color = spinnerLostColor.selectedItem.toString()
        val vereda = locationHelper.currentVereda
        val address = locationHelper.currentAddress

        // Automated cross-matching with existing records & alerts
        var potentialMatchId: String? = null
        if (microchip.isNotEmpty()) {
            val recordMatch = Microdataset.getAllRecords().find { it.microchip == microchip }
            if (recordMatch != null) {
                potentialMatchId = recordMatch.registro_id
            }
        }
        if (potentialMatchId == null) {
            val foundAlertMatch = Microdataset.getAllLostFoundAlerts().find {
                it.tipo == "HALLADO" && it.especie.equals(species, ignoreCase = true) &&
                        (it.raza.contains(breed, ignoreCase = true) || breed.contains(it.raza, ignoreCase = true))
            }
            if (foundAlertMatch != null) {
                potentialMatchId = foundAlertMatch.id
            }
        }

        val fullDescription = buildString {
            if (marks.isNotEmpty()) {
                append("Señas: ").append(marks).append(". ")
            }
            if (narrative.isNotEmpty()) {
                append(narrative)
            } else {
                append("Extraviado en ").append(vereda).append(", sector ").append(address).append(".")
            }
        }

        val alertId = "ALR-2026-${(100..999).random()}"
        val newAlert = LostFoundAlert(
            id = alertId,
            tipo = "PERDIDO",
            especie = species,
            nombre = animalName,
            raza = "$breed ($sex)",
            color = color,
            vereda = vereda,
            fecha_evento = dateStr.ifEmpty { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) },
            microchip = microchip.ifEmpty { null },
            contacto_nombre = contactName,
            contacto_telefono = contactPhone,
            estado = if (potentialMatchId != null) "Candidato Coincidencia Detectado" else "Activo / En Búsqueda",
            descripcion = fullDescription,
            posible_coincidencia_registro_id = potentialMatchId,
            dias_custodia_albergue = 0,
            validacion_manual_aprobada = false,
            direccion_referencia = address,
            latitud = locationHelper.currentLat,
            longitud = locationHelper.currentLng
        )

        Microdataset.addLostFoundAlert(newAlert)
        Toast.makeText(this, "Alerta de pérdida $alertId publicada exitosamente", Toast.LENGTH_LONG).show()
        setResult(Activity.RESULT_OK)
        finish()
    }
}
