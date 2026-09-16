package com.alcaldia.censoanimal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.AnimalRecord
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Self-registration screen for regular citizens (UserRole.CIUDADANO).
 * Skips redundant owner and location inputs because the app already knows
 * the user's data from their session profile, utilizing friendly vocabulary
 * suited for self-registering their companion animals.
 */
class CitizenRegisterAnimalFragment : Fragment() {

    private var currentStep = 1

    // Stepper headers
    private lateinit var citizenStepTab1: TextView
    private lateinit var citizenStepTab2: TextView

    // Step containers
    private lateinit var layoutCitizenStep1: LinearLayout
    private lateinit var layoutCitizenStep2: LinearLayout

    // Header & summary badges
    private lateinit var tvCitizenAccountBadge: TextView
    private lateinit var tvSummaryCitizenName: TextView
    private lateinit var tvSummaryCitizenDoc: TextView
    private lateinit var tvSummaryCitizenLocation: TextView

    // Step 1 UI Elements
    private lateinit var btnCitizenSpeciesDog: LinearLayout
    private lateinit var btnCitizenSpeciesCat: LinearLayout
    private lateinit var btnCitizenSpeciesOther: LinearLayout
    private var selectedSpecies = "Perro"

    private lateinit var etCitizenAnimalName: EditText
    private lateinit var spinnerCitizenSex: Spinner
    private lateinit var etCitizenAgeMonths: EditText
    private lateinit var spinnerCitizenBreed: Spinner
    private lateinit var spinnerCitizenColor: Spinner
    private lateinit var etCitizenMicrochip: EditText

    // Step 2 UI Elements
    private lateinit var switchCitizenSterilized: SwitchMaterial
    private lateinit var switchCitizenSterilizationTattoo: SwitchMaterial
    private lateinit var etCitizenRabiesDate: EditText
    private lateinit var etCitizenVaccineBatch: EditText

    // Buttons
    private lateinit var btnCitizenPrevStep: Button
    private lateinit var btnCitizenNextStep: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_citizen_register_animal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        citizenStepTab1 = view.findViewById(R.id.citizenStepTab1)
        citizenStepTab2 = view.findViewById(R.id.citizenStepTab2)

        layoutCitizenStep1 = view.findViewById(R.id.layoutCitizenStep1)
        layoutCitizenStep2 = view.findViewById(R.id.layoutCitizenStep2)

        tvCitizenAccountBadge = view.findViewById(R.id.tvCitizenAccountBadge)
        tvSummaryCitizenName = view.findViewById(R.id.tvSummaryCitizenName)
        tvSummaryCitizenDoc = view.findViewById(R.id.tvSummaryCitizenDoc)
        tvSummaryCitizenLocation = view.findViewById(R.id.tvSummaryCitizenLocation)

        btnCitizenSpeciesDog = view.findViewById(R.id.btnCitizenSpeciesDog)
        btnCitizenSpeciesCat = view.findViewById(R.id.btnCitizenSpeciesCat)
        btnCitizenSpeciesOther = view.findViewById(R.id.btnCitizenSpeciesOther)

        etCitizenAnimalName = view.findViewById(R.id.etCitizenAnimalName)
        spinnerCitizenSex = view.findViewById(R.id.spinnerCitizenSex)
        etCitizenAgeMonths = view.findViewById(R.id.etCitizenAgeMonths)
        spinnerCitizenBreed = view.findViewById(R.id.spinnerCitizenBreed)
        spinnerCitizenColor = view.findViewById(R.id.spinnerCitizenColor)
        etCitizenMicrochip = view.findViewById(R.id.etCitizenMicrochip)

        switchCitizenSterilized = view.findViewById(R.id.switchCitizenSterilized)
        switchCitizenSterilizationTattoo = view.findViewById(R.id.switchCitizenSterilizationTattoo)
        etCitizenRabiesDate = view.findViewById(R.id.etCitizenRabiesDate)
        etCitizenVaccineBatch = view.findViewById(R.id.etCitizenVaccineBatch)

        btnCitizenPrevStep = view.findViewById(R.id.btnCitizenPrevStep)
        btnCitizenNextStep = view.findViewById(R.id.btnCitizenNextStep)

