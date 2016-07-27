package com.mzherdev.kotlinrrr.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by mzherdev on 25.07.16.
 */

@RealmClass
open class GithubData : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    open var id: Int = 0

    @SerializedName("avatar_url")
    @Expose
    open var avatar:String? = null

    @SerializedName("name")
    @Expose
    open var name: String? = null

    @SerializedName("location")
    @Expose
    open var location: String? = null

    @SerializedName("public_repos")
    @Expose
    open var numOfPublicRepos: Int? = 0

    @SerializedName("created_at")
    @Expose
    open var dateCreated: String? = null

    @SerializedName("updated_at")
    @Expose
    open var dateUpdated: String? = null


    override fun toString(): String{
        return "GithubData(id=$id, avatar=$avatar, name=$name, location=$location, numOfPublicRepos=$numOfPublicRepos, dateCreated=$dateCreated)"
    }


}