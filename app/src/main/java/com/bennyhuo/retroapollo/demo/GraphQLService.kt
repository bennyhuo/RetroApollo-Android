package com.bennyhuo.retroapollo.demo

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.bennyhuo.retroapollo.RetroApollo
import com.bennyhuo.retroapollo.annotations.GraphQLQuery
import com.bennyhuo.retroapollo.coroutine.KotlinCoroutineCallAdapterFactory
import com.bennyhuo.retroapollo.demo.RepositoryStatisticsQuery.Data
import com.bennyhuo.retroapollo.rxjava.RxJavaCallAdapterFactory
import kotlinx.coroutines.experimental.Deferred
import okhttp3.OkHttpClient
import rx.Observable
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


/**
 * Created by benny on 7/31/17.
 */
interface GraphQLService {
    fun repositoryStatisticsQuery(@GraphQLQuery("owner") owner: String, @GraphQLQuery("repo") repo: String): Observable<Data>
    fun repositoryIssuesQuery(@GraphQLQuery("owner") owner: String, @GraphQLQuery("repo") repo: String): ApolloCall<RepositoryIssuesQuery.Data>
    fun userQuery(@GraphQLQuery("login") login: String): Single<Response<UserQuery.Data>>
    fun userQueryCoroutine(@GraphQLQuery("login") login: String): Deferred<Response<UserQuery.Data>>
}

val apolloClient by lazy {

//Build the Apollo Client
    ApolloClient.builder()
            .okHttpClient(OkHttpClient.Builder().addInterceptor(AuthInterceptor()).build())
            .serverUrl("https://api.github.com/graphql")
            .build()
}

val graphQLService by lazy {
    RetroApollo.Builder()
            .apolloClient(apolloClient)
            .addCallAdapterFactory(RxJavaCallAdapterFactory()
                    .observableScheduler(AndroidSchedulers.mainThread())
                    .subscribeScheduler(Schedulers.io()))
            .addCallAdapterFactory(KotlinCoroutineCallAdapterFactory())
            .build()
            .createGraphQLService(GraphQLService::class)
}