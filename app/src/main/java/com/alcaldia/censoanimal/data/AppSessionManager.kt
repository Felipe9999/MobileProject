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
        listener(currentProfile)
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
        // When logged out, default to Ciudadano / Public mode
        currentProfile = createProfileForRole(UserRole.CIUDADANO)
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
            UserRole.VETERINARIO -> UserProfile(
                name = "Dr. Ricardo Forero",
                email = "veterinario.campo@alcaldia.gov.co",
                role = UserRole.VETERINARIO,
                roleLabel = "Veterinario Aliado",
                professionalId = "COMVEZCOL No. 28491-CUN"
            )
            UserRole.FUNCIONARIO -> UserProfile(
                name = "Ing. Laura Gómez",
                email = "salud.publica@alcaldia.gov.co",
                role = UserRole.FUNCIONARIO,
                roleLabel = "Funcionario Municipal",
                professionalId = "ID Funcionario 80.234.192"
            )
            UserRole.CIUDADANO -> UserProfile(
                name = "Carlos Mendoza",
                email = "ciudadano.rural@gmail.com",
                role = UserRole.CIUDADANO,
                roleLabel = "Ciudadano / Propietario",
                professionalId = "CC 19.384.921"
            )
        }
    }

    private fun notifyListeners() {
        listeners.forEach { it(currentProfile) }
    }
}
