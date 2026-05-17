package com.bhavik.url_shortner.service;

import com.bhavik.url_shortner.DTO.UrlStateResponse;
import com.bhavik.url_shortner.Exception.AliasAlreadyExistsException;
import com.bhavik.url_shortner.Exception.UrlNotFoundException;
import com.bhavik.url_shortner.model.UrlMapping;
import com.bhavik.url_shortner.repository.UrlMappingRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final UrlMappingRepository urlMappingRepository;

    public UrlShortenerService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    @Transactional
    public String shortUrl(String originalUrl, String customAlias, Integer hoursToExpire){

        if (StringUtils.hasText(customAlias)) {

            Optional<UrlMapping> existingMapping = urlMappingRepository.findByShortCode(customAlias);
            if (existingMapping.isPresent()) {
                throw new AliasAlreadyExistsException("Alias '" + customAlias + "' is already in use.");
            }

            UrlMapping urlMapping = new UrlMapping();
            urlMapping.setOriginalUrl(originalUrl);
            urlMapping.setCreationDate(LocalDateTime.now());
            urlMapping.setShortCode(customAlias);
            if (hoursToExpire != null) {
                urlMapping.setExpirationDate(LocalDateTime.now().plusHours(hoursToExpire));
            }
            urlMappingRepository.save(urlMapping);
            return customAlias;
        } else {

            UrlMapping urlMapping = new UrlMapping();
            urlMapping.setOriginalUrl(originalUrl);
            urlMapping.setCreationDate(LocalDateTime.now());

            if (hoursToExpire != null) {
                urlMapping.setExpirationDate(LocalDateTime.now().plusHours(hoursToExpire));
            }

            UrlMapping savedEntity = urlMappingRepository.save(urlMapping);

            String shortCode = encodeBase62(savedEntity.getId());
            savedEntity.setShortCode(shortCode);

            urlMappingRepository.save(savedEntity);

            return shortCode;
        }

    }

    @Transactional
    public String getOriginalUrlAndIncrementClicks(String shortCode) {

        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));
        if (urlMapping.getExpirationDate() != null && urlMapping.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new UrlNotFoundException("This link has expired and is no longer active.");
        }
        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);

        return urlMapping.getOriginalUrl();
    }

    public UrlStateResponse getStats(String shortCode) {

        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No statistics found for short code: " + shortCode));

        String fullShortUrl = "http://localhost:8080/" + urlMapping.getShortCode();

        return new UrlStateResponse(
                urlMapping.getOriginalUrl(),
                fullShortUrl,
                urlMapping.getCreationDate(),
                urlMapping.getClickCount()
        );
    }
    private String encodeBase62(Long number){

        if(number == 0 ){
          return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        long num = number;

        while (num > 0) {

            int remainder = (int) (num % 62);
            sb.append(BASE62_CHARS.charAt(remainder));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}
