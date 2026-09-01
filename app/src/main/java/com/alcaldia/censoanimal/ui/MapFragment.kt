package com.alcaldia.censoanimal.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.AnimalRecord
import com.google.android.material.card.MaterialCardView

class MapFragment : Fragment() {

    private lateinit var spinnerMapVereda: Spinner
    private lateinit var tvMapDogsCount: TextView
    private lateinit var tvMapCatsCount: TextView
    private lateinit var layoutAnimalPins: FrameLayout
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

    private var selectedVereda = "Todas las Veredas"
    private var currentlySelectedRecord: AnimalRecord? = null
    private var currentZoom = 1.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinnerMapVereda = view.findViewById(R.id.spinnerMapVereda)
        tvMapDogsCount = view.findViewById(R.id.tvMapDogsCount)
        tvMapCatsCount = view.findViewById(R.id.tvMapCatsCount)
        layoutAnimalPins = view.findViewById(R.id.layoutAnimalPins)
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

        setupVeredaSpinner()
        setupZoomControls()

        btnMapOpenDetail.setOnClickListener {
            currentlySelectedRecord?.let { record ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("RECORD_ID", record.registro_id)
                startActivity(intent)
            }
        }

        // Delay pin rendering until layout measured
        layoutAnimalPins.post {
            renderMapPins()
        }
    }

    override fun onResume() {
        super.onResume()
        renderMapPins()
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
                renderMapPins()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupZoomControls() {
        btnZoomIn.setOnClickListener {
            if (currentZoom < 1.6f) {
                currentZoom += 0.2f
                layoutAnimalPins.scaleX = currentZoom
                layoutAnimalPins.scaleY = currentZoom
            }
        }

        btnZoomOut.setOnClickListener {
            if (currentZoom > 0.8f) {
                currentZoom -= 0.2f
                layoutAnimalPins.scaleX = currentZoom
                layoutAnimalPins.scaleY = currentZoom
            }
        }
    }

    private fun renderMapPins() {
        layoutAnimalPins.removeAllViews()

        val allRecords = Microdataset.getAllRecords()
        val filtered = allRecords.filter {
            selectedVereda == "Todas las Veredas" || it.territorio.equals(selectedVereda, ignoreCase = true)
        }

        val dogsCount = filtered.count { it.especie.equals("Perro", ignoreCase = true) }
        val catsCount = filtered.count { it.especie.equals("Gato", ignoreCase = true) }
        tvMapDogsCount.text = "🐶 Caninos: $dogsCount"
        tvMapCatsCount.text = "🐱 Felinos: $catsCount"

        val containerWidth = layoutAnimalPins.width
        val containerHeight = layoutAnimalPins.height

        if (containerWidth == 0 || containerHeight == 0) return

        // Geographic boundaries for normalized projection
        // Lat: 4.9750 to 4.9960
        // Lng: -73.9660 to -73.9470
        val minLat = 4.9750
        val maxLat = 4.9960
        val minLng = -73.9660
        val maxLng = -73.9470

        for (record in filtered) {
            val normX = ((record.longitud - minLng) / (maxLng - minLng)).coerceIn(0.08, 0.92)
            val normY = (1.0 - ((record.latitud - minLat) / (maxLat - minLat))).coerceIn(0.08, 0.92)

            val posX = (normX * containerWidth).toInt()
            val posY = (normY * containerHeight).toInt()

            val pinView = createPinView(record)
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = posX - 20
            params.topMargin = posY - 20
            params.gravity = Gravity.TOP or Gravity.START
            layoutAnimalPins.addView(pinView, params)
        }

        // Set initial selected item if available
        if (filtered.isNotEmpty()) {
            selectRecord(filtered[0])
        }
    }

    private fun createPinView(record: AnimalRecord): View {
        val isDog = record.especie.equals("Perro", ignoreCase = true)
        val isSelected = currentlySelectedRecord?.registro_id == record.registro_id

        val pinContainer = LinearLayout(requireContext())
        pinContainer.orientation = LinearLayout.VERTICAL
        pinContainer.gravity = Gravity.CENTER_HORIZONTAL
        pinContainer.setPadding(4, 4, 4, 4)

        val circleFrame = FrameLayout(requireContext())
        val size = if (isSelected) 40 else 32
        val circleParams = LinearLayout.LayoutParams(size, size)
        circleFrame.layoutParams = circleParams

        if (record.alerta_duplicado) {
            circleFrame.setBackgroundResource(R.drawable.bg_pill_rose)
        } else if (isDog) {
            circleFrame.setBackgroundResource(R.drawable.bg_pill_blue)
        } else {
            circleFrame.setBackgroundResource(R.drawable.bg_pill_emerald)
        }

        val tvEmoji = TextView(requireContext())
        tvEmoji.text = if (isDog) "🐶" else "🐱"
        tvEmoji.textSize = if (isSelected) 16f else 13f
        val emojiParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        emojiParams.gravity = Gravity.CENTER
        circleFrame.addView(tvEmoji, emojiParams)

        pinContainer.addView(circleFrame)

        // Label pill under pin
        val tvLabel = TextView(requireContext())
        tvLabel.text = record.animal_nombre
        tvLabel.textSize = 9f
        tvLabel.setTextColor(resources.getColor(R.color.slate_900, null))
        tvLabel.setBackgroundResource(R.drawable.bg_card_white)
        tvLabel.setPadding(6, 2, 6, 2)
        pinContainer.addView(tvLabel)

        pinContainer.setOnClickListener {
            selectRecord(record)
            renderMapPins()
        }

        return pinContainer
    }

    private fun selectRecord(record: AnimalRecord) {
        currentlySelectedRecord = record
        val isDog = record.especie.equals("Perro", ignoreCase = true)
        tvMapSelectedEmoji.text = if (isDog) "🐶" else "🐱"
        tvMapSelectedName.text = record.animal_nombre
        tvMapSelectedId.text = record.registro_id
        tvMapSelectedBreed.text = "${record.raza} • ${record.territorio}"
        tvMapSelectedGps.text = "GPS: ${record.latitud}, ${record.longitud}"
        tvMapSelectedOwner.text = "Resp: ${record.responsable_nombre}"
    }
}
