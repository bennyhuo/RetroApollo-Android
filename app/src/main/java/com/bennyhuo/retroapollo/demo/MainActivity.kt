package com.bennyhuo.retroapollo.demo

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.setOnClickListener {
            UserInfo.username = usernameView.text.toString()
            UserInfo.passwd = passwdView.text.toString()
//            graphQLService.repositoryStatisticsQuery("enbandari", "Kotlin-Tutorials").subscribe {
//                hello.text = it.toString()
//            }

            launch(UI) {
                val result = graphQLService.userQueryCoroutine("enbandari").await()
                hello.text = result.data().toString()
            }
        }
    }
}
