package com.alcaldia.censoanimal.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.AnimalRecord
import com.google.android.material.card.MaterialCardView

class ScannerActivity : AppCompatActivity() {

    private lateinit var btnScannerBack: ImageButton
    private lateinit var tvScannerPrivacyText: TextView
    private lateinit var btnToggleScannerPrivacy: Button
    private lateinit var laserLine: View
    private lateinit var tvScanningStatus: TextView

    private lateinit var btnSimulateQr: Button
    private lateinit var btnSimulateRfid: Button

    private lateinit var etManualSearch: EditText
    private lateinit var btnSearchManual: Button

    private lateinit var cardScannedResult: MaterialCardView
    private lateinit var tvScannedEmoji: TextView
    private lateinit var tvScannedName: TextView
    private lateinit var tvScannedSub: TextView
    private lateinit var btnOpenScannedDetail: Button

    private var laserAnimator: ObjectAnimator? = null
    private var isOfficialMode = true
    private var detectedRecord: AnimalRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        btnScannerBack = findViewById(R.id.btnScannerBack)
        tvScannerPrivacyText = findViewById(R.id.tvScannerPrivacyText)
        btnToggleScannerPrivacy = findViewById(R.id.btnToggleScannerPrivacy)
        laserLine = findViewById(R.id.laserLine)
        tvScanningStatus = findViewById(R.id.tvScanningStatus)

        btnSimulateQr = findViewById(R.id.btnSimulateQr)
        btnSimulateRfid = findViewById(R.id.btnSimulateRfid)

        etManualSearch = findViewById(R.id.etManualSearch)
        btnSearchManual = findViewById(R.id.btnSearchManual)

        cardScannedResult = findViewById(R.id.cardScannedResult)
        tvScannedEmoji = findViewById(R.id.tvScannedEmoji)
        tvScannedName = findViewById(R.id.tvScannedName)
        tvScannedSub = findViewById(R.id.tvScannedSub)
        btnOpenScannedDetail = findViewById(R.id.btnOpenScannedDetail)

        val btnScannerAccount = findViewById<View>(R.id.btnScannerAccount)
        val tvScannerAccountLabel = findViewById<TextView>(R.id.tvScannerAccountLabel)
        TopBarAccountHelper.setupAccountButton(this, btnScannerAccount, tvScannerAccountLabel)

        btnScannerBack.setOnClickListener { finish() }

        btnToggleScannerPrivacy.setOnClickListener {
            isOfficialMode = !isOfficialMode
            if (isOfficialMode) {
                tvScannerPrivacyText.text = "Modo Oficial • Desbloquea titular"
                btnToggleScannerPrivacy.text = "Cambiar a Ciudadano"
            } else {
                tvScannerPrivacyText.text = "Modo Ciudadano • Habeas Data Protegido"
                btnToggleScannerPrivacy.text = "Cambiar a Oficial"
            }
        }

        startLaserAnimation()

        // Quick simulation buttons
        btnSimulateQr.setOnClickListener {
            val lucas = Microdataset.findRecordById("CEN-2026-001")
            if (lucas != null) {
                displayScannedAnimal(lucas, "Código QR Verificado: CEN-2026-001")
            }
        }

        btnSimulateRfid.setOnClickListener {
            val michi = Microdataset.findRecordById("CEN-2026-003")
            if (michi != null) {
                displayScannedAnimal(michi, "Chip RFID Leído: 981098102938403")
            }
        }

        btnSearchManual.setOnClickListener {
            val query = etManualSearch.text.toString().trim().lowercase()
            if (query.isEmpty()) {
                Toast.makeText(this, "Ingrese número de microchip o código de censo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val found = Microdataset.getAllRecords().find {
                it.registro_id.lowercase() == query ||
                it.microchip?.lowercase() == query ||
                it.animal_nombre.lowercase() == query
            }

            if (found != null) {
                displayScannedAnimal(found, "Registro encontrado")
            } else {
                Toast.makeText(this, "No se encontró ningún animal con '$query'", Toast.LENGTH_SHORT).show()
            }
        }

        btnOpenScannedDetail.setOnClickListener {
            detectedRecord?.let { record ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("RECORD_ID", record.registro_id)
                startActivity(intent)
            }
        }
    }

    private fun startLaserAnimation() {
        laserLine.post {
            laserAnimator = ObjectAnimator.ofFloat(laserLine, "translationY", 0f, 200f).apply {
                duration = 1800
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    private fun displayScannedAnimal(record: AnimalRecord, reason: String) {
        detectedRecord = record
        tvScanningStatus.text = "✓ $reason"
        val isDog = record.especie.equals("Perro", ignoreCase = true)
        tvScannedEmoji.text = if (isDog) "🐶" else "🐱"
        tvScannedName.text = "${record.animal_nombre} (${record.registro_id})"
        tvScannedSub.text = "${record.especie} • ${record.raza} • ${record.territorio}"
        cardScannedResult.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        laserAnimator?.cancel()
    }
}
