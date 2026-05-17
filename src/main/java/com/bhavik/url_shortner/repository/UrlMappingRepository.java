package com.bhavik.url_shortner.repository;

import com.bhavik.url_shortner.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;


public interface UrlMappingRepository extends JpaRepository<UrlMapping,Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    long deleteByExpirationDateBefore(LocalDateTime now);
}
