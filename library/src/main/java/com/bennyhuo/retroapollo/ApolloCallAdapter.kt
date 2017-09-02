package com.bennyhuo.retroapollo

import com.apollographql.apollo.ApolloCall
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by benny on 8/5/17.
 */
class ApolloCallAdapterFactory: CallAdapter.Factory(){
    override fun get(returnType: Type): CallAdapter<*, *>? {
        if(getRawType(returnType) == ApolloCall::class.java){
            val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
            if(responseType is ParameterizedType){
                return null
            }
            return object:CallAdapter<Any, Any>{
                override fun responseType(): Type = responseType
                override fun adapt(call: ApolloCall<Any>) = call
            }
        }
        return null
    }
}