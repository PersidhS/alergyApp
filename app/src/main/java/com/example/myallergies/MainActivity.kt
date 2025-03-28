package com.example.myallergies

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import com.example.myallergies.utils.UserUtils

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var ivProfileCircle: ImageView
    private lateinit var ivUserPhoto: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvAllergies: TextView
    private var cachedAllergies: List<String>? = null
    private var cachedUserName: String? = null
    private var lastProfilePhotoPath: String? =
        null // Armazena o caminho/URL da última foto carregada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar views
        tvName = findViewById(R.id.tvName)
        tvAllergies = findViewById(R.id.tvAllergies)
        val btnScan: Button = findViewById(R.id.btnScan)
        val btnEditAllergies: Button = findViewById(R.id.btnEditAllergies)
        val btnSettings: Button = findViewById(R.id.btnSettings)
        drawerLayout = findViewById(R.id.drawer_layout)
        ivProfileCircle = findViewById(R.id.ivProfileCircle)
        ivUserPhoto = findViewById(R.id.ivUserPhoto)

        // Carregar foto do usuário
        loadUserPhotos()

        // Configurar dados do perfil
        cachedUserName = ""
        tvName.text = "Nome: $cachedUserName"

        // Carregar alergias salvas
        cachedAllergies = loadAllergies()
        updateAllergiesText()

        // Navegar para a página de escaneamento
        btnScan.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            intent.putStringArrayListExtra("allergies", ArrayList(cachedAllergies))
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
    }

    private fun loadAllergies(): List<String> {
        if (cachedAllergies == null) {
            val sharedPreferences = getSharedPreferences("AllergiesPrefs", MODE_PRIVATE)
            cachedAllergies =
                sharedPreferences.getStringSet("allergies", emptySet())?.toList() ?: emptyList()
        }
        return cachedAllergies!!
    }

    private fun updateAllergiesText() {
        tvAllergies.text = if (cachedAllergies?.isNotEmpty() == true) {
            "Alergias: ${cachedAllergies!!.joinToString(", ")}"
        } else {
            "Alergias: Nenhuma"
        }
    }

    private fun loadUserPhotos() {
        // Obter o caminho/URL da foto atual do usuário
        val currentPhotoPath =
            UserUtils.getUserPhotoPath(this)
        val photoUri = if (!currentPhotoPath.isNullOrEmpty()) Uri.parse(currentPhotoPath) else null

        // Verificar se a foto foi alterada
        if (currentPhotoPath != lastProfilePhotoPath) {
            UserUtils.loadUserPhoto(ivProfileCircle, this)
            UserUtils.loadUserPhoto(ivUserPhoto, this)
            lastProfilePhotoPath = currentPhotoPath // Atualizar o caminho/URL armazenado
        }
    }

    override fun onResume() {
        super.onResume()

        // Atualizar o nome do usuário
        if (cachedUserName == null) {
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            cachedUserName = sharedPreferences.getString("userName", "Per")
        }
        tvName.text = "Nome: $cachedUserName"

        // Verificar e recarregar a foto do usuário, se necessário
        loadUserPhotos()

        // Atualizar a lista de alergias
        cachedAllergies = loadAllergies()
        updateAllergiesText()
    }
}