package net.xenloops.wifiWarDriver


import java.util.Date

data class WifiNetwork(
    val ssid: String,
    val signal: Int,
    val channel: Int,
    val encryption: String,
    val bssid: String,
    val dateseen: String,
    val timeseen: String,
    val latitude: Double,
    val longitude: Double
)  {
    override fun toString(): String {
        return """
            SSID: $ssid
            Signal: ${signal}dBm
            Channel: $channel
            Encryption: $encryption
            BSSID: $bssid
            Date: $dateseen
            Time: $timeseen
            Latitude: ${latitude}
            Longitude: ${longitude} 
        """.trimIndent()
    }
}
