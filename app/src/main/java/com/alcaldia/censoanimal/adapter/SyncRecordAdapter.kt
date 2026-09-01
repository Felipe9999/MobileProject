package com.alcaldia.censoanimal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.model.AnimalRecord

class SyncRecordAdapter(
    private var records: List<AnimalRecord>,
    private val onFixConflictClick: (AnimalRecord) -> Unit
) : RecyclerView.Adapter<SyncRecordAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewSyncStatusStripe: View = view.findViewById(R.id.viewSyncStatusStripe)
        val tvSyncAnimalName: TextView = view.findViewById(R.id.tvSyncAnimalName)
        val tvSyncRecordId: TextView = view.findViewById(R.id.tvSyncRecordId)
        val tvSyncStatusBadge: TextView = view.findViewById(R.id.tvSyncStatusBadge)
        val tvSyncDetails: TextView = view.findViewById(R.id.tvSyncDetails)
        val tvSyncVersion: TextView = view.findViewById(R.id.tvSyncVersion)
        val tvSyncActionLabel: TextView = view.findViewById(R.id.tvSyncActionLabel)
        val layoutSyncConflict: LinearLayout = view.findViewById(R.id.layoutSyncConflict)
        val tvConflictText: TextView = view.findViewById(R.id.tvConflictText)
        val btnItemFixConflict: Button = view.findViewById(R.id.btnItemFixConflict)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sync_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = records[position]
        val context = holder.itemView.context

        holder.tvSyncAnimalName.text = item.animal_nombre
        holder.tvSyncRecordId.text = item.registro_id
        holder.tvSyncDetails.text = "${item.especie} • ${item.raza} • ${item.territorio}"
        holder.tvSyncVersion.text = "Versión: v${item.version_registro}"

        if (item.alerta_duplicado) {
            holder.viewSyncStatusStripe.setBackgroundColor(ContextCompat.getColor(context, R.color.rose_500))
            holder.tvSyncStatusBadge.text = "Conflicto"
            holder.tvSyncStatusBadge.setBackgroundResource(R.drawable.bg_pill_rose)
            holder.tvSyncStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.rose_700))
            holder.tvSyncActionLabel.text = "Requiere rectificación"
            holder.tvSyncActionLabel.setTextColor(ContextCompat.getColor(context, R.color.rose_600))
            
            holder.layoutSyncConflict.visibility = View.VISIBLE
            holder.tvConflictText.text = "Chip (${item.microchip}) en colisión"
            holder.btnItemFixConflict.setOnClickListener {
                onFixConflictClick(item)
            }
        } else if (item.offline_pending) {
            holder.viewSyncStatusStripe.setBackgroundColor(ContextCompat.getColor(context, R.color.amber_500))
            holder.tvSyncStatusBadge.text = "Pendiente"
            holder.tvSyncStatusBadge.setBackgroundResource(R.drawable.bg_pill_amber)
            holder.tvSyncStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.amber_700))
            holder.tvSyncActionLabel.text = "Transmisión lista"
            holder.tvSyncActionLabel.setTextColor(ContextCompat.getColor(context, R.color.blue_600))
            holder.layoutSyncConflict.visibility = View.GONE
        } else {
            holder.viewSyncStatusStripe.setBackgroundColor(ContextCompat.getColor(context, R.color.emerald_500))
            holder.tvSyncStatusBadge.text = "Sincronizado"
            holder.tvSyncStatusBadge.setBackgroundResource(R.drawable.bg_pill_emerald)
            holder.tvSyncStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.emerald_700))
            holder.tvSyncActionLabel.text = "En servidor"
            holder.tvSyncActionLabel.setTextColor(ContextCompat.getColor(context, R.color.emerald_600))
            holder.layoutSyncConflict.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = records.size

    fun updateData(newRecords: List<AnimalRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }
}
