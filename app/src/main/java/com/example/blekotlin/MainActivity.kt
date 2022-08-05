package com.example.blekotlin

import android.Manifest
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blekotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_COARSE_LOCATION = 1
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var deviceList: ArrayList<BluetoothDevice> = ArrayList()


    /* Listen for scan results */
    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("DEVICE",result.device.toString())
            deviceList.add(result.device)
            binding.recyclerViewDevices.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        initBLE()
        initUI()
    }

    private fun initUI() {

        binding.recyclerViewDevices.adapter = CustomAdapter(applicationContext, deviceList)
        binding.recyclerViewDevices.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDevices.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.buttonDiscover.setOnClickListener {

            if (binding.buttonDiscover.text == "start scanning") {
                deviceList.clear()
                startScanning()
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonDiscover.text = "stop scanning"
            } else {
                stopScanning()
                binding.progressBar.visibility  = View.INVISIBLE
                binding.buttonDiscover.text = "start scanning"
            }
        }
    }

    private fun initBLE() {

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        if (!bluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            registerForResult.launch(enableIntent)
        }

    }

    private fun startScanning() {
        GlobalScope.launch {
            checkBleScanPermission()
            bluetoothLeScanner.startScan(leScanCallback)
        }
    }

    private fun stopScanning() {
        GlobalScope.launch {
            checkBleScanPermission()
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    private fun checkBleScanPermission()
    {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.BLUETOOTH_SCAN)
        }
    }

    private val registerForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            Log.d("INTENT",intent.toString())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_COARSE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission granted
                } else {
                    Toast.makeText(this,"Without location access, this app cannot discover beacons.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}