package com.mzherdev.kotlinrrr

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by mzherdev on 26.07.16.
 */

class KotlinApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)
    }
}