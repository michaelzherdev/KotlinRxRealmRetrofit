package com.mzherdev.kotlinrrr

import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.mzherdev.kotlinrrr.databinding.ActivityMainBinding
import com.mzherdev.kotlinrrr.model.GithubData
import com.mzherdev.kotlinrrr.model.Repo
import com.mzherdev.kotlinrrr.service.GitService
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.RealmResults
import retrofit.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

const val URL = "https://api.github.com/"
const val USER_NAME = "michaelzherdev"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bind = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val gson = GsonBuilder().setExclusionStrategies(object : ExclusionStrategy {
            override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                return false
            }

            override fun shouldSkipField(f: FieldAttributes?): Boolean {
                return f?.declaredClass == RealmObject::class.java
            }
        }).create()

        val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(URL)
                .build()

        val gitService: GitService = retrofit.create(GitService::class.java)

        val realm = Realm.getDefaultInstance()


        val dbUser: GithubData? = RealmQuery.createQuery(realm, GithubData::class.java).findFirst()

        if (dbUser == null) {
            gitService.getGitUser(USER_NAME)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { gitUser ->
                                realm.beginTransaction()
                                realm.copyToRealmOrUpdate(gitUser)
                                realm.commitTransaction()
                                initView(bind, gitUser)
                            }, {
                        error ->
                        Log.e("MainActivity", error.message)
                    }
                    )
        } else {
            initView(bind, dbUser)
        }


        var userRepos = realm.where(Repo::class.java).findAll()
        val adapter = RecyclerAdapter(userRepos)

        gitService.getGitUserRepos(USER_NAME)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { repos ->
                            repos.forEach { repo ->
                                        realm.beginTransaction()
                                        realm.copyToRealmOrUpdate(repo)
                                        realm.commitTransaction()
                                if (userRepos == null || userRepos.size == 0) {
                                    adapter.repos = repos
                                    adapter.notifyDataSetChanged()
                                }
                            }


                        }, {
                    error ->
                    Log.e("MainActivity", error.message)
                }
                )

        val recyclerView = bind.recyclerView
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.setAdapter(adapter)
    }

    private fun initView(binding: ActivityMainBinding, dbUser: GithubData?) {

        Picasso.with(this).load(dbUser?.avatar).into(binding.userImage)

        binding.userName.text = dbUser?.name ?: USER_NAME
        binding.userLocation.text = dbUser?.location
        if (dbUser?.dateCreated != null)
            binding.dateCreated.text = "Registered: " + formatStringDate(dbUser?.dateCreated)
        if (dbUser?.dateUpdated != null)
            binding.dateUpdated.text = "Last Update: " + formatStringDate(dbUser?.dateUpdated)
        if (dbUser?.numOfPublicRepos != null)
            binding.publicRepos.text = "Number of repos: " + dbUser?.numOfPublicRepos
    }

    private fun formatStringDate(dateCreated: String?): String {
        var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        var output = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val d = format.parse(dateCreated)
        return output.format(d)
    }

    class RecyclerAdapter(repos: List<Repo>?) : RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>() {

        var repos: List<Repo>? = repos

        override fun onBindViewHolder(holder: ItemViewHolder?, position: Int) {
            if (holder != null) {
                holder.repoName.text = repos?.get(position)?.name
                holder.repoLink.setOnClickListener {
                    val url = repos?.get(position)?.link
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    it.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerAdapter.ItemViewHolder? =
                RecyclerAdapter.ItemViewHolder(LayoutInflater.from(parent?.context)
                        .inflate(R.layout.adapter_recycler, parent, false))

        override fun getItemCount(): Int = repos?.size ?: 0

        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val repoName = itemView.findViewById(R.id.repoName) as TextView
            val repoLink = itemView.findViewById(R.id.repoLink) as Button
        }
    }
}
