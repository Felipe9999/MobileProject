package com.alcaldia.censoanimal.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alcaldia.censoanimal.MainActivity
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.UserRole

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnRegisterBack: ImageButton
    private lateinit var etRegisterFullName: EditText
    private lateinit var etRegisterCitizenId: EditText
    private lateinit var etRegisterEmail: EditText
    private lateinit var etRegisterPassword: EditText
    private lateinit var etRegisterConfirmPassword: EditText
    private lateinit var cbRegisterTerms: CheckBox
    private lateinit var btnSubmitRegister: Button
    private lateinit var btnRegisterGoToLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnRegisterBack = findViewById(R.id.btnRegisterBack)
        etRegisterFullName = findViewById(R.id.etRegisterFullName)
        etRegisterCitizenId = findViewById(R.id.etRegisterCitizenId)
        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword)
        cbRegisterTerms = findViewById(R.id.cbRegisterTerms)
        btnSubmitRegister = findViewById(R.id.btnSubmitRegister)
        btnRegisterGoToLogin = findViewById(R.id.btnRegisterGoToLogin)
    }

    private fun setupListeners() {
        btnRegisterBack.setOnClickListener { finish() }

        btnRegisterGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnSubmitRegister.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {
        val fullName = etRegisterFullName.text.toString().trim()
        val citizenId = etRegisterCitizenId.text.toString().trim()
        val email = etRegisterEmail.text.toString().trim()
        val password = etRegisterPassword.text.toString().trim()
        val confirmPassword = etRegisterConfirmPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            etRegisterFullName.error = "Ingrese su nombre completo y apellidos"
            etRegisterFullName.requestFocus()
            return
        }

        if (citizenId.isEmpty()) {
            etRegisterCitizenId.error = "Ingrese su número de cédula de ciudadanía"
            etRegisterCitizenId.requestFocus()
            return
        }

        if (citizenId.length < 5) {
            etRegisterCitizenId.error = "Cédula inválida (mínimo 5 dígitos)"
            etRegisterCitizenId.requestFocus()
            return
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterEmail.error = "Ingrese un correo electrónico válido"
            etRegisterEmail.requestFocus()
            return
        }

        if (password.isEmpty() || password.length < 6) {
            etRegisterPassword.error = "La contraseña debe tener al menos 6 caracteres"
            etRegisterPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            etRegisterConfirmPassword.error = "Las contraseñas no coinciden"
            etRegisterConfirmPassword.requestFocus()
            return
        }

        if (!cbRegisterTerms.isChecked) {
            Toast.makeText(this, "Debe aceptar el tratamiento de datos personales", Toast.LENGTH_SHORT).show()
            return
        }

        // Mock account creation: log in as test citizen account with the provided details
        val displayName = fullName.ifEmpty { Microdataset.PRESET_ACCOUNTS[2].label }
        val registeredEmail = email.ifEmpty { Microdataset.PRESET_ACCOUNTS[2].email }

        AppSessionManager.login(
            role = UserRole.CIUDADANO,
            customEmail = registeredEmail,
            customName = displayName,
            customDocument = citizenId
        )

        Toast.makeText(
            this,
            "¡Cuenta creada con éxito! Bienvenido(a), $displayName",
            Toast.LENGTH_LONG
        ).show()

        // Clear stack and go directly into MainActivity as the registered citizen
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
