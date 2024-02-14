package com.bitmutex.shortener;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    // Add custom query methods if needed
    List<Analytics> findByDeviceIp(String deviceIp);
    long countByUrlShortenerId(Long urlShortenerId);
    List<Analytics> findByUrlShortenerId(Long urlShortenerId);

}
