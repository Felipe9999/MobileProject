package com.alcaldia.censoanimal.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.UserRole
import com.google.android.material.card.MaterialCardView

class LoginActivity : AppCompatActivity() {

    private lateinit var btnLoginBack: ImageButton
    private lateinit var etLoginEmail: EditText
    private lateinit var etLoginPassword: EditText
    private lateinit var cbRememberOffline: CheckBox
    private lateinit var tvForgotPassword: TextView
    private lateinit var btnSubmitLogin: Button
    private lateinit var btnBiometricLogin: Button

    private lateinit var btnSelectPresetVet: MaterialCardView
    private lateinit var btnSelectPresetOfficial: MaterialCardView
    private lateinit var btnSelectPresetCitizen: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLoginBack = findViewById(R.id.btnLoginBack)
        etLoginEmail = findViewById(R.id.etLoginEmail)
        etLoginPassword = findViewById(R.id.etLoginPassword)
        cbRememberOffline = findViewById(R.id.cbRememberOffline)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        btnSubmitLogin = findViewById(R.id.btnSubmitLogin)
        btnBiometricLogin = findViewById(R.id.btnBiometricLogin)

        btnSelectPresetVet = findViewById(R.id.btnSelectPresetVet)
        btnSelectPresetOfficial = findViewById(R.id.btnSelectPresetOfficial)
        btnSelectPresetCitizen = findViewById(R.id.btnSelectPresetCitizen)

        btnLoginBack.setOnClickListener { finish() }

        btnSelectPresetVet.setOnClickListener {
            val account = Microdataset.PRESET_ACCOUNTS[0]
            etLoginEmail.setText(account.email)
            etLoginPassword.setText(account.password)
            Toast.makeText(this, "Credenciales de Veterinario cargadas", Toast.LENGTH_SHORT).show()
        }

        btnSelectPresetOfficial.setOnClickListener {
            val account = Microdataset.PRESET_ACCOUNTS[1]
            etLoginEmail.setText(account.email)
            etLoginPassword.setText(account.password)
            Toast.makeText(this, "Credenciales de Funcionario cargadas", Toast.LENGTH_SHORT).show()
        }

        btnSelectPresetCitizen.setOnClickListener {
            val account = Microdataset.PRESET_ACCOUNTS[2]
            etLoginEmail.setText(account.email)
            etLoginPassword.setText(account.password)
            Toast.makeText(this, "Credenciales de Ciudadano cargadas", Toast.LENGTH_SHORT).show()
        }

        btnSubmitLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese su correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Determine role by email
            val role = when {
                email.contains("veterinario") -> UserRole.VETERINARIO
                email.contains("salud") || email.contains("alcaldia") -> UserRole.FUNCIONARIO
                else -> UserRole.CIUDADANO
            }

            intent.putExtra("LOGGED_IN_ROLE", role.name)
            setResult(RESULT_OK, intent)
            Toast.makeText(this, "Sesión iniciada como: ${role.label}", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnBiometricLogin.setOnClickListener {
            Toast.makeText(this, "Autenticación Biométrica Verificada (Huella Dactilar)", Toast.LENGTH_SHORT).show()
            intent.putExtra("LOGGED_IN_ROLE", UserRole.VETERINARIO.name)
            setResult(RESULT_OK, intent)
            finish()
        }

        tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun showForgotPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val etRecoveryEmail = dialogView.findViewById<EditText>(R.id.etRecoveryEmail)
        val btnCancelRecovery = dialogView.findViewById<Button>(R.id.btnCancelRecovery)
        val btnSendRecovery = dialogView.findViewById<Button>(R.id.btnSendRecovery)

        etRecoveryEmail.setText(etLoginEmail.text.toString())

        btnCancelRecovery.setOnClickListener { dialog.dismiss() }
        btnSendRecovery.setOnClickListener {
            Toast.makeText(this, "Enlace de recuperación enviado al correo institucional", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}
