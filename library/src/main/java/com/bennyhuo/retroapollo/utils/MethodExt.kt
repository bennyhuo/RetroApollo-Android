package com.bennyhuo.retroapollo.utils

import java.lang.reflect.Method

/**
 * Created by benny on 8/5/17.
 */

fun Method.error(message: String, vararg args: Any): RuntimeException {
    return error(null, message, *args)
}

fun Method.error(cause: Throwable?, message: String, vararg args: Any): RuntimeException {
    var message = message
    message = String.format(message, *args)
    return IllegalArgumentException(message
            + "\n    for method "
            + declaringClass.simpleName
            + ""
            + name, cause)
}

fun Method.parameterError(
        cause: Throwable, p: Int, message: String, vararg args: Any): RuntimeException {
    return error(cause, message + " (parameter #" + (p + 1) + ")", *args)
}

fun Method.parameterError(p: Int, message: String, vararg args: Any): RuntimeException {
    return error(message + " (parameter #" + (p + 1) + ")", *args)
}