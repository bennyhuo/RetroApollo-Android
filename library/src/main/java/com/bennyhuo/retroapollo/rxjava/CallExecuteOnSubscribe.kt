/*
 * Copyright 2017 Bennyhuo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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