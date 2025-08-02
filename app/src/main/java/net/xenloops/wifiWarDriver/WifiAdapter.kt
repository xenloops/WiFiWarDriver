package net.xenloops.wifiWarDriver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WifiAdapter(private var networks: List<WifiNetwork>) :
    RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    class WifiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ssidText: TextView = view.findViewById(R.id.ssidText)
        val detailsText: TextView = view.findViewById(R.id.detailsText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wifi_network, parent, false)
        return WifiViewHolder(view)
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        val network = networks[position]
        holder.ssidText.text = network.ssid
        holder.detailsText.text = """
             ${network.encryption} Ch ${network.channel} @ ${network.signal} dBm
            BSSID: ${network.bssid}
        """.trimIndent()
    }

    override fun getItemCount() = networks.size

    fun updateData(newNetworks: List<WifiNetwork>) {
        networks = newNetworks
        notifyDataSetChanged()
    }

}
