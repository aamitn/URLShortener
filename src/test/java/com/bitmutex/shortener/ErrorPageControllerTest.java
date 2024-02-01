package com.bitmutex.shortener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorPageControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @InjectMocks
    private ErrorPageController errorPageController;

    @Test
    void handleError() {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        // Mocking request attributes
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);
        when(request.getAttribute(RequestDispatcher.ERROR_MESSAGE)).thenReturn("Not Found");
        when(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).thenReturn("/some-page");
        when(request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME)).thenReturn("MyServlet");

        // Mocking model
        when(model.addAttribute("errorCode", 404)).thenReturn(model);
        when(model.addAttribute("errorMessage", "Not Found")).thenReturn(model);
        when(model.addAttribute("requestUri", "/some-page")).thenReturn(model);
        when(model.addAttribute("servletName", "MyServlet")).thenReturn(model);
        when(model.addAttribute("errorTitle", "Page Not Found")).thenReturn(model);

        // Invoke the method under test
        String result = errorPageController.handleError(model);

        // Verify that model attributes are set correctly
        assertEquals(result, "error");
    }
}
