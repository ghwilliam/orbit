/*
 Copyright (C) 2018 Electronic Arts Inc.  All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1.  Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2.  Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
     its contributors may be used to endorse or promote products derived
     from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cloud.orbit.core.maybe

import java.util.Optional

sealed class Maybe<out T> {
    abstract val isEmpty: Boolean
    val isPresent get() = !isEmpty

    abstract fun get(): T

    fun toOptional(): Optional<out T> = if(isEmpty) {
        Optional.empty()
    } else {
        Optional.of(get())
    }

    fun orNull(): T? = if(isEmpty) {
        null
    } else {
        get()
    }

    infix fun <V> flatMap(body: (T) -> Maybe<V>): Maybe<V> = if(isEmpty) {
        None
    } else {
        body(get())
    }

    infix fun <V> map(body: (T) -> V): Maybe<V> = if(isEmpty) {
        None
    } else {
        Some(body(get()))
    }

    companion object {
        @JvmStatic
        fun empty() = None

        @JvmStatic
        fun <V> of(value: V) = Some(value)

        @JvmStatic
        fun <V> fromOptional(optional: Optional<V>) = if(optional.isPresent) {
            of(optional.get())
        } else {
            empty()
        }
    }
}


object None: Maybe<Nothing>() {
    override val isEmpty = true
    override fun get() = throw IllegalAccessException("Trying to use None.get")
}

data class Some<T>(private val value: T): Maybe<T>() {
    override val isEmpty = false
    override fun get() = value
}