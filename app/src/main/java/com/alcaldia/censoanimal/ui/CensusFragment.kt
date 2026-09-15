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
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.data.UserProfile
import com.alcaldia.censoanimal.model.AnimalRecord
import com.alcaldia.censoanimal.model.UserRole
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

    private val sessionListener: (UserProfile) -> Unit = {
        activity?.runOnUiThread {
            updateMistreatmentButtonLabel()
        }
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

        btnOpenLostFound.setOnClickListener {
            startActivity(Intent(requireContext(), LostFoundActivity::class.java))
        }
        btnOpenMistreatment.setOnClickListener {
            val currentRole = AppSessionManager.currentProfile.role
            if (currentRole == com.alcaldia.censoanimal.model.UserRole.CIUDADANO) {
                startActivity(Intent(requireContext(), ReportMistreatmentActivity::class.java))
            } else {
                startActivity(Intent(requireContext(), MistreatmentActivity::class.java))
            }
        }

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
        updateMistreatmentButtonLabel()
        AppSessionManager.addSessionListener(sessionListener)
        applyFilters()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        AppSessionManager.removeSessionListener(sessionListener)
    }

    override fun onResume() {
        super.onResume()
        updateMistreatmentButtonLabel()
        applyFilters()
    }

    private fun updateMistreatmentButtonLabel() {
        if (AppSessionManager.currentProfile.role == UserRole.CIUDADANO) {
            btnOpenMistreatment.text = "🛡️ Denunciar Maltrato"
        } else {
            btnOpenMistreatment.text = "🛡️ Casos de Maltrato"
        }
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
}

