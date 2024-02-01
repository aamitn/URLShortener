package com.bitmutex.shortener;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ContactControllerTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private SmsService smsService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ContactController contactController;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(contactController).build();
    }


    @Test
    public void testSubmitContactFormSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some file".getBytes());

        when(contactRepository.save(any(ContactMessage.class))).thenReturn(new ContactMessage());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/contact/submitContactForm")
                        .file(file)
                        .param("name", "John Doe")
                        .param("email", "john@example.com")
                        .param("phoneNumber", "1234567890")
                        .param("sourceType", "WEBSITE")
                        .param("message", "Hello World"))
                .andExpect(MockMvcResultMatchers.flash().attribute("successMessage", "Form submitted successfully!"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/contact"));
    }

}
