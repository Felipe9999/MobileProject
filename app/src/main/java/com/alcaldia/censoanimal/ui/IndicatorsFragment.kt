package com.alcaldia.censoanimal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.adapter.MunicipalIndicatorAdapter
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.MunicipalIndicator

class IndicatorsFragment : Fragment() {

    private lateinit var rvMunicipalIndicators: RecyclerView
    private lateinit var adapter: MunicipalIndicatorAdapter

    private lateinit var chipCategoryAll: TextView
    private lateinit var chipCategoryHealth: TextView
    private lateinit var chipCategoryPopulation: TextView
    private lateinit var chipCategoryTerritory: TextView
    private lateinit var chipCategoryQuality: TextView

    private var selectedCategory = "ALL"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_indicators, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvMunicipalIndicators = view.findViewById(R.id.rvMunicipalIndicators)
        chipCategoryAll = view.findViewById(R.id.chipCategoryAll)
        chipCategoryHealth = view.findViewById(R.id.chipCategoryHealth)
        chipCategoryPopulation = view.findViewById(R.id.chipCategoryPopulation)
        chipCategoryTerritory = view.findViewById(R.id.chipCategoryTerritory)
        chipCategoryQuality = view.findViewById(R.id.chipCategoryQuality)

        rvMunicipalIndicators.layoutManager = LinearLayoutManager(requireContext())
        adapter = MunicipalIndicatorAdapter(Microdataset.MUNICIPAL_INDICATORS)
        rvMunicipalIndicators.adapter = adapter

        setupCategoryChips()
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            chipCategoryAll,
            chipCategoryHealth,
            chipCategoryPopulation,
            chipCategoryTerritory,
            chipCategoryQuality
        )

        fun selectChip(selected: TextView, categoryKey: String) {
            selectedCategory = categoryKey
            for (chip in chips) {
                if (chip == selected) {
                    chip.setBackgroundResource(R.drawable.bg_button_primary)
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    chip.setBackgroundResource(R.drawable.bg_pill_slate)
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.slate_700))
                }
            }
            filterIndicators()
        }

        chipCategoryAll.setOnClickListener { selectChip(chipCategoryAll, "ALL") }
        chipCategoryHealth.setOnClickListener { selectChip(chipCategoryHealth, "Salud Pública") }
        chipCategoryPopulation.setOnClickListener { selectChip(chipCategoryPopulation, "Población") }
        chipCategoryTerritory.setOnClickListener { selectChip(chipCategoryTerritory, "Territorio") }
        chipCategoryQuality.setOnClickListener { selectChip(chipCategoryQuality, "Calidad de Datos") }
    }

    private fun filterIndicators() {
        val all = Microdataset.MUNICIPAL_INDICATORS
        val filtered = if (selectedCategory == "ALL") {
            all
        } else {
            all.filter { it.categoria.equals(selectedCategory, ignoreCase = true) }
        }
        adapter.updateData(filtered)
    }
}
