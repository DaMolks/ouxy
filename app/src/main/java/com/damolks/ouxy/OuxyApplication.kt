package com.damolks.ouxy

import android.app.Application
import com.damolks.ouxy.data.database.OuxyDatabase

class OuxyApplication : Application() {
    val database: OuxyDatabase by lazy { OuxyDatabase.getDatabase(this) }
}