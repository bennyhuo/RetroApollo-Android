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