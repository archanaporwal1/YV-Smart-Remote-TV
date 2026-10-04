package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.TvDevice

@Database(entities = [TvDevice::class], version = 1, exportSchema = false)
abstract class TvDatabase : RoomDatabase() {
  abstract fun tvDao(): TvDao

  companion object {
    @Volatile
    private var INSTANCE: TvDatabase? = null

    fun getDatabase(context: Context): TvDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          TvDatabase::class.java,
          "tv_remote_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
