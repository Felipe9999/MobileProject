package com.alcaldia.censoanimal.data

import com.alcaldia.censoanimal.model.UserRole

data class UserProfile(
    val name: String,
    val email: String,
    val role: UserRole,
    val roleLabel: String,
    val professionalId: String,
    val entity: String = "Secretaría de Desarrollo Rural y Ambiente",
    val municipality: String = "Zipaquirá, Cundinamarca",
    var isAutoSyncEnabled: Boolean = true,
    var isBiometricEnabled: Boolean = true,
    var isNotificationsEnabled: Boolean = true
)

object AppSessionManager {

    private val listeners = mutableListOf<(UserProfile) -> Unit>()

    var currentProfile: UserProfile = createProfileForRole(UserRole.VETERINARIO)
        private set

    var isLoggedIn: Boolean = true
        private set

    fun addSessionListener(listener: (UserProfile) -> Unit) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeSessionListener(listener: (UserProfile) -> Unit) {
        listeners.remove(listener)
    }

    fun switchRole(newRole: UserRole) {
        currentProfile = createProfileForRole(newRole)
        isLoggedIn = true
        notifyListeners()
    }

    fun login(role: UserRole, customEmail: String? = null, customName: String? = null) {
        val baseProfile = createProfileForRole(role)
        currentProfile = baseProfile.copy(
            email = customEmail ?: baseProfile.email,
            name = customName ?: baseProfile.name
        )
        isLoggedIn = true
        notifyListeners()
    }

    fun logout() {
        isLoggedIn = false
        // When logged out, transition to unregistered user mode
        currentProfile = createProfileForRole(UserRole.NO_REGISTRADO)
        notifyListeners()
    }

    fun updatePreferences(autoSync: Boolean, biometric: Boolean, notifications: Boolean) {
        currentProfile.isAutoSyncEnabled = autoSync
        currentProfile.isBiometricEnabled = biometric
        currentProfile.isNotificationsEnabled = notifications
        notifyListeners()
    }

    private fun createProfileForRole(role: UserRole): UserProfile {
        return when (role) {
            UserRole.NO_REGISTRADO -> UserProfile(
                name = "Usuario No Registrado",
                email = "Sin registrar",
                role = UserRole.NO_REGISTRADO,
                roleLabel = "Usuario no registrado",
                professionalId = "Acceso Público"
            )
            UserRole.VETERINARIO -> UserProfile(
                name = "Dr. Ricardo Forero",
                email = "veterinario.campo@alcaldia.gov.co",
                role = UserRole.VETERINARIO,
                roleLabel = "Veterinario Aliado",
                professionalId = "COMVEZCOL No. 28491-CUN"
            )
            UserRole.ADMINISTRADOR -> UserProfile(
                name = "Ing. Laura Gómez",
                email = "admin.ambiental@alcaldia.gov.co",
                role = UserRole.ADMINISTRADOR,
                roleLabel = "Administrador Municipal",
                professionalId = "ID Admin 80.234.192"
            )
            UserRole.CIUDADANO -> UserProfile(
                name = "Carlos Mendoza",
                email = "ciudadano.rural@gmail.com",
                role = UserRole.CIUDADANO,
                roleLabel = "Usuario Registrado",
                professionalId = "CC 19.384.921"
            )
        }
    }

    private fun notifyListeners() {
        listeners.forEach { it(currentProfile) }
    }
}
