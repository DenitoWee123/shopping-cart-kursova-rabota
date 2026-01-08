//package shopping_cart.ControllerTests;

//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import shopping_cart.Dto.FantastikoDto;
//import shopping_cart.service.FantastikoService;

//import java.time.LocalDate;
//import shopping_cart.controller.FantastikoController;
//import jakarta.servlet.ServletException;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(FantastikoController.class)
//class FantastikoControllerTest {

//    @Autowired
//    private MockMvc mockMvc; // браузър/HTTP клиент

//    @MockitoBean
//    private FantastikoService fantastikoService;

//    @Test
//    void testDownload_ShouldReturnOkAndJson() throws Exception {
//        String expectedFileName = "fantastiko_test.pdf";
//        LocalDate startDate = LocalDate.now();
//        LocalDate endDate = startDate.plusDays(7);

//        FantastikoDto mockResponse = new FantastikoDto(expectedFileName, startDate, endDate);

//        when(fantastikoService.downloadBrochure()).thenReturn(mockResponse);

//        mockMvc.perform(get("/api/brochures/download")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.filename").value(expectedFileName))
//                .andExpect(jsonPath("$.validFrom").exists())
//                .andExpect(jsonPath("$.validTo").exists());
//    }

//    @Test
//    void testDownload_InternalServerError() throws Exception{
//        when(fantastikoService.downloadBrochure())
//                .thenThrow(new RuntimeException("Грешка при свързването"));

//        ServletException exception = assertThrows(ServletException.class, () -> {
//            mockMvc.perform(get("/api/brochures/download"));
//        });

//        Throwable cause = exception.getCause();
//        assertTrue(cause instanceof RuntimeException);
//        assertTrue(cause.getMessage().equals("Грешка при свързването"));
//    }
//}