package com.example.pinplaces.models

data class PinPlaceModel (
    val id : Int,
    val title : String,
    val image : String,
    val description : String,
    val date : String,
    val location : String,
    val latitude : Double,
    val longitude : Double
)
