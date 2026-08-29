package com.alcaldia.censoanimal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.model.AnimalRecord

class AnimalRecordAdapter(
    private var records: List<AnimalRecord>,
    private val onItemClick: (AnimalRecord) -> Unit
) : RecyclerView.Adapter<AnimalRecordAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAnimalEmoji: TextView = view.findViewById(R.id.tvAnimalEmoji)
        val avatarFrame: FrameLayout = view.findViewById(R.id.avatarFrame)
        val tvAnimalName: TextView = view.findViewById(R.id.tvAnimalName)
        val tvRegistroId: TextView = view.findViewById(R.id.tvRegistroId)
        val badgeStatus: TextView = view.findViewById(R.id.badgeStatus)
        val tvBreedAndColor: TextView = view.findViewById(R.id.tvBreedAndColor)
        val tvVereda: TextView = view.findViewById(R.id.tvVereda)
        val tvMicrochip: TextView = view.findViewById(R.id.tvMicrochip)
        val badgeSterilized: TextView = view.findViewById(R.id.badgeSterilized)
        val tvResponsibleName: TextView = view.findViewById(R.id.tvResponsibleName)
        val layoutAlertDuplicate: LinearLayout = view.findViewById(R.id.layoutAlertDuplicate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = records[position]
        val context = holder.itemView.context

        // Emoji & Avatar style
        val isDog = item.especie.equals("Perro", ignoreCase = true)
        holder.tvAnimalEmoji.text = if (isDog) "🐶" else "🐱"
        
        holder.tvAnimalName.text = item.animal_nombre
        holder.tvRegistroId.text = item.registro_id
        
        // Status
        if (item.estado_animal.equals("Fallecido", ignoreCase = true)) {
            holder.badgeStatus.text = "Fallecido"
            holder.badgeStatus.setBackgroundResource(R.drawable.bg_pill_rose)
            holder.badgeStatus.setTextColor(ContextCompat.getColor(context, R.color.rose_700))
        } else {
            holder.badgeStatus.text = "Activo"
            holder.badgeStatus.setBackgroundResource(R.drawable.bg_pill_emerald)
            holder.badgeStatus.setTextColor(ContextCompat.getColor(context, R.color.emerald_700))
        }

        holder.tvBreedAndColor.text = "${item.raza} • ${item.sexo}"
        holder.tvVereda.text = item.territorio

        // Microchip
        if (!item.microchip.isNullOrBlank()) {
            holder.tvMicrochip.text = item.microchip
        } else {
            holder.tvMicrochip.text = "Sin Microchip"
        }

        // Sterilization
        if (item.esterilizado) {
            holder.badgeSterilized.visibility = View.VISIBLE
            holder.badgeSterilized.text = "Esterilizado"
            holder.badgeSterilized.setBackgroundResource(R.drawable.bg_pill_blue)
            holder.badgeSterilized.setTextColor(ContextCompat.getColor(context, R.color.blue_700))
        } else {
            holder.badgeSterilized.visibility = View.VISIBLE
            holder.badgeSterilized.text = "Sin Esterilizar"
            holder.badgeSterilized.setBackgroundResource(R.drawable.bg_pill_amber)
            holder.badgeSterilized.setTextColor(ContextCompat.getColor(context, R.color.amber_700))
        }

        holder.tvResponsibleName.text = "Resp: ${item.responsable_nombre}"

        // Duplicate alert banner
        if (item.alerta_duplicado) {
            holder.layoutAlertDuplicate.visibility = View.VISIBLE
        } else {
            holder.layoutAlertDuplicate.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = records.size

    fun updateData(newRecords: List<AnimalRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }
}
