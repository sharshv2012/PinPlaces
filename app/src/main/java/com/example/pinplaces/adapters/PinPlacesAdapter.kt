package com.example.pinplaces.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pinplaces.databinding.ItemPinPlaceBinding
import com.example.pinplaces.models.PinPlaceModel


open class PinPlacesAdapter (
    private var list: ArrayList<PinPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){




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
        }

    }

    private class MyViewHolder(val binding: ItemPinPlaceBinding) : RecyclerView.ViewHolder(binding.root)

}
