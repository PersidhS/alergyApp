package com.example.myallergies

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import com.example.myallergies.components.UserPhotoView
import com.example.myallergies.utils.UserUtils // Importando o UserUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvName: TextView = findViewById(R.id.tvName)
        val tvAllergies: TextView = findViewById(R.id.tvAllergies)
        val btnScan: Button = findViewById(R.id.btnScan)
        val btnEditAllergies: Button = findViewById(R.id.btnEditAllergies)
        val btnSettings: Button = findViewById(R.id.btnSettings)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val ivProfileCircle: ImageView = findViewById(R.id.ivProfileCircle)

        // Configurar dados do perfil
        val name = "Per"
        tvName.text = "Nome: $name"

        // Carregar alergias salvas
        val allergies = loadAllergies()
        tvAllergies.text = if (allergies.isNotEmpty()) {
            "Alergias: ${allergies.joinToString(", ")}"
        } else {
            "Alergias: Nenhuma"
        }

        // Navegar para a página de escaneamento
        btnScan.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            intent.putStringArrayListExtra("allergies", ArrayList(allergies))
            startActivity(intent)
        }

        // Navegar para a página de edição de alergias
        btnEditAllergies.setOnClickListener {
            val intent = Intent(this, EditAllergiesActivity::class.java)
            startActivity(intent)
        }

        // Navegar para a página de configurações
        btnSettings.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
        }

        // Abrir o menu lateral ao clicar no círculo
        ivProfileCircle.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Carregar a foto do usuário no modal ao abrir o menu lateral
        ivProfileCircle.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            UserUtils.loadUserPhoto(ivProfileCircle, this) // Atualiza a foto no modal
        }
    }

    private fun loadAllergies(): List<String> {
        // Carregar alergias salvas usando SharedPreferences
        val sharedPreferences = getSharedPreferences("AllergiesPrefs", MODE_PRIVATE)
        return sharedPreferences.getStringSet("allergies", emptySet())?.toList() ?: emptyList()
    }

    override fun onResume() {
        super.onResume()

        // Atualizar o nome do usuário
        val tvName: TextView = findViewById(R.id.tvName)
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedName = sharedPreferences.getString("userName", "Per")
        tvName.text = "Nome: $savedName"

        // Atualizar a foto do usuário usando UserUtils
        val ivProfileCircle: ImageView = findViewById(R.id.ivProfileCircle)
        UserUtils.loadUserPhoto(ivProfileCircle, this)

        // Atualizar a lista de alergias
        val tvAllergies: TextView = findViewById(R.id.tvAllergies)
        val allergies = loadAllergies()
        tvAllergies.text = if (allergies.isNotEmpty()) {
            "Alergias: ${allergies.joinToString(", ")}"
        } else {
            "Alergias: Nenhuma"
        }
    }
}