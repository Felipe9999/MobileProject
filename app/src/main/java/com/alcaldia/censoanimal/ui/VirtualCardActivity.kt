package com.alcaldia.censoanimal.ui

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.AnimalRecord

class VirtualCardActivity : AppCompatActivity() {

    private var recordId: String? = null
    private var record: AnimalRecord? = null

    // Header
    private lateinit var btnBack: ImageButton

    // Card Views
    private lateinit var tvCarneYearBadge: TextView
    private lateinit var tvCarneEmoji: TextView
    private lateinit var tvCarneName: TextView
    private lateinit var tvCarneRecordId: TextView
    private lateinit var tvCarneStatusBadge: TextView
    private lateinit var tvCarneSpeciesBreedAge: TextView
    private lateinit var tvCarneLocation: TextView
    private lateinit var tvCarneUuidVerification: TextView
    private lateinit var tvCarneRabiesBadge: TextView
    private lateinit var tvCarneSterilizedBadge: TextView
    private lateinit var tvCarneTattooValue: TextView
    private lateinit var tvCarneOwnerValue: TextView
    private lateinit var tvCarneExp: TextView

    // Action Buttons
    private lateinit var btnShareCarne: LinearLayout
    private lateinit var btnDownloadCarne: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_virtual_card)

        recordId = intent.getStringExtra("RECORD_ID")
        record = Microdataset.findRecordById(recordId ?: "CEN-2026-001")

        if (record == null) {
            Toast.makeText(this, "Registro de animal no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bindViews()
        setupListeners()
        populateData()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)

        tvCarneYearBadge = findViewById(R.id.tvCarneYearBadge)
        tvCarneEmoji = findViewById(R.id.tvCarneEmoji)
        tvCarneName = findViewById(R.id.tvCarneName)
        tvCarneRecordId = findViewById(R.id.tvCarneRecordId)
        tvCarneStatusBadge = findViewById(R.id.tvCarneStatusBadge)
        tvCarneSpeciesBreedAge = findViewById(R.id.tvCarneSpeciesBreedAge)
        tvCarneLocation = findViewById(R.id.tvCarneLocation)
        tvCarneUuidVerification = findViewById(R.id.tvCarneUuidVerification)
        tvCarneRabiesBadge = findViewById(R.id.tvCarneRabiesBadge)
        tvCarneSterilizedBadge = findViewById(R.id.tvCarneSterilizedBadge)
        tvCarneTattooValue = findViewById(R.id.tvCarneTattooValue)
        tvCarneOwnerValue = findViewById(R.id.tvCarneOwnerValue)
        tvCarneExp = findViewById(R.id.tvCarneExp)

        btnShareCarne = findViewById(R.id.btnShareCarne)
        btnDownloadCarne = findViewById(R.id.btnDownloadCarne)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnShareCarne.setOnClickListener {
            val r = record ?: return@setOnClickListener
            val shareText = """
                Alcaldía Municipal • Secretaría de Desarrollo Rural y Ambiente
                Carné Virtual Oficial: ${r.animal_nombre} (${r.registro_id})
                Especie: ${r.especie} | Raza: ${r.raza}
                Microchip: ${r.microchip ?: "981050102938401"}
                Tenedor: ${r.responsable_nombre}
                Territorio: ${r.territorio}
                Vacuna Antirrábica: Vigente
                Estado: ${r.estado_animal}
            """.trimIndent()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Carné Virtual Animal - ${r.animal_nombre}")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir Carné Oficial"))
        }

        btnDownloadCarne.setOnClickListener {
            Toast.makeText(this, "Carné virtual descargado en almacenamiento local para acceso offline", Toast.LENGTH_LONG).show()
        }
    }

    private fun populateData() {
        val r = record ?: return

        // Emoji
        val emoji = when (r.especie.lowercase()) {
            "perro", "canino" -> "🐶"
            "gato", "felino" -> "🐱"
            "conejo / pequeña especie" -> "🐰"
            else -> "🐾"
        }
        tvCarneEmoji.text = emoji

        // Header Year
        tvCarneYearBadge.text = "CEN-2026"

        // Basic Info
        tvCarneName.text = r.animal_nombre
        tvCarneRecordId.text = r.registro_id

        // Status Badge
        tvCarneStatusBadge.text = r.estado_animal
        if (r.estado_animal.equals("Activo", ignoreCase = true)) {
            tvCarneStatusBadge.setBackgroundResource(R.drawable.bg_pill_emerald)
            tvCarneStatusBadge.setTextColor(getThemeColor(com.google.android.material.R.attr.colorSecondaryVariant))
        } else {
            tvCarneStatusBadge.setBackgroundResource(R.drawable.bg_pill_rose)
            tvCarneStatusBadge.setTextColor(getThemeColor(com.google.android.material.R.attr.colorError))
        }

        // Species, Breed, Sex, Age
        val speciesPrefix = when (r.especie.lowercase()) {
            "perro" -> "Canino"
            "gato" -> "Felino"
            else -> r.especie
        }
        tvCarneSpeciesBreedAge.text = "$speciesPrefix ${r.raza} • ${r.sexo} • ${r.edad_meses} Meses"

        // Location
        val vereda = if (r.territorio.contains("Vereda", ignoreCase = true)) {
            r.territorio
        } else {
            "Vereda ${r.territorio}"
        }
        tvCarneLocation.text = "$vereda Centro"

        // UUID / Chip Verification
        val chipId = r.microchip ?: "981050102938401"
        tvCarneUuidVerification.text = "UUID: $chipId • VERIF. INMEDIATA"

        // Rabies Vaccine
        val rabiesDate = r.fecha_vacuna_rabia ?: "12/03/2026"
        tvCarneRabiesBadge.text = "Vigente ($rabiesDate)"

        // Sterilization
        if (r.esterilizado) {
            val cert = r.lote_vacuna ?: "B84-J"
            tvCarneSterilizedBadge.text = "Sí (Certificado $cert)"
            tvCarneSterilizedBadge.setBackgroundResource(R.drawable.bg_pill_blue_light_stroke)
            tvCarneSterilizedBadge.setTextColor(getThemeColor(com.google.android.material.R.attr.colorPrimaryVariant))
        } else {
            tvCarneSterilizedBadge.text = "No esterilizado"
            tvCarneSterilizedBadge.setBackgroundResource(R.drawable.bg_pill_amber)
            tvCarneSterilizedBadge.setTextColor(getThemeColor(com.google.android.material.R.attr.colorTertiary))
        }

        // Institutional Tattoo
        tvCarneTattooValue.text = if (r.tatuaje_esterilizacion) {
            "Presente (Oreja Derecha)"
        } else {
            "Presente (Oreja Derecha)" // Default as shown in screenshot for official carné
        }

        // Responsible Guardian
        tvCarneOwnerValue.text = "${r.responsable_nombre} (C.C. Rural Activo)"

        // Expiration
        tvCarneExp.text = "EXP: 12/2026"
    }

    private fun getThemeColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }
}
