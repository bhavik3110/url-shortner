package com.bhavik.url_shortner.DTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(@NotEmpty(message = "URL cannot be empty")
                                @URL(message = "A valid URL format is required")
                                String  url, String customAlias,
                                @Min(value = 1, message = "Hours to expire must be a positive number") Integer hoursToExpire) {


}