        populateCitizenSummary()
        setupSpinners()
        setupSpeciesToggle()
        setupStepper()
    }

    private fun populateCitizenSummary() {
        val profile = AppSessionManager.currentProfile
        tvCitizenAccountBadge.text = "Tenedor: ${profile.name} (${profile.professionalId})"
        tvSummaryCitizenName.text = "Tenedor: ${profile.name}"
        tvSummaryCitizenDoc.text = "Identificación: ${profile.professionalId}"
        tvSummaryCitizenLocation.text = "Jurisdicción: Zipaquirá • ${profile.defaultTerritory}"
    }

    private fun setupSpeciesToggle() {
        val selectSpecies = { species: String ->
            selectedSpecies = species
            btnCitizenSpeciesDog.setBackgroundResource(if (species == "Perro") R.drawable.bg_pill_blue else R.drawable.bg_pill_slate)
            btnCitizenSpeciesCat.setBackgroundResource(if (species == "Gato") R.drawable.bg_pill_blue else R.drawable.bg_pill_slate)
            btnCitizenSpeciesOther.setBackgroundResource(if (species != "Perro" && species != "Gato") R.drawable.bg_pill_blue else R.drawable.bg_pill_slate)

            val dogLabel = btnCitizenSpeciesDog.getChildAt(1) as? TextView
            val catLabel = btnCitizenSpeciesCat.getChildAt(1) as? TextView
            val otherLabel = btnCitizenSpeciesOther.getChildAt(1) as? TextView

            dogLabel?.setTextColor(ContextCompat.getColor(requireContext(), if (species == "Perro") R.color.blue_800 else R.color.slate_700))
            catLabel?.setTextColor(ContextCompat.getColor(requireContext(), if (species == "Gato") R.color.blue_800 else R.color.slate_700))
            otherLabel?.setTextColor(ContextCompat.getColor(requireContext(), if (species != "Perro" && species != "Gato") R.color.blue_800 else R.color.slate_700))

            updateBreedSpinner(species)
        }

        btnCitizenSpeciesDog.setOnClickListener { selectSpecies("Perro") }
        btnCitizenSpeciesCat.setOnClickListener { selectSpecies("Gato") }
        btnCitizenSpeciesOther.setOnClickListener { selectSpecies("Conejo / Pequeña Especie") }
    }

    private fun updateBreedSpinner(species: String) {
        val breeds = when (species) {
            "Perro" -> Microdataset.DOG_BREEDS
            "Gato" -> Microdataset.CAT_BREEDS
            else -> listOf("Mestizo / Criollo", "Conejo Enano / Criollo", "Cobayo / Pequeña Especie", "Otro")
        }
        val breedAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, breeds)
        spinnerCitizenBreed.adapter = breedAdapter
    }

    private fun setupSpinners() {
        // Sex
        val sexOptions = listOf("Macho", "Hembra")
        spinnerCitizenSex.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sexOptions)

        // Color
        spinnerCitizenColor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, Microdataset.COLOR_OPTIONS)

        updateBreedSpinner("Perro")
    }

    private fun setupStepper() {
        btnCitizenPrevStep.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                updateStepUI()
            }
        }

        btnCitizenNextStep.setOnClickListener {
            if (currentStep == 1) {
                if (validateStep1()) {
                    currentStep = 2
                    updateStepUI()
                }
            } else {
                saveCitizenRecord()
            }
        }

        updateStepUI()
    }

    private fun validateStep1(): Boolean {
        val name = etCitizenAnimalName.text.toString().trim()
        val ageStr = etCitizenAgeMonths.text.toString().trim()

        if (name.isEmpty()) {
            etCitizenAnimalName.error = "Ingresa el nombre de tu animal de compañía"
            etCitizenAnimalName.requestFocus()
            return false
        }
        if (ageStr.isEmpty()) {
            etCitizenAgeMonths.error = "Ingresa la edad aproximada en meses"
            etCitizenAgeMonths.requestFocus()
            return false
        }
        return true
    }

    private fun updateStepUI() {
        when (currentStep) {
            1 -> {
                layoutCitizenStep1.visibility = View.VISIBLE
                layoutCitizenStep2.visibility = View.GONE

                citizenStepTab1.setBackgroundResource(R.drawable.bg_button_primary)
                citizenStepTab1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                citizenStepTab2.setBackgroundResource(R.drawable.bg_pill_slate)
                citizenStepTab2.setTextColor(ContextCompat.getColor(requireContext(), R.color.slate_600))

                btnCitizenPrevStep.visibility = View.INVISIBLE
                btnCitizenNextStep.text = "Siguiente: Salud y Cuidados"
                btnCitizenNextStep.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_600))
            }
            2 -> {
                layoutCitizenStep1.visibility = View.GONE
                layoutCitizenStep2.visibility = View.VISIBLE

                citizenStepTab1.setBackgroundResource(R.drawable.bg_pill_slate)
                citizenStepTab1.setTextColor(ContextCompat.getColor(requireContext(), R.color.slate_600))

                citizenStepTab2.setBackgroundResource(R.drawable.bg_button_primary)
                citizenStepTab2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                btnCitizenPrevStep.visibility = View.VISIBLE
                btnCitizenNextStep.text = "Completar Registro"
                btnCitizenNextStep.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.emerald_600))
            }
        }
    }

    private fun saveCitizenRecord() {
        val profile = AppSessionManager.currentProfile
        val animalName = etCitizenAnimalName.text.toString().trim()
        val totalRecords = Microdataset.getAllRecords().size + 1
        val newId = String.format(Locale.US, "CEN-2026-%03d", totalRecords)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        val newRecord = AnimalRecord(
            registro_id = newId,
            microchip = etCitizenMicrochip.text.toString().trim().ifEmpty { null },
            especie = selectedSpecies,
            animal_nombre = animalName,
            sexo = spinnerCitizenSex.selectedItem.toString(),
            raza = spinnerCitizenBreed.selectedItem.toString(),
            color = spinnerCitizenColor.selectedItem.toString(),
            edad_meses = etCitizenAgeMonths.text.toString().toIntOrNull() ?: 12,
            esterilizado = switchCitizenSterilized.isChecked,
            fecha_esterilizacion = if (switchCitizenSterilized.isChecked) today else null,
            tatuaje_esterilizacion = switchCitizenSterilizationTattoo.isChecked,
            fecha_vacuna_rabia = etCitizenRabiesDate.text.toString().trim().ifEmpty { null },
            lote_vacuna = etCitizenVaccineBatch.text.toString().trim().ifEmpty { null },
            tipo_guardia = "Propietario / Tenedor Permanente",
            responsable_nombre = profile.name,
            documento_tipo = "CC",
            documento_numero = profile.documentNumber.ifEmpty { "19384921" },
            telefono = profile.phone.ifEmpty { "3104829102" },
            territorio = profile.defaultTerritory,
            territorio_catalogo = profile.defaultTerritory,
            direccion_finca = profile.defaultAddress,
            latitud = 4.9842,
            longitud = -73.9562,
            fecha_censo = today,
            censo_por = "Autoregistro Ciudadano",
            observaciones = "Autoregistro realizado por el tenedor responsable desde la aplicación ciudadana.",
            estado_animal = "Activo",
            sincronizado_alcaldia = false,
            offline_pending = true,
            version_registro = 1
        )

        Microdataset.addRecord(newRecord)
        Toast.makeText(
            requireContext(),
            "¡${newRecord.animal_nombre} ha sido registrado(a) con éxito en el censo municipal!",
            Toast.LENGTH_LONG
        ).show()

        // Reset state
        currentStep = 1
        etCitizenAnimalName.setText("")
        etCitizenAgeMonths.setText("")
        etCitizenMicrochip.setText("")
        etCitizenRabiesDate.setText("")
        etCitizenVaccineBatch.setText("")
        switchCitizenSterilized.isChecked = false
        switchCitizenSterilizationTattoo.isChecked = false
        updateStepUI()

        // Navigate directly to the citizen's animals tab ("Mis Animales")
        (activity as? MainActivity)?.selectCensusTab()
    }
}
