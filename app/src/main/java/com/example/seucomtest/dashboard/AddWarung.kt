package com.example.seucomtest.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View.VISIBLE
import com.example.seucomtest.utils.UtilsFunction.setImage
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.seucomtest.databinding.ActivityAddWarungBinding
import com.example.seucomtest.utils.Constant
import com.example.seucomtest.utils.UtilsFunction.showEdtError
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class AddWarung : AppCompatActivity() {

    var warungName : TextInputEditText ?= null
    var cordinate : TextInputEditText ?= null
    var address : TextInputEditText ?= null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var binding : ActivityAddWarungBinding ?= null

    private val VIDEO_PICK_CAMERA_CODE   = 101
    private val LOCATION_CODE   = 102
    private val CAMERA_REQUEST_CODE = 103

    private var imageUri : Uri?= null
    private var mDatabase = FirebaseDatabase.getInstance().getReference("Warung")
    private var id : String ?= null
    private var key : String = mDatabase.push().key.toString()
    private lateinit var cameraPermission : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWarungBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        initVar()
        attachListener()
    }

    private fun initVar() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 5000
        cameraPermission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val warungName = intent.getStringExtra("nama")
        val warungAddress = intent.getStringExtra("alamat")
        val warungCordinate = intent.getStringExtra("kordinat")
        val warungImage = intent.getStringExtra("image")
        id = intent.getStringExtra("id")
        if (warungName != null) showEdit(warungName, warungAddress, warungCordinate, warungImage)
    }

    private fun showEdit(
        warungName: String,
        warungAddress: String?,
        warungCordinate: String?,
        warungImage: String?
    ) {
        binding?.etWarungName?.setText(warungName)
        binding?.etAdress?.setText(warungAddress)
        binding?.etCordinate?.setText(warungCordinate)
        binding?.ivAddWarung?.let { setImage(this@AddWarung, warungImage ?: "", it) }
        binding?.btnSubmit?.text = "SAVE"

        binding?.btnFabDelete?.visibility = VISIBLE
    }

    private fun attachListener() {

        binding?.btnFabDelete?.setOnClickListener {
            mDatabase.child(id ?: "").removeValue()
            finish()
        }

        binding?.ivAddWarung?.setOnClickListener{
            imagePickCamera()
        }

        binding?.btnPickCordinate?.setOnClickListener {
            getCordinate()
        }

        binding?.btnSubmit?.setOnClickListener {
            warungName = binding?.etWarungName
            cordinate = binding?.etCordinate
            address = binding?.etAdress
            Toast.makeText(applicationContext, "${Constant.warungNameList}", Toast.LENGTH_SHORT).show()
            when{
                imageUri == null -> Toast.makeText(applicationContext, "Silahkan masukan Photo Warung", Toast.LENGTH_SHORT).show()
                warungName?.text.isNullOrEmpty() -> showEdtError(warungName, "Nama Warung Tidak Boleh Kosong")
                cordinate?.text.isNullOrEmpty() -> showEdtError(cordinate, "Cordinate Tidak Boleh Kosong")
                address?.text.isNullOrEmpty() -> showEdtError(address, "Cordinate Tidak Boleh Kosong")
                Constant.warungNameList?.contains(warungName?.text.toString()) -> showEdtError(warungName, "Nama Warung Tidak Boleh Sama")
                else -> {
                    saveToDatabase()
                }
            }
        }
    }

    private fun saveToDatabase() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        val timeStamp = "" + System.currentTimeMillis()
        val filePathName = "Camera/camera_$timeStamp"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathName)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                val uriTask = taskSnapshot.storage.downloadUrl
                uriTask.addOnCompleteListener {
                        val downloadUri = uriTask?.result
                        Log.d("uri", "${uriTask?.result}")
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = key
                        hashMap["nama_warung"] = warungName?.text.toString()
                        hashMap["kordinate"] = cordinate?.text.toString()
                        hashMap["alamat"] = address?.text.toString()
                        hashMap["downloadUri"] = "$downloadUri"

                        if (id != null) key = id.toString()
                        mDatabase.child(key ?: "")
                            .setValue(hashMap)
                            .addOnSuccessListener {taskSnapshot ->
                                Toast.makeText(this, "berhasil di upload", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener{e ->
                                Toast.makeText(this, "${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                }
            }
            .addOnFailureListener{
                Toast.makeText(applicationContext, "${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }

            .addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
            }

    }

    private fun getCordinate() {
        if (checkLocationPermission()){
            if (isLocationEnabled(applicationContext)){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result

                    if (location == null) {
                        newLocationData()
                    } else {
                        val long : String = location.longitude.toString()
                        val lat : String = location.latitude.toString()

                       binding?.etCordinate?.setText("$long,$lat")
                    }
                }
            }
        }else{
            requestLocationPermisson()
        }
    }

    @SuppressLint("MissingPermission")
    private fun newLocationData() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        @SuppressLint("SetTextI18n")
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            binding?.etCordinate?.setText( lastLocation?.longitude.toString() + lastLocation.latitude.toString() )
//            myRef?.child(id ?: "")?.child("long")?.setValue(lastLocation?.longitude ?: 0.1)
//            myRef?.child(id ?: "")?.child("lat")?.setValue(lastLocation?.latitude   ?: 0.1)
            Log.d("Debug:", "your last last location: " + lastLocation.longitude.toString())
//            tv_Text.text = "You Last Location is : Long: "+ lastLocation.longitude + " , Lat: " + lastLocation.latitude + "\n" + getCityName(lastLocation.latitude,lastLocation.longitude)
        }
    }

    private fun requestLocationPermisson() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_CODE ->  {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Debug:", "You have the Permission")
                }
            }
        }

    }

    private fun imagePickCamera(){

        ImagePicker.with(this)
            .cameraOnly()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == ImagePicker.REQUEST_CODE){
                imageUri  = data?.data
                binding?.ivAddWarung?.setImageURI(imageUri)
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        //this function will return a boolean
        //true: if we have permission
        //false if not
        if (
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun isLocationEnabled(context : Context): Boolean {
        //this function will return to us the state of the location service
        //if the gps or the network provider is enabled then it will return true otherwise it will return false
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}