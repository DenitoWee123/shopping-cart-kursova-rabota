package shopping_cart.ControllerTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shopping_cart.Dto.KauflandDto;
import shopping_cart.service.KauflandService;
import shopping_cart.controller.KauflandController;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KauflandController.class)
class KauflandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KauflandService kauflandService;

    @Test
    void testDownloadBrochureGet_Success() throws Exception {
        String expectedFile = "Kaufland_2025.pdf";
        KauflandDto mockDto = KauflandDto.success(expectedFile, LocalDate.now(), LocalDate.now().plusDays(7), 50, 10);

        when(kauflandService.downloadBrochure()).thenReturn(mockDto);

        mockMvc.perform(get("/api/kaufland/download")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pdfFilename").value(expectedFile))
                .andExpect(jsonPath("$.productsCount").value(50));
    }
    @Test
    void testDownloadBrochurePost_Success() throws Exception {
        // Arrange
        KauflandDto mockDto = KauflandDto.success("Kaufland_Post.pdf", LocalDate.now(), LocalDate.now().plusDays(7), 20, 5);
        when(kauflandService.downloadBrochure()).thenReturn(mockDto);

        mockMvc.perform(post("/api/kaufland/download-brochure")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Очакваме 200 OK
                .andExpect(jsonPath("$.pdfFilename").value("Kaufland_Post.pdf"));
    }
    @Test
    void testDownloadBrochure_Error() throws Exception {
        String errorMsg = "Сайтът не отговаря";
        when(kauflandService.downloadBrochure()).thenThrow(new RuntimeException(errorMsg));

        mockMvc.perform(get("/api/kaufland/download"))
                .andExpect(status().isBadRequest()) // 400
                .andExpect(content().string(containsString("Грешка при сваляне на Kaufland брошура")));
    }

    @Test
    void testStatus() throws Exception {
        mockMvc.perform(get("/api/kaufland/status"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Kaufland service е активен")));
    }

    @Test
    void testTriggerManually() throws Exception {

        mockMvc.perform(get("/api/kaufland/trigger"))
                .andExpect(status().isOk())
                .andExpect(content().string("Заявка за сваляне на Kaufland брошура е приета (работи във фонов режим)"));
    }
}