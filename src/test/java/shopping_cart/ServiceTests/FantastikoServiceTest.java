package shopping_cart.ServiceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.chrome.ChromeDriver;
import shopping_cart.entity.PriceEntity;
import shopping_cart.entity.ProductEntity;
import shopping_cart.mapper.PriceMapper;
import shopping_cart.mapper.ProductMapper;
import shopping_cart.repository.FantastikoRepository;
import shopping_cart.service.FantastikoService;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FantastikoServiceTest {

    @Mock
    private ChromeDriver driver;
    @Mock
    private FantastikoRepository repository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private PriceMapper priceMapper;

    private FantastikoService service;

    @BeforeEach
    void setUp() {
        service = new FantastikoService(driver, repository, productMapper, priceMapper);
    }

    @Test
    void testExtractPrice_StandardFormat() throws Exception {
        String result = invokeExtractPrice("Цена 5.99 лв");
        assertEquals("5.99", result);
    }

    @Test
    void testExtractPrice_WithDash() throws Exception {
        String result = invokeExtractPrice("Супер цена 5-99 лв");
        assertEquals("5.99", result);
    }

    @Test
    void testExtractPrice_WithComma() throws Exception {
        String result = invokeExtractPrice("Цена 12,50 BGN");
        assertEquals("12.50", result);
    }

    @Test
    void testExtractPrice_DualCurrencyLogic() throws Exception {
        String line = "Цена 10.00 лв 5.11 eur";
        String result = invokeExtractPrice(line);
        assertEquals("10.00", result);
    }

    @Test
    void testCleanProductName() throws Exception {
        String dirtyName = "   -  Краставици  цена за кг  ";
        String cleaned = invokeCleanProductName(dirtyName);

        assertEquals("Краставици", cleaned);
    }

    @Test
    void testCleanProductName_RemovesPrices() throws Exception {
        String dirtyName = "Сирене краве 12.50 лв";
        String cleaned = invokeCleanProductName(dirtyName);

        assertEquals("Сирене краве", cleaned);
    }

    @Test
    void testSaveProductAndPrice_NewProduct() throws Exception {
        String productName = "Test Product";
        String priceStr = "10.50";

        when(productMapper.findBySku(productName)).thenReturn(null);

        Method method = FantastikoService.class.getDeclaredMethod("saveProductAndPrice", String.class, String.class);
        method.setAccessible(true);
        method.invoke(service, productName, priceStr);

        ArgumentCaptor<ProductEntity> productCaptor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productMapper, times(1)).insert(productCaptor.capture());

        ProductEntity savedProduct = productCaptor.getValue();
        assertEquals(productName, savedProduct.getName());
        assertNotNull(savedProduct.getId());

        //проверка дали се извиква priceMapper
        ArgumentCaptor<PriceEntity> priceCaptor = ArgumentCaptor.forClass(PriceEntity.class);
        verify(priceMapper, times(1)).insert(priceCaptor.capture());

        PriceEntity savedPrice = priceCaptor.getValue();
        assertEquals(new BigDecimal("10.50"), savedPrice.getPrice());
        assertEquals(savedProduct.getId(), savedPrice.getProductId());
    }

    @Test
    void testSaveProductAndPrice_ExistingProduct() throws Exception {
        String productName = "Existing Product";
        String priceStr = "5.00";
        UUID existingId = UUID.randomUUID();

        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setId(existingId);
        existingProduct.setName(productName);

        when(productMapper.findBySku(productName)).thenReturn(existingProduct);

        Method method = FantastikoService.class.getDeclaredMethod("saveProductAndPrice", String.class, String.class);
        method.setAccessible(true);
        method.invoke(service, productName, priceStr);

        verify(productMapper, never()).insert(any());

        ArgumentCaptor<PriceEntity> priceCaptor = ArgumentCaptor.forClass(PriceEntity.class);
        verify(priceMapper).insert(priceCaptor.capture());
        assertEquals(existingId, priceCaptor.getValue().getProductId());
    }

    private String invokeExtractPrice(String line) throws Exception {
        Method method = FantastikoService.class.getDeclaredMethod("extractPrice", String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, line);
    }

    private String invokeCleanProductName(String rawName) throws Exception {
        Method method = FantastikoService.class.getDeclaredMethod("cleanProductName", String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, rawName);
    }
}