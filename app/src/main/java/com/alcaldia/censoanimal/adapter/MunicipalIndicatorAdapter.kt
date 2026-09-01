package com.alcaldia.censoanimal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.model.MunicipalIndicator

class MunicipalIndicatorAdapter(
    private var indicators: List<MunicipalIndicator>
) : RecyclerView.Adapter<MunicipalIndicatorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIndicatorCode: TextView = view.findViewById(R.id.tvIndicatorCode)
        val tvIndicatorCategory: TextView = view.findViewById(R.id.tvIndicatorCategory)
        val tvIndicatorName: TextView = view.findViewById(R.id.tvIndicatorName)
        val tvIndicatorValue: TextView = view.findViewById(R.id.tvIndicatorValue)
        val tvIndicatorStatusBadge: TextView = view.findViewById(R.id.tvIndicatorStatusBadge)
        val tvIndicatorFormula: TextView = view.findViewById(R.id.tvIndicatorFormula)
        val tvIndicatorDecision: TextView = view.findViewById(R.id.tvIndicatorDecision)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_municipal_indicator, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = indicators[position]
        val context = holder.itemView.context

        holder.tvIndicatorCode.text = item.codigo
        holder.tvIndicatorCategory.text = item.categoria
        holder.tvIndicatorName.text = item.nombre
        holder.tvIndicatorValue.text = item.valor
        holder.tvIndicatorFormula.text = item.formula
        holder.tvIndicatorDecision.text = item.decisionInstitucional

        when (item.estado) {
            "Optimo" -> {
                holder.tvIndicatorStatusBadge.text = "Óptimo"
                holder.tvIndicatorStatusBadge.setBackgroundResource(R.drawable.bg_pill_emerald)
                holder.tvIndicatorStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.emerald_700))
                holder.tvIndicatorValue.setTextColor(ContextCompat.getColor(context, R.color.emerald_700))
            }
            "Alerta" -> {
                holder.tvIndicatorStatusBadge.text = "Alerta"
                holder.tvIndicatorStatusBadge.setBackgroundResource(R.drawable.bg_pill_amber)
                holder.tvIndicatorStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.amber_700))
                holder.tvIndicatorValue.setTextColor(ContextCompat.getColor(context, R.color.amber_700))
            }
            "Critico" -> {
                holder.tvIndicatorStatusBadge.text = "Crítico"
                holder.tvIndicatorStatusBadge.setBackgroundResource(R.drawable.bg_pill_rose)
                holder.tvIndicatorStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.rose_700))
                holder.tvIndicatorValue.setTextColor(ContextCompat.getColor(context, R.color.rose_700))
            }
        }
    }

    override fun getItemCount(): Int = indicators.size

    fun updateData(newIndicators: List<MunicipalIndicator>) {
        indicators = newIndicators
        notifyDataSetChanged()
    }
}
