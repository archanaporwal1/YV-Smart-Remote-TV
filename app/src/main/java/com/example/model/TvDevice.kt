package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tv_devices")
data class TvDevice(
  @PrimaryKey
  val id: String,
  val name: String,
  val ipAddress: String,
  val port: Int = 6466,
  val macAddress: String = "",
  val brand: String = "Android TV",
  val model: String = "Smart TV OS",
  val isOnline: Boolean = true,
  val isFavorite: Boolean = false,
  val lastConnectedTime: Long = System.currentTimeMillis()
)
