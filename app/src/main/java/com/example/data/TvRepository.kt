package com.example.data

import com.example.model.TvDevice
import kotlinx.coroutines.flow.Flow

class TvRepository(private val tvDao: TvDao) {
  val savedDevices: Flow<List<TvDevice>> = tvDao.getAllDevices()

  suspend fun saveDevice(device: TvDevice) {
    tvDao.insertOrUpdate(device)
  }

  suspend fun saveDevices(devices: List<TvDevice>) {
    tvDao.insertAll(devices)
  }

  suspend fun removeDevice(device: TvDevice) {
    tvDao.delete(device)
  }

  suspend fun removeDeviceById(id: String) {
    tvDao.deleteById(id)
  }

  suspend fun getDevice(id: String): TvDevice? {
    return tvDao.getDeviceById(id)
  }
}
