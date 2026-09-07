package com.daily.nexamartpartner.core.network

object ErrorMessageResolver {
    fun resolve(statusCode: Int?): String {
        return when (statusCode) {
            400 -> "The request was invalid. Please review and try again."
            401 -> "Your session has expired. Please login again."
            403 -> "You don't have permission to perform this action."
            404 -> "The requested information was not found."
            409 -> "The order has already been updated. Refresh and try again."
            422 -> "Some details are invalid. Please correct and continue."
            429 -> "Too many requests. Please wait and try again."
            500 -> "Something went wrong on the server. Please try again."
            502, 503, 504 -> "Service is temporarily unavailable. Please try again shortly."
            else -> "We couldn't complete the request. Please try again."
        }
    }
}
