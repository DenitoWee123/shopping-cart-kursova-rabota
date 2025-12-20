package shopping_cart.Dto;

import java.time.LocalDate;

public class BillaDto {
    private String fileName;
    private LocalDate startDate;
    private LocalDate endDate;

    // Конструктор
    public BillaDto(String fileName, LocalDate startDate, LocalDate endDate) {
        this.fileName = fileName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}