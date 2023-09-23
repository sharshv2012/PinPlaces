package com.example.pinplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pinplaces.R
import com.example.pinplaces.databinding.ActivityHappyPlaceDetailBinding
import com.example.pinplaces.models.PinPlaceModel

class PinPlaceDetailActivity : AppCompatActivity() {
    var binding : ActivityHappyPlaceDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        var pinPlaceDetailsModel :PinPlaceModel? = null

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            pinPlaceDetailsModel = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)
        }

        if(pinPlaceDetailsModel != null){
            setSupportActionBar(binding?.toolbarPinPlaceDetail )
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = pinPlaceDetailsModel.title

            binding?.toolbarPinPlaceDetail?.setNavigationOnClickListener {
                onBackPressed()
            }

            binding?.ivPlaceImage?.setImageURI(Uri.parse(pinPlaceDetailsModel.image))
            binding?.tvDescription?.text = pinPlaceDetailsModel.description
            binding?.tvLocation?.text = pinPlaceDetailsModel.location
        }
    }
}