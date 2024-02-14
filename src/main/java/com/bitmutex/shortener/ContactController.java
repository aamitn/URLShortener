package com.bitmutex.shortener;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;


@Controller
@RequestMapping("/contact")
public class ContactController {

    final
    ContactRepository contactRepository;

    private final SmsService smsService;

    private final EmailService emailService;

    public ContactController(ContactRepository contactRepository, SmsService smsService, EmailService emailService) {
        this.contactRepository = contactRepository;
        this.smsService = smsService;
        this.emailService = emailService;
    }

    @GetMapping
    public String showContactForm() {
        return "contact";
    }

    @PostMapping("/submitContactForm")
    public String submitContactForm(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam SourceType sourceType,
            @RequestPart (required = false) MultipartFile file,
            @RequestParam String message,
            RedirectAttributes redirectAttributes) {

        try {
            // Validate and process file attachment
            if (file == null || file.isEmpty() || file.getSize() <= 2 * 1024 * 1024) {
                byte[] attachment = (file != null) ? file.getBytes() : null;

                // Save the form data to the database
                ContactMessage contactMessage = new ContactMessage(name, email, phoneNumber, sourceType, attachment, message);
                contactRepository.save(contactMessage);

                // Send email to admin with the form content
                String adminSubject = "Thank you for contacting us!";
                String adminMessage = "<html><body><p>New contact form submission:</p><table border=\"1\">" +
                        "<tr><td><b>Name:</b></td><td>" + name + "</td></tr>" +
                        "<tr><td><b>Email:</b></td><td>" + email + "</td></tr>" +
                        "<tr><td><b>Phone Number:</b></td><td>" + phoneNumber + "</td></tr>" +
                        "<tr><td><b>Source Type:</b></td><td>" + sourceType + "</td></tr>" +
                        "<tr><td><b>Message:</b></td><td>" + message + "</td></tr>" +
                        "</table></body></html>";

                emailService.sendMail(email, adminSubject, adminMessage);

                // Send SMS only if phone number is present and not null
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    smsService.sendSms(phoneNumber,"Thank you for contacting us! Your message has been received. We will contact you soon");
                }

                // Add success flash attribute
                redirectAttributes.addFlashAttribute("successMessage", "Form submitted successfully!");

                return "redirect:/contact";
            } else {
                // Add error flash attribute for file size exceeded
                redirectAttributes.addFlashAttribute("errorMessage", "File size should be less than 2MB.");

                return "redirect:/contact";
            }
        } catch (IOException e) {
            // Handle exception
            // Add error flash attribute for file processing error
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing file.");

            return "redirect:/contact";
        }
    }

}