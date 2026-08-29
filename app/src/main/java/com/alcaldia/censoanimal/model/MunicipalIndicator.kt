package com.alcaldia.censoanimal.model

data class MunicipalIndicator(
    val id: String,
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val valor: String,
    val formula: String,
    val estado: String, // "Optimo" | "Alerta" | "Critico"
    val decisionInstitucional: String
)
