package com.alcaldia.censoanimal.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.MistreatmentReport
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportMistreatmentActivity : AppCompatActivity() {

    private lateinit var btnReportBack: ImageButton
    private lateinit var btnReportAccount: LinearLayout
    private lateinit var tvReportAccountLabel: TextView

    private lateinit var rgCitizenPrivacy: RadioGroup
    private lateinit var rbCitizenReserved: RadioButton
    private lateinit var rbCitizenAnonymous: RadioButton
    private lateinit var layoutCitizenContact: LinearLayout
    private lateinit var etCitizenContact: EditText

    private lateinit var spinnerCitizenVereda: Spinner
    private lateinit var etCitizenAddress: EditText
    private lateinit var spinnerCitizenSeverity: Spinner
    private lateinit var etCitizenDescription: EditText

    private lateinit var btnCitizenCancel: Button
    private lateinit var btnCitizenSubmit: Button

    private lateinit var tvCitizenReportCount: TextView
    private lateinit var containerCitizenReports: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_mistreatment)

        initViews()
        setupListeners()
        populateDropdowns()
        loadCitizenReports()
    }

    private fun initViews() {
        btnReportBack = findViewById(R.id.btnReportBack)
        btnReportAccount = findViewById(R.id.btnReportAccount)
        tvReportAccountLabel = findViewById(R.id.tvReportAccountLabel)

        rgCitizenPrivacy = findViewById(R.id.rgCitizenPrivacy)
        rbCitizenReserved = findViewById(R.id.rbCitizenReserved)
        rbCitizenAnonymous = findViewById(R.id.rbCitizenAnonymous)
        layoutCitizenContact = findViewById(R.id.layoutCitizenContact)
        etCitizenContact = findViewById(R.id.etCitizenContact)

        spinnerCitizenVereda = findViewById(R.id.spinnerCitizenVereda)
        etCitizenAddress = findViewById(R.id.etCitizenAddress)
        spinnerCitizenSeverity = findViewById(R.id.spinnerCitizenSeverity)
        etCitizenDescription = findViewById(R.id.etCitizenDescription)

        btnCitizenCancel = findViewById(R.id.btnCitizenCancel)
        btnCitizenSubmit = findViewById(R.id.btnCitizenSubmit)

        tvCitizenReportCount = findViewById(R.id.tvCitizenReportCount)
        containerCitizenReports = findViewById(R.id.containerCitizenReports)

        var lastKnownRole = AppSessionManager.currentProfile.role
        TopBarAccountHelper.setupAccountButton(this, btnReportAccount, tvReportAccountLabel) { profile ->
            if (profile.role != lastKnownRole) {
                lastKnownRole = profile.role
                if (profile.role != com.alcaldia.censoanimal.model.UserRole.CIUDADANO) {
                    startActivity(android.content.Intent(this, MistreatmentActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setupListeners() {
        btnReportBack.setOnClickListener { finish() }

        rgCitizenPrivacy.setOnCheckedChangeListener { _, checkedId ->
            layoutCitizenContact.visibility = if (checkedId == R.id.rbCitizenReserved) View.VISIBLE else View.GONE
        }

        // Prefill contact if current profile is Citizen
        val profile = AppSessionManager.currentProfile
        if (profile.role == com.alcaldia.censoanimal.model.UserRole.CIUDADANO) {
            etCitizenContact.setText("${profile.name} - Tel. 310 948 2910")
        }

        btnCitizenCancel.setOnClickListener { finish() }

        btnCitizenSubmit.setOnClickListener {
            submitReport()
        }
    }

    private fun populateDropdowns() {
        spinnerCitizenVereda.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Microdataset.OFFICIAL_VEREDAS
        )

        val severityOptions = listOf(
            "Leve (Falta de sombra / agua limpia ocasional)",
            "Moderado (Desnutrición evidente / Encierro permanente)",
            "Grave / Urgente (Lesiones activas / Agresión física severa)"
        )
        spinnerCitizenSeverity.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            severityOptions
        )
    }

    private fun submitReport() {
        val isReserved = rbCitizenReserved.isChecked
        val contactText = etCitizenContact.text.toString().trim()
        val addressText = etCitizenAddress.text.toString().trim()
        val descriptionText = etCitizenDescription.text.toString().trim()
        val selectedVereda = spinnerCitizenVereda.selectedItem.toString()

        if (addressText.isEmpty()) {
            etCitizenAddress.error = "Ingrese la dirección o referencia del predio"
            etCitizenAddress.requestFocus()
            return
        }

        if (descriptionText.isEmpty()) {
            etCitizenDescription.error = "Describa los hechos observados"
            etCitizenDescription.requestFocus()
            return
        }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val generatedId = "MAL-2026-${(100..999).random()}"

        val rawSeverity = spinnerCitizenSeverity.selectedItem.toString()
        val mappedSeverity = when {
            rawSeverity.startsWith("Leve") -> "Leve"
            rawSeverity.startsWith("Moderado") -> "Moderado"
            else -> "Grave / Urgente"
        }

        val newReport = MistreatmentReport(
            id = generatedId,
            fecha_radicado = today,
            modo_privacidad = if (isReserved) "Identidad Reservada" else "Completamente Anónimo",
            denunciante_nombre = if (isReserved) (if (contactText.isNotEmpty()) contactText.substringBefore("-").trim() else "Ciudadano Zipaquirá") else null,
            denunciante_contacto = if (isReserved) contactText.ifEmpty { "310 000 0000" } else null,
            vereda = selectedVereda,
            direccion_exacta = addressText,
            descripcion_hechos = descriptionText,
            nivel_gravedad_aparente = mappedSeverity,
            estado_caso = "Pendiente Visita",
            acta_visita = null
        )

        Microdataset.addMistreatmentReport(newReport)

        Toast.makeText(this, "¡Denuncia radicada exitosamente! Radicado: $generatedId", Toast.LENGTH_LONG).show()

        // Clear input form
        etCitizenAddress.text.clear()
        etCitizenDescription.text.clear()

        // Refresh citizen's reports list
        loadCitizenReports()
    }

    private fun loadCitizenReports() {
        containerCitizenReports.removeAllViews()
        val density = resources.displayMetrics.density

        val allReports = Microdataset.getAllMistreatmentReports()
        // In prototype, show all reports or latest filed reports
        tvCitizenReportCount.text = "${allReports.size} radicados oficiales en el sistema municipal"

        allReports.take(4).forEach { report ->
            val card = MaterialCardView(this).apply {
                radius = 12f * density
                cardElevation = 1f
                strokeWidth = (1 * density).toInt()
                setStrokeColor(ContextCompat.getColor(context, R.color.slate_200))
                setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, (10 * density).toInt())
                }
                layoutParams = params
            }

            val cardContent = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding((12 * density).toInt(), (10 * density).toInt(), (12 * density).toInt(), (10 * density).toInt())
            }

            // Top row
            val topRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val tvId = TextView(this).apply {
                text = "${report.id} • ${report.fecha_radicado}"
                setTextColor(ContextCompat.getColor(context, R.color.slate_900))
                textSize = 12f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvStatus = TextView(this).apply {
                val hasActa = report.acta_visita != null
                text = if (hasActa) "📋 Con Visita Técnica" else "⏳ En Espera de Inspección"
                setTextColor(ContextCompat.getColor(context, if (hasActa) R.color.emerald_700 else R.color.amber_700))
                textSize = 11f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            topRow.addView(tvId)
            topRow.addView(tvStatus)
            cardContent.addView(topRow)

            // Vereda & Severity
            val tvMeta = TextView(this).apply {
                text = "📍 ${report.vereda} • Severidad: ${report.nivel_gravedad_aparente}"
                setTextColor(ContextCompat.getColor(context, R.color.slate_600))
                textSize = 11f
                setPadding(0, (2 * density).toInt(), 0, (2 * density).toInt())
            }
            cardContent.addView(tvMeta)

            // Facts
            val tvDesc = TextView(this).apply {
                text = "\"${report.descripcion_hechos}\""
                setTextColor(ContextCompat.getColor(context, R.color.slate_700))
                textSize = 11f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.ITALIC)
            }
            cardContent.addView(tvDesc)

            card.addView(cardContent)
            containerCitizenReports.addView(card)
        }
    }
}
