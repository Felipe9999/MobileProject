package com.alcaldia.censoanimal.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.UserProfile
import com.alcaldia.censoanimal.model.UserRole

object TopBarAccountHelper {

    fun setupAccountButton(
        activity: Activity,
        accountButton: View,
        tvAccountLabel: TextView? = null,
        onSessionUpdated: ((UserProfile) -> Unit)? = null
    ) {
        fun updateLabel(profile: UserProfile) {
            tvAccountLabel?.text = when (profile.role) {
                UserRole.VETERINARIO -> "Veterinario"
                UserRole.FUNCIONARIO -> "Funcionario"
                UserRole.CIUDADANO -> "Ciudadano"
            }
        }

        updateLabel(AppSessionManager.currentProfile)

        AppSessionManager.addSessionListener { profile ->
            activity.runOnUiThread {
                updateLabel(profile)
                onSessionUpdated?.invoke(profile)
            }
        }

        accountButton.setOnClickListener {
            showAccountDropdown(activity, accountButton, onSessionUpdated)
        }
    }

    fun showAccountDropdown(
        activity: Activity,
        anchorView: View,
        onSessionUpdated: ((UserProfile) -> Unit)? = null
    ) {
        val inflater = LayoutInflater.from(activity)
        val popupView = inflater.inflate(R.layout.layout_account_dropdown, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 16f
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isOutsideTouchable = true
        }

        val profile = AppSessionManager.currentProfile

        val tvDropdownUserName = popupView.findViewById<TextView>(R.id.tvDropdownUserName)
        val tvDropdownUserRole = popupView.findViewById<TextView>(R.id.tvDropdownUserRole)
        val tvDropdownUserEmail = popupView.findViewById<TextView>(R.id.tvDropdownUserEmail)
        val btnDropdownSettings = popupView.findViewById<View>(R.id.btnDropdownSettings)
        val btnDropdownLogout = popupView.findViewById<View>(R.id.btnDropdownLogout)

        tvDropdownUserName.text = profile.name
        tvDropdownUserRole.text = profile.roleLabel
        tvDropdownUserEmail.text = profile.email

        btnDropdownSettings.setOnClickListener {
            popupWindow.dismiss()
            showAccountSettingsDialog(activity, onSessionUpdated)
        }

        btnDropdownLogout.setOnClickListener {
            popupWindow.dismiss()
            confirmLogout(activity, onSessionUpdated)
        }

        // Show anchored below account button, aligned to its right
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        popupWindow.showAsDropDown(anchorView, -120, 8, Gravity.NO_GRAVITY)
    }

    fun showAccountSettingsDialog(
        activity: Activity,
        onSessionUpdated: ((UserProfile) -> Unit)? = null
    ) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_account_settings, null)
        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val profile = AppSessionManager.currentProfile

        val tvSettingsUserName = dialogView.findViewById<TextView>(R.id.tvSettingsUserName)
        val tvSettingsUserRole = dialogView.findViewById<TextView>(R.id.tvSettingsUserRole)
        val tvSettingsEmail = dialogView.findViewById<TextView>(R.id.tvSettingsEmail)
        val tvSettingsId = dialogView.findViewById<TextView>(R.id.tvSettingsId)
        val btnCloseSettings = dialogView.findViewById<ImageButton>(R.id.btnCloseSettings)

        val btnRoleVet = dialogView.findViewById<Button>(R.id.btnRoleVet)
        val btnRoleOfficial = dialogView.findViewById<Button>(R.id.btnRoleOfficial)
        val btnRoleCitizen = dialogView.findViewById<Button>(R.id.btnRoleCitizen)

        val switchAutoSync = dialogView.findViewById<SwitchCompat>(R.id.switchAutoSync)
        val switchBiometric = dialogView.findViewById<SwitchCompat>(R.id.switchBiometric)
        val switchNotifications = dialogView.findViewById<SwitchCompat>(R.id.switchNotifications)

        val btnSettingsLogout = dialogView.findViewById<Button>(R.id.btnSettingsLogout)
        val btnSettingsSave = dialogView.findViewById<Button>(R.id.btnSettingsSave)

        fun bindProfile(p: UserProfile) {
            tvSettingsUserName.text = p.name
            tvSettingsUserRole.text = p.roleLabel
            tvSettingsEmail.text = "✉ ${p.email}"
            tvSettingsId.text = "🪪 ${p.professionalId}"
            switchAutoSync.isChecked = p.isAutoSyncEnabled
            switchBiometric.isChecked = p.isBiometricEnabled
            switchNotifications.isChecked = p.isNotificationsEnabled
        }

        bindProfile(profile)

        btnRoleVet.setOnClickListener {
            AppSessionManager.switchRole(UserRole.VETERINARIO)
            bindProfile(AppSessionManager.currentProfile)
            Toast.makeText(activity, "Rol cambiado a Veterinario", Toast.LENGTH_SHORT).show()
        }

        btnRoleOfficial.setOnClickListener {
            AppSessionManager.switchRole(UserRole.FUNCIONARIO)
            bindProfile(AppSessionManager.currentProfile)
            Toast.makeText(activity, "Rol cambiado a Funcionario", Toast.LENGTH_SHORT).show()
        }

        btnRoleCitizen.setOnClickListener {
            AppSessionManager.switchRole(UserRole.CIUDADANO)
            bindProfile(AppSessionManager.currentProfile)
            Toast.makeText(activity, "Rol cambiado a Ciudadano", Toast.LENGTH_SHORT).show()
        }

        btnCloseSettings.setOnClickListener { dialog.dismiss() }

        btnSettingsSave.setOnClickListener {
            AppSessionManager.updatePreferences(
                autoSync = switchAutoSync.isChecked,
                biometric = switchBiometric.isChecked,
                notifications = switchNotifications.isChecked
            )
            Toast.makeText(activity, "Preferencias de cuenta actualizadas", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnSettingsLogout.setOnClickListener {
            dialog.dismiss()
            confirmLogout(activity, onSessionUpdated)
        }

        dialog.show()
    }

    fun confirmLogout(
        activity: Activity,
        onSessionUpdated: ((UserProfile) -> Unit)? = null
    ) {
        val current = AppSessionManager.currentProfile

        AlertDialog.Builder(activity)
            .setTitle("¿Cerrar Sesión?")
            .setMessage("¿Desea cerrar la sesión de ${current.name} (${current.roleLabel})? Los datos guardados localmente se mantendrán seguros en este dispositivo.")
            .setPositiveButton("Cerrar Sesión") { _, _ ->
                AppSessionManager.logout()
                Toast.makeText(activity, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
                onSessionUpdated?.invoke(AppSessionManager.currentProfile)

                // Navigate to LoginActivity
                val intent = Intent(activity, LoginActivity::class.java)
                activity.startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
