package shopping_cart.Dto;

import java.time.LocalDate;

public class FantastikoDto {
    private String filename;
    private LocalDate validFrom;
    private LocalDate validTo;

    public FantastikoDto(String filename, LocalDate validFrom, LocalDate validTo) {
        this.filename = filename;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public String getFilename() {
        return filename;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }
}
