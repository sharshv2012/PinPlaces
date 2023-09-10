package com.example.pinplaces

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pinplaces.databinding.ActivityAddHappyPlaceBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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
        binding?.tvAddImage?.setOnClickListener(this)
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

        builder.setNeutralButton("cancel"){ dialogInterface , which-> // here which can be replaced by an _ as it isn't used.
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){ dialogInterface , which-> //// here which can be replaced by an _ as it isn't used.
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
            binding?.tvAddImage ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select Photo From Gallery" , "Capture Photo From Camera")
                pictureDialog.setItems(pictureDialogItems){
                    dialog , which ->
                    when(which){
                        0 -> choosePhotoFromGallery()

                        1 -> Toast.makeText(this@AddHappyPlaceActivity ,
                            "Permissions granted" , Toast.LENGTH_LONG).show()

                    }
                }
                pictureDialog.show()
            }
        }
    }
    private fun choosePhotoFromGallery(){
        Dexter.withContext(this@AddHappyPlaceActivity).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE ,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE ,
            android.Manifest.permission.CAMERA,
            //android.Manifest.permission.READ_MEDIA_IMAGES

        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()){
                    Toast.makeText(this@AddHappyPlaceActivity ,
                        "Permissions granted" , Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions : MutableList<PermissionRequest>?,
                token : PermissionToken?)
            {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("" +
        "It looks like you have turned off permissions required "
        + "for this feature. It can be enabled under the " +
        "Applications Settings")
            .setPositiveButton("GO TO SETTINGS")
            {_ , _ ->  // _ , _ is used because the parameter variables aren't used below.
                try { // in these cases always use try catch blocks.
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package" ,packageName , null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e : ActivityNotFoundException){
                    e.printStackTrace()
                }

            }
            .setNegativeButton("Cancel"){dialog , which -> // here which can be replaced by an _ as it isn't used.
                dialog.dismiss()
            }
        .setCancelable(false) // will not allow user to cancel by clicking on the remaining area
        .show()
    }
    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat , Locale.getDefault())
        binding?.etDate!!.setText(sdf.format(cal.time).toString())
    }
}