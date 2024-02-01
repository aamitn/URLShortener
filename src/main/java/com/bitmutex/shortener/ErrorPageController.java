package com.bitmutex.shortener;

import jakarta.servlet.RequestDispatcher;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            // Object status = request.getAttribute("jakarta.servlet.error.status_code");
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

            if (status != null) {
                int statusCode = Integer.parseInt(status.toString());
                String statusMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString();
                String requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI).toString();
                String servletName = request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME).toString();

                model.addAttribute("errorCode", statusCode);
                model.addAttribute("errorMessage", statusMessage);
                model.addAttribute("requestUri", requestUri);
                model.addAttribute("servletName", servletName);


                // Customize error title, code, and message based on status code
                switch (statusCode) {
                    case 404:
                        model.addAttribute("errorTitle", "Page Not Found");
                        break;
                    case 500:
                        model.addAttribute("errorTitle", "Internal Server Error");

                        break;
                    // Add more cases as needed
                    default:
                        model.addAttribute("errorTitle", "Error");
                }
            }
        }

        // Default error handling
        return "error";
    }

}
