package com.bitmutex.shortener;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Transactional
@Repository
public interface UrlShortenerRepository extends JpaRepository<UrlShortener, Long> {

    List<UrlShortener> findByUserId(Long userId);


    UrlShortener findByShortUrl(String shortUrl);

    // Add a method to find UrlShortener by ID
    UrlShortener findById(long id);

    // Add a method to find analytics by UrlShortener
   // List<Analytics> findAnalyticsListByUrlShortener_Id(Long urlShortenerId);
    @Query("SELECT COUNT(DISTINCT a.deviceIp) FROM Analytics a WHERE a.urlShortener.id = :urlShortenerId")
    Long countUniqueHitsByUrlShortenerId(@Param("urlShortenerId") Long urlShortenerId);

    @Query("SELECT u.qrCode FROM UrlShortener u WHERE u.shortUrl = :shortUrl")
    String findQrCodeByShortUrl(@Param("shortUrl") String shortUrl);


    UrlShortener findByShortUrlAndLinkType(String shortUrl, String linkType);

    // Updated methods
    @Query("SELECT u.linkStatus FROM UrlShortener u WHERE u.shortUrl = :shortUrl")
    int getLinkStatusByShortUrl(@Param("shortUrl") String shortUrl);

    @Modifying
    @Query("UPDATE UrlShortener u SET u.linkStatus = :linkStatus WHERE u.shortUrl = :shortUrl")
    void setLinkStatusByShortUrl(@Param("shortUrl") String shortUrl, @Param("linkStatus") int linkStatus);


    // Count short URLs by user ID
    @Query("SELECT COUNT(u) FROM UrlShortener u WHERE u.userId = :userId AND u.linkType = 'short'")
    int countShortUrlsByUserId(@Param("userId") Long userId);

    // Count bio pages by user ID
    @Query("SELECT COUNT(u) FROM UrlShortener u WHERE u.userId = :userId AND u.linkType = 'bio'")
    int countBioPagesByUserId(@Param("userId") Long userId);

}