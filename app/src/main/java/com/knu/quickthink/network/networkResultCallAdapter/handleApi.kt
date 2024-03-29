package com.knu.quickthink.network.networkResultCallAdapter

import com.knu.quickthink.model.NetworkResult
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

fun <T : Any> handleApi(
    execute: () -> Response<T>
): NetworkResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Timber.tag("network").d("handleApi : response.isSuccessful && body not null")
            NetworkResult.Success(body)
        }else if(response.code() == 200){
            @Suppress("UNCHECKED_CAST")
                NetworkResult.Success("success" as T)
        }
        else{
            Timber.tag("network").d("handleApi : response not Successful or body is null ")
            NetworkResult.Error(code = response.code(), message = response.message())
        }
    } catch (e: HttpException) {
        Timber.tag("network").d("handleApi : Error HttpException")
        NetworkResult.Error(code = e.code(), message = e.message())
    } catch (e: Throwable) {
        Timber.tag("network").d("handleApi : Exception")
        NetworkResult.Exception(e)
    }
}

