package com.alcaldia.censoanimal.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
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
    private lateinit var tvDetailTattooDesc: TextView
    private lateinit var tvDetailRabiesStatus: TextView
    private lateinit var btnRenewVaccine: Button

    // Guardian & Behavior
    private lateinit var tvDetailGuardianTypeChip: TextView
    private lateinit var tvDetailTattooChip: TextView
    private lateinit var tvDetailBehaviorCondition: TextView

    // Clinical History
    private lateinit var btnAddClinicalRecord: Button
    private lateinit var layoutClinicalRecordsContainer: android.widget.LinearLayout

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

        tvDetailGuardianTypeChip = findViewById(R.id.tvDetailGuardianTypeChip)
        tvDetailTattooChip = findViewById(R.id.tvDetailTattooChip)
        tvDetailBehaviorCondition = findViewById(R.id.tvDetailBehaviorCondition)

        tvDetailSterilizationStatus = findViewById(R.id.tvDetailSterilizationStatus)
        btnToggleSterilization = findViewById(R.id.btnToggleSterilization)
        tvDetailTattooDesc = findViewById(R.id.tvDetailTattooDesc)
        tvDetailRabiesStatus = findViewById(R.id.tvDetailRabiesStatus)
        btnRenewVaccine = findViewById(R.id.btnRenewVaccine)

        btnAddClinicalRecord = findViewById(R.id.btnAddClinicalRecord)
        layoutClinicalRecordsContainer = findViewById(R.id.layoutClinicalRecordsContainer)

        tvDetailOwnerName = findViewById(R.id.tvDetailOwnerName)
        tvDetailOwnerDoc = findViewById(R.id.tvDetailOwnerDoc)
        tvDetailOwnerPhone = findViewById(R.id.tvDetailOwnerPhone)

        tvDetailVereda = findViewById(R.id.tvDetailVereda)
        tvDetailFarmAddress = findViewById(R.id.tvDetailFarmAddress)
        tvDetailGpsCoords = findViewById(R.id.tvDetailGpsCoords)

        btnRegisterDecease = findViewById(R.id.btnRegisterDecease)

        val btnDetailAccount = findViewById<android.view.View>(R.id.btnDetailAccount)
        val tvDetailAccountLabel = findViewById<TextView>(R.id.tvDetailAccountLabel)
        TopBarAccountHelper.setupAccountButton(this, btnDetailAccount, tvDetailAccountLabel)
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

        btnAddClinicalRecord.setOnClickListener {
            showAddClinicalRecordDialog()
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

        // Guardian Type & Behavioral chips
        tvDetailGuardianTypeChip.text = record.tipo_guardia ?: "Propietario / Tenedor"
        
        if (record.tatuaje_esterilizacion == true) {
            tvDetailTattooChip.visibility = View.VISIBLE
            tvDetailTattooDesc.visibility = View.VISIBLE
            tvDetailTattooDesc.text = "✔ Tatuaje de esterilización confirmado en inspección"
        } else {
            tvDetailTattooChip.visibility = View.GONE
            tvDetailTattooDesc.visibility = View.GONE
        }

        tvDetailBehaviorCondition.text = "Condición etológica: ${record.condicion_comportamental ?: "Sociable con personas y otros animales"}"

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

        populateClinicalRecords()
        updatePrivacyUI()
    }

    private fun populateClinicalRecords() {
        val record = animalRecord ?: return
        layoutClinicalRecordsContainer.removeAllViews()

        val records = Microdataset.getClinicalRecords(record.registro_id)
        if (records.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "No se registran atenciones clínicas previas para este animal."
                setTextColor(ContextCompat.getColor(context, R.color.slate_500))
                textSize = 12f
                setPadding(0, 8, 0, 8)
            }
            layoutClinicalRecordsContainer.addView(emptyTv)
            return
        }

        records.forEach { item ->
            val cardView = com.google.android.material.card.MaterialCardView(this).apply {
                radius = 12f * resources.displayMetrics.density
                cardElevation = 0f
                strokeWidth = (1 * resources.displayMetrics.density).toInt()
                setStrokeColor(ContextCompat.getColor(context, R.color.slate_200))
                setCardBackgroundColor(ContextCompat.getColor(context, R.color.slate_50))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, (8 * resources.displayMetrics.density).toInt())
                }
                layoutParams = params
            }

            val cardContent = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(
                    (12 * resources.displayMetrics.density).toInt(),
                    (10 * resources.displayMetrics.density).toInt(),
                    (12 * resources.displayMetrics.density).toInt(),
                    (10 * resources.displayMetrics.density).toInt()
                )
            }

            // Row 1: Event type & Date
            val headerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val titleTv = TextView(this).apply {
                text = item.tipo_evento
                setTextColor(ContextCompat.getColor(context, R.color.blue_900))
                textSize = 12f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val dateTv = TextView(this).apply {
                text = item.fecha
                setTextColor(ContextCompat.getColor(context, R.color.slate_500))
                textSize = 10f
            }

            headerRow.addView(titleTv)
            headerRow.addView(dateTv)
            cardContent.addView(headerRow)

            // Professional
            val vetTv = TextView(this).apply {
                text = "Atendido por: ${item.profesional}"
                setTextColor(ContextCompat.getColor(context, R.color.slate_700))
                textSize = 11f
                setPadding(0, 2, 0, 4)
            }
            cardContent.addView(vetTv)

            // Findings
            val findingsTv = TextView(this).apply {
                text = "Hallazgos: ${item.hallazgos}"
                setTextColor(ContextCompat.getColor(context, R.color.slate_600))
                textSize = 11f
            }
            cardContent.addView(findingsTv)

            // Commitment / Follow-up if any
            if (!item.compromiso_correctivo.isNullOrEmpty()) {
                val commitmentTv = TextView(this).apply {
                    val statusText = if (item.cumplido) "✔ Cumplido" else "⏳ Pendiente"
                    text = "Compromiso: ${item.compromiso_correctivo} ($statusText)"
                    setTextColor(ContextCompat.getColor(context, if (item.cumplido) R.color.emerald_800 else R.color.amber_700))
                    textSize = 11f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setPadding(0, 4, 0, 0)
                }
                cardContent.addView(commitmentTv)
            }

            cardView.addView(cardContent)
            layoutClinicalRecordsContainer.addView(cardView)
        }
    }

    private fun showAddClinicalRecordDialog() {
        val record = animalRecord ?: return
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_clinical_record, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val spinnerEventType = dialogView.findViewById<Spinner>(R.id.spinnerClinicalEventType)
        val etVet = dialogView.findViewById<EditText>(R.id.etClinicalVet)
        val etAnamnesis = dialogView.findViewById<EditText>(R.id.etClinicalAnamnesis)
        val etFindings = dialogView.findViewById<EditText>(R.id.etClinicalFindings)
        val etCommitment = dialogView.findViewById<EditText>(R.id.etClinicalCommitment)
        val cbFulfilled = dialogView.findViewById<android.widget.CheckBox>(R.id.cbClinicalFulfilled)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelClinical)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveClinical)

        val eventTypes = listOf(
            "Consulta General / Valoración",
            "Jornada Vacunación Antirrábica",
            "Esterilización Quirúrgica",
            "Desparasitación Interna/Externa",
            "Consulta Canino de Manejo Especial (Ley 1801)",
            "Seguimiento Caso de Maltrato"
        )
        spinnerEventType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, eventTypes)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val vet = etVet.text.toString().trim().ifEmpty { "Dra. Mariana Gómez (M.V.)" }
            val anamnesis = etAnamnesis.text.toString().trim().ifEmpty { "Control veterinario rutinario" }
            val findings = etFindings.text.toString().trim().ifEmpty { "Paciente en condición corporal adecuada, sin alteraciones aparentes." }
            val commitment = etCommitment.text.toString().trim().ifEmpty { null }
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

            val newRecord = com.alcaldia.censoanimal.model.ClinicalRecord(
                id = "CLI-${System.currentTimeMillis() % 10000}",
                registro_id = record.registro_id,
                fecha = today,
                tipo_evento = spinnerEventType.selectedItem.toString(),
                profesional = vet,
                anamnesis = anamnesis,
                hallazgos = findings,
                compromiso_correctivo = commitment,
                cumplido = cbFulfilled.isChecked
            )

            Microdataset.addClinicalRecord(record.registro_id, newRecord)
            Toast.makeText(this, "Atención clínica agregada al historial", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            populateClinicalRecords()
        }

        dialog.show()
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
