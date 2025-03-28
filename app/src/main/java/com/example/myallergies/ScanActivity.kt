package com.example.myallergies

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ScanActivity : AppCompatActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private var camera: Camera? = null
    private var isFlashEnabled = false
    private val scannedAllergyProducts = mutableListOf<String>() // Lista acumulativa de produtos escaneados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        val tvAllergies: TextView = findViewById(R.id.tvAllergiesScan)
        val btnFlash: ImageButton = findViewById(R.id.btnFlash)
        val btnBack: Button = findViewById(R.id.btnBack)
        val btnAddIngredients: Button = findViewById(R.id.btnAddIngredients)
        val btnScan: Button = findViewById(R.id.btnScan)
        val btnNewScan: Button = findViewById(R.id.btnScanNewProduct)
        val btnVoltar = findViewById<ImageButton>(R.id.btnBackHeader)

        // Obter a lista de alergias passada pela MainActivity
        val allergies = intent.getStringArrayListExtra("allergies") ?: arrayListOf()

        // Atualizar o TextView ao iniciar a Activity
        updateTextViewWithProducts(tvAllergies, scannedAllergyProducts)

        // Botão de Flash
        btnFlash.setOnClickListener {
            if (camera != null) {
                isFlashEnabled = !isFlashEnabled
                camera?.cameraControl?.enableTorch(isFlashEnabled)
                btnFlash.setImageResource(if (isFlashEnabled) R.drawable.ic_flash_on else R.drawable.ic_flash_off)
            }
        }

        // Botão de Voltar
        btnBack.setOnClickListener {
            finish() // Finaliza a Activity e retorna para a anterior
        }

        // Botão de Adicionar Ingredientes
        btnAddIngredients.setOnClickListener {
            showAddIngredientsPopup(allergies, tvAllergies)
        }

        // Botão de Escanear Novo Produto
        btnNewScan.setOnClickListener {
            scannedAllergyProducts.clear()
            updateTextViewWithProducts(tvAllergies, scannedAllergyProducts)
            btnScan.visibility = View.VISIBLE // Torna o botão "Escanear" visível novamente

            // Desativa a câmera
            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll() // Libera todos os recursos da câmera

                // Limpa o SurfaceProvider do PreviewView
                val previewView: PreviewView = findViewById(R.id.previewView)
                previewView.surfaceProvider.to(null) // Remove o SurfaceProvider para limpar o preview
                previewView.post {
                    previewView.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            android.R.color.black
                        )
                    ) // Define fundo preto
                }
            }, ContextCompat.getMainExecutor(this))

            Toast.makeText(this, "Pronto para escanear um novo produto.", Toast.LENGTH_SHORT).show()
        }

        // Botão de Escanear
        btnScan.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                btnScan.visibility = View.GONE // Torna o botão "Escanear" invisível

                // Desativa o flash ao iniciar o escaneamento
                if (isFlashEnabled) {
                    isFlashEnabled = false
                    camera?.cameraControl?.enableTorch(false)
                    btnFlash.setImageResource(R.drawable.ic_flash_off) // Atualiza o ícone do flash
                }

                startScanning(allergies, tvAllergies)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }

        btnVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateTextViewWithProducts(tv: TextView, products: List<String>) {
        if (products.isNotEmpty()) {
            tv.text = "Produtos encontrados: ${products.joinToString(", ")}"
            tv.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        } else {
            tv.text = "Nenhum produto escaneado ainda."
            tv.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        }
    }

    private fun showAddIngredientsPopup(allergies: ArrayList<String>, tvAllergies: TextView) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_add_ingredients, null)
        val etIngredients: EditText = popupView.findViewById(R.id.etIngredients)
        val btnSubmitIngredients: Button = popupView.findViewById(R.id.btnSubmitIngredients)
        val tvPopupResult: TextView = popupView.findViewById(R.id.tvPopupResult)

        val dialog = AlertDialog.Builder(this)
            .setView(popupView)
            .create()

        btnSubmitIngredients.setOnClickListener {
            val ingredientsText = etIngredients.text.toString()
            if (ingredientsText.isNotEmpty()) {
                val matchedAllergies =
                    allergies.filter { ingredientsText.contains(it, ignoreCase = true) }
                if (matchedAllergies.isNotEmpty()) {
                    matchedAllergies.forEach { allergy ->
                        if (!scannedAllergyProducts.contains(allergy)) {
                            scannedAllergyProducts.add(allergy)
                        }
                    }
                    updateTextViewWithProducts(tvPopupResult, scannedAllergyProducts)
                } else {
                    tvPopupResult.text =
                        "Este produto não contém nenhum item da sua lista de alergias."
                    tvPopupResult.setTextColor(
                        ContextCompat.getColor(
                            this,
                            android.R.color.holo_green_dark
                        )
                    )
                }
                tvPopupResult.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Por favor, insira os ingredientes.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialog.show()
    }

    private fun startScanning(allergies: ArrayList<String>, tvAllergies: TextView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(findViewById<PreviewView>(R.id.previewView).surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also { imageAnalysis ->
                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                        processImageProxy(imageProxy, allergies, tvAllergies)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this,
                    "Erro ao acessar a câmera: ${exc.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(
        imageProxy: ImageProxy,
        allergies: ArrayList<String>,
        tvAllergies: TextView
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val scanResult = visionText.text
                    val matchedAllergies =
                        allergies.filter { scanResult.contains(it, ignoreCase = true) }
                    if (matchedAllergies.isNotEmpty()) {
                        matchedAllergies.forEach { allergy ->
                            if (!scannedAllergyProducts.contains(allergy)) {
                                scannedAllergyProducts.add(allergy)
                            }
                        }
                        updateTextViewWithProducts(tvAllergies, scannedAllergyProducts)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro ao processar imagem: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}