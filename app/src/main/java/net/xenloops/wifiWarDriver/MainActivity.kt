package net.xenloops.wifiWarDriver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.xenloops.wifiWarDriver.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var wifiManager: WifiManager
    private lateinit var wifiReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Request permissions if needed
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            0
        )

        // Register the receiver
        wifiReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val results = wifiManager.scanResults

                val formatted = results.joinToString("\n\n") { result ->
                    val capabilities = result.capabilities
                    val encryption = when {
                        capabilities.contains("WEP") -> "WEP"
                        capabilities.contains("WPA") -> "WPA/2"
                        capabilities.contains("EAP") -> "WPA-Ent"
                        capabilities.contains("SAE") -> "WPA3"
                        else -> "Open"
                    }
                    val name = when {
                        result.SSID.isEmpty() -> "No SSID"
                        else -> result.SSID
                    }

                    """
                    ** $name / ${result.BSSID.uppercase()} **
                    Sig: ${result.level} dBm  Ch: ${convertFrequencyToChannel(result.frequency)}  Encryption: $encryption
                    """.trimIndent()
                }

                binding.wifiList.text = formatted.ifEmpty { "No networks found." }
                Log.d("WiFiDebug", "Updated TextView with ${results.size} networks")
            }
        }

        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

        // Start initial scan
        val success = wifiManager.startScan()
        Log.d("WiFiDebug", "Scan started? $success")

        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                val ok = wifiManager.startScan()
                Log.d("WiFiDebug", "Repeating scan, success? $ok")
                delay(2000)
            }
        }

    }


    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            123
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("WiFiDebug", "Permission result: ${grantResults.firstOrNull()}")
        if (requestCode == 123 && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted: scan WiFi", Toast.LENGTH_LONG).show()
            scanWifi()
        } else {
            Toast.makeText(this, "Permission required to scan WiFi", Toast.LENGTH_LONG).show()
        }
    }

    private fun scanWifi() {
        lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                binding.wifiList.text = wifiReceiver.resultData
                delay(1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiReceiver)
    }

    private fun convertFrequencyToChannel(freq: Int): Int {
        return when (freq) {
            in 2412..2484 -> (freq - 2407) / 5
            in 5170..5825 -> (freq - 5000) / 5
            else -> -1 // unknown
        }
    }

    private fun parseEncryption(capabilities: String): String {
        return when {
            capabilities.contains("WPA3") -> "WPA3"
            capabilities.contains("WPA2") -> "WPA2"
            capabilities.contains("WPA") -> "WPA"
            capabilities.contains("WEP") -> "WEP"
            capabilities.contains("PSK") -> "WPA/WPA2"
            capabilities.contains("EAP") -> "EAP"
            else -> "Open"
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


}