package com.bitmutex.shortener;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class BioControllerTest {

    @Mock
    private UrlShortenerService urlShortenerService;

    @Mock
    private Model model;

    @InjectMocks
    private BioController bioController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowBioEditPage() {
        // Given
        String expectedViewName = "bio";

        // When
        String actualViewName = bioController.showBioEditPage();

        // Then
        assertEquals(actualViewName, expectedViewName);
    }
}
