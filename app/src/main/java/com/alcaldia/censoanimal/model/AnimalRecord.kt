package com.alcaldia.censoanimal.model

data class AnimalRecord(
    val registro_id: String,
    var microchip: String?,
    var especie: String, // "Perro" | "Gato"
    var animal_nombre: String,
    var sexo: String, // "Macho" | "Hembra"
    var raza: String,
    var color: String,
    var edad_meses: Int,
    var esterilizado: Boolean,
    var fecha_esterilizacion: String?,
    var fecha_vacuna_rabia: String,
    var lote_vacuna: String?,
    var responsable_nombre: String,
    var documento_tipo: String,
    var documento_numero: String,
    var telefono: String,
    var territorio: String,
    var territorio_catalogo: String,
    var direccion_finca: String,
    var latitud: Double,
    var longitud: Double,
    var fecha_censo: String,
    var censo_por: String,
    var observaciones: String,
    var estado_animal: String = "Activo", // "Activo" | "Fallecido"
    var fecha_fallecimiento: String? = null,
    var motivo_fallecimiento: String? = null,
    var sincronizado_alcaldia: Boolean = true,
    var version_registro: Int = 1,
    var offline_pending: Boolean = false,
    var alerta_duplicado: Boolean = false,
    var qr_publico_hash: String? = null
)
