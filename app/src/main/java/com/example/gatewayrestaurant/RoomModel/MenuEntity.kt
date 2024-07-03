package com.example.gatewayrestaurant.RoomModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu")
data class MenuEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameLowerCase: String?,
    val nextAvailableFrom: String?,
    val nextAvailableTo: String?,
    val availableFrom: String?,
    val availableTo: String?,
    val price: String?,
    val image: String,
    val isPopular: String?,
    val offerPrice: String?
)
