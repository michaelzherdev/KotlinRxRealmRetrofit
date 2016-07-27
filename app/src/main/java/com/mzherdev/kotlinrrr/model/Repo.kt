package com.mzherdev.kotlinrrr.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by mzherdev on 26.07.16.
 */
@RealmClass
open class Repo : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    open var id: Int = 0

    @SerializedName("html_url")
    @Expose
    open var link:String? = null

    @SerializedName("name")
    @Expose
    open var name: String? = null

    override fun toString(): String{
        return "Repo(id=$id, name=$name, link=$link )"
    }

}