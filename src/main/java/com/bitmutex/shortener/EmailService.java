package com.bitmutex.shortener;


import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String to, String subject, String message) {

        try {
            MimeMessageHelper helper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);


            // Construct the HTML content with header and footer
            String htmlContent = "<html><body>"
                    + "<header>" + "<h1>LinkShortener</h1>" + "</header>" // Customize as needed
                    + message // Include the main message content
                    + "<footer>" + "<p>&copy; The LinkShortener Company 2023</p>" + "</footer>" // Customize as needed
                    + "</body></html>";

            helper.setText(htmlContent, true); // Set as HTML content

            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            // Handle email sending errors appropriately
            throw new UrlShortenerException("Email Sending Error",e);
          //  e.printStackTrace();
        }
    }
}