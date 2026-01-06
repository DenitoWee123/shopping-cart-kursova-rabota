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
import shopping_cart.service.KauflandService;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KauflandServiceTest {

    @Mock
    private ChromeDriver driver;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private PriceMapper priceMapper;

    private KauflandService service;

    private static final UUID KAUFLAND_STORE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    void setUp() {
        service = new KauflandService(driver, productMapper, priceMapper);
    }

    @Test
    void testExtractPrice_CommaFormat() throws Exception {
        String result = invokeExtractPrice("Краставици 2,99 лв");
        assertEquals("2.99", result);
    }

    @Test
    void testExtractPrice_DotFormat() throws Exception {
        String result = invokeExtractPrice("Домати 3.50 BGN");
        assertEquals("3.50", result);
    }

    @Test
    void testExtractPrice_NoPrice() throws Exception {
        String result = invokeExtractPrice("Само текст без цифри");
        assertEquals("0.00", result);
    }

    @Test
    void testExtractPrice_ComplexLine() throws Exception {
        String result = invokeExtractPrice("Супер оферта само днес 12,55 лв за кг");
        assertEquals("12.55", result);
    }

    @Test
    void testCleanProductName_RemoveParentheses() throws Exception {
        String raw = "Банани (Еквадор)";
        String result = invokeCleanProductName(raw);
        assertEquals("Банани", result);
    }

    @Test
    void testCleanProductName_SplitBySpaces() throws Exception {
        String raw = "Ябълки   Внос";
        String result = invokeCleanProductName(raw);
        assertEquals("Ябълки", result);
    }

    @Test
    void testCleanProductName_Trimming() throws Exception {
        String raw = "  .  Портокали  - ";
        String result = invokeCleanProductName(raw);
        assertEquals("Портокали", result);
    }

    @Test
    void testSaveProductAndPrice_NewProduct() throws Exception {
        String productName = "New Product";
        String priceStr = "10.99";

        when(productMapper.findBySku(productName)).thenReturn(null);

        invokeSaveProductAndPrice(productName, priceStr);

        ArgumentCaptor<ProductEntity> productCaptor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productMapper, times(1)).insert(productCaptor.capture());

        ProductEntity savedProduct = productCaptor.getValue();
        assertEquals(productName, savedProduct.getName());
        assertNotNull(savedProduct.getId());

        ArgumentCaptor<PriceEntity> priceCaptor = ArgumentCaptor.forClass(PriceEntity.class);
        verify(priceMapper, times(1)).insert(priceCaptor.capture());

        PriceEntity savedPrice = priceCaptor.getValue();
        assertEquals(new BigDecimal("10.99"), savedPrice.getPrice());
        assertEquals(KAUFLAND_STORE_ID, savedPrice.getStoreId());
        assertEquals(savedProduct.getId(), savedPrice.getProductId());
    }

    @Test
    void testSaveProductAndPrice_ExistingProduct() throws Exception {
        String productName = "Existing Product";
        String priceStr = "5.55";
        UUID existingId = UUID.randomUUID();

        ProductEntity existingProduct = new ProductEntity();
        existingProduct.setId(existingId);
        existingProduct.setName(productName);
        existingProduct.setCreatedAt(OffsetDateTime.now());

        when(productMapper.findBySku(productName)).thenReturn(existingProduct);

        invokeSaveProductAndPrice(productName, priceStr);
        verify(productMapper, never()).insert(any());

        ArgumentCaptor<PriceEntity> priceCaptor = ArgumentCaptor.forClass(PriceEntity.class);
        verify(priceMapper, times(1)).insert(priceCaptor.capture());

        assertEquals(existingId, priceCaptor.getValue().getProductId());
        assertEquals(new BigDecimal("5.55"), priceCaptor.getValue().getPrice());
    }

    @Test
    void testSaveProductAndPrice_EmptyName() throws Exception {
        String productName = "   ";
        String priceStr = "10.00";

        invokeSaveProductAndPrice(productName, priceStr);

        verify(productMapper, never()).findBySku(any());
        verify(productMapper, never()).insert(any());
        verify(priceMapper, never()).insert(any());
    }


    private String invokeExtractPrice(String line) throws Exception {
        Method method = KauflandService.class.getDeclaredMethod("extractPrice", String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, line);
    }

    private String invokeCleanProductName(String rawName) throws Exception {
        Method method = KauflandService.class.getDeclaredMethod("cleanProductName", String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, rawName);
    }

    private void invokeSaveProductAndPrice(String name, String priceStr) throws Exception {
        Method method = KauflandService.class.getDeclaredMethod("saveProductAndPrice", String.class, String.class);
        method.setAccessible(true);
        method.invoke(service, name, priceStr);
    }
}