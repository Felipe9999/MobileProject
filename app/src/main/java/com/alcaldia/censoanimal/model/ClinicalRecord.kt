package com.alcaldia.censoanimal.model

data class ClinicalRecord(
    val id: String,
    val registro_id: String,
    val fecha: String,
    val tipo_evento: String, // "Jornada Vacunación", "Consulta General", "Esterilización Quirúrgica", "Desparasitación", "Urgencia / Trauma"
    val profesional: String,
    val anamnesis: String,
    val hallazgos: String,
    val compromiso_correctivo: String?,
    var cumplido: Boolean = true
)
