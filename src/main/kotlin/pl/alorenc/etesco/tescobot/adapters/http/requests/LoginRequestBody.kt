package pl.alorenc.etesco.tescobot.adapters.http.requests

import com.fasterxml.jackson.annotation.JsonGetter

data class LoginRequestBody(
    val onSuccessUrl: String,
    val email: String,
    val password: String,
    @get:JsonGetter("_csrf") val csrf: String
)