package com.example.pinplaces

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pinplaces.databinding.ActivityAddHappyPlaceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private var binding:ActivityAddHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarPinPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarPinPlace?.setNavigationOnClickListener {
            alertDialogFunction()
        }

        dateSetListener = OnDateSetListener {
                view, year, month, dayOfMonth ->

            cal.set(Calendar.YEAR , year)
            cal.set(Calendar.MONTH , month)
            cal.set(Calendar.DAY_OF_MONTH , dayOfMonth)
            updateDateInView()
        }
        binding?.etDate?.setOnClickListener(this)
    }


    override fun onBackPressed() {
        alertDialogFunction()
    }
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

    override fun onClick(v: View?) {
        when(v!!){
            binding?.etDate ->{
                DatePickerDialog(this@AddHappyPlaceActivity ,
                    dateSetListener ,
                    cal.get(Calendar.YEAR) ,
                    cal.get(Calendar.MONTH) ,
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()

            }
        }
    }

    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat , Locale.getDefault())
        binding?.etDate!!.setText(sdf.format(cal.time).toString())
    }
}