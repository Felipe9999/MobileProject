package com.alcaldia.censoanimal.model

enum class Species(val label: String) {
    PERRO("Perro"),
    GATO("Gato")
}

enum class Sex(val label: String) {
    MACHO("Macho"),
    HEMBRA("Hembra")
}

enum class AnimalState(val label: String) {
    ACTIVO("Activo"),
    FALLECIDO("Fallecido")
}

enum class UserRole(val label: String, val roleKey: String) {
    VETERINARIO("Veterinario Aliado", "veterinario"),
    FUNCIONARIO("Funcionario Municipal", "funcionario"),
    CIUDADANO("Ciudadano / Propietario", "ciudadano")
}

enum class SyncStatus(val label: String) {
    LOCAL("Local (Room)"),
    SYNCED("Sincronizado"),
    CONFLICT("Conflicto")
}
