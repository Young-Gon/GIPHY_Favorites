package com.gondev.giphyfavorites.model.network

import java.io.PrintWriter
import java.io.StringWriter

sealed class State<out T>(
	val data: T?
) {
	class Loading<T>(data: T?) : State<T>(data)

	class Success<T>(data: T?) : State<T>(data)

	class Error<T>(val throwable: Throwable, data: T?) : State<T>(data){
		fun getStackTrace(): String {
			val stringWriter = StringWriter()
			throwable.printStackTrace(PrintWriter(stringWriter))
			return stringWriter.toString()
		}
	}

	companion object{
		fun <T> loading(data: T?) = Loading(data)

		fun <T> success(data: T?) = Success(data)

		fun <T> error(throwable: Throwable, data: T?) =
			Error(throwable, data)
	}
}
