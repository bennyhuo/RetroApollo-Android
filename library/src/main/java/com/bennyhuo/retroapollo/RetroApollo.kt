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

package com.bennyhuo.retroapollo

import com.apollographql.apollo.ApolloClient
import com.bennyhuo.retroapollo.CallAdapter.Factory
import com.bennyhuo.retroapollo.utils.Utils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Created by benny on 8/4/17.
 */
class RetroApollo private constructor(val apolloClient: ApolloClient, val callAdapterFactories: List<CallAdapter.Factory>) {

    class Builder {
        private var apolloClient: ApolloClient? = null

        fun apolloClient(apolloClient: ApolloClient): Builder {
            this.apolloClient = apolloClient
            return this
        }

        private val callAdapterFactories = arrayListOf<Factory> (ApolloCallAdapterFactory())

        fun addCallAdapterFactory(callAdapterFactory: CallAdapter.Factory): Builder {
            callAdapterFactories.add(callAdapterFactory)
            return this
        }

        fun build() = apolloClient?.let {
            RetroApollo(it, callAdapterFactories)
        } ?: throw IllegalStateException("ApolloClient cannot be null.")
    }

    private val serviceMethodCache = ConcurrentHashMap<Method, ApolloServiceMethod<*>>()

    fun <T : Any> createGraphQLService(serviceClass: KClass<T>): T {
        Utils.validateServiceInterface<T>(serviceClass.java)
        return Proxy.newProxyInstance(serviceClass.java.classLoader, arrayOf<Class<T>>(serviceClass.java),
                object : InvocationHandler {
                    @Throws(Throwable::class)
                    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any {
                        if (method.declaringClass == Any::class.java || method.declaringClass == Object::class.java) {
                            return method.invoke(this, args)
                        }

                        val serviceMethod = loadServiceMethod(method)
                        return serviceMethod(args)
                    }
                }) as T
    }

    fun loadServiceMethod(method: Method): ApolloServiceMethod<*> {
        var serviceMethod = serviceMethodCache[method]
        if (serviceMethod == null) {
            synchronized(serviceMethodCache) {
                serviceMethod = serviceMethodCache[method] ?: ApolloServiceMethod.Builder(this, method).build()
                        .also { serviceMethodCache[method] = it }
            }
        }
        return serviceMethod!!
    }

    fun getCallAdapter(type: Type): CallAdapter<Any, Any>? {
        for (callAdapterFactory in callAdapterFactories) {
            val callAdapter = callAdapterFactory.get(type)
            return callAdapter as? CallAdapter<Any, Any> ?: continue
        }
        return null
    }
}