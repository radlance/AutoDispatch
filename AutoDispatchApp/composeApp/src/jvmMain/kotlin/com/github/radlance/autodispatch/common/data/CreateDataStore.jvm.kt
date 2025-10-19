package com.github.radlance.autodispatch.common.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createDataStore(): DataStore<Preferences> {
    return createDataStore(
        producePath = { dataStoreFileName ->
            val file = File(System.getProperty("java.io.tmpdir"), dataStoreFileName)
            file.absolutePath
        }
    )
}