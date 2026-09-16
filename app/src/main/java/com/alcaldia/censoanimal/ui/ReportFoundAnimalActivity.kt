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

class ReportFoundAnimalActivity : AppCompatActivity() {

    private lateinit var btnBackReportFound: ImageButton
    private lateinit var btnAccountReportFound: LinearLayout
    private lateinit var tvAccountReportFoundLabel: TextView

    private lateinit var spinnerFoundCustodyState: Spinner
    private lateinit var etFoundCustodyDays: EditText

    private lateinit var etFoundTentativeName: EditText
    private lateinit var spinnerFoundSpecies: Spinner
    private lateinit var spinnerFoundSex: Spinner
    private lateinit var etFoundBreed: EditText
    private lateinit var spinnerFoundColor: Spinner
    private lateinit var etFoundMicrochip: EditText
    private lateinit var etFoundAccessories: EditText
    private lateinit var etFoundDate: EditText

    private lateinit var etFoundContactName: EditText
    private lateinit var etFoundContactPhone: EditText
    private lateinit var etFoundDescription: EditText

    private lateinit var btnSubmitReportFound: Button
    private lateinit var btnCancelReportFound: Button

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
        setContentView(R.layout.activity_report_found_animal)

        initViews()
        setupSpinners()
        setupLocationPicker()
        setupListeners()
        populateDefaults()
    }

    private fun initViews() {
        btnBackReportFound = findViewById(R.id.btnBackReportFound)
        btnAccountReportFound = findViewById(R.id.btnAccountReportFound)
        tvAccountReportFoundLabel = findViewById(R.id.tvAccountReportFoundLabel)

        spinnerFoundCustodyState = findViewById(R.id.spinnerFoundCustodyState)
        etFoundCustodyDays = findViewById(R.id.etFoundCustodyDays)

        etFoundTentativeName = findViewById(R.id.etFoundTentativeName)
        spinnerFoundSpecies = findViewById(R.id.spinnerFoundSpecies)
        spinnerFoundSex = findViewById(R.id.spinnerFoundSex)
        etFoundBreed = findViewById(R.id.etFoundBreed)
        spinnerFoundColor = findViewById(R.id.spinnerFoundColor)
        etFoundMicrochip = findViewById(R.id.etFoundMicrochip)
        etFoundAccessories = findViewById(R.id.etFoundAccessories)
        etFoundDate = findViewById(R.id.etFoundDate)

        etFoundContactName = findViewById(R.id.etFoundContactName)
        etFoundContactPhone = findViewById(R.id.etFoundContactPhone)
        etFoundDescription = findViewById(R.id.etFoundDescription)

        btnSubmitReportFound = findViewById(R.id.btnSubmitReportFound)
        btnCancelReportFound = findViewById(R.id.btnCancelReportFound)

        TopBarAccountHelper.setupAccountButton(this, btnAccountReportFound, tvAccountReportFoundLabel)
    }

    private fun setupSpinners() {
        spinnerFoundCustodyState.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf(
                "En vía pública / libre (Requiere rescate)",
                "En custodia temporal comunitaria / casa de paso",
                "Ingresado al Albergue Municipal de Zipaquirá"
            )
        )

        spinnerFoundSpecies.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Perro", "Gato", "Conejo / Pequeña Especie", "Otro")
        )

        spinnerFoundSex.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Macho", "Hembra", "Desconocido")
        )

        spinnerFoundColor.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Microdataset.COLOR_OPTIONS
        )
    }

    private fun setupLocationPicker() {
        val locationContainer = findViewById<android.view.View>(R.id.locationPickerSectionFound)
        locationHelper = FormLocationHelper.bind(
            container = locationContainer,
            launcher = locationPickerLauncher,
            customTitle = "3. Lugar Exacto del Hallazgo o Custodia *",
            customSubtitle = "Ubica en el mapa el punto de hallazgo. La vereda y dirección se autocompletan."
        )
    }

    private fun populateDefaults() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        etFoundDate.setText(today)
        etFoundCustodyDays.setText("0")

        val profile = AppSessionManager.currentProfile
        if (profile.name.isNotEmpty() && profile.documentNumber != "No Registrado") {
            etFoundContactName.setText(profile.name)
            etFoundContactPhone.setText(profile.phone.ifEmpty { "3200000000" })
        } else {
            etFoundContactName.setText("Ciudadano Zipaquirá")
            etFoundContactPhone.setText("3200000000")
        }
    }

    private fun setupListeners() {
        btnBackReportFound.setOnClickListener { finish() }
        btnCancelReportFound.setOnClickListener { finish() }

        btnSubmitReportFound.setOnClickListener {
            submitReport()
        }
    }

    private fun submitReport() {
        val breed = etFoundBreed.text.toString().trim()
        val contactName = etFoundContactName.text.toString().trim()
        val contactPhone = etFoundContactPhone.text.toString().trim()
        val dateStr = etFoundDate.text.toString().trim()
        val tentativeName = etFoundTentativeName.text.toString().trim()
        val microchip = etFoundMicrochip.text.toString().trim()
        val accessories = etFoundAccessories.text.toString().trim()
        val narrative = etFoundDescription.text.toString().trim()
        val custodyDays = etFoundCustodyDays.text.toString().trim().toIntOrNull() ?: 0

        if (breed.isEmpty()) {
            etFoundBreed.error = "Ingresa la raza o fenotipo aparente"
            etFoundBreed.requestFocus()
            return
        }

        if (contactName.isEmpty()) {
            etFoundContactName.error = "Ingresa el nombre del rescatista o informante"
            etFoundContactName.requestFocus()
            return
        }

        if (contactPhone.isEmpty()) {
            etFoundContactPhone.error = "Ingresa el teléfono de contacto"
            etFoundContactPhone.requestFocus()
            return
        }

        val species = spinnerFoundSpecies.selectedItem.toString()
        val sex = spinnerFoundSex.selectedItem.toString()
        val color = spinnerFoundColor.selectedItem.toString()
        val custodyState = spinnerFoundCustodyState.selectedItem.toString()
        val vereda = locationHelper.currentVereda
        val address = locationHelper.currentAddress

        // Automated cross-matching with existing PERDIDO alerts & registered animals
        var potentialMatchId: String? = null
        if (microchip.isNotEmpty()) {
            val recordMatch = Microdataset.getAllRecords().find { it.microchip == microchip }
            if (recordMatch != null) {
                potentialMatchId = recordMatch.registro_id
            }
        }
        if (potentialMatchId == null) {
            val lostAlertMatch = Microdataset.getAllLostFoundAlerts().find {
                it.tipo == "PERDIDO" && it.especie.equals(species, ignoreCase = true) &&
                        (it.raza.contains(breed, ignoreCase = true) || breed.contains(it.raza, ignoreCase = true))
            }
            if (lostAlertMatch != null) {
                potentialMatchId = lostAlertMatch.id
            }
        }

        // Check 20-day rule (TRD FR-2.5)
        val alertState = when {
            custodyDays >= 20 || custodyState.contains("Albergue") && custodyDays >= 20 -> {
                "Custodia Municipal (+20 Días - Declarado Abandonado)"
            }
            potentialMatchId != null -> {
                "Candidato Coincidencia Detectado"
            }
            else -> {
                "Activo / En Custodia ($custodyState)"
            }
        }

        val fullDescription = buildString {
            append("Custodia: ").append(custodyState).append(". ")
            if (accessories.isNotEmpty()) {
                append("Accesorios: ").append(accessories).append(". ")
            }
            if (narrative.isNotEmpty()) {
                append(narrative)
            } else {
                append("Hallado en ").append(vereda).append(", sector ").append(address).append(".")
            }
        }

        val alertId = "ALR-2026-${(100..999).random()}"
        val newAlert = LostFoundAlert(
            id = alertId,
            tipo = "HALLADO",
            especie = species,
            nombre = tentativeName.ifEmpty { null },
            raza = "$breed ($sex)",
            color = color,
            vereda = vereda,
            fecha_evento = dateStr.ifEmpty { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) },
            microchip = microchip.ifEmpty { null },
            contacto_nombre = contactName,
            contacto_telefono = contactPhone,
            estado = alertState,
            descripcion = fullDescription,
            posible_coincidencia_registro_id = potentialMatchId,
            dias_custodia_albergue = custodyDays,
            validacion_manual_aprobada = false,
            direccion_referencia = address,
            latitud = locationHelper.currentLat,
            longitud = locationHelper.currentLng
        )

        Microdataset.addLostFoundAlert(newAlert)
        Toast.makeText(this, "Alerta de hallazgo $alertId publicada exitosamente", Toast.LENGTH_LONG).show()
        setResult(Activity.RESULT_OK)
        finish()
    }
}
