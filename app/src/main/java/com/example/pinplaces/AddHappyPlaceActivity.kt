package com.example.pinplaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pinplaces.databinding.ActivityAddHappyPlaceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddHappyPlaceActivity : AppCompatActivity() {
    private var binding:ActivityAddHappyPlaceBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarPinPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarPinPlace?.setNavigationOnClickListener {
            alertDialogFunction()
        }
    }

   // no need of onBackPressed()

    private fun alertDialogFunction(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("this is an alert dialog. which is used to show alert in our app.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("yes") {dialogInterface ,
                                          which -> finish()
            dialogInterface.dismiss()
        }

        builder.setNeutralButton("cancel"){ dialogInterface , which->
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){ dialogInterface , which->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false) // will not allow user to cancel by clicking on the remaining area
        alertDialog.show()

    }
}