package com.alcaldia.censoanimal.model

data class MistreatmentReport(
    val id: String,
    val fecha_radicado: String,
    val modo_privacidad: String, // "Identidad Reservada" | "Completamente Anónimo"
    val denunciante_nombre: String?,
    val denunciante_contacto: String?,
    val vereda: String,
    val direccion_exacta: String,
    val descripcion_hechos: String,
    val nivel_gravedad_aparente: String, // "Leve", "Moderado", "Grave / Urgente"
    var estado_caso: String, // "Pendiente Visita", "Acta Diligenciada", "En Seguimiento", "Cerrado / Sancionado"
    var acta_visita: ActaVisita? = null
)

data class ActaVisita(
    val id: String,
    val reporte_id: String,
    val fecha_visita: String,
    val inspector_veterinario: String,
    val condicion_corporal: String, // "1 - Caquéctico", "2 - Bajo peso", "3 - Ideal", "4 - Sobrepeso", "5 - Obeso"
    val condicion_espacio: String, // "Adecuado", "Restringido / Atado permanente", "Insuficiente / Intemperie"
    val condicion_alimentacion: String, // "Agua y alimento disponible", "Alimento insuficiente", "Sin agua potable"
    val clasificacion_final: String, // "Sin Maltrato", "Tenencia Irresponsable", "Maltrato Leve", "Maltrato Moderado", "Maltrato Grave"
    val dias_plazo_compromiso: Int, // 20 para Leve, 10 para Moderado, 0 para Grave
    val compromiso_texto: String,
    val requiere_aprehension_policia: Boolean = false,
    var compromiso_cumplido: Boolean = false
)
