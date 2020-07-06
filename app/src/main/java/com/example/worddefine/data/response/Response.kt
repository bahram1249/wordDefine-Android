package com.example.worddefine.data.response

class ResponseStatus(
    var type: ResponseType,
    var message: String? = null
)

enum class ResponseType {
    Successful,
    RequestInProgress,

    // WebService Error
    NotAccess,

    // WebService Error
    NotFound,
    WebServiceError,
    FailedToConnect
}