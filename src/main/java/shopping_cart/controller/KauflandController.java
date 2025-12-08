package shopping_cart.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping_cart.Dto.KauflandDto;
import shopping_cart.service.KauflandService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/kaufland")
@CrossOrigin(origins = "*")
public class KauflandController {

    private final KauflandService kauflandService;

    public KauflandController(KauflandService kauflandService) {
        this.kauflandService = kauflandService;
    }

    @PostMapping("/download-brochure")
    public ResponseEntity<KauflandDto> downloadBrochure() {
        try {
            KauflandDto result = kauflandService.downloadBrochure();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(KauflandDto.error("Грешка при сваляне на Kaufland брошура: " + e.getMessage()));
        }
    }
    @GetMapping("/download")
    public ResponseEntity<KauflandDto> downloadBrochureGet() {
        try {
            KauflandDto result = kauflandService.downloadBrochure();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(KauflandDto.error("Грешка при сваляне на Kaufland брошура: " + e.getMessage()));
        }
    }

    @GetMapping("/trigger")
    public ResponseEntity<String> triggerManually() {
        new Thread(() -> {
            try {
                kauflandService.downloadBrochure();
                System.out.println("Ръчно стартирано сваляне на Kaufland – успешно!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return ResponseEntity.ok("Заявка за сваляне на Kaufland брошура е приета (работи във фонов режим)");
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Kaufland service е активен – " + LocalDate.now());
    }
}