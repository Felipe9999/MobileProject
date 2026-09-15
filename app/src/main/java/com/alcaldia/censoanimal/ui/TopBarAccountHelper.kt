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

        val sessionListener: (UserProfile) -> Unit = { profile ->
            if (!activity.isFinishing && !activity.isDestroyed) {
                activity.runOnUiThread {
                    updateLabel(profile)
                    if (AppSessionManager.isLoggedIn) {
                        onSessionUpdated?.invoke(profile)
                    }
                }
            }
        }

        AppSessionManager.addSessionListener(sessionListener)

        if (activity is androidx.lifecycle.LifecycleOwner) {
            activity.lifecycle.addObserver(object : androidx.lifecycle.DefaultLifecycleObserver {
                override fun onDestroy(owner: androidx.lifecycle.LifecycleOwner) {
                    AppSessionManager.removeSessionListener(sessionListener)
                }
            })
        }

        accountButton.setOnClickListener {
            showAccountDropdown(activity, accountButton, onSessionUpdated)
        }
    }

    fun openAccountSettings(activity: Activity) {
        val intent = Intent(activity, AccountSettingsActivity::class.java)
        activity.startActivity(intent)
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
            openAccountSettings(activity)
        }

        btnDropdownLogout.setOnClickListener {
            popupWindow.dismiss()
            confirmLogout(activity, onSessionUpdated)
        }

        // Measure popup to align right edge with anchor view
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupWidth = popupView.measuredWidth
        val density = activity.resources.displayMetrics.density
        val xOffset = anchorView.width - popupWidth
        val yOffset = (6 * density).toInt()

        popupWindow.showAsDropDown(anchorView, xOffset, yOffset)
    }

    fun showAccountSettingsDialog(
        activity: Activity,
        onSessionUpdated: ((UserProfile) -> Unit)? = null
    ) {
        openAccountSettings(activity)
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

                // Navigate to LoginActivity and clear existing task stack
                val intent = Intent(activity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                activity.startActivity(intent)
                activity.finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
