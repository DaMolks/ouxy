package com.damolks.ouxy

import android.app.Application
import com.damolks.ouxy.data.database.OuxyDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OuxyApplication : Application() {
    val database: OuxyDatabase by lazy { OuxyDatabase.getDatabase(this) }
}