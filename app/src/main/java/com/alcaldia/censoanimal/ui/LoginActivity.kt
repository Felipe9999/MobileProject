package com.alcaldia.censoanimal.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.alcaldia.censoanimal.R
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.PresetAccount
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

    private lateinit var tvPresetVetBadge: TextView
    private lateinit var tvPresetOfficialBadge: TextView
    private lateinit var tvPresetCitizenBadge: TextView

    private var selectedRole: UserRole? = null

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

        tvPresetVetBadge = findViewById(R.id.tvPresetVetBadge)
        tvPresetOfficialBadge = findViewById(R.id.tvPresetOfficialBadge)
        tvPresetCitizenBadge = findViewById(R.id.tvPresetCitizenBadge)

        btnLoginBack.setOnClickListener { finish() }

        // Default to Veterinario preset for easy demo testing
        selectPreset(Microdataset.PRESET_ACCOUNTS[0])

        btnSelectPresetVet.setOnClickListener {
            selectPreset(Microdataset.PRESET_ACCOUNTS[0])
            Toast.makeText(this, "Credenciales de Veterinario cargadas", Toast.LENGTH_SHORT).show()
        }

        btnSelectPresetOfficial.setOnClickListener {
            selectPreset(Microdataset.PRESET_ACCOUNTS[1])
            Toast.makeText(this, "Credenciales de Funcionario cargadas", Toast.LENGTH_SHORT).show()
        }

        btnSelectPresetCitizen.setOnClickListener {
            selectPreset(Microdataset.PRESET_ACCOUNTS[2])
            Toast.makeText(this, "Credenciales de Ciudadano cargadas", Toast.LENGTH_SHORT).show()
        }

        etLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = s?.toString()?.trim() ?: ""
                when {
                    email.equals(Microdataset.PRESET_ACCOUNTS[0].email, ignoreCase = true) -> highlightRoleCard(UserRole.VETERINARIO)
                    email.equals(Microdataset.PRESET_ACCOUNTS[1].email, ignoreCase = true) -> highlightRoleCard(UserRole.FUNCIONARIO)
                    email.equals(Microdataset.PRESET_ACCOUNTS[2].email, ignoreCase = true) -> highlightRoleCard(UserRole.CIUDADANO)
                    else -> highlightRoleCard(null)
                }
            }
        })

        btnSubmitLogin.setOnClickListener {
            performLogin()
        }

        btnBiometricLogin.setOnClickListener {
            val role = selectedRole ?: UserRole.VETERINARIO
            val account = Microdataset.PRESET_ACCOUNTS.find { it.role == role } ?: Microdataset.PRESET_ACCOUNTS[0]
            AppSessionManager.login(
                role = role,
                customEmail = account.email,
                customName = account.label
            )
            val resultIntent = Intent().apply {
                putExtra("LOGGED_IN_ROLE", role.name)
                putExtra("LOGGED_IN_EMAIL", account.email)
            }
            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Autenticación Biométrica Verificada (${role.label})", Toast.LENGTH_SHORT).show()
            finish()
        }

        tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun selectPreset(account: PresetAccount) {
        selectedRole = account.role
        etLoginEmail.setText(account.email)
        etLoginPassword.setText(account.password)
        highlightRoleCard(account.role)
    }

    private fun highlightRoleCard(role: UserRole?) {
        val density = resources.displayMetrics.density
        val strokeSelected = (2 * density).toInt()
        val strokeNormal = (1 * density).toInt()

        // Vet card
        val isVet = role == UserRole.VETERINARIO
        btnSelectPresetVet.strokeWidth = if (isVet) strokeSelected else strokeNormal
        btnSelectPresetVet.strokeColor = ContextCompat.getColor(
            this,
            if (isVet) R.color.blue_400 else R.color.slate_700
        )
        btnSelectPresetVet.setCardBackgroundColor(
            ContextCompat.getColor(this, if (isVet) R.color.slate_700 else R.color.slate_800)
        )
        tvPresetVetBadge.text = if (isVet) "✓ Activo" else "Usar"

        // Official card
        val isOfficial = role == UserRole.FUNCIONARIO
        btnSelectPresetOfficial.strokeWidth = if (isOfficial) strokeSelected else strokeNormal
        btnSelectPresetOfficial.strokeColor = ContextCompat.getColor(
            this,
            if (isOfficial) R.color.emerald_400 else R.color.slate_700
        )
        btnSelectPresetOfficial.setCardBackgroundColor(
            ContextCompat.getColor(this, if (isOfficial) R.color.slate_700 else R.color.slate_800)
        )
        tvPresetOfficialBadge.text = if (isOfficial) "✓ Activo" else "Usar"

        // Citizen card
        val isCitizen = role == UserRole.CIUDADANO
        btnSelectPresetCitizen.strokeWidth = if (isCitizen) strokeSelected else strokeNormal
        btnSelectPresetCitizen.strokeColor = ContextCompat.getColor(
            this,
            if (isCitizen) R.color.amber_400 else R.color.slate_700
        )
        btnSelectPresetCitizen.setCardBackgroundColor(
            ContextCompat.getColor(this, if (isCitizen) R.color.slate_700 else R.color.slate_800)
        )
        tvPresetCitizenBadge.text = if (isCitizen) "✓ Activo" else "Usar"

        if (role != null) {
            selectedRole = role
        }
    }

    private fun performLogin() {
        val email = etLoginEmail.text.toString().trim()
        val password = etLoginPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese su correo y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Check exact match against preset accounts
        val matchedPreset = Microdataset.PRESET_ACCOUNTS.find {
            it.email.equals(email, ignoreCase = true)
        }

        // 2. Determine target role
        val finalRole = when {
            matchedPreset != null -> matchedPreset.role
            selectedRole != null -> selectedRole!!
            email.contains("vet", ignoreCase = true) -> UserRole.VETERINARIO
            email.contains("salud", ignoreCase = true) ||
                    email.contains("alcaldia", ignoreCase = true) ||
                    email.contains("func", ignoreCase = true) ||
                    email.contains("oficial", ignoreCase = true) ||
                    email.contains("admin", ignoreCase = true) -> UserRole.FUNCIONARIO
            else -> UserRole.CIUDADANO
        }

        // 3. Update global AppSessionManager so all views & activities sync immediately
        AppSessionManager.login(
            role = finalRole,
            customEmail = email,
            customName = matchedPreset?.label
        )

        // 4. Return result for activities using ActivityResultLauncher
        val resultIntent = Intent().apply {
            putExtra("LOGGED_IN_ROLE", finalRole.name)
            putExtra("LOGGED_IN_EMAIL", email)
        }
        setResult(RESULT_OK, resultIntent)

        Toast.makeText(this, "Sesión iniciada como: ${finalRole.label}", Toast.LENGTH_SHORT).show()
        finish()
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
