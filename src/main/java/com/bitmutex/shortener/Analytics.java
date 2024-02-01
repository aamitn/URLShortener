package com.bitmutex.shortener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;


@Entity
@Table(name = "analytics")
public class Analytics {


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UrlShortener getUrlShortener() {
        return urlShortener;
    }

    public void setUrlShortener(UrlShortener urlShortener) {
        this.urlShortener = urlShortener;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analytics_id")
    private Long id;

    // In UrlAnalytics class
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_shortener_id", nullable = false)
    @JsonIgnore
    private UrlShortener urlShortener;

    @Column(name = "device_ip")
    private String deviceIp;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "device_type")
    private String deviceType;

    public String getAcceptTypes() {
        return acceptTypes;
    }

    public void setAcceptTypes(String acceptTypes) {
        this.acceptTypes = acceptTypes;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    @Column(name = "accept_language")
    private String acceptLanguage;
    @Column(name = "accept_types")
    private String acceptTypes;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "timestamp")
    private String timestamp;

    public Double getRedirectionTime() {
        return redirectionTime;
    }

    public void SetRedirectionTime(Double  redirectionTime) {
        this.redirectionTime = redirectionTime;
    }

    @Column(name = "redirection_time")
    private Double redirectionTime;


    // Other analytics data fields...

    // Getters and setters...
}
