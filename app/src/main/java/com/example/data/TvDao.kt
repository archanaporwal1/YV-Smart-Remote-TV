package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.TvDevice
import kotlinx.coroutines.flow.Flow

@Dao
interface TvDao {
  @Query("SELECT * FROM tv_devices ORDER BY isFavorite DESC, lastConnectedTime DESC")
  fun getAllDevices(): Flow<List<TvDevice>>

  @Query("SELECT * FROM tv_devices WHERE id = :id LIMIT 1")
  suspend fun getDeviceById(id: String): TvDevice?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(device: TvDevice)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(devices: List<TvDevice>)

  @Update
  suspend fun update(device: TvDevice)

  @Delete
  suspend fun delete(device: TvDevice)

  @Query("DELETE FROM tv_devices WHERE id = :id")
  suspend fun deleteById(id: String)
}
