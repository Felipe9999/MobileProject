package com.alcaldia.censoanimal.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.AnimalRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private var recordId: String? = null
    private var animalRecord: AnimalRecord? = null
    private var isOfficialPrivacyMode = true

    // UI elements
    private lateinit var btnDetailBack: ImageButton
    private lateinit var btnDetailShowQr: ImageButton
    private lateinit var tvDetailHeaderName: TextView

    // Privacy Bar
    private lateinit var ivPrivacyIcon: ImageView
    private lateinit var tvPrivacyModeLabel: TextView
    private lateinit var btnTogglePrivacy: Button

    // Header Card
    private lateinit var tvDetailEmoji: TextView
    private lateinit var tvDetailName: TextView
    private lateinit var tvDetailStateBadge: TextView
    private lateinit var tvDetailBreedAndSex: TextView
    private lateinit var tvDetailColor: TextView
    private lateinit var tvDetailMicrochip: TextView

    // Sanitary Card
    private lateinit var tvDetailSterilizationStatus: TextView
    private lateinit var btnToggleSterilization: Button
    private lateinit var tvDetailRabiesStatus: TextView
    private lateinit var btnRenewVaccine: Button

    // Owner Card
    private lateinit var tvDetailOwnerName: TextView
    private lateinit var tvDetailOwnerDoc: TextView
    private lateinit var tvDetailOwnerPhone: TextView

    // Location Card
    private lateinit var tvDetailVereda: TextView
    private lateinit var tvDetailFarmAddress: TextView
    private lateinit var tvDetailGpsCoords: TextView

    // Decease Action
    private lateinit var btnRegisterDecease: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recordId = intent.getStringExtra("RECORD_ID")
        animalRecord = Microdataset.findRecordById(recordId ?: "CEN-2026-001")

        if (animalRecord == null) {
            Toast.makeText(this, "Registro no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bindViews()
        setupListeners()
        populateData()
    }

    private fun bindViews() {
        btnDetailBack = findViewById(R.id.btnDetailBack)
        btnDetailShowQr = findViewById(R.id.btnDetailShowQr)
        tvDetailHeaderName = findViewById(R.id.tvDetailHeaderName)

        ivPrivacyIcon = findViewById(R.id.ivPrivacyIcon)
        tvPrivacyModeLabel = findViewById(R.id.tvPrivacyModeLabel)
        btnTogglePrivacy = findViewById(R.id.btnTogglePrivacy)

        tvDetailEmoji = findViewById(R.id.tvDetailEmoji)
        tvDetailName = findViewById(R.id.tvDetailName)
        tvDetailStateBadge = findViewById(R.id.tvDetailStateBadge)
        tvDetailBreedAndSex = findViewById(R.id.tvDetailBreedAndSex)
        tvDetailColor = findViewById(R.id.tvDetailColor)
        tvDetailMicrochip = findViewById(R.id.tvDetailMicrochip)

        tvDetailSterilizationStatus = findViewById(R.id.tvDetailSterilizationStatus)
        btnToggleSterilization = findViewById(R.id.btnToggleSterilization)
        tvDetailRabiesStatus = findViewById(R.id.tvDetailRabiesStatus)
        btnRenewVaccine = findViewById(R.id.btnRenewVaccine)

        tvDetailOwnerName = findViewById(R.id.tvDetailOwnerName)
        tvDetailOwnerDoc = findViewById(R.id.tvDetailOwnerDoc)
        tvDetailOwnerPhone = findViewById(R.id.tvDetailOwnerPhone)

        tvDetailVereda = findViewById(R.id.tvDetailVereda)
        tvDetailFarmAddress = findViewById(R.id.tvDetailFarmAddress)
        tvDetailGpsCoords = findViewById(R.id.tvDetailGpsCoords)

        btnRegisterDecease = findViewById(R.id.btnRegisterDecease)
    }

    private fun setupListeners() {
        btnDetailBack.setOnClickListener { finish() }

        btnDetailShowQr.setOnClickListener { showQrDialog() }

        btnTogglePrivacy.setOnClickListener {
            isOfficialPrivacyMode = !isOfficialPrivacyMode
            updatePrivacyUI()
        }

        btnToggleSterilization.setOnClickListener {
            val item = animalRecord ?: return@setOnClickListener
            val nextState = !item.esterilizado
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            Microdataset.toggleSterilization(item.registro_id, nextState, today)
            Toast.makeText(this, "Estado de esterilización actualizado", Toast.LENGTH_SHORT).show()
            populateData()
        }

        btnRenewVaccine.setOnClickListener {
            val item = animalRecord ?: return@setOnClickListener
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            Microdataset.updateVaccineDate(item.registro_id, today)
            Toast.makeText(this, "Vacuna antirrábica renovada al día de hoy", Toast.LENGTH_SHORT).show()
            populateData()
        }

        btnRegisterDecease.setOnClickListener {
            showDeathDialog()
        }
    }

    private fun populateData() {
        val record = animalRecord ?: return

        tvDetailHeaderName.text = "Ficha: ${record.animal_nombre}"

        val isDog = record.especie.equals("Perro", ignoreCase = true)
        tvDetailEmoji.text = if (isDog) "🐶" else "🐱"
        tvDetailName.text = record.animal_nombre

        // State Badge
        if (record.estado_animal.equals("Fallecido", ignoreCase = true)) {
            tvDetailStateBadge.text = "Fallecido"
            tvDetailStateBadge.setBackgroundResource(R.drawable.bg_pill_rose)
            tvDetailStateBadge.setTextColor(ContextCompat.getColor(this, R.color.rose_700))
            btnRegisterDecease.visibility = View.GONE
        } else {
            tvDetailStateBadge.text = "Activo"
            tvDetailStateBadge.setBackgroundResource(R.drawable.bg_pill_emerald)
            tvDetailStateBadge.setTextColor(ContextCompat.getColor(this, R.color.emerald_700))
            btnRegisterDecease.visibility = View.VISIBLE
        }

        tvDetailBreedAndSex.text = "${record.especie} • ${record.raza} • ${record.sexo} • ${record.edad_meses} meses"
        tvDetailColor.text = "Color: ${record.color}"
        tvDetailMicrochip.text = record.microchip ?: "Sin Microchip Registrado"

        // Sterilization
        if (record.esterilizado) {
            tvDetailSterilizationStatus.text = "Esterilizado: Sí (${record.fecha_esterilizacion ?: "Registrado"})"
        } else {
            tvDetailSterilizationStatus.text = "Esterilizado: No (Pendiente jornada)"
        }

        // Vaccine
        tvDetailRabiesStatus.text = "Vacuna Antirrábica: ${record.fecha_vacuna_rabia} (${record.lote_vacuna ?: "Lote estándar"})"

        // Location
        tvDetailVereda.text = "Vereda: ${record.territorio}"
        tvDetailFarmAddress.text = "Predio / Finca: ${record.direccion_finca}"
        tvDetailGpsCoords.text = "GPS WGS84: ${record.latitud}, ${record.longitud}"

        updatePrivacyUI()
    }

    private fun updatePrivacyUI() {
        val record = animalRecord ?: return

        if (isOfficialPrivacyMode) {
            ivPrivacyIcon.setImageResource(R.drawable.ic_lock)
            tvPrivacyModeLabel.text = "Modo Oficial • Datos de contacto visibles"
            btnTogglePrivacy.text = "Cambiar a Ciudadano"

            tvDetailOwnerName.text = "Nombre: ${record.responsable_nombre}"
            tvDetailOwnerDoc.text = "Documento: ${record.documento_tipo} ${record.documento_numero}"
            tvDetailOwnerPhone.text = "Teléfono: ${record.telefono}"
        } else {
            ivPrivacyIcon.setImageResource(R.drawable.ic_shield_check)
            tvPrivacyModeLabel.text = "Modo Ciudadano • Habeas Data Protegido"
            btnTogglePrivacy.text = "Cambiar a Oficial"

            tvDetailOwnerName.text = "Nombre: C***** R********"
            tvDetailOwnerDoc.text = "Documento: CC ***-***-746"
            tvDetailOwnerPhone.text = "Teléfono: 310 *** **02"
        }
    }

    private fun showQrDialog() {
        val record = animalRecord ?: return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_qr_code, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val tvQrRecordId = dialogView.findViewById<TextView>(R.id.tvQrRecordId)
        val btnDismissQr = dialogView.findViewById<Button>(R.id.btnDismissQr)

        tvQrRecordId.text = "${record.registro_id} • ${record.animal_nombre}"
        btnDismissQr.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun showDeathDialog() {
        val record = animalRecord ?: return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_death_registration, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val etDeathDate = dialogView.findViewById<EditText>(R.id.etDeathDate)
        val spinnerDeathReason = dialogView.findViewById<Spinner>(R.id.spinnerDeathReason)
        val btnCancelDeath = dialogView.findViewById<Button>(R.id.btnCancelDeath)
        val btnConfirmDeath = dialogView.findViewById<Button>(R.id.btnConfirmDeath)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        etDeathDate.setText(today)

        val reasons = listOf("Causa Natural / Senectud", "Enfermedad infecciosa", "Accidente / Trauma", "Eutanasia humanitaria", "Desconocido")
        spinnerDeathReason.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, reasons)

        btnCancelDeath.setOnClickListener { dialog.dismiss() }

        btnConfirmDeath.setOnClickListener {
            val deathDate = etDeathDate.text.toString().trim().ifEmpty { today }
            val reason = spinnerDeathReason.selectedItem.toString()

            Microdataset.registerDeath(record.registro_id, deathDate, reason)
            Toast.makeText(this, "Fallecimiento registrado con éxito", Toast.LENGTH_LONG).show()
            dialog.dismiss()
            populateData()
        }

        dialog.show()
    }
}
