package com.github.radlance.autodispatch.common.data

import com.github.radlance.autodispatch.auth.data.SignInUserDto
import com.github.radlance.autodispatch.auth.data.TokenDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

interface ApiService {
    suspend fun signIn(loginUser: SignInUserDto): String
}

internal class KtorApiService(private val httpClient: HttpClient) : ApiService {

    override suspend fun signIn(loginUser: SignInUserDto): String {
        return httpClient.post("auth/login") {
            setBody(loginUser)
        }.body<TokenDto>().accessToken
    }
}