package com.alcaldia.censoanimal.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.AnimalRecord
import com.alcaldia.censoanimal.model.UserRole
import com.google.android.material.card.MaterialCardView

class MapFragment : Fragment() {

    private lateinit var spinnerMapVereda: Spinner
    private lateinit var tvMapDogsCount: TextView
    private lateinit var tvMapCatsCount: TextView
    private lateinit var webViewMap: WebView
    private lateinit var cardMapSelectedRecord: MaterialCardView

    // Selected record views
    private lateinit var tvMapSelectedEmoji: TextView
    private lateinit var tvMapSelectedName: TextView
    private lateinit var tvMapSelectedId: TextView
    private lateinit var tvMapSelectedBreed: TextView
    private lateinit var tvMapSelectedGps: TextView
    private lateinit var tvMapSelectedOwner: TextView
    private lateinit var btnMapOpenDetail: Button

    private lateinit var btnZoomIn: ImageButton
    private lateinit var btnZoomOut: ImageButton
    private lateinit var btnRecenterMap: ImageButton

    private var selectedVereda = "Todas las Veredas"
    private var currentlySelectedRecord: AnimalRecord? = null
    private var isMapConfigured = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!checkRoleAccess()) return

        spinnerMapVereda = view.findViewById(R.id.spinnerMapVereda)
        tvMapDogsCount = view.findViewById(R.id.tvMapDogsCount)
        tvMapCatsCount = view.findViewById(R.id.tvMapCatsCount)
        webViewMap = view.findViewById(R.id.webViewMap)
        cardMapSelectedRecord = view.findViewById(R.id.cardMapSelectedRecord)

        tvMapSelectedEmoji = view.findViewById(R.id.tvMapSelectedEmoji)
        tvMapSelectedName = view.findViewById(R.id.tvMapSelectedName)
        tvMapSelectedId = view.findViewById(R.id.tvMapSelectedId)
        tvMapSelectedBreed = view.findViewById(R.id.tvMapSelectedBreed)
        tvMapSelectedGps = view.findViewById(R.id.tvMapSelectedGps)
        tvMapSelectedOwner = view.findViewById(R.id.tvMapSelectedOwner)
        btnMapOpenDetail = view.findViewById(R.id.btnMapOpenDetail)

        btnZoomIn = view.findViewById(R.id.btnZoomIn)
        btnZoomOut = view.findViewById(R.id.btnZoomOut)
        btnRecenterMap = view.findViewById(R.id.btnRecenterMap)

        setupVeredaSpinner()
        setupZoomControls()

        btnMapOpenDetail.setOnClickListener {
            currentlySelectedRecord?.let { record ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("RECORD_ID", record.registro_id)
                startActivity(intent)
            }
        }

        initOsmMap()
    }

    override fun onResume() {
        super.onResume()
        if (checkRoleAccess()) {
            updateCounts()
            if (!isMapConfigured) {
                initOsmMap()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isMapConfigured = false
    }

    private fun checkRoleAccess(): Boolean {
        val role = AppSessionManager.currentProfile.role
        if (role != UserRole.VETERINARIO && role != UserRole.ADMINISTRADOR) {
            (activity as? com.alcaldia.censoanimal.MainActivity)?.selectCensusTab()
            return false
        }
        return true
    }

    private fun setupVeredaSpinner() {
        val veredas = mutableListOf("Todas las Veredas")
        veredas.addAll(Microdataset.OFFICIAL_VEREDAS)

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            veredas
        )
        spinnerMapVereda.adapter = spinnerAdapter

        spinnerMapVereda.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedVereda = veredas[position]
                updateCounts()
                if (isMapConfigured) {
                    OpenStreetMapHelper.filterAnimalMapVereda(webViewMap, selectedVereda)
                }

                val filtered = getFilteredRecords()
                if (filtered.isNotEmpty()) {
                    selectRecord(filtered[0])
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupZoomControls() {
        btnZoomIn.setOnClickListener {
            OpenStreetMapHelper.zoomInAnimalMap(webViewMap)
        }

        btnZoomOut.setOnClickListener {
            OpenStreetMapHelper.zoomOutAnimalMap(webViewMap)
        }

        btnRecenterMap.setOnClickListener {
            OpenStreetMapHelper.fitAllAnimalMap(webViewMap)
        }
    }

    private fun getFilteredRecords(): List<AnimalRecord> {
        val allRecords = Microdataset.getAllRecords()
        return allRecords.filter {
            selectedVereda == "Todas las Veredas" || it.territorio.equals(selectedVereda, ignoreCase = true)
        }
    }

    private fun updateCounts() {
        val filtered = getFilteredRecords()
        val dogsCount = filtered.count { it.especie.equals("Perro", ignoreCase = true) }
        val catsCount = filtered.count { it.especie.equals("Gato", ignoreCase = true) }
        tvMapDogsCount.text = "🐶 Caninos: $dogsCount"
        tvMapCatsCount.text = "🐱 Felinos: $catsCount"
    }

    private fun initOsmMap() {
        val allRecords = Microdataset.getAllRecords()
        updateCounts()

        OpenStreetMapHelper.configureAnimalMapWebView(
            webViewMap,
            allRecords,
            selectedVereda,
            object : OpenStreetMapHelper.OnAnimalSelectedListener {
                override fun onAnimalSelected(recordId: String) {
                    val found = Microdataset.getAllRecords().find { it.registro_id == recordId }
                    found?.let { selectRecord(it) }
                }
            }
        )
        isMapConfigured = true

        val filtered = getFilteredRecords()
        if (filtered.isNotEmpty()) {
            selectRecord(filtered[0])
        }
    }

    private fun selectRecord(record: AnimalRecord) {
        currentlySelectedRecord = record
        val isDog = record.especie.equals("Perro", ignoreCase = true)
        tvMapSelectedEmoji.text = if (isDog) "🐶" else "🐱"
        tvMapSelectedName.text = record.animal_nombre
        tvMapSelectedId.text = record.registro_id
        tvMapSelectedBreed.text = "${record.raza} • ${record.territorio}"
        tvMapSelectedGps.text = "GPS: %.4f, %.4f".format(record.latitud, record.longitud)
        tvMapSelectedOwner.text = "Resp: ${record.responsable_nombre}"
    }
}
