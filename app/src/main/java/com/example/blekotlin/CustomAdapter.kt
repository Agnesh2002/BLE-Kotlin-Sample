package com.example.blekotlin

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val context: Context, private val deviceList: ArrayList<BluetoothDevice>) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_layout,parent,false)
        return MyViewHolder(v)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvAddress.text = deviceList[position].address
        holder.tvName.text = deviceList[position].name?.toString()

        holder.connect.setOnClickListener {
            val i = Intent(context, DeviceControlActivity::class.java)
            i.putExtra("DEVICE_NAME",deviceList[position].name?.toString())
            i.putExtra("DEVICE_ADDRESS",deviceList[position].address)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            holder.connect.context.startActivity(i)
            Toast.makeText(context, deviceList[position].name?.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val tvAddress: TextView = view.findViewById(R.id.custom_tv_address)
        val tvName: TextView = view.findViewById(R.id.custom_tv_name)
        val connect: Button = view.findViewById(R.id.custom_btn_connect)
    }

}