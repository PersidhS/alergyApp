package com.example.myallergies

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvName: TextView = findViewById(R.id.tvName)
        val tvAllergies: TextView = findViewById(R.id.tvAllergies)
        val btnScan: Button = findViewById(R.id.btnScan)
        val btnEditAllergies: Button = findViewById(R.id.btnEditAllergies)

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
    }

    private fun loadAllergies(): List<String> {
        // Carregar alergias salvas usando SharedPreferences
        val sharedPreferences = getSharedPreferences("AllergiesPrefs", MODE_PRIVATE)
        return sharedPreferences.getStringSet("allergies", emptySet())?.toList() ?: emptyList()
    }

    override fun onResume() {
        super.onResume()
        // Atualizar a lista de alergias quando a atividade for retomada
        val tvAllergies: TextView = findViewById(R.id.tvAllergies)
        val allergies = loadAllergies()
        tvAllergies.text = if (allergies.isNotEmpty()) {
            "Alergias: ${allergies.joinToString(", ")}"
        } else {
            "Alergias: Nenhuma"
        }
    }
}