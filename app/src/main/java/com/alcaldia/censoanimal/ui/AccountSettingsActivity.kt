package com.alcaldia.censoanimal.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.UserProfile
import com.alcaldia.censoanimal.model.UserRole
import com.google.android.material.card.MaterialCardView

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var btnSettingsBack: ImageButton

    // Profile summary
    private lateinit var tvSettingsUserName: TextView
    private lateinit var tvSettingsUserRole: TextView
    private lateinit var tvSettingsEmail: TextView
    private lateinit var tvSettingsId: TextView

    // Role switcher cards
    private lateinit var cardRoleVet: MaterialCardView
    private lateinit var flIconVet: FrameLayout
    private lateinit var ivCheckVet: ImageView

    private lateinit var cardRoleOfficial: MaterialCardView
    private lateinit var flIconOfficial: FrameLayout
    private lateinit var ivCheckOfficial: ImageView

    private lateinit var cardRoleCitizen: MaterialCardView
    private lateinit var flIconCitizen: FrameLayout
    private lateinit var ivCheckCitizen: ImageView

    private lateinit var cardRoleUnregistered: MaterialCardView
    private lateinit var flIconUnregistered: FrameLayout
    private lateinit var ivCheckUnregistered: ImageView

    // Preference switches
    private lateinit var switchAutoSync: SwitchCompat
    private lateinit var switchBiometric: SwitchCompat
    private lateinit var switchNotifications: SwitchCompat

    // Action buttons
    private lateinit var btnSettingsSave: Button
    private lateinit var btnSettingsLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        bindViews()
        populateProfile(AppSessionManager.currentProfile)
        setupRoleClicks()
        setupActionButtons()
    }

    private fun bindViews() {
        btnSettingsBack = findViewById(R.id.btnSettingsBack)

        tvSettingsUserName = findViewById(R.id.tvSettingsUserName)
        tvSettingsUserRole = findViewById(R.id.tvSettingsUserRole)
        tvSettingsEmail = findViewById(R.id.tvSettingsEmail)
        tvSettingsId = findViewById(R.id.tvSettingsId)

        cardRoleVet = findViewById(R.id.cardRoleVet)
        flIconVet = findViewById(R.id.flIconVet)
        ivCheckVet = findViewById(R.id.ivCheckVet)

        cardRoleOfficial = findViewById(R.id.cardRoleOfficial)
        flIconOfficial = findViewById(R.id.flIconOfficial)
        ivCheckOfficial = findViewById(R.id.ivCheckOfficial)

        cardRoleCitizen = findViewById(R.id.cardRoleCitizen)
        flIconCitizen = findViewById(R.id.flIconCitizen)
        ivCheckCitizen = findViewById(R.id.ivCheckCitizen)

        cardRoleUnregistered = findViewById(R.id.cardRoleUnregistered)
        flIconUnregistered = findViewById(R.id.flIconUnregistered)
        ivCheckUnregistered = findViewById(R.id.ivCheckUnregistered)

        switchAutoSync = findViewById(R.id.switchAutoSync)
        switchBiometric = findViewById(R.id.switchBiometric)
        switchNotifications = findViewById(R.id.switchNotifications)

        btnSettingsSave = findViewById(R.id.btnSettingsSave)
        btnSettingsLogout = findViewById(R.id.btnSettingsLogout)

        btnSettingsBack.setOnClickListener {
            finish()
        }
    }

    private fun populateProfile(profile: UserProfile) {
        tvSettingsUserName.text = profile.name
        tvSettingsUserRole.text = profile.roleLabel
        tvSettingsEmail.text = profile.email
        tvSettingsId.text = profile.professionalId

        switchAutoSync.isChecked = profile.isAutoSyncEnabled
        switchBiometric.isChecked = profile.isBiometricEnabled
        switchNotifications.isChecked = profile.isNotificationsEnabled

        highlightSelectedRole(profile.role)
    }

    private fun highlightSelectedRole(role: UserRole) {
        val density = resources.displayMetrics.density
        val strokeSelected = (2 * density).toInt()
        val strokeNormal = (1 * density).toInt()

        val colorBlue = ContextCompat.getColor(this, R.color.blue_600)
        val colorSlate = ContextCompat.getColor(this, R.color.slate_200)

        val bgPillBlue = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue_100))
        val bgPillSlate = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.slate_100))

        // Vet
        val isVet = role == UserRole.VETERINARIO
        cardRoleVet.strokeWidth = if (isVet) strokeSelected else strokeNormal
        cardRoleVet.strokeColor = if (isVet) colorBlue else colorSlate
        flIconVet.backgroundTintList = if (isVet) bgPillBlue else bgPillSlate
        ivCheckVet.visibility = if (isVet) View.VISIBLE else View.GONE

        // Admin
        val isAdmin = role == UserRole.ADMINISTRADOR
        cardRoleOfficial.strokeWidth = if (isAdmin) strokeSelected else strokeNormal
        cardRoleOfficial.strokeColor = if (isAdmin) colorBlue else colorSlate
        flIconOfficial.backgroundTintList = if (isAdmin) bgPillBlue else bgPillSlate
        ivCheckOfficial.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Citizen
        val isCitizen = role == UserRole.CIUDADANO
        cardRoleCitizen.strokeWidth = if (isCitizen) strokeSelected else strokeNormal
        cardRoleCitizen.strokeColor = if (isCitizen) colorBlue else colorSlate
        flIconCitizen.backgroundTintList = if (isCitizen) bgPillBlue else bgPillSlate
        ivCheckCitizen.visibility = if (isCitizen) View.VISIBLE else View.GONE

        // Unregistered
        val isUnregistered = role == UserRole.NO_REGISTRADO
        cardRoleUnregistered.strokeWidth = if (isUnregistered) strokeSelected else strokeNormal
        cardRoleUnregistered.strokeColor = if (isUnregistered) colorBlue else colorSlate
        flIconUnregistered.backgroundTintList = if (isUnregistered) bgPillBlue else bgPillSlate
        ivCheckUnregistered.visibility = if (isUnregistered) View.VISIBLE else View.GONE
    }

    private fun setupRoleClicks() {
        cardRoleVet.setOnClickListener {
            if (AppSessionManager.currentProfile.role != UserRole.VETERINARIO) {
                AppSessionManager.switchRole(UserRole.VETERINARIO)
                populateProfile(AppSessionManager.currentProfile)
                Toast.makeText(this, "Rol cambiado a Veterinario Aliado", Toast.LENGTH_SHORT).show()
            }
        }

        cardRoleOfficial.setOnClickListener {
            if (AppSessionManager.currentProfile.role != UserRole.ADMINISTRADOR) {
                AppSessionManager.switchRole(UserRole.ADMINISTRADOR)
                populateProfile(AppSessionManager.currentProfile)
                Toast.makeText(this, "Rol cambiado a Administrador Municipal", Toast.LENGTH_SHORT).show()
            }
        }

        cardRoleCitizen.setOnClickListener {
            if (AppSessionManager.currentProfile.role != UserRole.CIUDADANO) {
                AppSessionManager.switchRole(UserRole.CIUDADANO)
                populateProfile(AppSessionManager.currentProfile)
                Toast.makeText(this, "Rol cambiado a Usuario Registrado", Toast.LENGTH_SHORT).show()
            }
        }

        cardRoleUnregistered.setOnClickListener {
            if (AppSessionManager.currentProfile.role != UserRole.NO_REGISTRADO) {
                AppSessionManager.switchRole(UserRole.NO_REGISTRADO)
                populateProfile(AppSessionManager.currentProfile)
                Toast.makeText(this, "Modo cambiado a Usuario No Registrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupActionButtons() {
        btnSettingsSave.setOnClickListener {
            AppSessionManager.updatePreferences(
                autoSync = switchAutoSync.isChecked,
                biometric = switchBiometric.isChecked,
                notifications = switchNotifications.isChecked
            )
            Toast.makeText(this, "Preferencias guardadas exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnSettingsLogout.setOnClickListener {
            TopBarAccountHelper.confirmLogout(this)
        }
    }
}
