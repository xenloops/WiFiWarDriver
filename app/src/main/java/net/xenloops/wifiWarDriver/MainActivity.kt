package net.xenloops.wifiWarDriver

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import net.xenloops.wifiWarDriver.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var wifiManager: WifiManager
    private lateinit var adapter: WifiAdapter
    private lateinit var results: List<WifiNetwork>
    private lateinit var date: String
    private lateinit var time: String

    // Register the receiver
    private val wifiReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
              results = wifiManager.scanResults
                  .sortedByDescending { it.level }
                  .map { result ->
                  WifiNetwork(
                      ssid = result.SSID.ifEmpty { "<Hidden SSID>" },
                      signal = result.level,
                      channel = convertFrequencyToChannel(result.frequency),
                      encryption = parseEncryption(result.capabilities),
                      bssid = result.BSSID,
                      dateseen = date,
                      timeseen = time,
                      latitude = Location.latitude,
                      longitude = Location.longitude
                  )
              }

            adapter.updateData(results)

            writeLog()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = WifiAdapter(emptyList())
        binding.wifiRecycler.layoutManager = LinearLayoutManager(this)
        binding.wifiRecycler.adapter = adapter

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "WiFi is disabled... enabling it.", Toast.LENGTH_SHORT).show()
            wifiManager.isWifiEnabled = true
        }

        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

        if (hasPermission()) {
            scanWifi()
        } else {
            requestPermission()
        }
        val loc = Location
        loc.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        startClock()
        loc.getLastLocation(this, this, binding)

    }

    private fun writeLog() {
        LogFile.writeWifiLogToFile(this, results)
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
        val success = wifiManager.startScan()
        if (!success) {
            Toast.makeText(this, "WiFi scan failed to start", Toast.LENGTH_SHORT).show()
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
            capabilities.contains("PSK") -> "WPA-PSK"
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