package com.alcaldia.censoanimal.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.adapter.AnimalRecordAdapter
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.AnimalRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CensusFragment : Fragment() {

    private lateinit var rvCensusRecords: RecyclerView
    private lateinit var adapter: AnimalRecordAdapter
    private lateinit var etSearchCensus: EditText
    private lateinit var btnClearSearch: ImageButton
    private lateinit var spinnerVeredaFilter: Spinner
    private lateinit var tvTotalBadge: TextView
    private lateinit var layoutEmptyState: LinearLayout

    private lateinit var chipFilterAll: TextView
    private lateinit var chipFilterDogs: TextView
    private lateinit var chipFilterCats: TextView
    private lateinit var chipFilterSterilized: TextView
    private lateinit var chipFilterAlerts: TextView

    private lateinit var btnOpenLostFound: Button
    private lateinit var btnOpenMistreatment: Button

    private var currentFilterType = "ALL" // "ALL", "DOGS", "CATS", "STERILIZED", "ALERTS"
    private var selectedVereda = "Todas las Veredas"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_census, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvCensusRecords = view.findViewById(R.id.rvCensusRecords)
        etSearchCensus = view.findViewById(R.id.etSearchCensus)
        btnClearSearch = view.findViewById(R.id.btnClearSearch)
        spinnerVeredaFilter = view.findViewById(R.id.spinnerVeredaFilter)
        tvTotalBadge = view.findViewById(R.id.tvTotalBadge)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)

        chipFilterAll = view.findViewById(R.id.chipFilterAll)
        chipFilterDogs = view.findViewById(R.id.chipFilterDogs)
        chipFilterCats = view.findViewById(R.id.chipFilterCats)
        chipFilterSterilized = view.findViewById(R.id.chipFilterSterilized)
        chipFilterAlerts = view.findViewById(R.id.chipFilterAlerts)

        btnOpenLostFound = view.findViewById(R.id.btnOpenLostFound)
        btnOpenMistreatment = view.findViewById(R.id.btnOpenMistreatment)

        btnOpenLostFound.setOnClickListener { showLostFoundDialog() }
        btnOpenMistreatment.setOnClickListener { showMistreatmentDialog() }

        rvCensusRecords.layoutManager = LinearLayoutManager(requireContext())
        adapter = AnimalRecordAdapter(Microdataset.getAllRecords()) { selectedRecord ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("RECORD_ID", selectedRecord.registro_id)
            startActivity(intent)
        }
        rvCensusRecords.adapter = adapter

        setupFilters()
        setupVeredaSpinner()
        setupSearch()
        applyFilters()
    }

    override fun onResume() {
        super.onResume()
        applyFilters()
    }

    private fun setupVeredaSpinner() {
        val veredas = mutableListOf("Todas las Veredas")
        veredas.addAll(Microdataset.OFFICIAL_VEREDAS)

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            veredas
        )
        spinnerVeredaFilter.adapter = spinnerAdapter

        spinnerVeredaFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedVereda = veredas[position]
                applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupFilters() {
        val chips = listOf(chipFilterAll, chipFilterDogs, chipFilterCats, chipFilterSterilized, chipFilterAlerts)

        fun selectChip(selected: TextView, filterKey: String) {
            currentFilterType = filterKey
            for (chip in chips) {
                if (chip == selected) {
                    chip.setBackgroundResource(R.drawable.bg_button_primary)
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    chip.setBackgroundResource(R.drawable.bg_pill_slate)
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.slate_700))
                }
            }
            applyFilters()
        }

        chipFilterAll.setOnClickListener { selectChip(chipFilterAll, "ALL") }
        chipFilterDogs.setOnClickListener { selectChip(chipFilterDogs, "DOGS") }
        chipFilterCats.setOnClickListener { selectChip(chipFilterCats, "CATS") }
        chipFilterSterilized.setOnClickListener { selectChip(chipFilterSterilized, "STERILIZED") }
        chipFilterAlerts.setOnClickListener { selectChip(chipFilterAlerts, "ALERTS") }
    }

    private fun setupSearch() {
        etSearchCensus.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearSearch.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnClearSearch.setOnClickListener {
            etSearchCensus.setText("")
        }
    }

    private fun applyFilters() {
        val query = etSearchCensus.text.toString().trim().lowercase()
        val allRecords = Microdataset.getAllRecords()

        val filtered = allRecords.filter { record ->
            // Search query filter
            val matchesQuery = query.isEmpty() ||
                record.animal_nombre.lowercase().contains(query) ||
                record.registro_id.lowercase().contains(query) ||
                (record.microchip?.lowercase()?.contains(query) == true) ||
                record.responsable_nombre.lowercase().contains(query) ||
                record.raza.lowercase().contains(query) ||
                record.territorio.lowercase().contains(query)

            // Vereda filter
            val matchesVereda = (selectedVereda == "Todas las Veredas") ||
                record.territorio.equals(selectedVereda, ignoreCase = true)

            // Category filter
            val matchesCategory = when (currentFilterType) {
                "DOGS" -> record.especie.equals("Perro", ignoreCase = true)
                "CATS" -> record.especie.equals("Gato", ignoreCase = true)
                "STERILIZED" -> record.esterilizado
                "ALERTS" -> record.alerta_duplicado
                else -> true
            }

            matchesQuery && matchesVereda && matchesCategory
        }

        adapter.updateData(filtered)
        tvTotalBadge.text = "${filtered.size} censados"
        (activity as? com.alcaldia.censoanimal.MainActivity)?.updateTopBarBadge("${filtered.size} censados")

        if (filtered.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            rvCensusRecords.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            rvCensusRecords.visibility = View.VISIBLE
        }
    }

    // --- LOST & FOUND MODULE (TRD FR-2.4 & FR-2.5) ---

    private fun showLostFoundDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_lost_found, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseLostFound)
        val btnNewAlert = dialogView.findViewById<Button>(R.id.btnNewAlert)
        val tvSummary = dialogView.findViewById<TextView>(R.id.tvLostFoundSummary)
        val layoutContainer = dialogView.findViewById<LinearLayout>(R.id.layoutAlertsContainer)

        btnClose.setOnClickListener { dialog.dismiss() }
        btnNewAlert.setOnClickListener {
            dialog.dismiss()
            showNewAlertDialog()
        }

        fun refreshList() {
            layoutContainer.removeAllViews()
            val alerts = Microdataset.getAllLostFoundAlerts()
            tvSummary.text = "${alerts.size} alertas en Zipaquirá"

            if (alerts.isEmpty()) {
                val emptyTv = TextView(requireContext()).apply {
                    text = "No hay alertas activas de pérdidas ni hallazgos."
                    setTextColor(ContextCompat.getColor(context, R.color.slate_500))
                    textSize = 12f
                    setPadding(0, 16, 0, 16)
                }
                layoutContainer.addView(emptyTv)
                return
            }

            alerts.forEach { alert ->
                val cardView = com.google.android.material.card.MaterialCardView(requireContext()).apply {
                    radius = 12f * resources.displayMetrics.density
                    cardElevation = 1f
                    strokeWidth = (1 * resources.displayMetrics.density).toInt()
                    setStrokeColor(ContextCompat.getColor(context, R.color.slate_200))
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, (10 * resources.displayMetrics.density).toInt())
                    }
                    layoutParams = params
                }

                val cardContent = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(
                        (14 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt(),
                        (14 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt()
                    )
                }

                // Top Tag Row
                val topRow = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }

                val typeBadge = TextView(requireContext()).apply {
                    val isLost = alert.tipo.equals("PERDIDO", ignoreCase = true)
                    text = alert.tipo.uppercase()
                    setBackgroundResource(if (isLost) R.drawable.bg_pill_rose else R.drawable.bg_pill_blue)
                    setTextColor(ContextCompat.getColor(context, if (isLost) R.color.rose_700 else R.color.blue_800))
                    textSize = 10f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setPadding((8 * resources.displayMetrics.density).toInt(), (2 * resources.displayMetrics.density).toInt(), (8 * resources.displayMetrics.density).toInt(), (2 * resources.displayMetrics.density).toInt())
                }

                val idTv = TextView(requireContext()).apply {
                    text = "  ${alert.id} • ${alert.fecha_evento}"
                    setTextColor(ContextCompat.getColor(context, R.color.slate_500))
                    textSize = 10f
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val statusTv = TextView(requireContext()).apply {
                    text = alert.estado
                    setTextColor(ContextCompat.getColor(context, if (alert.estado.contains("Reunificado")) R.color.emerald_700 else R.color.amber_700))
                    textSize = 10f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }

                topRow.addView(typeBadge)
                topRow.addView(idTv)
                topRow.addView(statusTv)
                cardContent.addView(topRow)

                // Title / Breed
                val titleTv = TextView(requireContext()).apply {
                    val nameStr = alert.nombre ?: "Sin nombre"
                    text = "$nameStr • ${alert.raza} (${alert.especie})"
                    setTextColor(ContextCompat.getColor(context, R.color.slate_900))
                    textSize = 13f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setPadding(0, (6 * resources.displayMetrics.density).toInt(), 0, 0)
                }
                cardContent.addView(titleTv)

                // Location & Contact
                val locTv = TextView(requireContext()).apply {
                    text = "📍 ${alert.vereda} | Contacto: ${alert.contacto_nombre} (${alert.contacto_telefono})"
                    setTextColor(ContextCompat.getColor(context, R.color.slate_600))
                    textSize = 11f
                    setPadding(0, 2, 0, 2)
                }
                cardContent.addView(locTv)

                // Description
                val descTv = TextView(requireContext()).apply {
                    text = alert.descripcion
                    setTextColor(ContextCompat.getColor(context, R.color.slate_500))
                    textSize = 11f
                }
                cardContent.addView(descTv)

                // 20-Day Stray-to-Abandoned Warning (TRD FR-2.5)
                if (alert.dias_custodia_albergue >= 20 || alert.estado.contains("+20")) {
                    val alert20Tv = TextView(requireContext()).apply {
                        text = "⚠️ REGLA 20 DÍAS: +20 días en custodia sin reclamo. Declarado en abandono / Ingresa a Adopción Municipal."
                        setBackgroundResource(R.drawable.bg_pill_amber)
                        setTextColor(ContextCompat.getColor(context, R.color.amber_900))
                        textSize = 10f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        setPadding((8 * resources.displayMetrics.density).toInt(), (4 * resources.displayMetrics.density).toInt(), (8 * resources.displayMetrics.density).toInt(), (4 * resources.displayMetrics.density).toInt())
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, (6 * resources.displayMetrics.density).toInt(), 0, 0)
                        }
                        layoutParams = params
                    }
                    cardContent.addView(alert20Tv)
                }

                // Match Validation Box (TRD FR-2.4)
                if (!alert.posible_coincidencia_registro_id.isNullOrEmpty()) {
                    val matchLayout = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.VERTICAL
                        setBackgroundResource(R.drawable.bg_pill_emerald)
                        setPadding((10 * resources.displayMetrics.density).toInt(), (8 * resources.displayMetrics.density).toInt(), (10 * resources.displayMetrics.density).toInt(), (8 * resources.displayMetrics.density).toInt())
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, (6 * resources.displayMetrics.density).toInt(), 0, 0)
                        }
                        layoutParams = params
                    }

                    val matchTv = TextView(requireContext()).apply {
                        val isResolved = alert.validacion_manual_aprobada
                        text = if (isResolved) "✔ Coincidencia Validada Manualmente con Registro ${alert.posible_coincidencia_registro_id}"
                        else "🔍 Posible Coincidencia Algorítmica con Registro ${alert.posible_coincidencia_registro_id} (Requiere Validación Manual de Inspector)"
                        setTextColor(ContextCompat.getColor(context, if (isResolved) R.color.emerald_900 else R.color.blue_900))
                        textSize = 10f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    matchLayout.addView(matchTv)

                    if (!alert.validacion_manual_aprobada) {
                        val btnApproveMatch = Button(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                            text = "Aprobar Validación Manual de Coincidencia"
                            textSize = 10f
                            setTextColor(ContextCompat.getColor(context, R.color.emerald_800))
                            setOnClickListener {
                                Microdataset.approveManualMatch(alert.id)
                                Toast.makeText(context, "Coincidencia aprobada y reunificación registrada", Toast.LENGTH_SHORT).show()
                                refreshList()
                            }
                        }
                        matchLayout.addView(btnApproveMatch)
                    }

                    cardContent.addView(matchLayout)
                }

                cardView.addView(cardContent)
                layoutContainer.addView(cardView)
            }
        }

        refreshList()
        dialog.show()
    }

    private fun showNewAlertDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_new_lost_found, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val spinnerType = dialogView.findViewById<Spinner>(R.id.spinnerAlertType)
        val spinnerSpecies = dialogView.findViewById<Spinner>(R.id.spinnerAlertSpecies)
        val spinnerVereda = dialogView.findViewById<Spinner>(R.id.spinnerAlertVereda)
        val etNameBreed = dialogView.findViewById<EditText>(R.id.etAlertNameBreed)
        val etContact = dialogView.findViewById<EditText>(R.id.etAlertContact)
        val etDescription = dialogView.findViewById<EditText>(R.id.etAlertDescription)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelNewAlert)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveNewAlert)

        spinnerType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("PERDIDO", "HALLADO"))
        spinnerSpecies.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Perro", "Gato", "Otro"))
        spinnerVereda.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Microdataset.OFFICIAL_VEREDAS)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val nameBreed = etNameBreed.text.toString().trim().ifEmpty { "Mestizo Criollo" }
            val contact = etContact.text.toString().trim().ifEmpty { "Ciudadano Zipaquirá - 3100000000" }
            val desc = etDescription.text.toString().trim().ifEmpty { "Sin descripción particular" }
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val isFound = spinnerType.selectedItem.toString() == "HALLADO"

            val newAlert = com.alcaldia.censoanimal.model.LostFoundAlert(
                id = "ALR-2026-${(100..999).random()}",
                tipo = spinnerType.selectedItem.toString(),
                especie = spinnerSpecies.selectedItem.toString(),
                nombre = if (isFound) null else nameBreed,
                raza = nameBreed,
                color = "Varios",
                vereda = spinnerVereda.selectedItem.toString(),
                fecha_evento = today,
                microchip = null,
                contacto_nombre = contact.substringBefore("-").trim(),
                contacto_telefono = if (contact.contains("-")) contact.substringAfter("-").trim() else "3100000000",
                estado = "Activo / En Búsqueda",
                descripcion = desc,
                posible_coincidencia_registro_id = if (isFound) "CEN-2026-004" else null,
                dias_custodia_albergue = 0,
                validacion_manual_aprobada = false
            )

            Microdataset.addLostFoundAlert(newAlert)
            Toast.makeText(requireContext(), "Alerta publicada exitosamente", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            showLostFoundDialog()
        }

        dialog.show()
    }

    // --- MISTREATMENT & CASE MANAGEMENT MODULE (TRD FR-4.1 & FR-4.2) ---

    private fun showMistreatmentDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_mistreatment, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseMistreatment)
        val btnNewComplaint = dialogView.findViewById<Button>(R.id.btnNewComplaint)
        val tvSummary = dialogView.findViewById<TextView>(R.id.tvMistreatmentSummary)
        val layoutContainer = dialogView.findViewById<LinearLayout>(R.id.layoutCasesContainer)

        btnClose.setOnClickListener { dialog.dismiss() }
        btnNewComplaint.setOnClickListener {
            dialog.dismiss()
            showNewComplaintDialog()
        }

        fun refreshCases() {
            layoutContainer.removeAllViews()
            val reports = Microdataset.getAllMistreatmentReports()
            tvSummary.text = "${reports.size} radicados de inspección"

            if (reports.isEmpty()) {
                val emptyTv = TextView(requireContext()).apply {
                    text = "No hay denuncias radicadas pendientes."
                    setTextColor(ContextCompat.getColor(context, R.color.slate_500))
                    textSize = 12f
                    setPadding(0, 16, 0, 16)
                }
                layoutContainer.addView(emptyTv)
                return
            }

            reports.forEach { report ->
                val cardView = com.google.android.material.card.MaterialCardView(requireContext()).apply {
                    radius = 12f * resources.displayMetrics.density
                    cardElevation = 1f
                    strokeWidth = (1 * resources.displayMetrics.density).toInt()
                    setStrokeColor(ContextCompat.getColor(context, R.color.slate_200))
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, (10 * resources.displayMetrics.density).toInt())
                    }
                    layoutParams = params
                }

                val cardContent = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(
                        (14 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt(),
                        (14 * resources.displayMetrics.density).toInt(),
                        (12 * resources.displayMetrics.density).toInt()
                    )
                }

                // Row 1: Radicado, Privacy mode & Severity
                val headerRow = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }

                val radicadoTv = TextView(requireContext()).apply {
                    text = report.id
                    setTextColor(ContextCompat.getColor(context, R.color.slate_900))
                    textSize = 12f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val privacyTv = TextView(requireContext()).apply {
                    text = if (report.modo_privacidad.contains("Reservada")) "🔒 Identidad Reservada" else "🕶️ 100% Anónimo"
                    setTextColor(ContextCompat.getColor(context, R.color.blue_800))
                    textSize = 10f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }

                headerRow.addView(radicadoTv)
                headerRow.addView(privacyTv)
                cardContent.addView(headerRow)

                // Severity & Location
                val metaTv = TextView(requireContext()).apply {
                    text = "Severidad: ${report.nivel_gravedad_aparente} • Vereda: ${report.vereda}\nPredio: ${report.direccion_exacta}"
                    setTextColor(ContextCompat.getColor(context, R.color.slate_700))
                    textSize = 11f
                    setPadding(0, 4, 0, 2)
                }
                cardContent.addView(metaTv)

                // Narrative
                val narrativeTv = TextView(requireContext()).apply {
                    text = "\"${report.descripcion_hechos}\""
                    setTextColor(ContextCompat.getColor(context, R.color.slate_600))
                    textSize = 11f
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.ITALIC)
                }
                cardContent.addView(narrativeTv)

                // Acta de Visita Status or Button (TRD FR-4.2)
                val existingActa = report.acta_visita
                if (existingActa != null) {
                    val actaBox = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.VERTICAL
                        setBackgroundResource(R.drawable.bg_pill_slate)
                        setPadding((10 * resources.displayMetrics.density).toInt(), (8 * resources.displayMetrics.density).toInt(), (10 * resources.displayMetrics.density).toInt(), (8 * resources.displayMetrics.density).toInt())
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, (6 * resources.displayMetrics.density).toInt(), 0, 0)
                        }
                        layoutParams = params
                    }

                    val actaTitle = TextView(requireContext()).apply {
                        text = "📋 Acta Firmada: ${existingActa.inspector_veterinario} (${existingActa.fecha_visita})"
                        setTextColor(ContextCompat.getColor(context, R.color.slate_900))
                        textSize = 10f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    actaBox.addView(actaTitle)

                    val actaScore = TextView(requireContext()).apply {
                        text = "Condición Corporal: ${existingActa.condicion_corporal} • Clasificación: ${existingActa.clasificacion_final}"
                        setTextColor(ContextCompat.getColor(context, R.color.slate_700))
                        textSize = 10f
                    }
                    actaBox.addView(actaScore)

                    val actaCommitment = TextView(requireContext()).apply {
                        val days = existingActa.dias_plazo_compromiso
                        text = "Compromiso: ${existingActa.compromiso_texto} (Plazo: $days días)"
                        setTextColor(ContextCompat.getColor(context, R.color.blue_900))
                        textSize = 10f
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    actaBox.addView(actaCommitment)

                    if (existingActa.requiere_aprehension_policia) {
                        val apprehensionTv = TextView(requireContext()).apply {
                            text = "🚨 REMITIDO A POLICÍA AMBIENTAL PARA APREHENSIÓN PREVENTIVA"
                            setTextColor(ContextCompat.getColor(context, R.color.rose_700))
                            textSize = 10f
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        actaBox.addView(apprehensionTv)
                    }

                    cardContent.addView(actaBox)
                } else {
                    val btnFillActa = Button(requireContext()).apply {
                        text = "Diligenciar Acta de Visita Técnica (M.V.)"
                        textSize = 11f
                        setBackgroundColor(ContextCompat.getColor(context, R.color.blue_700))
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (38 * resources.displayMetrics.density).toInt()
                        ).apply {
                            setMargins(0, (6 * resources.displayMetrics.density).toInt(), 0, 0)
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
                layoutContainer.addView(cardView)
            }
        }

        refreshCases()
        dialog.show()
    }

    private fun showNewComplaintDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_new_mistreatment, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
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

        spinnerVereda.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Microdataset.OFFICIAL_VEREDAS)
        spinnerSeverity.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Leve", "Moderado", "Grave / Urgente"))

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

            val newReport = com.alcaldia.censoanimal.model.MistreatmentReport(
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
            Toast.makeText(requireContext(), "Denuncia radicada formalmente", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            showMistreatmentDialog()
        }

        dialog.show()
    }

    private fun showActaVisitaDialog(radicadoId: String, onSaved: () -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_acta_visita, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
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

        spinnerBodyScore.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("1 - Caquéctico", "2 - Bajo peso", "3 - Ideal", "4 - Sobrepeso", "5 - Obeso"))
        spinnerSeverity.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("Sin Maltrato", "Tenencia Irresponsable", "Maltrato Leve", "Maltrato Moderado", "Maltrato Grave"))
        spinnerDays.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, listOf("5 días hábiles", "10 días hábiles", "15 días hábiles", "20 días hábiles"))

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

            val acta = com.alcaldia.censoanimal.model.ActaVisita(
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
            Toast.makeText(requireContext(), "Acta técnica radicada exitosamente", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            onSaved()
        }

        dialog.show()
    }
}

