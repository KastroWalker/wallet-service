package dev.kastro.application.exceptions

import io.micronaut.http.HttpStatus

open class ApiException(
    message: String,
    val status: HttpStatus,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)

class RepositoryException(message: String, throwable: Throwable) :
    ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR, throwable)

class SqsException(message: String, throwable: Throwable) :
    ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR, throwable)

class BadRequestException(message: String, throwable: Throwable? = null) :
    ApiException(message, HttpStatus.BAD_REQUEST, throwable)

class ResourceNotFoundException(message: String, throwable: Throwable? = null) :
    ApiException(message, HttpStatus.NOT_FOUND, throwable)
