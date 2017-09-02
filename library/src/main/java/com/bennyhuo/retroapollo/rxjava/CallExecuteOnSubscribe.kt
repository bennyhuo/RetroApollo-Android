package com.bennyhuo.retroapollo.rxjava

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import rx.Observable.OnSubscribe
import rx.Subscriber


/**
 * Created by benny on 8/6/17.
 */
class CallExecuteOnSubscribe<T>(private val apolloCall: ApolloCall<T>): OnSubscribe<Response<T>> {
    override fun call(t: Subscriber<in Response<T>>) {
        val callArbiter = CallArbiter(apolloCall, t)
        t.add(callArbiter)
        t.setProducer(callArbiter)
    }
}