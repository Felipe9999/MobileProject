package com.alcaldia.censoanimal.model

data class DataQualityIssue(
    val id: String,
    val registro_id: String,
    val campo: String,
    val tipo: String, // "Critico" | "Advertencia" | "Informativo"
    val descripcion: String,
    val accionSugerida: String
)

data class PresetAccount(
    val role: UserRole,
    val label: String,
    val email: String,
    val password: String,
    val badge: String,
    val badgeColor: String,
    val description: String
)
