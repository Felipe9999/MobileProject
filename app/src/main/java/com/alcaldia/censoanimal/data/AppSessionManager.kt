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
    var isNotificationsEnabled: Boolean = true,
    val documentNumber: String = "19384921",
    val phone: String = "3104829102",
    val defaultTerritory: String = "Barandillas (Urbano)",
    val defaultAddress: String = "Cra 7 # 12-45, Zipaquirá"
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

    fun login(
        role: UserRole,
        customEmail: String? = null,
        customName: String? = null,
        customDocument: String? = null
    ) {
        val baseProfile = createProfileForRole(role)
        val docNum = customDocument ?: baseProfile.documentNumber
        currentProfile = baseProfile.copy(
            email = customEmail ?: baseProfile.email,
            name = customName ?: baseProfile.name,
            documentNumber = docNum,
            professionalId = if (role == UserRole.CIUDADANO && customDocument != null) "CC $customDocument" else baseProfile.professionalId
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
                professionalId = "Acceso Público",
                documentNumber = "No Registrado",
                phone = ""
            )
            UserRole.VETERINARIO -> UserProfile(
                name = "Dr. Ricardo Forero",
                email = "veterinario.campo@alcaldia.gov.co",
                role = UserRole.VETERINARIO,
                roleLabel = "Veterinario Aliado",
                professionalId = "COMVEZCOL No. 28491-CUN",
                documentNumber = "79812401",
                phone = "3158901234"
            )
            UserRole.ADMINISTRADOR -> UserProfile(
                name = "Ing. Laura Gómez",
                email = "admin.ambiental@alcaldia.gov.co",
                role = UserRole.ADMINISTRADOR,
                roleLabel = "Administrador Municipal",
                professionalId = "ID Admin 80.234.192",
                documentNumber = "80234192",
                phone = "3201122334"
            )
            UserRole.CIUDADANO -> UserProfile(
                name = "Carlos Mendoza",
                email = "ciudadano.rural@gmail.com",
                role = UserRole.CIUDADANO,
                roleLabel = "Usuario Registrado",
                professionalId = "CC 19.384.921",
                documentNumber = "19384921",
                phone = "3104829102",
                defaultTerritory = "Barandillas (Urbano)",
                defaultAddress = "Cra 7 # 12-45, Zipaquirá"
            )
        }
    }

    private fun notifyListeners() {
        listeners.forEach { it(currentProfile) }
    }
}
