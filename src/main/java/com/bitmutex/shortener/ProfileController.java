package com.bitmutex.shortener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Optional;

@RequestMapping("/")
@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String viewProfile(@RequestParam String username, Model model) {

        // Fetch the user details from the database
        try {
            UserDetailsDto user = userService.getUserDetailsByUsername(username);
            UserEntity userElevated = userService.findByEmail(user.getEmail());


            // Add user details to the model
            model.addAttribute("user", user);
            model.addAttribute("userelevated", userElevated);
        }catch (UsernameNotFoundException ex){
            return "error";
        }
        return "profile";
    }



    @PostMapping("/profile/update-phone-number")
    public String updatePhoneNumber(@RequestParam String username,
                                    @RequestParam String newPhoneNumber,
                                    Model model,
                                    HttpServletRequest request) {

        try {
            // Generate and send OTP to the user's new phone number
            String generatedOtp = otpService.generateAndStoreOtp(username, newPhoneNumber);

            HttpSession session = request.getSession();
            session.setAttribute("otp", generatedOtp);

            // Send the OTP to the user (you need to implement this part)
           // var client = HttpClient.newHttpClient();
           // var apiKey = "Vucf1nCa4ed-AMNGv6CnsycfQT28yLUA8NEvY7IZ87-Piv855UBcjfo29Zb8XPZt";



            var messageVariable = "Your OTP is :"+ generatedOtp;  // Define your variable with the desired content
            smsService.sendSms(newPhoneNumber,messageVariable);


            // Store the generated OTP and the associated phone number in a secure way
            //otpService.storeOtp(username, newPhoneNumber, generatedOtp);

            // Add necessary details to the model for displaying the OTP verification form
            model.addAttribute("username", username);
            model.addAttribute("newPhoneNumber", newPhoneNumber);
            model.addAttribute("generatedOtp", generatedOtp);

            // Redirect to the OTP verification page
            return "redirect:/profile/verify-otp?username="+username+"&newPhoneNumber="+newPhoneNumber;

        } catch (Exception ex) {
            model.addAttribute("error", "Failed to initiate phone number update.");
            return "error";
        }
    }


    @PostMapping("/profile/verify-otp")
    public String verifyOtpAndUpdatePhoneNumber(@RequestParam String username,
                                                @RequestParam String newPhoneNumber,
                                                @RequestParam String enteredOtp,
                                                RedirectAttributes redirectAttributes,
                                                Model model) {

        try {
            //Get User Details
            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            model.addAttribute("username", user.getUsername());
            // Retrieve the stored OTP associated with the username and new phone number
         //   String storedOtp = otpService.getOtp(username, newPhoneNumber);
            // Verify the entered OTP
            if (otpService.getOtp(username, newPhoneNumber).orElse("").equals(enteredOtp)) {
                // OTP is correct, update the phone number in the database
                user.setPhoneNumber(newPhoneNumber);
                userRepository.save(user);

                // Remove the stored OTP after successful verification
                otpService.removeOtp(username, newPhoneNumber);

                // Add success message to the model
                model.addAttribute("success", "Phone number updated successfully.");
                return"redirect:/profile?username="+username;
            } else {
                // Incorrect OTP, add an error message to the flash attributes
                redirectAttributes.addFlashAttribute("error", "Incorrect OTP. Please try again.");

                return "redirect:/profile/verify-otp?username=" + username + "&newPhoneNumber=" + newPhoneNumber;
            }

            // Verify the entered OTP
     /*       if (enteredOtp.equals("1234")) {

                //Save Phone Number to DB
                user.setPhoneNumber(newPhoneNumber);
                userRepository.save(user);
                http://localhost:8080/profile?username=amit
                // Add success message to the model
                model.addAttribute("success", "Phone number updated successfully.");
                return"redirect:/profile?username="+username;
            } else {
                // Incorrect OTP, add an error message to the model
                model.addAttribute("error", "Incorrect OTP. Please try again.");
                return "redirect:/profile/verify-otp?error";
            }  */

        } catch (Exception ex) {
            model.addAttribute("error", "Failed to verify OTP and update phone number.");
        }

        return "verify-otp";
    }

    @GetMapping("/profile/verify-otp")
    public String showOtpForm(@RequestParam String username,
                              @RequestParam String newPhoneNumber,
                              Model model) {

        model.addAttribute("username", username);
        model.addAttribute("newPhoneNumber", newPhoneNumber);
        return "verify-otp";
    }

}