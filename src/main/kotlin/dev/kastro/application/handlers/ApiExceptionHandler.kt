package dev.kastro.application.handlers

import dev.kastro.application.exceptions.ApiException
import dev.kastro.application.exceptions.ErrorResponse
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton

@Produces
@Singleton
@Requires(classes = [ApiException::class, ExceptionHandler::class])
class ApiExceptionHandler : ExceptionHandler<ApiException, HttpResponse<ErrorResponse>> {
    override fun handle(request: HttpRequest<*>, exception: ApiException): HttpResponse<ErrorResponse> {
//        logger.error(t = exception) { "${exception.message} ${exception.cause?.message}" }

        val errorResponse = ErrorResponse(
            errorCode = exception.status.name,
            message = exception.message ?: ""
        )

        val response = HttpResponse.status<ErrorResponse>(exception.status)
        response.body(errorResponse)

        return response
    }
}
