package com.alcaldia.censoanimal.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.adapter.SyncRecordAdapter
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.AnimalRecord
import com.google.android.material.card.MaterialCardView
import kotlin.random.Random

class OfflineSyncFragment : Fragment() {

    private lateinit var tvLocalCount: TextView
    private lateinit var tvServerCount: TextView
    private lateinit var tvPendingCount: TextView
    private lateinit var btnSyncAll: Button
    private lateinit var cardDuplicateWarning: MaterialCardView
    private lateinit var btnResolveConflict: Button
    private lateinit var rvSyncRecords: RecyclerView
    private lateinit var syncAdapter: SyncRecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_offline_sync, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLocalCount = view.findViewById(R.id.tvLocalCount)
        tvServerCount = view.findViewById(R.id.tvServerCount)
        tvPendingCount = view.findViewById(R.id.tvPendingCount)
        btnSyncAll = view.findViewById(R.id.btnSyncAll)
        cardDuplicateWarning = view.findViewById(R.id.cardDuplicateWarning)
        btnResolveConflict = view.findViewById(R.id.btnResolveConflict)
        rvSyncRecords = view.findViewById(R.id.rvSyncRecords)

        rvSyncRecords.layoutManager = LinearLayoutManager(requireContext())
        syncAdapter = SyncRecordAdapter(Microdataset.getAllRecords()) { conflictRecord ->
            showResolveConflictDialog(conflictRecord)
        }
        rvSyncRecords.adapter = syncAdapter

        btnSyncAll.setOnClickListener {
            val syncedCount = Microdataset.syncAllOffline()
            if (syncedCount > 0) {
                Toast.makeText(requireContext(), "$syncedCount registros transmitidos con éxito a la Alcaldía", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No hay registros pendientes o los existentes tienen conflictos de validación", Toast.LENGTH_LONG).show()
            }
            refreshData()
        }

        btnResolveConflict.setOnClickListener {
            val duplicateRecord = Microdataset.getAllRecords().find { it.alerta_duplicado }
            if (duplicateRecord != null) {
                showResolveConflictDialog(duplicateRecord)
            } else {
                Toast.makeText(requireContext(), "No hay conflictos pendientes", Toast.LENGTH_SHORT).show()
            }
        }

        refreshData()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        val records = Microdataset.getAllRecords()
        val total = records.size
        val pending = records.count { it.offline_pending }
        val synced = records.count { it.sincronizado_alcaldia }
        val hasConflict = records.any { it.alerta_duplicado }

        tvLocalCount.text = "$total"
        tvServerCount.text = "$synced"
        tvPendingCount.text = "$pending"

        cardDuplicateWarning.visibility = if (hasConflict) View.VISIBLE else View.GONE
        syncAdapter.updateData(records)
    }

    private fun showResolveConflictDialog(record: AnimalRecord) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_resolve_conflict, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val etNewChipCode = dialogView.findViewById<EditText>(R.id.etNewChipCode)
        val btnGenerateReplacementChip = dialogView.findViewById<Button>(R.id.btnGenerateReplacementChip)
        val btnCancelConflict = dialogView.findViewById<Button>(R.id.btnCancelConflict)
        val btnSaveNewChip = dialogView.findViewById<Button>(R.id.btnSaveNewChip)

        etNewChipCode.setText("981098102938415")

        btnGenerateReplacementChip.setOnClickListener {
            val rand = Random.nextInt(100000, 999999)
            etNewChipCode.setText("981098102$rand")
        }

        btnCancelConflict.setOnClickListener {
            dialog.dismiss()
        }

        btnSaveNewChip.setOnClickListener {
            val newCode = etNewChipCode.text.toString().trim()
            if (newCode.length < 15) {
                Toast.makeText(requireContext(), "El código debe tener 15 dígitos estándar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Microdataset.resolveDuplicateChip(record.registro_id, newCode)
            Toast.makeText(requireContext(), "Microchip rectificado y sincronizado con éxito", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            refreshData()
        }

        dialog.show()
    }
}
