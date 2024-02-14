package com.bitmutex.shortener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import jakarta.persistence.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

@Entity
@Table(name = "url_shortener")
public class UrlShortener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;
    private String shortUrl;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(mappedBy = "urlShortener", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Analytics> analyticsList;

    @Column(name = "link_type")
    private String linkType;

    public int getLinkStatus() {
        return linkStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "password")

    private String password;

    public void setLinkStatus(int linkStatus) {
        this.linkStatus = linkStatus;
    }

    @Column(name = "link_status")
    private int linkStatus;

    // Constructors

    public UrlShortener() {
        // Default constructor required by JPA
    }

    public UrlShortener(String originalUrl, String shortUrl) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
    }

    // Getters and Setters

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public List<Analytics> getAnalyticsList() {
        return analyticsList;
    }

    public void setAnalyticsList(List<Analytics> analyticsList) {
        this.analyticsList = analyticsList;
    }



    //HIT COUNT
    @Column(name = "hits")
    private long hits;
    public long getHits() {
        return hits;
    }
    public void setHits(long hits) {
        this.hits = hits;
    }

    //UNIQUE HIT COUNT
    @Column(name = "unique_hits")
    private Long uniqueHits;
    public Long getUniqueHits() {
        return uniqueHits;
    }
    public void setUniqueHits(Long uniqueHits) {
        this.uniqueHits = uniqueHits;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    //QR CODE
    @Column(name = "qr_code", length = 2048)  // Adjust the length based on your QR code image size
    private String qrCode;
    public void generateQrCode(String serverBaseUrl, String logoPath, String fgColor, String bgColor) {
        // Construct the full URL including the server base URL and shortcode
        String fullUrl = serverBaseUrl + "/" + this.getShortUrl();

        // Generate the QR code and store it as a Base64-encoded string
        this.qrCode = generateQrCodeBase64(fullUrl, fgColor,bgColor, logoPath);
    }

    private String generateQrCodeBase64(String content, String foregroundColorHex, String backgroundColorHex, String logoPath) {
        try {
            int width = 300; // Set the width and height of the QR code image
            int height = 300;

            // Convert hex strings to Color objects or use default colors
            Color foregroundColor = getColorFromHex(foregroundColorHex, Color.RED);
            Color backgroundColor = getColorFromHex(backgroundColorHex, Color.WHITE);

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageConfig config = new MatrixToImageConfig(foregroundColor.getRGB(), backgroundColor.getRGB());
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream, config);

            BufferedImage qrImage = ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));

            if (logoPath != null && !logoPath.isEmpty()) {
                // Add logo to the center of the QR code
                addLogoToQrCode(qrImage, logoPath);
            }

            // Convert the modified image to a Base64-encoded string
            ByteArrayOutputStream modifiedStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", modifiedStream);
            byte[] imageBytes = modifiedStream.toByteArray();

            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            // Handle exceptions appropriately
            throw new UrlShortenerException("Error generating B64QR for url",e);
        }
    }
    private void addLogoToQrCode(BufferedImage qrImage, String logoPath) throws IOException {
        // Read the logo image
        BufferedImage logoImage = ImageIO.read(new File(logoPath));

        // Calculate the position to place the logo at the center
        int logoX = (qrImage.getWidth() - logoImage.getWidth()) / 2;
        int logoY = (qrImage.getHeight() - logoImage.getHeight()) / 2;

        // Draw the logo on the QR code
        Graphics2D graphics = qrImage.createGraphics();
        graphics.drawImage(logoImage, logoX, logoY, null);
        graphics.dispose();
    }
    private Color getColorFromHex(String hexColor, Color defaultColor) {
        try {
            return Color.decode(hexColor);
        } catch (NumberFormatException e) {
            // If parsing fails, return the default color
            return defaultColor;
        }
    }
    public String getBioContent() {
        return bioContent;
    }

    public void setBioContent(String bioContent) {
        this.bioContent = bioContent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "created_at", columnDefinition = "DATE")
    private Date timestamp;


    //BIO PAGE
    @Column(name = "bio_content", columnDefinition = "TEXT")
    private String bioContent;

    // toString, hashCode, equals, if necessary
    @Override
    public String toString() {
        return "UrlShortener{" +
                "id=" + id +
                ", originalUrl='" + originalUrl + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", userId=" + userId +
                ", linkType='" + linkType + '\'' +
                '}';
    }
}
