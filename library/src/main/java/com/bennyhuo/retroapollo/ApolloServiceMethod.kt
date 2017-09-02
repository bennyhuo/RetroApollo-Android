package com.bennyhuo.retroapollo

import com.bennyhuo.retroapollo.annotations.GraphQLQuery
import com.bennyhuo.retroapollo.utils.Utils
import com.bennyhuo.retroapollo.utils.error
import com.apollographql.apollo.api.Query
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType

/**
 * Created by benny on 8/5/17.
 */
class ApolloServiceMethod<T: Any>(val retroApollo: RetroApollo, val method: Method, val buildBuilderMethod: Method, val buildQueryMethod: Method, val fieldSetters: List<Method>, val callAdapter: CallAdapter<Any, T>) {

    class Builder(val retroApollo: RetroApollo, val method: Method) {

        private val callAdapter: CallAdapter<Any, Any>

        private val buildQueryMethod: Method

        private val fieldSetters = ArrayList<Method>()

        private val buildBuilderMethod: Method

        init {
            val returnType = method.genericReturnType
            if (Utils.hasUnresolvableType(returnType)) {
                throw method.error(
                        "Method return type must not include a type variable or wildcard: %s", returnType)
            }
            if (returnType === Void.TYPE) {
                throw method.error("Service methods cannot return void.")
            }

            if (returnType !is ParameterizedType) {
                val name = (returnType as Class<*>).simpleName
                throw IllegalStateException(name + " return type must be parameterized"
                        + " as " + name + "<Foo> or " + name + "<out Foo>")
            }

            callAdapter = retroApollo.getCallAdapter(returnType) ?: throw IllegalStateException("$returnType is not supported.")

            // XXX.Data.class
            val dataType = callAdapter.responseType() as Class<*>

            //XXX.class
            buildBuilderMethod = dataType.enclosingClass.getDeclaredMethod("builder")
            val builderClass = dataType.enclosingClass.declaredClasses.filter { it.simpleName == "Builder" }.first()

            method.parameterAnnotations.zip(method.parameterTypes).mapTo(fieldSetters) {
                paramPair ->
                val annotation = paramPair.first.filter { it is GraphQLQuery }.first() as GraphQLQuery
                builderClass.getDeclaredMethod(annotation.value, paramPair.second)
            }

            buildQueryMethod = builderClass.getDeclaredMethod("build")
        }

        fun build() = ApolloServiceMethod<Any>(retroApollo, method, buildBuilderMethod, buildQueryMethod, fieldSetters, callAdapter)
    }

    operator fun invoke(args: Array<Any>?): T{
        val builder = buildBuilderMethod(null)
        args?.let {
            fieldSetters.zip(it).forEach {
                it.first.invoke(builder, it.second)
            }
        }

        return callAdapter.adapt(retroApollo.apolloClient.query(buildQueryMethod(builder) as Query<*, Any, *>))
    }

}