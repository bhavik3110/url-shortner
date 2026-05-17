package com.bhavik.url_shortner.DTO;

import java.time.LocalDateTime;

public record UrlStateResponse(String originalUrl,
                               String shortUrl,
                               LocalDateTime creationDate,
                               long clickCount) {
}
