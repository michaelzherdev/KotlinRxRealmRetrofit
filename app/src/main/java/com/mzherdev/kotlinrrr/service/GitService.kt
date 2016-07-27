package com.mzherdev.kotlinrrr.service

import com.mzherdev.kotlinrrr.model.GithubData
import com.mzherdev.kotlinrrr.model.Repo
import retrofit.http.GET
import retrofit.http.Path
import rx.Observable

/**
 * Created by macuser on 25.07.16.
 */
interface GitService {

    @GET("users/{username}")
    fun getGitUser(@Path("username") username: String): Observable<GithubData>

    @GET("users/{username}/repos")
    fun getGitUserRepos(@Path("username") username: String): Observable<List<Repo>>
}