package net.xenloops.wifiWarDriver

import android.content.Context
import android.util.Log
import java.io.File

object LogFile {

    fun writeWifiLogToFile(context: Context, lines: List<WifiNetwork>) {
        try {
            val filename = "wifi_log.txt"
            val file = File(context.filesDir, filename)

            file.printWriter().use { out ->
                // Write CSV header
                out.print("SSID,")
                out.print("BSSID,")
                out.print("Signal strength,")
                out.print("Encryption,")
                out.print("Channel,")
                out.print("Date seen,")
                out.print("Time seen,")
                out.print("Latitude,")
                out.println("Longitude")
                // Write data for each entry
                lines.forEach {
                    out.print("\"" + it.ssid + "\",")
                    out.print("\"" + it.bssid + "\",")
                    out.print(it.signal.toString() + ",")
                    out.print("\"" + it.encryption + "\",")
                    out.print(it.channel.toString() + ",")
                    out.print(it.dateseen + ",")
                    out.print(it.timeseen + ",")
                    out.print(it.latitude.toString() + ",")
                    out.print(it.longitude.toString())
                    out.println()
                }
            }

            Log.i("LogFile", "Successful write to ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e("LogFile", "Error writing to file:", e)
        }

    }

}