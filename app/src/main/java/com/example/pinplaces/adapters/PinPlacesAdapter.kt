package com.example.pinplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pinplaces.activities.AddPinPlaceActivity
import com.example.pinplaces.activities.MainActivity
import com.example.pinplaces.database.DatabaseHandler
import com.example.pinplaces.databinding.ItemPinPlaceBinding
import com.example.pinplaces.models.PinPlaceModel


open class PinPlacesAdapter (
    private var list: ArrayList<PinPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemPinPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            holder.binding.ivPlaceImage.setImageURI(Uri.parse(model.image))
            holder.binding.tvTitle.text = model.title
            holder.binding.tvDescription.text = model.description
            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }

    }

    fun notifyEditItem(activity : Activity, position: Int, requestCode: Int){
        val intent = Intent(activity, AddPinPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivityForResult(intent , requestCode)
        notifyItemChanged(position)
    }

    fun removeAt(position: Int , context: Context){
        val dbHandler = DatabaseHandler(context)
        val isDeleted = dbHandler.deletePinPlace(list[position])
        if (isDeleted > 0 ){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){ // we did all this because an adapter can't have it's own onclick listener.
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int , model: PinPlaceModel)
    }

    private class MyViewHolder(val binding: ItemPinPlaceBinding) : RecyclerView.ViewHolder(binding.root)

}
