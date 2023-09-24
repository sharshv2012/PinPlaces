package com.example.pinplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pinplaces.adapters.PinPlacesAdapter
import com.example.pinplaces.database.DatabaseHandler
import com.example.pinplaces.databinding.ActivityMainBinding
import com.example.pinplaces.models.PinPlaceModel
import com.example.pinplaces.utils.SwipeToEditCallback

class MainActivity : AppCompatActivity() {
    var binding : ActivityMainBinding? = null
    private lateinit var resultLauncher : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                getPinPlaceListFromLocalDB()
            }else{
                Log.e("activity" , "Cancelled or back Pressed")
            }
        }
        binding?.fabHappyPlace?.setOnClickListener {
            val intent = Intent(this , AddPinPlaceActivity::class.java)
            resultLauncher.launch(intent)
        }

        getPinPlaceListFromLocalDB()

    }
    private fun setupHappyPlacesRecyclerView(pinPlacesList: ArrayList<PinPlaceModel>){
        binding?.rvPinPlacesList?.layoutManager = LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false)
        binding?.rvPinPlacesList?.setHasFixedSize(true)
        val placesAdapter = PinPlacesAdapter(pinPlacesList)
        binding?.rvPinPlacesList?.adapter = placesAdapter
        placesAdapter.setOnClickListener(object : PinPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: PinPlaceModel) {
                val intent = Intent(this@MainActivity , PinPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)

                startActivity(intent)
            }
        })

        val editSwipeHandler = object  : SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding?.rvPinPlacesList?.adapter as PinPlacesAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.rvPinPlacesList)
    }
    private fun getPinPlaceListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getPinPlaceList : ArrayList<PinPlaceModel> = dbHandler.getPinPlacesList()

        if(getPinPlaceList.size > 0){
            for (i in getPinPlaceList){
                binding?.rvPinPlacesList?.visibility = View.VISIBLE
                binding?.tvNoRcrdsAvailble?.visibility = View.GONE
                setupHappyPlacesRecyclerView(getPinPlaceList)
            }
        }else{
            binding?.rvPinPlacesList?.visibility = View.GONE
            binding?.tvNoRcrdsAvailble?.visibility = View.VISIBLE
        }
    }
    companion object{
        var EXTRA_PLACE_DETAILS = "extra_place_details"
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }
}