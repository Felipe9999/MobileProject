package com.alcaldia.censoanimal.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
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
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.ActaVisita
import com.alcaldia.censoanimal.model.MistreatmentReport
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MistreatmentActivity : AppCompatActivity() {

    private enum class CaseFilter { ALL, PENDING, SIGNED, POLICE }

    private lateinit var btnMistreatmentBack: ImageButton
    private lateinit var btnMistreatmentAccount: LinearLayout
    private lateinit var tvMistreatmentAccountLabel: TextView
    private lateinit var tvMistreatmentSummary: TextView
    private lateinit var btnNewComplaint: Button

    private lateinit var etSearchCases: EditText
    private lateinit var chipFilterAllCases: TextView
    private lateinit var chipFilterPendingCases: TextView
    private lateinit var chipFilterSignedCases: TextView
    private lateinit var chipFilterPoliceCases: TextView

    private lateinit var layoutCasesContainer: LinearLayout
    private lateinit var layoutEmptyMistreatment: LinearLayout

    private var currentFilter = CaseFilter.ALL
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mistreatment)

        initViews()
        setupListeners()
        refreshCases()
    }

    private fun initViews() {
        btnMistreatmentBack = findViewById(R.id.btnMistreatmentBack)
        btnMistreatmentAccount = findViewById(R.id.btnMistreatmentAccount)
        tvMistreatmentAccountLabel = findViewById(R.id.tvMistreatmentAccountLabel)
        tvMistreatmentSummary = findViewById(R.id.tvMistreatmentSummary)
        btnNewComplaint = findViewById(R.id.btnNewComplaint)

        etSearchCases = findViewById(R.id.etSearchCases)
        chipFilterAllCases = findViewById(R.id.chipFilterAllCases)
        chipFilterPendingCases = findViewById(R.id.chipFilterPendingCases)
        chipFilterSignedCases = findViewById(R.id.chipFilterSignedCases)
        chipFilterPoliceCases = findViewById(R.id.chipFilterPoliceCases)

        layoutCasesContainer = findViewById(R.id.layoutCasesContainer)
        layoutEmptyMistreatment = findViewById(R.id.layoutEmptyMistreatment)

        TopBarAccountHelper.setupAccountButton(this, btnMistreatmentAccount, tvMistreatmentAccountLabel) { profile ->
            if (profile.role == com.alcaldia.censoanimal.model.UserRole.CIUDADANO) {
                // If role changed to citizen while on this activity, finish or redirect to ReportMistreatmentActivity
                startActivity(Intent(this, ReportMistreatmentActivity::class.java))
                finish()
            }
        }
    }

    private fun setupListeners() {
        btnMistreatmentBack.setOnClickListener { finish() }

        btnNewComplaint.setOnClickListener {
            showNewComplaintDialog()
        }

        chipFilterAllCases.setOnClickListener { updateFilter(CaseFilter.ALL) }
        chipFilterPendingCases.setOnClickListener { updateFilter(CaseFilter.PENDING) }
        chipFilterSignedCases.setOnClickListener { updateFilter(CaseFilter.SIGNED) }
        chipFilterPoliceCases.setOnClickListener { updateFilter(CaseFilter.POLICE) }

        etSearchCases.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString()?.trim()?.lowercase(Locale.getDefault()) ?: ""
                refreshCases()
            }
        })
    }

    private fun updateFilter(filter: CaseFilter) {
        currentFilter = filter

        fun styleChip(chip: TextView, isSelected: Boolean) {
            if (isSelected) {
                chip.setBackgroundResource(R.drawable.bg_pill_slate)
                chip.backgroundTintList = ContextCompat.getColorStateList(this, R.color.slate_900)
                chip.setTextColor(ContextCompat.getColor(this, R.color.white))
                chip.typeface = android.graphics.Typeface.DEFAULT_BOLD
            } else {
                chip.setBackgroundResource(R.drawable.bg_pill_slate)
                chip.backgroundTintList = null
                chip.setTextColor(ContextCompat.getColor(this, R.color.slate_600))
                chip.typeface = android.graphics.Typeface.DEFAULT
            }
        }

        styleChip(chipFilterAllCases, filter == CaseFilter.ALL)
        styleChip(chipFilterPendingCases, filter == CaseFilter.PENDING)
        styleChip(chipFilterSignedCases, filter == CaseFilter.SIGNED)
        styleChip(chipFilterPoliceCases, filter == CaseFilter.POLICE)

        refreshCases()
    }

    private fun refreshCases() {
        layoutCasesContainer.removeAllViews()
        val allReports = Microdataset.getAllMistreatmentReports()

        val filtered = allReports.filter { report ->
            val matchesFilter = when (currentFilter) {
                CaseFilter.ALL -> true
                CaseFilter.PENDING -> report.acta_visita == null
                CaseFilter.SIGNED -> report.acta_visita != null
                CaseFilter.POLICE -> report.acta_visita?.requiere_aprehension_policia == true
            }

            val matchesSearch = if (searchQuery.isEmpty()) true else {
                report.id.lowercase().contains(searchQuery) ||
                        report.vereda.lowercase().contains(searchQuery) ||
                        report.direccion_exacta.lowercase().contains(searchQuery) ||
                        report.descripcion_hechos.lowercase().contains(searchQuery) ||
                        report.nivel_gravedad_aparente.lowercase().contains(searchQuery)
            }

            matchesFilter && matchesSearch
        }

        tvMistreatmentSummary.text = "${allReports.size} radicados de inspección (${filtered.size} visibles)"

        if (filtered.isEmpty()) {
            layoutEmptyMistreatment.visibility = View.VISIBLE
            return
        }
        layoutEmptyMistreatment.visibility = View.GONE

        filtered.forEach { report ->
            val cardView = buildCaseCard(report)
            layoutCasesContainer.addView(cardView)
        }
    }

    private fun buildCaseCard(report: MistreatmentReport): View {
        val density = resources.displayMetrics.density
        val cardView = MaterialCardView(this).apply {
            radius = 12f * density
            cardElevation = 1f
            strokeWidth = (1 * density).toInt()
            setStrokeColor(ContextCompat.getColor(context, R.color.slate_200))
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, (12 * density).toInt())
            }
            layoutParams = params
        }

        val cardContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (14 * density).toInt(),
                (12 * density).toInt(),
                (14 * density).toInt(),
                (12 * density).toInt()
            )
        }

        // Header Row: Radicado ID, Privacy Mode & Date
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val radicadoTv = TextView(this).apply {
            text = "${report.id} • ${report.fecha_radicado}"
            setTextColor(ContextCompat.getColor(context, R.color.slate_900))
            textSize = 12f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val isReserved = report.modo_privacidad.contains("Reservada")
        val privacyTv = TextView(this).apply {
            text = if (isReserved) "🔒 Identidad Reservada" else "🕶️ 100% Anónimo"
            setBackgroundResource(if (isReserved) R.drawable.bg_pill_blue else R.drawable.bg_pill_slate)
            setTextColor(ContextCompat.getColor(context, if (isReserved) R.color.blue_800 else R.color.slate_700))
            textSize = 10f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding((8 * density).toInt(), (2 * density).toInt(), (8 * density).toInt(), (2 * density).toInt())
        }

        headerRow.addView(radicadoTv)
        headerRow.addView(privacyTv)
        cardContent.addView(headerRow)

        // Severity & Location
        val severityColor = when (report.nivel_gravedad_aparente.lowercase()) {
            "grave / urgente", "grave" -> R.color.rose_700
            "moderado" -> R.color.amber_700
            else -> R.color.emerald_700
        }

        val metaTv = TextView(this).apply {
            text = "Severidad: ${report.nivel_gravedad_aparente} • Vereda: ${report.vereda}\nPredio: ${report.direccion_exacta}"
            setTextColor(ContextCompat.getColor(context, R.color.slate_700))
            textSize = 12f
            setPadding(0, (4 * density).toInt(), 0, (2 * density).toInt())
        }
        cardContent.addView(metaTv)

        // Narrative Description
        val narrativeTv = TextView(this).apply {
            text = "\"${report.descripcion_hechos}\""
            setTextColor(ContextCompat.getColor(context, R.color.slate_600))
            textSize = 12f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.ITALIC)
        }
        cardContent.addView(narrativeTv)

        // Acta de Visita Status or Action Button (TRD FR-4.2)
        val existingActa = report.acta_visita
        if (existingActa != null) {
            val actaBox = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundResource(R.drawable.bg_pill_slate)
                setPadding((12 * density).toInt(), (10 * density).toInt(), (12 * density).toInt(), (10 * density).toInt())
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, (8 * density).toInt(), 0, 0)
                }
                layoutParams = params
            }

            val actaTitle = TextView(this).apply {
                text = "📋 Acta Técnica Radicada: ${existingActa.inspector_veterinario} (${existingActa.fecha_visita})"
                setTextColor(ContextCompat.getColor(context, R.color.slate_900))
                textSize = 11f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            actaBox.addView(actaTitle)

            val actaScore = TextView(this).apply {
                text = "Condición Corporal: ${existingActa.condicion_corporal} • Dictamen: ${existingActa.clasificacion_final}"
                setTextColor(ContextCompat.getColor(context, R.color.slate_700))
                textSize = 11f
                setPadding(0, (2 * density).toInt(), 0, (2 * density).toInt())
            }
            actaBox.addView(actaScore)

            val actaCommitment = TextView(this).apply {
                val days = existingActa.dias_plazo_compromiso
                text = "Compromiso: ${existingActa.compromiso_texto} (Plazo legal: $days días hábiles)"
                setTextColor(ContextCompat.getColor(context, R.color.blue_900))
                textSize = 11f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            actaBox.addView(actaCommitment)

            if (existingActa.requiere_aprehension_policia) {
                val apprehensionTv = TextView(this).apply {
                    text = "🚨 REMITIDO A POLICÍA AMBIENTAL PARA APREHENSIÓN PREVENTIVA (Ley 1774/2016)"
                    setTextColor(ContextCompat.getColor(context, R.color.rose_700))
                    textSize = 10f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setPadding(0, (4 * density).toInt(), 0, 0)
                }
                actaBox.addView(apprehensionTv)
            }

            cardContent.addView(actaBox)
        } else {
            val btnFillActa = Button(this).apply {
                text = "Diligenciar Acta de Visita Técnica (M.V.)"
                textSize = 12f
                setBackgroundColor(ContextCompat.getColor(context, R.color.blue_700))
                setTextColor(ContextCompat.getColor(context, R.color.white))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (42 * density).toInt()
                ).apply {
                    setMargins(0, (8 * density).toInt(), 0, 0)
                }
                layoutParams = params
                setOnClickListener {
                    showActaVisitaDialog(report.id) {
                        refreshCases()
                    }
                }
            }
            cardContent.addView(btnFillActa)
        }

        cardView.addView(cardContent)
        return cardView
    }

    private fun showNewComplaintDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_mistreatment, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val rgPrivacy = dialogView.findViewById<RadioGroup>(R.id.rgPrivacyMode)
        val rbReserved = dialogView.findViewById<RadioButton>(R.id.rbReservedIdentity)
        val etContact = dialogView.findViewById<EditText>(R.id.etComplaintContact)
        val spinnerVereda = dialogView.findViewById<Spinner>(R.id.spinnerComplaintVereda)
        val etAddress = dialogView.findViewById<EditText>(R.id.etComplaintAddress)
        val spinnerSeverity = dialogView.findViewById<Spinner>(R.id.spinnerComplaintSeverity)
        val etDescription = dialogView.findViewById<EditText>(R.id.etComplaintDescription)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelComplaint)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveComplaint)

        spinnerVereda.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Microdataset.OFFICIAL_VEREDAS)
        spinnerSeverity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Leve", "Moderado", "Grave / Urgente"))

        rgPrivacy.setOnCheckedChangeListener { _, checkedId ->
            etContact.visibility = if (checkedId == R.id.rbReservedIdentity) View.VISIBLE else View.GONE
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val isReserved = rbReserved.isChecked
            val contact = if (isReserved) etContact.text.toString().trim().ifEmpty { "Identidad Reservada" } else null
            val address = etAddress.text.toString().trim().ifEmpty { "Predio rural sin nomenclatura" }
            val desc = etDescription.text.toString().trim().ifEmpty { "Presunta vulneración de bienestar animal reportada" }
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

            val newReport = MistreatmentReport(
                id = "MAL-2026-${(100..999).random()}",
                fecha_radicado = today,
                modo_privacidad = if (isReserved) "Identidad Reservada" else "Completamente Anónimo",
                denunciante_nombre = if (isReserved) "Ciudadano Denunciante" else null,
                denunciante_contacto = contact,
                vereda = spinnerVereda.selectedItem.toString(),
                direccion_exacta = address,
                descripcion_hechos = desc,
                nivel_gravedad_aparente = spinnerSeverity.selectedItem.toString(),
                estado_caso = "Pendiente Visita",
                acta_visita = null
            )

            Microdataset.addMistreatmentReport(newReport)
            Toast.makeText(this, "Denuncia radicada formalmente", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            refreshCases()
        }

        dialog.show()
    }

    private fun showActaVisitaDialog(radicadoId: String, onSaved: () -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_acta_visita, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvActaTitle)
        val etInspector = dialogView.findViewById<EditText>(R.id.etActaInspector)
        val spinnerBodyScore = dialogView.findViewById<Spinner>(R.id.spinnerBodyScore)
        val etFindings = dialogView.findViewById<EditText>(R.id.etActaFindings)
        val spinnerSeverity = dialogView.findViewById<Spinner>(R.id.spinnerActaSeverity)
        val etCommitments = dialogView.findViewById<EditText>(R.id.etActaCommitments)
        val spinnerDays = dialogView.findViewById<Spinner>(R.id.spinnerActaDays)
        val cbPolice = dialogView.findViewById<CheckBox>(R.id.cbPoliceApprehension)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelActa)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveActa)

        tvTitle.text = "Acta de Visita - Radicado $radicadoId"

        spinnerBodyScore.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("1 - Caquéctico", "2 - Bajo peso", "3 - Ideal", "4 - Sobrepeso", "5 - Obeso"))
        spinnerSeverity.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Sin Maltrato", "Tenencia Irresponsable", "Maltrato Leve", "Maltrato Moderado", "Maltrato Grave"))
        spinnerDays.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("5 días hábiles", "10 días hábiles", "15 días hábiles", "20 días hábiles"))

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val inspector = etInspector.text.toString().trim().ifEmpty { "Dr. Carlos Rodríguez (M.V. Inspector Sanitario)" }
            val findings = etFindings.text.toString().trim().ifEmpty { "Se verificaron las condiciones locativas y de bienestar animal en sitio." }
            val commitments = etCommitments.text.toString().trim().ifEmpty { "Mejorar condiciones de albergue e hidratación." }
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

            val days = when (spinnerDays.selectedItemPosition) {
                0 -> 5
                1 -> 10
                2 -> 15
                else -> 20
            }

            val acta = ActaVisita(
                id = "ACTA-2026-${(100..999).random()}",
                reporte_id = radicadoId,
                fecha_visita = today,
                inspector_veterinario = inspector,
                condicion_corporal = spinnerBodyScore.selectedItem.toString(),
                condicion_espacio = "Insuficiente / Intemperie",
                condicion_alimentacion = "Alimento insuficiente",
                clasificacion_final = spinnerSeverity.selectedItem.toString(),
                dias_plazo_compromiso = days,
                compromiso_texto = "$commitments ($findings)",
                requiere_aprehension_policia = cbPolice.isChecked,
                compromiso_cumplido = false
            )

            Microdataset.saveActaVisita(radicadoId, acta)
            Toast.makeText(this, "Acta técnica radicada exitosamente", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            onSaved()
        }

        dialog.show()
    }
}
