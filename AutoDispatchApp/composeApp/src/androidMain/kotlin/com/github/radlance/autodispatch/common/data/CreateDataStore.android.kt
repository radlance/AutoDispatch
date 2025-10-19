package com.github.radlance.autodispatch.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(context: Context): DataStore<Preferences> {
    return createDataStore { dataStoreFileName ->
        context.filesDir.resolve(dataStoreFileName).absolutePath
    }
}