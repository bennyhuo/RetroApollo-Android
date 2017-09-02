package com.bennyhuo.retroapollo.demo

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response

/**
 * Created by benny on 6/23/17.
 */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Chain): Response {
        val userCredentials = UserInfo.username + ":" + UserInfo.passwd
        val auth = "Basic " + String(Base64.encode(userCredentials.toByteArray(), Base64.DEFAULT))
                .trim()
        val original = chain.request()
        return chain.proceed(original.newBuilder()
                .header("Authorization", auth)
                .build())
    }

}