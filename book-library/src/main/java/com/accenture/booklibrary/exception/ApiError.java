package com.accenture.booklibrary.exception;

import java.time.LocalDateTime;

public record ApiError(String message, int statusCode, LocalDateTime time, String uri) {
}
