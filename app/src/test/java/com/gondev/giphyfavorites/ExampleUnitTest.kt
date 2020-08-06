package com.gondev.giphyfavorites

import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val gifList=listOf(1, 2, 3, 4, 5, 6)
        val netResult=listOf(0, 1, 2, 3)

        val result = netResult.filter { result ->
            gifList.none { it == result }
        }

        Assert.assertArrayEquals(result.toIntArray(), listOf(0).toIntArray())
    }
}