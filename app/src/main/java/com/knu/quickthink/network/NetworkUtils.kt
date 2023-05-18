package com.knu.quickthink.network

class RetrofitFailureStateException(error: String ?, val code: Int) : Exception(error)