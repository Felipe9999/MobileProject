package com.alcaldia.censoanimal.model

data class LostFoundAlert(
    val id: String,
    val tipo: String, // "PERDIDO" | "HALLADO"
    val especie: String,
    val nombre: String?,
    val raza: String,
    val color: String,
    val vereda: String,
    val fecha_evento: String,
    val microchip: String?,
    val contacto_nombre: String,
    val contacto_telefono: String,
    var estado: String, // "Activo / En Búsqueda", "Candidato Coincidencia Detectado", "Reunificado con Guardián", "Custodia Municipal (+20 Días - Declarado Abandonado)"
    val descripcion: String,
    val posible_coincidencia_registro_id: String? = null,
    val dias_custodia_albergue: Int = 0,
    var validacion_manual_aprobada: Boolean = false
)
