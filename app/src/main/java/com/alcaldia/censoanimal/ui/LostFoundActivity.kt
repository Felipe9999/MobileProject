package com.alcaldia.censoanimal.ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.LostFoundAlert
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LostFoundActivity : AppCompatActivity() {

    private enum class FilterType { ALL, LOST, FOUND, RULE_20 }

    private lateinit var btnLostFoundBack: ImageButton
    private lateinit var btnLostFoundAccount: LinearLayout
    private lateinit var tvLostFoundAccountLabel: TextView
    private lateinit var tvLostFoundSummary: TextView
    private lateinit var btnNewAlert: Button

    private lateinit var etSearchAlerts: EditText
    private lateinit var chipFilterAll: TextView
    private lateinit var chipFilterLost: TextView
    private lateinit var chipFilterFound: TextView
    private lateinit var chipFilterRule20: TextView

    private lateinit var layoutAlertsContainer: LinearLayout
    private lateinit var layoutEmptyLostFound: LinearLayout

    private var currentFilter = FilterType.ALL
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_found)

        initViews()
        setupListeners()
        refreshList()
    }

    private fun initViews() {
        btnLostFoundBack = findViewById(R.id.btnLostFoundBack)
        btnLostFoundAccount = findViewById(R.id.btnLostFoundAccount)
        tvLostFoundAccountLabel = findViewById(R.id.tvLostFoundAccountLabel)
        tvLostFoundSummary = findViewById(R.id.tvLostFoundSummary)
        btnNewAlert = findViewById(R.id.btnNewAlert)

        etSearchAlerts = findViewById(R.id.etSearchAlerts)
        chipFilterAll = findViewById(R.id.chipFilterAll)
        chipFilterLost = findViewById(R.id.chipFilterLost)
        chipFilterFound = findViewById(R.id.chipFilterFound)
        chipFilterRule20 = findViewById(R.id.chipFilterRule20)

        layoutAlertsContainer = findViewById(R.id.layoutAlertsContainer)
        layoutEmptyLostFound = findViewById(R.id.layoutEmptyLostFound)

        TopBarAccountHelper.setupAccountButton(this, btnLostFoundAccount, tvLostFoundAccountLabel)
    }

    private fun setupListeners() {
        btnLostFoundBack.setOnClickListener { finish() }

        btnNewAlert.setOnClickListener {
            showNewAlertDialog()
        }

        chipFilterAll.setOnClickListener { updateFilter(FilterType.ALL) }
        chipFilterLost.setOnClickListener { updateFilter(FilterType.LOST) }
        chipFilterFound.setOnClickListener { updateFilter(FilterType.FOUND) }
        chipFilterRule20.setOnClickListener { updateFilter(FilterType.RULE_20) }

        etSearchAlerts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString()?.trim()?.lowercase(Locale.getDefault()) ?: ""
                refreshList()
            }
        })
    }

    private fun updateFilter(filter: FilterType) {
        currentFilter = filter

        val density = resources.displayMetrics.density

        fun styleChip(chip: TextView, isSelected: Boolean) {
            if (isSelected) {
                chip.setBackgroundResource(R.drawable.bg_pill_blue)
                chip.setTextColor(ContextCompat.getColor(this, R.color.blue_800))
                chip.typeface = android.graphics.Typeface.DEFAULT_BOLD
            } else {
                chip.setBackgroundResource(R.drawable.bg_pill_slate)
                chip.setTextColor(ContextCompat.getColor(this, R.color.slate_600))
                chip.typeface = android.graphics.Typeface.DEFAULT
            }
        }

        styleChip(chipFilterAll, filter == FilterType.ALL)
        styleChip(chipFilterLost, filter == FilterType.LOST)
        styleChip(chipFilterFound, filter == FilterType.FOUND)
        styleChip(chipFilterRule20, filter == FilterType.RULE_20)

        refreshList()
    }

    private fun refreshList() {
        layoutAlertsContainer.removeAllViews()
        val allAlerts = Microdataset.getAllLostFoundAlerts()

        val filtered = allAlerts.filter { alert ->
            val matchesFilter = when (currentFilter) {
                FilterType.ALL -> true
                FilterType.LOST -> alert.tipo.equals("PERDIDO", ignoreCase = true)
                FilterType.FOUND -> alert.tipo.equals("HALLADO", ignoreCase = true)
                FilterType.RULE_20 -> alert.dias_custodia_albergue >= 20 || alert.estado.contains("+20")
            }

            val matchesSearch = if (searchQuery.isEmpty()) true else {
                (alert.nombre?.lowercase()?.contains(searchQuery) == true) ||
                        alert.raza.lowercase().contains(searchQuery) ||
                        alert.vereda.lowercase().contains(searchQuery) ||
                        alert.contacto_nombre.lowercase().contains(searchQuery) ||
                        alert.descripcion.lowercase().contains(searchQuery) ||
                        alert.id.lowercase().contains(searchQuery)
            }

            matchesFilter && matchesSearch
        }

        tvLostFoundSummary.text = "${allAlerts.size} alertas en Zipaquirá (${filtered.size} visibles)"

        if (filtered.isEmpty()) {
            layoutEmptyLostFound.visibility = View.VISIBLE
            return
        }
        layoutEmptyLostFound.visibility = View.GONE

        filtered.forEach { alert ->
            val cardView = buildAlertCard(alert)
            layoutAlertsContainer.addView(cardView)
        }
    }

    private fun buildAlertCard(alert: LostFoundAlert): View {
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

        // Top Row: Type Badge + ID & Date + Status
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val isLost = alert.tipo.equals("PERDIDO", ignoreCase = true)
        val typeBadge = TextView(this).apply {
            text = alert.tipo.uppercase()
            setBackgroundResource(if (isLost) R.drawable.bg_pill_rose else R.drawable.bg_pill_blue)
            setTextColor(ContextCompat.getColor(context, if (isLost) R.color.rose_700 else R.color.blue_800))
            textSize = 10f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding((8 * density).toInt(), (2 * density).toInt(), (8 * density).toInt(), (2 * density).toInt())
        }

        val idTv = TextView(this).apply {
            text = "  ${alert.id} • ${alert.fecha_evento}"
            setTextColor(ContextCompat.getColor(context, R.color.slate_500))
            textSize = 11f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val statusTv = TextView(this).apply {
            text = alert.estado
            setTextColor(ContextCompat.getColor(context, if (alert.estado.contains("Reunificado")) R.color.emerald_700 else R.color.amber_700))
            textSize = 11f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        topRow.addView(typeBadge)
        topRow.addView(idTv)
        topRow.addView(statusTv)
        cardContent.addView(topRow)

        // Title / Name / Breed
        val titleTv = TextView(this).apply {
            val nameStr = alert.nombre ?: "Sin nombre"
            text = "$nameStr • ${alert.raza} (${alert.especie})"
            setTextColor(ContextCompat.getColor(context, R.color.slate_900))
            textSize = 14f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, (6 * density).toInt(), 0, 0)
        }
        cardContent.addView(titleTv)

        // Location & Contact
        val locTv = TextView(this).apply {
            text = "📍 Vereda: ${alert.vereda} • Contacto: ${alert.contacto_nombre} (${alert.contacto_telefono})"
            setTextColor(ContextCompat.getColor(context, R.color.slate_600))
            textSize = 12f
            setPadding(0, (2 * density).toInt(), 0, (2 * density).toInt())
        }
        cardContent.addView(locTv)

        // Narrative Description
        val descTv = TextView(this).apply {
            text = alert.descripcion
            setTextColor(ContextCompat.getColor(context, R.color.slate_700))
            textSize = 12f
        }
        cardContent.addView(descTv)

        // 20-Day Stray-to-Abandoned Warning (TRD FR-2.5)
        if (alert.dias_custodia_albergue >= 20 || alert.estado.contains("+20")) {
            val alert20Tv = TextView(this).apply {
                text = "⚠️ REGLA 20 DÍAS: Superó 20 días en custodia municipal sin reclamo formal. Declarado en abandono / Ingresa a programa de Adopción Municipal."
                setBackgroundResource(R.drawable.bg_pill_amber)
                setTextColor(ContextCompat.getColor(context, R.color.amber_900))
                textSize = 11f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding((10 * density).toInt(), (6 * density).toInt(), (10 * density).toInt(), (6 * density).toInt())
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, (8 * density).toInt(), 0, 0)
                }
                layoutParams = params
            }
            cardContent.addView(alert20Tv)
        }

        // Match Validation Box (TRD FR-2.4)
        if (!alert.posible_coincidencia_registro_id.isNullOrEmpty()) {
            val matchLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundResource(R.drawable.bg_pill_emerald)
                setPadding((10 * density).toInt(), (8 * density).toInt(), (10 * density).toInt(), (8 * density).toInt())
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, (8 * density).toInt(), 0, 0)
                }
                layoutParams = params
            }

            val matchTv = TextView(this).apply {
                val isResolved = alert.validacion_manual_aprobada
                text = if (isResolved) {
                    "✔ Coincidencia Validada Manualmente con Registro ${alert.posible_coincidencia_registro_id} (Caso Reunificado)"
                } else {
                    "🔍 Posible Coincidencia Algorítmica con Registro ${alert.posible_coincidencia_registro_id} (Requiere Validación Manual de Inspector)"
                }
                setTextColor(ContextCompat.getColor(context, if (isResolved) R.color.emerald_900 else R.color.blue_900))
                textSize = 11f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            matchLayout.addView(matchTv)

            if (!alert.validacion_manual_aprobada) {
                val btnApproveMatch = Button(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle).apply {
                    text = "Aprobar Validación Manual de Coincidencia"
                    textSize = 11f
                    setTextColor(ContextCompat.getColor(context, R.color.emerald_800))
                    val btnParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, (4 * density).toInt(), 0, 0)
                    }
                    layoutParams = btnParams
                    setOnClickListener {
                        Microdataset.approveManualMatch(alert.id)
                        Toast.makeText(context, "Coincidencia aprobada y reunificación registrada en el censo", Toast.LENGTH_SHORT).show()
                        refreshList()
                    }
                }
                matchLayout.addView(btnApproveMatch)
            }

            cardContent.addView(matchLayout)
        }

        cardView.addView(cardContent)
        return cardView
    }

    private fun showNewAlertDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_lost_found, null)
        val dialog = AlertDialog.Builder(this)
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

        spinnerType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("PERDIDO", "HALLADO"))
        spinnerSpecies.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Perro", "Gato", "Otro"))
        spinnerVereda.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Microdataset.OFFICIAL_VEREDAS)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val nameBreed = etNameBreed.text.toString().trim().ifEmpty { "Mestizo Criollo" }
            val contact = etContact.text.toString().trim().ifEmpty { "Ciudadano Zipaquirá - 3100000000" }
            val desc = etDescription.text.toString().trim().ifEmpty { "Sin descripción particular" }
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val isFound = spinnerType.selectedItem.toString() == "HALLADO"

            val newAlert = LostFoundAlert(
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
            Toast.makeText(this, "Alerta publicada exitosamente", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            refreshList()
        }

        dialog.show()
    }
}
