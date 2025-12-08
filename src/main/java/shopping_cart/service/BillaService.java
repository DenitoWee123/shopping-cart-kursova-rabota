package shopping_cart.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.InputStream;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import java.awt.geom.Rectangle2D;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import java.nio.file.StandardCopyOption;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shopping_cart.Dto.BillaDto; // Увери се, че имаш този DTO
import shopping_cart.entity.PriceEntity;
import shopping_cart.entity.ProductEntity;
import shopping_cart.mapper.PriceMapper;
import shopping_cart.mapper.ProductMapper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BillaService {

    private final ChromeDriver driver;
    private final ProductMapper productMapper;
    private final PriceMapper priceMapper;

    @Value("${billa.url}") // напр: https://www.billa.bg/promocii/sedmichna-broshura
    private String brochurePageUrl;

    public BillaService(ChromeDriver driver,
                        ProductMapper productMapper,
                        PriceMapper priceMapper) {
        this.driver = driver;
        this.productMapper = productMapper;
        this.priceMapper = priceMapper;
    }

    // ... (В началото на BillaService.java - Увери се, че всички Selenium импорти са налични)

    public BillaDto downloadBrochure() throws Exception {
        System.out.println(">>> BILLA SERVICE: Започва сваляне на брошура от homepage (с навигация до слайдера)");

        File downloadDir = new File("downloads");
        if (!downloadDir.exists()) downloadDir.mkdirs();

        // 1. Почистване на стари файлове
        clearOldFiles(downloadDir, ".pdf", ".crdownload");
        clearOldFiles(new File("./pdfimages_products_billa"), ".png");

        // 2. Отваряне на homepage
        driver.get(brochurePageUrl); // https://www.billa.bg/
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // За бавно зареждане

        // --- Стъпка А: Затваряне на бисквитките ---
        try {
            WebElement cookieBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("onetrust-accept-btn-handler")));
            cookieBtn.click();
            System.out.println("Бисквитките са приети.");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Няма бисквитки.");
        }

        // --- Стъпка Б: Автоматична навигация до брошурата през слайдера ---
        LocalDate validFrom = LocalDate.now();
        LocalDate validTo = validFrom.plusDays(7);
        boolean navigated = false;

        try {
            // 1. Търсим слайдера (ws-slider-group__inner)
            WebElement slider = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("ul.ws-slider-group__inner, .ws-slider-group__inner")));
            System.out.println("Намерен слайдер на homepage.");

            // 2. Търсим първия teaser блок в слайдера
            WebElement firstTeaser = slider.findElement(By.cssSelector("div.ws-teaser__content.pa-4:first-of-type, .ws-teaser__content:first-child"));
            System.out.println("Намерен първи teaser: " + firstTeaser.getText());

            // Извличаме дати от teaser (ако има текст като "04.12. - 10.12.")
            try {
                String teaserText = firstTeaser.getText();
                if (teaserText.contains("-") && teaserText.matches(".*\\d{2}\\.\\d{2}\\.\\d{4}.*")) {
                    String[] parts = teaserText.split("\\s*-\\s*");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    validFrom = LocalDate.parse(parts[0].trim().replaceAll("[^0-9.]", ""), formatter);
                    validTo = LocalDate.parse(parts[1].trim().replaceAll("[^0-9.]", ""), formatter);
                    System.out.println("Дати от teaser: " + validFrom + " до " + validTo);
                }
            } catch (Exception e) {
                System.out.println("Не можах да извлека дати от teaser.");
            }

            // 3. Кликваме линка в teaser-а (първият <a>)
            WebElement brochureLink = firstTeaser.findElement(By.tagName("a")); // Или By.cssSelector("a[href*='promocii']")
            String targetUrl = brochureLink.getAttribute("href");
            if (targetUrl.startsWith("/")) {
                targetUrl = "https://www.billa.bg" + targetUrl; // Абсолютен URL
            }

            // Скрол и клик
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", brochureLink);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", brochureLink);
            navigated = true;
            System.out.println("Кликнато върху брошура линк: " + targetUrl);

            // Изчакваме зареждане на подстраницата
            Thread.sleep(3000);

        } catch (Exception e1) {
            System.out.println("Навигация през слайдера не сработи: " + e1.getMessage() + " – fallback към директна навигация.");

            // Fallback 1: Директно към промоции
            try {
                driver.get("https://www.billa.bg/promocii/sedmichna-broshura");
                navigated = true;
                System.out.println("Fallback: Директно до седмична брошура.");
                Thread.sleep(3000);
            } catch (Exception e2) {
                // Fallback 2: Търсене по меню (ако има main menu с "Промоции")
                try {
                    WebElement menuLink = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[contains(text(), 'Промоции') or contains(@href, 'promocii')]")));
                    menuLink.click();
                    Thread.sleep(2000);
                    // После клик на брошура
                    WebElement brochureMenu = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[contains(text(), 'Брошура') or contains(@href, 'broshura')]")));
                    brochureMenu.click();
                    navigated = true;
                    System.out.println("Fallback: Навигация през менюто.");
                } catch (Exception e3) {
                    throw new RuntimeException("Не може да се навигира до брошурата! Провери homepage структурата.", e3);
                }
            }
        }

        // --- Стъпка В: Сваляне на PDF (сега сме на брошура страницата) ---
        String pdfHref = null;
        WebElement pdfElement = null;
        boolean inIframe = false;

        if (navigated) {
            try {
                // 1. Търсим Publitas iframe на брошура страницата
                WebElement iframe = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("iframe[src*='publitas.com'], iframe[src*='viewer'], iframe[class*='publication']")));
                driver.switchTo().frame(iframe);
                inIframe = true;
                System.out.println("Превключено към Publitas iframe на брошурата.");

                Thread.sleep(4000); // За пълен load

                // 2. Селектори за PDF в iframe (множество за Publitas)
                List<By> selectors = Arrays.asList(
                        By.id("downloadAsPdf"),
                        By.cssSelector("a[data-href='download_pdf'], a[download]"),
                        By.cssSelector("a[aria-label*='PDF'], a[aria-label*='Download']"),
                        By.cssSelector(".download-button, .pui-download, button[title*='PDF']"),
                        By.xpath("//a[contains(@href, '.pdf') or contains(@href, 'publitas') and (contains(., 'PDF') or contains(., 'Изтегли'))]")
                );

                for (By selector : selectors) {
                    try {
                        pdfElement = wait.until(ExpectedConditions.elementToBeClickable(selector));
                        System.out.println("Намерен PDF елемент в iframe с: " + selector);
                        break;
                    } catch (Exception ignored) {}
                }

                if (pdfElement != null) {
                    pdfHref = pdfElement.getAttribute("href");
                    if (pdfHref != null && pdfHref.contains(".pdf")) {
                        // Директно сваляне
                        driver.get(pdfHref);
                        System.out.println("Директно сваляне: " + pdfHref);
                    } else {
                        // Клик
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", pdfElement);
                        System.out.println("Кликнато в iframe.");
                    }
                } else {
                    throw new Exception("PDF елемент не е намерен в iframe.");
                }
            } catch (Exception e) {
                System.out.println("Iframe фейл: " + e.getMessage() + " – директен API fallback.");
                driver.switchTo().defaultContent(); // Излизаме от iframe ако сме в него
                String apiUrl = "https://www.billa.bg/api/brochure/pdf/current"; // Или конкретния от твоя snippet
                driver.get(apiUrl);
            }

            // Връщане от iframe
            if (inIframe) {
                driver.switchTo().defaultContent();
            }
        }

        // 3. Изчакване на PDF
        File pdfFile = waitForPdfDownload(downloadDir, 90);
        if (pdfFile == null || pdfFile.length() < 500_000) {
            throw new RuntimeException("PDF не се свали (очаквано >5MB)!");
        }

        // 4. Преименуване
        String newName = String.format("Billa-Brochure-%s-%s.pdf",
                validFrom.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                validTo.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        File finalPdf = new File(downloadDir, newName);
        if (pdfFile.renameTo(finalPdf)) {
            pdfFile = finalPdf;
        }
        System.out.println("СВАЛЕН: " + pdfFile.getAbsolutePath());

        // 5. Обработка
        try (PDDocument document = PDDocument.load(pdfFile)) {
            parseProductsFromPdf(document);
            extractProductImagesFromPdf(document);
        } catch (Exception e) {
            System.err.println("Обработка PDF: " + e.getMessage());
        }

        return new BillaDto(pdfFile.getName(), validFrom, validTo);
    }

    // =========================================================
    // ПАРСВАНЕ НА ТЕКСТ (Продукти и Цени)
    private void parseProductsFromPdf(PDDocument document) throws Exception {
        System.out.println(">>> ЗАПОЧВА ПАРСВАНЕ НА BILLA PDF (DEBUG MODE)...");

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setSortByPosition(true);

        UUID storeId = UUID.fromString("00000000-0000-0000-0000-000000000003");

        int totalPages = document.getNumberOfPages();
        for (int p = 1; p <= totalPages; p++) {
            stripper.setStartPage(p);
            stripper.setEndPage(p);

            String text = stripper.getText(document);
            String[] lines = text.split("\\r?\\n");

            List<String> nameBuffer = new ArrayList<>();

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // ДЕБЪГ: Виж какво четем
                // System.out.println("READ: [" + line + "]");

                // 1. Търсим цена (по-гъвкав Regex)
                // Хваща: "12.99 лв", "12,99", "12.99", "1.50"
                if (line.matches(".*\\d+[.,]\\d{2}.*")) {

                    // Изчистване на цената от букви (напр. "12.99 лв" -> "12.99")
                    String priceStr = extractPrice(line);

                    // Взимаме името от последните редове в буфера
                    String name = getNameFromBuffer(nameBuffer);

                    if (isValidProduct(name)) {
                        saveProductAndPrice(name, priceStr, storeId);
                        // Изчистваме буфера, защото започва нов продукт
                        nameBuffer.clear();
                    }
                } else {
                    // 2. Ако не е цена, добавяме в буфера
                    // Филтрираме само най-очевидния боклук
                    if (!isJunk(line)) {
                        nameBuffer.add(line);
                        // Пазим само последните 3 реда (най-вероятно името е там)
                        if (nameBuffer.size() > 3) nameBuffer.remove(0);
                    }
                }
            }
        }
        System.out.println(">>> ПРИКЛЮЧИ ПАРСВАНЕТО.");
    }



    private String getNameFromBuffer(List<String> buffer) {
        if (buffer.isEmpty()) return "Unknown Product";
        // Обединяваме последните редове
        return String.join(" ", buffer).trim();
    }

    private boolean isJunk(String line) {
        // Филтрираме системни текстове на Billa
        String s = line.toLowerCase();
        return s.contains("billa") || s.contains("card") || s.contains("валидно") ||
                s.contains("стр.") || s.contains("www") || s.length() < 2;
    }

    private boolean isValidProduct(String name) {
        return name.length() > 3 && !name.contains("Unknown");
    }



    // ТВОЯТ SAVE МЕТОД (със задължителен try-catch)


    // Помощен метод за обработка на текста от една колона



    // BillaService.java (extractPrice)
    private String extractPrice(String line) {
        Matcher matcher = Pattern.compile("(\\d+[.,]\\d{2})").matcher(line);
        if (matcher.find()) {
            return matcher.group(1).replace(",", ".");
        }
        return "0.00";
    }


    // BillaService.java (saveProductAndPrice - КЛЮЧОВА КОРЕКЦИЯ)
    private void saveProductAndPrice(String name, String priceStr, UUID storeId) {
        try {
            ProductEntity product = new ProductEntity();
            product.setId(UUID.randomUUID());
            // Ограничаваме името, за да не надхвърля VARCHAR(255) в БД
            product.setName(name.length() > 255 ? name.substring(0, 252) + "..." : name);
            product.setCreatedAt(OffsetDateTime.now());

            // ⚠️ Тук може да е грешката, ако Mapper-ът изисква category/sku/description
            productMapper.insert(product);

            PriceEntity priceEntity = new PriceEntity();
            priceEntity.setId(UUID.randomUUID()); // ⚠️ Трябва да има ID, ако е NOT NULL в БД
            priceEntity.setProductId(product.getId());
            priceEntity.setPrice(new BigDecimal(priceStr));
            priceEntity.setTimestamp(OffsetDateTime.now());
            priceEntity.setStoreId(storeId);
            priceMapper.insert(priceEntity);

            // 🟢 УСПЕШЕН ЛОГ
            System.out.println("Billa → " + name + " | " + priceStr + " лв");
        } catch (Exception e) {
            // 🚨 ЛОГВАМЕ КОНКРЕТНАТА ГРЕШКА
            System.err.println("❌ MyBatis ГРЕШКА при запис на BILLA продукт: " + name);
            e.printStackTrace(); // <--- ТОВА ЩЕ РАЗКРИЕ ПРОБЛЕМА!
        }
    }

    // =========================================================
    // СНИМКИ (IMAGE EXTRACTION) - Адаптирано за BILLA
    // =========================================================
    private void extractProductImagesFromPdf(PDDocument document) throws Exception {
        PDFRenderer renderer = new PDFRenderer(document);
        File outDir = new File("./pdfimages_products_billa/");
        if (!outDir.exists()) outDir.mkdirs();

        int imageIndex = 1;
        for (int pageNum = 0; pageNum < document.getNumberOfPages(); pageNum++) {
            // Рендерираме страницата като картинка
            BufferedImage pageImage = renderer.renderImageWithDPI(pageNum, 200, ImageType.RGB);

            // Търсим зони с цени (BILLA често ползва жълто и червено)
            List<Rectangle> priceZones = findBillaPriceZones(pageImage);

            Set<Integer> usedY = new HashSet<>();

            for (Rectangle zone : priceZones) {
                // Избягваме дублиране на снимки за един и същи ред
                if (usedY.stream().anyMatch(y -> Math.abs(y - zone.y) < 100)) continue;
                usedY.add(zone.y);

                // Изрязваме продукта НАД цената
                int w = 600;
                int h = 700; // Височина на продукта
                int centerX = zone.x + zone.width / 2;
                int x = Math.max(0, centerX - w / 2);
                int y = Math.max(0, zone.y - h + 50); // Взимаме малко и от цената

                if (x + w > pageImage.getWidth()) x = pageImage.getWidth() - w;
                if (y + h > pageImage.getHeight()) h = pageImage.getHeight() - y;
                if (h < 300) continue; // Твърде малка снимка

                BufferedImage crop = pageImage.getSubimage(x, y, w, h);
                String filename = String.format("billa_product_%03d.png", imageIndex++);
                ImageIO.write(crop, "PNG", new File(outDir, filename));
            }
        }
    }

    // Търсене на цветовете на BILLA (Жълто и Червено)
    private List<Rectangle> findBillaPriceZones(BufferedImage img) {
        List<Rectangle> zones = new ArrayList<>();
        boolean[][] visited = new boolean[img.getHeight()][img.getWidth()];

        for (int y = 100; y < img.getHeight() - 100; y += 20) {
            for (int x = 50; x < img.getWidth() - 50; x += 20) {
                if (visited[y][x]) continue;
                Color c = new Color(img.getRGB(x, y));

                // Billa Жълто (Примерно: R>220, G>200, B<100)
                boolean isYellow = c.getRed() > 200 && c.getGreen() > 180 && c.getBlue() < 100;
                // Billa Червено (за промоции)
                boolean isRed = c.getRed() > 200 && c.getGreen() < 100 && c.getBlue() < 100;

                if (isYellow || isRed) {
                    Rectangle r = floodFillColorBlock(img, x, y, visited, c);
                    // Филтър за големина на карето с цената
                    if (r.width > 50 && r.width < 500 && r.height > 30 && r.height < 200) {
                        zones.add(r);
                        markVisitedAround(visited, r, 50);
                    }
                }
            }
        }
        zones.sort(Comparator.comparingInt(r -> r.y));
        return zones;
    }

    // Стандартен Flood Fill (може да се ползва същия като в Kaufland)
    private Rectangle floodFillColorBlock(BufferedImage img, int sx, int sy, boolean[][] visited, Color target) {
        Queue<int[]> q = new LinkedList<>();
        q.add(new int[]{sx, sy});
        visited[sy][sx] = true;
        int minX = sx, maxX = sx, minY = sy, maxY = sy;

        while (!q.isEmpty()) {
            int[] p = q.poll();
            int x = p[0], y = p[1];
            minX = Math.min(minX, x); maxX = Math.max(maxX, x);
            minY = Math.min(minY, y); maxY = Math.max(maxY, y);
            int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};

            for (int[] d : dirs) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && nx < img.getWidth() && ny >= 0 && ny < img.getHeight() && !visited[ny][nx]) {
                    Color c = new Color(img.getRGB(nx, ny));
                    if (colorDistance(c, target) < 2500) { // Толеранс за подобен цвят
                        visited[ny][nx] = true;
                        q.add(new int[]{nx, ny});
                    }
                }
            }
        }
        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private int colorDistance(Color a, Color b) {
        int dr = a.getRed() - b.getRed();
        int dg = a.getGreen() - b.getGreen();
        int db = a.getBlue() - b.getBlue();
        return dr*dr + dg*dg + db*db;
    }

    private void markVisitedAround(boolean[][] visited, Rectangle r, int pad) {
        for (int y = r.y - pad; y <= r.y + r.height + pad; y++) {
            for (int x = r.x - pad; x <= r.x + r.width + pad; x++) {
                if (y >= 0 && y < visited.length && x >= 0 && x < visited[0].length) {
                    visited[y][x] = true;
                }
            }
        }
    }

    // =========================================================
    // HELPER METHODS (Файлове и Чакане)
    // =========================================================
    private void clearOldFiles(File dir, String... extensions) {
        if (!dir.exists()) return;
        File[] files = dir.listFiles((d, name) -> {
            for (String ext : extensions) if (name.toLowerCase().endsWith(ext)) return true;
            return false;
        });
        if (files != null) for (File f : files) f.delete();
    }

    private File waitForPdfDownload(File downloadDir, int timeoutSeconds) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutSeconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            File[] files = downloadDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".pdf") && !name.endsWith(".crdownload"));

            if (files != null && files.length > 0) {
                // Взимаме най-новия файл
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                File file = files[0];
                if (file.length() > 100_000) {
                    Thread.sleep(1000); // Изчакваме още малко да се освободи
                    return file;
                }
            }
            Thread.sleep(1000);
        }
        return null;
    }
}