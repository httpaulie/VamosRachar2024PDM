package com.example.vamosrachar

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.Locale


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentAddress: String = ""
    private lateinit var totalAmount: EditText
    private lateinit var numberOfPeople: EditText
    private lateinit var result: TextView
    private lateinit var shareButton: Button
    private lateinit var ttsButton: Button
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        totalAmount = findViewById(R.id.totalAmount)
        numberOfPeople = findViewById(R.id.numberOfPeople)
        result = findViewById(R.id.result)
        shareButton = findViewById(R.id.shareButton)
        ttsButton = findViewById(R.id.ttsButton)
        tts = TextToSpeech(this, this)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateAndDisplayResult()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        requestLocationPermission()

        totalAmount.addTextChangedListener(textWatcher)
        numberOfPeople.addTextChangedListener(textWatcher)

        shareButton.setOnClickListener {
            shareResult()
        }

        ttsButton.setOnClickListener {
            speakResult()
        }
    }

    private fun requestLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permissão concedida
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
            }
        }

        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    currentAddress = address.getAddressLine(0) ?: "Endereço desconhecido"
                } else {
                    Toast.makeText(this, "Endereço não encontrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show()
            }
        }
    }

        private fun generateShareReport(totalAmount: String, numberOfPeople: String, result: String): String {
        return "Conta total: R$$totalAmount\n" +
                "Número de pessoas: $numberOfPeople\n" +
                //"Valor individual: R$$result\n" +
                "$result\n" +
                "Endereço: $currentAddress"
        }


    private fun calculateAndDisplayResult() {
        val amount = totalAmount.text.toString().toDoubleOrNull()
        val people = numberOfPeople.text.toString().toIntOrNull()

        if (amount != null && people != null && people > 0) {
            val resultValue = amount / people
            val resultText = "Valor Individual: R$%.2f".format(resultValue)
            result.text = resultText
        } else {
            result.text = "Valor Individual:  "
        }
    }

    private fun shareResult() {
        val resultText = result.text.toString()
        val totalAmountText = totalAmount.text.toString()
        val numberOfPeopleText = numberOfPeople.text.toString()
        val report = generateShareReport(totalAmountText, numberOfPeopleText, resultText)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, report)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))

//        val resultText = result.text.toString()
//        if (resultText.isNotEmpty()) {
//            val shareIntent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, resultText)
//                type = "text/plain"
//            }
//            startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
//        }
    }

    private fun speakResult() {
        val resultText = result.text.toString()
        if (resultText.isNotEmpty()) {
            tts.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("pt", "BR")
        }
    }

    override fun onDestroy() {
        if (tts != null) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}