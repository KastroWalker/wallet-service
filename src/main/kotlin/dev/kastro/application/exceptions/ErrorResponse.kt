package dev.kastro.application.exceptions

data class ErrorResponse(
    val errorCode: String,
    val message: String
)
