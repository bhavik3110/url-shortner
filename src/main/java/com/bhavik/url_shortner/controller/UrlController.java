package com.bhavik.url_shortner.controller;

import com.bhavik.url_shortner.DTO.ShortenUrlRequest;
import com.bhavik.url_shortner.DTO.ShortenUrlResponse;
import com.bhavik.url_shortner.DTO.UrlStateResponse;
import com.bhavik.url_shortner.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class UrlController {


  private final UrlShortenerService urlShortenerService;

    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

  @PostMapping("/api/v1/url/shorten")
  public ResponseEntity<ShortenUrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {

    String shortCode = urlShortenerService.shortUrl(request.url(),request.customAlias(),request.hoursToExpire());

    String fullShortUrl = "http://localhost:8080/" + shortCode;
    ShortenUrlResponse response = new ShortenUrlResponse(fullShortUrl);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{shortCode}")
  public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

    String originalUrl = urlShortenerService.getOriginalUrlAndIncrementClicks(shortCode);

    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
  }

}
