package com.bitmutex.shortener;

import java.util.Date;

public class UrlDetailsDTO {
    private Long id;
    private String originalUrl;
    private String shortUrl;
    private String linkType;
    private long hits;
    private Long uniqueHits;
    private Date timestamp;
    private String qrCode;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;

    public void setHits(long hits) {
        this.hits = hits;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getBioContent() {
        return bioContent;
    }

    public void setBioContent(String bioContent) {
        this.bioContent = bioContent;
    }

    private String bioContent;

    // Existing constructors, getters, and setters

    // Constructor with parameters
    public UrlDetailsDTO(Long id, Long userId, String originalUrl, String shortUrl, String linkType, long hits, Long uniqueHits, Date timestamp, String qrCode, String bioContent) {
        this.id = id;
        this.userId=userId;
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.linkType = linkType;
        this.hits = hits;
        this.uniqueHits = uniqueHits;
        this.timestamp = timestamp;
        this.qrCode = qrCode;
        this.bioContent = bioContent;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }

    public Long getUniqueHits() {
        return uniqueHits;
    }

    public void setUniqueHits(Long uniqueHits) {
        this.uniqueHits = uniqueHits;
    }

    // Constructors, getters, and setters
}