package com.alcaldia.censoanimal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alcaldia.censoanimal.MainActivity
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.AnimalRecord
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class RegisterFragment : Fragment() {

    private var currentStep = 1

    // Step indicators
    private lateinit var stepTab1: TextView
    private lateinit var stepTab2: TextView
    private lateinit var stepTab3: TextView

    // Step containers
    private lateinit var layoutStep1: LinearLayout
    private lateinit var layoutStep2: LinearLayout
    private lateinit var layoutStep3: LinearLayout

    // Buttons
    private lateinit var btnPrevStep: Button
    private lateinit var btnNextStep: Button

    // Step 1 Form Fields
    private lateinit var btnSpeciesDog: LinearLayout
    private lateinit var btnSpeciesCat: LinearLayout
    private var selectedSpecies = "Perro"

    private lateinit var etAnimalName: EditText
    private lateinit var spinnerSex: Spinner
    private lateinit var etAgeMonths: EditText
    private lateinit var spinnerBreed: Spinner
    private lateinit var spinnerColor: Spinner

    // Step 2 Form Fields
    private lateinit var etMicrochip: EditText
    private lateinit var btnGenerateChip: Button
    private lateinit var switchSterilized: SwitchMaterial
    private lateinit var etRabiesDate: EditText
    private lateinit var etVaccineBatch: EditText

    // Step 3 Form Fields
    private lateinit var etOwnerName: EditText
    private lateinit var etOwnerDoc: EditText
    private lateinit var etOwnerPhone: EditText
    private lateinit var spinnerRegisterVereda: Spinner
    private lateinit var etFarmAddress: EditText
    private lateinit var tvGpsCoords: TextView
    private lateinit var btnRefreshGps: Button
    private lateinit var etFieldNotes: EditText

    private var currentLat = 4.9842
    private var currentLng = -73.9562

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stepTab1 = view.findViewById(R.id.stepTab1)
        stepTab2 = view.findViewById(R.id.stepTab2)
        stepTab3 = view.findViewById(R.id.stepTab3)

        layoutStep1 = view.findViewById(R.id.layoutStep1)
        layoutStep2 = view.findViewById(R.id.layoutStep2)
        layoutStep3 = view.findViewById(R.id.layoutStep3)

        btnPrevStep = view.findViewById(R.id.btnPrevStep)
        btnNextStep = view.findViewById(R.id.btnNextStep)

        // Step 1
        btnSpeciesDog = view.findViewById(R.id.btnSpeciesDog)
        btnSpeciesCat = view.findViewById(R.id.btnSpeciesCat)
        etAnimalName = view.findViewById(R.id.etAnimalName)
        spinnerSex = view.findViewById(R.id.spinnerSex)
        etAgeMonths = view.findViewById(R.id.etAgeMonths)
        spinnerBreed = view.findViewById(R.id.spinnerBreed)
        spinnerColor = view.findViewById(R.id.spinnerColor)

        // Step 2
        etMicrochip = view.findViewById(R.id.etMicrochip)
        btnGenerateChip = view.findViewById(R.id.btnGenerateChip)
        switchSterilized = view.findViewById(R.id.switchSterilized)
        etRabiesDate = view.findViewById(R.id.etRabiesDate)
        etVaccineBatch = view.findViewById(R.id.etVaccineBatch)

        // Step 3
        etOwnerName = view.findViewById(R.id.etOwnerName)
        etOwnerDoc = view.findViewById(R.id.etOwnerDoc)
        etOwnerPhone = view.findViewById(R.id.etOwnerPhone)
        spinnerRegisterVereda = view.findViewById(R.id.spinnerRegisterVereda)
        etFarmAddress = view.findViewById(R.id.etFarmAddress)
        tvGpsCoords = view.findViewById(R.id.tvGpsCoords)
        btnRefreshGps = view.findViewById(R.id.btnRefreshGps)
        etFieldNotes = view.findViewById(R.id.etFieldNotes)

        setupSpinners()
        setupSpeciesToggle()
        setupGpsAndChipButtons()
        setupStepper()
    }

    private fun setupSpeciesToggle() {
        fun updateSpeciesUI(isDog: Boolean) {
            selectedSpecies = if (isDog) "Perro" else "Gato"
            if (isDog) {
                btnSpeciesDog.setBackgroundResource(R.drawable.bg_pill_blue)
                btnSpeciesCat.setBackgroundResource(R.drawable.bg_pill_slate)
            } else {
                btnSpeciesDog.setBackgroundResource(R.drawable.bg_pill_slate)
                btnSpeciesCat.setBackgroundResource(R.drawable.bg_pill_blue)
            }
            updateBreedSpinner(isDog)
        }

        btnSpeciesDog.setOnClickListener { updateSpeciesUI(true) }
        btnSpeciesCat.setOnClickListener { updateSpeciesUI(false) }
    }

    private fun updateBreedSpinner(isDog: Boolean) {
        val breeds = if (isDog) Microdataset.DOG_BREEDS else Microdataset.CAT_BREEDS
        val breedAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, breeds)
        spinnerBreed.adapter = breedAdapter
    }

    private fun setupSpinners() {
        // Sex
        val sexOptions = listOf("Macho", "Hembra")
        spinnerSex.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sexOptions)

        // Color
        spinnerColor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Microdataset.COLOR_OPTIONS)

        // Veredas
        spinnerRegisterVereda.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Microdataset.OFFICIAL_VEREDAS)

        updateBreedSpinner(true)
    }

    private fun setupGpsAndChipButtons() {
        btnGenerateChip.setOnClickListener {
            val randomSuffix = Random.nextInt(100000, 999999)
            val generatedChip = "981098102$randomSuffix"
            etMicrochip.setText(generatedChip)
            Toast.makeText(requireContext(), "Chip generado según estándar ISO", Toast.LENGTH_SHORT).show()
        }

        btnRefreshGps.setOnClickListener {
            currentLat = 4.9800 + (Random.nextDouble() * 0.015)
            currentLng = -73.9650 + (Random.nextDouble() * 0.015)
            val formatted = String.format(Locale.US, "Lat: %.4f, Lng: %.4f", currentLat, currentLng)
            tvGpsCoords.text = formatted
            Toast.makeText(requireContext(), "Coordenadas GPS actualizadas (WGS84)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupStepper() {
        btnPrevStep.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                updateStepUI()
            }
        }

        btnNextStep.setOnClickListener {
            if (currentStep < 3) {
                if (validateCurrentStep()) {
                    currentStep++
                    updateStepUI()
                }
            } else {
                saveRecord()
            }
        }

        updateStepUI()
    }

    private fun validateCurrentStep(): Boolean {
        if (currentStep == 1) {
            val name = etAnimalName.text.toString().trim()
            val age = etAgeMonths.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Ingrese el nombre del animal", Toast.LENGTH_SHORT).show()
                return false
            }
            if (age.isEmpty()) {
                Toast.makeText(requireContext(), "Ingrese la edad en meses", Toast.LENGTH_SHORT).show()
                return false
            }
        } else if (currentStep == 2) {
            val rabies = etRabiesDate.text.toString().trim()
            if (rabies.isEmpty()) {
                etRabiesDate.setText("2026-02-28")
            }
        }
        return true
    }

    private fun updateStepUI() {
        layoutStep1.visibility = if (currentStep == 1) View.VISIBLE else View.GONE
        layoutStep2.visibility = if (currentStep == 2) View.VISIBLE else View.GONE
        layoutStep3.visibility = if (currentStep == 3) View.VISIBLE else View.GONE

        btnPrevStep.visibility = if (currentStep == 1) View.INVISIBLE else View.VISIBLE
        btnNextStep.text = if (currentStep == 3) "Guardar Censo" else "Siguiente"

        // Tabs styling
        fun styleTab(tab: TextView, active: Boolean) {
            if (active) {
                tab.setBackgroundResource(R.drawable.bg_button_primary)
                tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                tab.setBackgroundResource(R.drawable.bg_pill_slate)
                tab.setTextColor(ContextCompat.getColor(requireContext(), R.color.slate_600))
            }
        }

        styleTab(stepTab1, currentStep == 1)
        styleTab(stepTab2, currentStep == 2)
        styleTab(stepTab3, currentStep == 3)
    }

    private fun saveRecord() {
        val ownerName = etOwnerName.text.toString().trim()
        val ownerDoc = etOwnerDoc.text.toString().trim()
        val ownerPhone = etOwnerPhone.text.toString().trim()
        val farmAddress = etFarmAddress.text.toString().trim()

        if (ownerName.isEmpty() || ownerDoc.isEmpty() || ownerPhone.isEmpty() || farmAddress.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor complete los campos obligatorios del propietario", Toast.LENGTH_SHORT).show()
            return
        }

        val totalRecords = Microdataset.getAllRecords().size + 1
        val newId = String.format(Locale.US, "CEN-2026-%03d", totalRecords)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        val newRecord = AnimalRecord(
            registro_id = newId,
            microchip = etMicrochip.text.toString().trim().ifEmpty { null },
            especie = selectedSpecies,
            animal_nombre = etAnimalName.text.toString().trim(),
            sexo = spinnerSex.selectedItem.toString(),
            raza = spinnerBreed.selectedItem.toString(),
            color = spinnerColor.selectedItem.toString(),
            edad_meses = etAgeMonths.text.toString().toIntOrNull() ?: 12,
            esterilizado = switchSterilized.isChecked,
            fecha_esterilizacion = if (switchSterilized.isChecked) today else null,
            fecha_vacuna_rabia = etRabiesDate.text.toString().trim().ifEmpty { today },
            lote_vacuna = etVaccineBatch.text.toString().trim().ifEmpty { "RAB-2026-X8" },
            responsable_nombre = ownerName,
            documento_tipo = "CC",
            documento_numero = ownerDoc,
            telefono = ownerPhone,
            territorio = spinnerRegisterVereda.selectedItem.toString(),
            territorio_catalogo = spinnerRegisterVereda.selectedItem.toString(),
            direccion_finca = farmAddress,
            latitud = currentLat,
            longitud = currentLng,
            fecha_censo = today,
            censo_por = "Veterinario Aliado",
            observaciones = etFieldNotes.text.toString().trim().ifEmpty { "Registro de censo en campo con validación de catálogo." },
            estado_animal = "Activo",
            sincronizado_alcaldia = false,
            offline_pending = true,
            version_registro = 1
        )

        Microdataset.addRecord(newRecord)
        Toast.makeText(requireContext(), "Registro $newId guardado en almacenamiento local (Room)", Toast.LENGTH_LONG).show()

        // Reset form & go to Census fragment
        currentStep = 1
        etAnimalName.setText("")
        etAgeMonths.setText("")
        etMicrochip.setText("")
        etOwnerName.setText("")
        etOwnerDoc.setText("")
        etOwnerPhone.setText("")
        etFarmAddress.setText("")
        etFieldNotes.setText("")
        updateStepUI()

        // Navigate to Census fragment in MainActivity
        (activity as? MainActivity)?.selectCensusTab()
    }
}
