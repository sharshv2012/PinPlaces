package com.example.pinplaces.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.pinplaces.adapters.PinPlacesAdapter
import com.example.pinplaces.database.DatabaseHandler
import com.example.pinplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.pinplaces.models.PinPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddPinPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private var binding:ActivityAddHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: OnDateSetListener
    private lateinit var imageCaptureLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude : Double = 0.0
    private var mLongitude : Double = 0.0

    private var mPinPlaceDetails : PinPlaceModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarPinPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarPinPlace?.setNavigationOnClickListener {
            alertDialogFunction()
        }

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mPinPlaceDetails = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }


        dateSetListener = OnDateSetListener {
                view, year, month, dayOfMonth ->

            cal.set(Calendar.YEAR , year)
            cal.set(Calendar.MONTH , month)
            cal.set(Calendar.DAY_OF_MONTH , dayOfMonth)
            updateDateInView()
        }
        updateDateInView() // To automatically update the date
        imageCaptureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val thumbNail: Bitmap = result.data?.extras?.get("data")as Bitmap
                saveImageToInternalStorage = saveImageToInternalStorage(thumbNail)
                Log.e("SavedImage" , "path :: $saveImageToInternalStorage")
                binding?.ivPlaceImage?.setImageBitmap(thumbNail)
            }
        }

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                try {
                    val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver ,selectedImageUri)
                    saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                    Log.e("SavedImage" , "path :: $saveImageToInternalStorage")
                    binding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
                }catch (e : IOException){
                    e.printStackTrace()
                    Toast.makeText(this , "Something Went Wrong!" , Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (mPinPlaceDetails != null){
            supportActionBar?.title = "Edit Your PinnedPlace"
            binding?.etTitle?.setText(mPinPlaceDetails!!.title)
            binding?.etDescription?.setText(mPinPlaceDetails!!.description)
            binding?.etDate?.setText(mPinPlaceDetails!!.date)
            binding?.etLocation?.setText(mPinPlaceDetails!!.location)
            mLatitude = mPinPlaceDetails!!.latitude
            mLongitude = mPinPlaceDetails!!.longitude

            saveImageToInternalStorage = Uri.parse(mPinPlaceDetails!!.image)

            binding?.ivPlaceImage?.setImageURI(saveImageToInternalStorage)
            binding?.btnSave?.text = "UPDATE"


        }

        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
    }


    override fun onBackPressed() {
        alertDialogFunction()
    }
    private fun alertDialogFunction(){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Do you want to exit?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("yes") {dialogInterface , which ->
            finish()
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
                DatePickerDialog(this@AddPinPlaceActivity ,
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

                        1 -> takePhotoFromCamera()

                    }
                }
                pictureDialog.show()
            }
            binding?.btnSave ->{
                when{
                    binding?.etTitle?.text.isNullOrEmpty() ->{
                        Toast.makeText(this@AddPinPlaceActivity , "Please Enter Title" ,Toast.LENGTH_LONG).show()
                    }
                    binding?.etDescription?.text.isNullOrEmpty() ->{
                        Toast.makeText(this@AddPinPlaceActivity , "Please Enter Description" ,Toast.LENGTH_LONG).show()
                    }
                    binding?.etLocation?.text.isNullOrEmpty() ->{
                        Toast.makeText(this@AddPinPlaceActivity , "Please Enter Location" ,Toast.LENGTH_LONG).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this@AddPinPlaceActivity , "Please Select or Capture An Image" ,Toast.LENGTH_LONG).show()
                    }
                    else ->{
                        val pinPlaceModel = PinPlaceModel(
                            if (mPinPlaceDetails == null) 0 else mPinPlaceDetails!!.id ,                          binding?.etTitle?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding?.etDescription?.text.toString(),
                            binding?.etDate?.text.toString(),
                            binding?.etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val dbHandler = DatabaseHandler(this)
                        if(mPinPlaceDetails == null){
                            val addPinPlace = dbHandler.addPinPlace(pinPlaceModel)
                            Toast.makeText(this , "Details Are Inserted Successfully" ,Toast.LENGTH_LONG).show()
                            if(addPinPlace > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }else{
                            val updatePinPlace = dbHandler.updatePinPlace(pinPlaceModel)
                            Toast.makeText(this , "Details Updated Successfully" ,Toast.LENGTH_LONG).show()
                            if(updatePinPlace > 0){
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun takePhotoFromCamera(){


        Dexter.withContext(this@AddPinPlaceActivity).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE ,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE ,
            android.Manifest.permission.CAMERA,
            //android.Manifest.permission.READ_MEDIA_IMAGES

        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    imageCaptureLauncher.launch(galleryIntent)
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
    private fun choosePhotoFromGallery(){

        Dexter.withContext(this@AddPinPlaceActivity).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE ,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE ,
            android.Manifest.permission.CAMERA,
            //android.Manifest.permission.READ_MEDIA_IMAGES

        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    selectImageLauncher.launch(galleryIntent)

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

    private fun saveImageToInternalStorage (bitmap: Bitmap):Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("HappyPlacesImage" , Context.MODE_PRIVATE)
        file = File(file , "${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , stream)
            stream.flush()
            stream.close()
        }catch (e : IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
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