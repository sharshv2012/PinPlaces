package com.example.pinplaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pinplaces.databinding.ActivityAddHappyPlaceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddHappyPlaceActivity : AppCompatActivity() {
    var binding:ActivityAddHappyPlaceBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarPinPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarPinPlace?.setNavigationOnClickListener {

        }
    }

    override fun onBackPressed() {
        //showing dialog and then closing the application..
        showDialog()
    }

    private fun showDialog(){
        MaterialAlertDialogBuilder(this).apply {
            setTitle("are you sure?")
            setMessage("want to close the application ?")
            setPositiveButton("Yes") { _, _ -> finish() }
            setNegativeButton("No", null)
            show()
        }
    }
}