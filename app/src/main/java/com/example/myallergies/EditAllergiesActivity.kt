package com.example.myallergies

import AllergiesAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EditAllergiesActivity : AppCompatActivity() {
    private val allergiesList = mutableListOf<String>() // Lista de alergias
    private lateinit var allergiesAdapter: AllergiesAdapter // Adapter para o RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_allergies)

        val btnAddAllergy = findViewById<Button>(R.id.btnAddAllergy)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val rvAllergies = findViewById<RecyclerView>(R.id.rvAllergies)
        val btnVoltar = findViewById<ImageButton>(R.id.btnBackToHome)

        // Configurar RecyclerView
        allergiesAdapter = AllergiesAdapter(allergiesList) { position ->
            // Callback para excluir item
            allergiesList.removeAt(position)
            allergiesAdapter.notifyItemRemoved(position)
        }
        rvAllergies.layoutManager = LinearLayoutManager(this)
        rvAllergies.adapter = allergiesAdapter

        // Carregar alergias salvas
        loadAndUpdateAllergies()

        // Adicionar alergia à lista
        btnAddAllergy.setOnClickListener {
            val etAllergy: EditText = findViewById(R.id.etAllergy)
            val allergyInput = etAllergy.text.toString().trim()
            if (allergyInput.isNotEmpty()) {
                // Dividir a string por vírgulas e adicionar os itens à lista
                val newAllergies = allergyInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val lowercaseAllergiesList = allergiesList.map { it.lowercase() } // Lista em lowercase para comparação

                newAllergies.forEach { allergy ->
                    if (!lowercaseAllergiesList.contains(allergy.lowercase())) {
                        allergiesList.add(allergy) // Adicionar apenas se não existir (case insensitive)
                    } else {
                        Toast.makeText(this, "Alergia '$allergy' já está na lista.", Toast.LENGTH_SHORT).show()
                    }
                }

                allergiesAdapter.notifyDataSetChanged() // Atualizar o RecyclerView
                etAllergy.text.clear()
            } else {
                Toast.makeText(this, "Por favor, insira uma ou mais alergias separadas por vírgula.", Toast.LENGTH_SHORT).show()
            }
        }

        // Salvar alergias
        btnSave.setOnClickListener {
            saveAllergies(allergiesList)
            Toast.makeText(this, "Alergias salvas com sucesso!", Toast.LENGTH_SHORT).show()
            loadAndUpdateAllergies() // Recarregar a lista após salvar
        }

        btnVoltar.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveAllergies(allergies: List<String>) {
        val sharedPreferences = getSharedPreferences("AllergiesPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("allergies", allergies.toSet())
        editor.apply()
        finish()
    }

    private fun loadAllergies(): MutableList<String> {
        // Carregar alergias salvas usando SharedPreferences
        val sharedPreferences = getSharedPreferences("AllergiesPrefs", MODE_PRIVATE)
        return (sharedPreferences.getStringSet("allergies", emptySet())?.toList()
            ?: emptyList()).toMutableList()
    }

    private fun loadAndUpdateAllergies() {
        // Atualizar a lista de alergias e notificar o Adapter
        allergiesList.clear()
        allergiesList.addAll(loadAllergies())
        allergiesAdapter.notifyDataSetChanged() // Notificar o Adapter sobre as mudanças
    }
}