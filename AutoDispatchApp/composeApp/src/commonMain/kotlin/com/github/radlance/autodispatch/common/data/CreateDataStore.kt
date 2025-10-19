package com.github.radlance.autodispatch.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import okio.Path.Companion.toPath

private val lock = SynchronizedObject()
private lateinit var dataStore: DataStore<Preferences>

fun createDataStore(producePath: (dataStoreFileName: String) -> String): DataStore<Preferences> {
    return synchronized(lock) {
        if (::dataStore.isInitialized) {
            dataStore
        } else {
            PreferenceDataStoreFactory.createWithPath(
                produceFile = { producePath(dataStoreFileName).toPath() }
            ).also { dataStore = it }
        }
    }
}

private const val dataStoreFileName = "settings.preferences_pb"