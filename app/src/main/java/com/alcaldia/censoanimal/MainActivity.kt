package com.alcaldia.censoanimal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alcaldia.censoanimal.data.AppSessionManager
import com.alcaldia.censoanimal.data.Microdataset
import com.alcaldia.censoanimal.model.UserRole
import com.alcaldia.censoanimal.ui.CensusFragment
import com.alcaldia.censoanimal.ui.IndicatorsFragment
import com.alcaldia.censoanimal.ui.LoginActivity
import com.alcaldia.censoanimal.ui.MapFragment
import com.alcaldia.censoanimal.ui.OfflineSyncFragment
import com.alcaldia.censoanimal.ui.RegisterFragment
import com.alcaldia.censoanimal.ui.ScannerActivity
import com.alcaldia.censoanimal.ui.TopBarAccountHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val IS_TOP_NAV_ENABLED = false

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var tvCurrentTime: TextView
    private lateinit var btnUserRole: LinearLayout
    private lateinit var tvRoleName: TextView
    private lateinit var ivRoleIcon: ImageView

    private lateinit var btnNetworkToggle: LinearLayout
    private lateinit var tvNetworkLabel: TextView
    private lateinit var ivNetworkIcon: ImageView

    private lateinit var btnQuickScanner: ImageButton

    // Standardized Top Bar Views
    private lateinit var tvTopBarSubtitle: TextView
    private lateinit var tvTopBarTitle: TextView
    private lateinit var tvTopBarBadge: TextView
    private lateinit var btnTopBarAccount: LinearLayout
    private lateinit var tvTopBarAccountLabel: TextView

    private lateinit var systemStatusBar: LinearLayout

    private var isOnline = true

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val roleName = result.data?.getStringExtra("LOGGED_IN_ROLE")
            if (roleName != null) {
                try {
                    val role = UserRole.valueOf(roleName)
                    AppSessionManager.switchRole(role)
                } catch (_: Exception) {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        btnUserRole = findViewById(R.id.btnUserRole)
        tvRoleName = findViewById(R.id.tvRoleName)
        ivRoleIcon = findViewById(R.id.ivRoleIcon)

        systemStatusBar = findViewById(R.id.systemStatusBar)
        btnNetworkToggle = findViewById(R.id.btnNetworkToggle)
        tvNetworkLabel = findViewById(R.id.tvNetworkLabel)
        ivNetworkIcon = findViewById(R.id.ivNetworkIcon)

        btnQuickScanner = findViewById(R.id.btnQuickScanner)

        // Standardized Top Bar Bindings
        tvTopBarSubtitle = findViewById(R.id.tvTopBarSubtitle)
        tvTopBarTitle = findViewById(R.id.tvTopBarTitle)
        tvTopBarBadge = findViewById(R.id.tvTopBarBadge)
        btnTopBarAccount = findViewById(R.id.btnTopBarAccount)
        tvTopBarAccountLabel = findViewById(R.id.tvTopBarAccountLabel)

        TopBarAccountHelper.setupAccountButton(this, btnTopBarAccount, tvTopBarAccountLabel) {
            updateRoleUI()
        }

        AppSessionManager.addSessionListener {
            runOnUiThread { updateRoleUI() }
        }

        updateClock()
        updateRoleUI()
        updateNetworkUI()
        if(IS_TOP_NAV_ENABLED) systemStatusBar.visibility = android.view.View.VISIBLE
        else  systemStatusBar.visibility = android.view.View.GONE

        setupBottomNavigation()
        setupTopBarListeners()

        // Default to Census tab
        if (savedInstanceState == null) {
            loadFragment(CensusFragment())
            updateTopBar(
                getString(R.string.header_census_title),
                getString(R.string.header_census_sub),
                "${Microdataset.INITIAL_ANIMAL_RECORDS.size} censados"
            )
        }
    }

    fun updateTopBar(title: String, subtitle: String, badgeText: String? = null) {
        tvTopBarTitle.text = title
        tvTopBarSubtitle.text = subtitle
        updateTopBarBadge(badgeText)
    }

    fun updateTopBarBadge(badgeText: String?) {
        if (badgeText != null) {
            tvTopBarBadge.text = badgeText
            tvTopBarBadge.visibility = View.VISIBLE
        } else {
            tvTopBarBadge.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateRoleUI()
    }

    private fun updateClock() {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        tvCurrentTime.text = time
    }

    private fun updateRoleUI() {
        val currentRole = AppSessionManager.currentProfile.role
        when (currentRole) {
            UserRole.VETERINARIO -> {
                tvRoleName.text = "Veterinario"
                tvRoleName.setTextColor(ContextCompat.getColor(this, R.color.blue_200))
                ivRoleIcon.setImageResource(R.drawable.ic_user)
            }
            UserRole.FUNCIONARIO -> {
                tvRoleName.text = "Funcionario"
                tvRoleName.setTextColor(ContextCompat.getColor(this, R.color.emerald_200))
                ivRoleIcon.setImageResource(R.drawable.ic_landmark)
            }
            UserRole.CIUDADANO -> {
                tvRoleName.text = "Ciudadano"
                tvRoleName.setTextColor(ContextCompat.getColor(this, R.color.amber_200))
                ivRoleIcon.setImageResource(R.drawable.ic_user)
            }
        }
        tvTopBarAccountLabel.text = when (currentRole) {
            UserRole.VETERINARIO -> "Veterinario"
            UserRole.FUNCIONARIO -> "Funcionario"
            UserRole.CIUDADANO -> "Ciudadano"
        }
    }

    private fun updateNetworkUI() {
        if (isOnline) {
            tvNetworkLabel.text = "5G Online"
            tvNetworkLabel.setTextColor(ContextCompat.getColor(this, R.color.emerald_300))
            ivNetworkIcon.setImageResource(R.drawable.ic_wifi)
            ivNetworkIcon.setColorFilter(ContextCompat.getColor(this, R.color.emerald_400))
        } else {
            tvNetworkLabel.text = "Offline Rural"
            tvNetworkLabel.setTextColor(ContextCompat.getColor(this, R.color.amber_300))
            ivNetworkIcon.setImageResource(R.drawable.ic_wifi_off)
            ivNetworkIcon.setColorFilter(ContextCompat.getColor(this, R.color.amber_400))
        }
    }

    private fun setupTopBarListeners() {
        btnUserRole.setOnClickListener {
            showRoleSwitcherDialog()
        }

        btnNetworkToggle.setOnClickListener {
            isOnline = !isOnline
            updateNetworkUI()
            val msg = if (isOnline) "Modo 5G Online activado (Sincronización automática)" else "Modo Offline Rural activado (Guardando en Room)"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        btnQuickScanner.setOnClickListener {
            val intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showRoleSwitcherDialog() {
        val roles = arrayOf(
            "👨‍⚕️ Veterinario Aliado (Censo y Chips)",
            "🏛️ Funcionario Municipal (Auditoría e Indicadores)",
            "👤 Ciudadano / Propietario (Habeas Data)"
        )

        AlertDialog.Builder(this)
            .setTitle("Cambiar Rol Activo")
            .setItems(roles) { _, which ->
                val newRole = when (which) {
                    0 -> UserRole.VETERINARIO
                    1 -> UserRole.FUNCIONARIO
                    else -> UserRole.CIUDADANO
                }
                AppSessionManager.switchRole(newRole)
                updateRoleUI()
                Toast.makeText(this, "Perfil cambiado a: ${newRole.label}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_census -> {
                    loadFragment(CensusFragment())
                    updateTopBar(
                        getString(R.string.header_census_title),
                        getString(R.string.header_census_sub),
                        "${Microdataset.INITIAL_ANIMAL_RECORDS.size} censados"
                    )
                    true
                }
                R.id.nav_register -> {
                    loadFragment(RegisterFragment())
                    updateTopBar(
                        getString(R.string.header_register_title),
                        getString(R.string.header_register_sub),
                        null
                    )
                    true
                }
                R.id.nav_sync -> {
                    loadFragment(OfflineSyncFragment())
                    updateTopBar(
                        getString(R.string.header_sync_title),
                        getString(R.string.header_sync_sub),
                        null
                    )
                    true
                }
                R.id.nav_indicators -> {
                    loadFragment(IndicatorsFragment())
                    updateTopBar(
                        getString(R.string.header_indicators_title),
                        getString(R.string.header_indicators_sub),
                        null
                    )
                    true
                }
                R.id.nav_map -> {
                    loadFragment(MapFragment())
                    updateTopBar(
                        getString(R.string.header_map_title),
                        getString(R.string.header_map_sub),
                        null
                    )
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun selectCensusTab() {
        bottomNavigation.selectedItemId = R.id.nav_census
    }
}
