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

package cloud.orbit.core.tries

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TryTest {
    class TryTestException(): RuntimeException("TryTestException")

    @Test
    fun validateBasicSuccess() {
        val success = Try  { "success"}
        Assertions.assertTrue(success.isSuccess)
        Assertions.assertEquals("success", success.get())
    }

    @Test
    fun validateBasicFailure() {
        val fail = Try  { throw TryTestException() }
        Assertions.assertTrue(fail.isFailure)
        Assertions.assertThrows(TryTestException::class.java, fail::get)
    }

    @Test
    fun getOrElseTest() {
        val success = Try { "get" } getOrElse { "else" }
        Assertions.assertEquals("get", success)

        val fail = Try<String> { throw TryTestException() } getOrElse { "else" }
        Assertions.assertEquals("else", fail)
    }

    @Test
    fun orNullTest() {
        val success = Try { "get" }.orNull()
        Assertions.assertEquals("get", success)

        val fail = Try<String> { throw TryTestException() }.orNull()
        Assertions.assertEquals(null, fail)
    }

    @Test
    fun mapTest() {
        val square = Try { 5 } map { it * it }
        Assertions.assertEquals(25, square.get())

        val failInTry = Try<Int> { throw TryTestException() } map { it * it }
        Assertions.assertTrue(failInTry.isFailure)

        val failInMap = Try { 5 } map { throw TryTestException() }
        Assertions.assertTrue(failInMap.isFailure)
    }

    @Test
    fun flatMapTest() {
        val square = Try { 5 } flatMap { Try { it * it } }
        Assertions.assertEquals(25, square.get())

        val failInTry = Try<Int> { throw TryTestException() } flatMap { Try{ it * it}  }
        Assertions.assertTrue(failInTry.isFailure)

        val failInMap = Try { 5 } flatMap { Try { throw TryTestException() } }
        Assertions.assertTrue(failInMap.isFailure)
    }

    @Test
    fun onSuccessTest() {
        var didTrigger = false
        Try { 5 * 5 } onSuccess { didTrigger = true }
        Assertions.assertTrue(didTrigger)
    }

    @Test
    fun onFailureTest() {
        var didTrigger = false
        Try { throw TryTestException() } onFailure { didTrigger = true }
        Assertions.assertTrue(didTrigger)
    }

    @Test
    fun equalityTest() {
        val firstSuccess = Try { 5 }
        val secondSuccess = Try { 5 }
        val thirdSuccess = Try { 10 }

        Assertions.assertEquals(firstSuccess, firstSuccess)
        Assertions.assertEquals(firstSuccess, secondSuccess)
        Assertions.assertNotEquals(firstSuccess, thirdSuccess)

        val commonException = TryTestException()
        val firstFail = Try { commonException }
        val secondFail = Try { commonException }
        val thirdFail = Try { TryTestException() }

        Assertions.assertEquals(firstFail, firstFail)
        Assertions.assertEquals(firstFail, secondFail)
        Assertions.assertNotEquals(firstFail, thirdFail)

        Assertions.assertNotEquals(firstSuccess, firstFail)
    }

    @Test
    fun maybeConversionTest() {
        val maybeSuccess = Try { "trySuccess" }.toMaybe()
        Assertions.assertTrue(maybeSuccess.isPresent)
        Assertions.assertEquals("trySuccess", maybeSuccess.get())

        val maybeFail = Try { throw TryTestException() }.toMaybe()
        Assertions.assertTrue(maybeFail.isEmpty)
    }

    @Test
    fun optionalConversionTest() {
        val optionalSuccess = Try { "trySuccess" }.toOptional()
        Assertions.assertTrue(optionalSuccess.isPresent)
        Assertions.assertEquals("trySuccess", optionalSuccess.get())

        val optionalFailure = Try { throw TryTestException() }.toOptional()
        Assertions.assertFalse(optionalFailure.isPresent)
    }
}