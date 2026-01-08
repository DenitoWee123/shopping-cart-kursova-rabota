//package shopping_cart.MapperTests;

//import org.junit.jupiter.api.Test;
//import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import shopping_cart.entity.PriceEntity;
//import shopping_cart.entity.ProductEntity;
//import shopping_cart.mapper.PriceMapper;
//import shopping_cart.mapper.ProductMapper;

//import java.math.BigDecimal;
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.util.List;
//import java.util.UUID;

//import static org.assertj.core.api.Assertions.assertThat;

//@MybatisTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class PriceMapperTest {

//    @Autowired
//    private PriceMapper priceMapper;

//    @Autowired
//    private ProductMapper productMapper;

//    @Test
//    void testInsertAndGetAll() {
//        UUID productId = UUID.randomUUID();
//        UUID storeId = UUID.randomUUID();
//        UUID priceId = UUID.randomUUID();

//        createProduct(productId, "Test Product");

//        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC)
//                .withNano(0); // Махаме наносекундите

//        PriceEntity newPrice = PriceEntity.builder()
//                .id(priceId)
//                .productId(productId)
//                .storeId(storeId)
//                .price(new BigDecimal("10.50"))
//                .currency("BGN")
//                .createdAt(now)
//                .build();

//        priceMapper.insert(newPrice);
//        List<PriceEntity> allPrices = priceMapper.getAll();

//        assertThat(allPrices).isNotEmpty();

//        PriceEntity retrieved = allPrices.stream()
//                .filter(p -> p.getId().equals(priceId))
//                .findFirst()
//                .orElse(null);

//        assertThat(retrieved).isNotNull();
//        assertThat(retrieved.getId()).isEqualTo(priceId);
//        assertThat(retrieved.getPrice()).isEqualByComparingTo("10.50");
//        assertThat(retrieved.getStoreId()).isEqualTo(storeId);
//    }

//    @Test
//    void testDeletePricesByStoreId() {
//        UUID storeA = UUID.randomUUID();
//        UUID storeB = UUID.randomUUID();
//        UUID prodId = UUID.randomUUID();

//        createProduct(prodId, "Common Product");

//        createPrice(storeA, prodId);
//        createPrice(storeA, prodId);
//        UUID priceInStoreB = createPrice(storeB, prodId);

//        priceMapper.deletePricesByStoreId(storeA);

//        List<PriceEntity> allPrices = priceMapper.getAll();
//        boolean hasStoreA = allPrices.stream().anyMatch(p -> p.getStoreId().equals(storeA));
//        assertThat(hasStoreA).isFalse();

//        boolean hasStoreB = allPrices.stream().anyMatch(p -> p.getId().equals(priceInStoreB));
//        assertThat(hasStoreB).isTrue();
//    }

//    @Test
//    void testFindByProductAndStoreAndDate() {
//        UUID productId = UUID.randomUUID();
//        UUID storeId = UUID.randomUUID();

//        createProduct(productId, "Date Product");

//        OffsetDateTime todayAtNoon = OffsetDateTime.now(ZoneOffset.UTC)
//                .withHour(12).withMinute(0).withSecond(0).withNano(0);

//        PriceEntity price = PriceEntity.builder()
//                .id(UUID.randomUUID())
//                .productId(productId)
//                .storeId(storeId)
//                .price(BigDecimal.TEN)
//                .currency("BGN")
//                .createdAt(todayAtNoon)
//                .build();

//        priceMapper.insert(price);

//        List<PriceEntity> result = priceMapper.findByProductAndStoreAndDate(
//                productId,
//                storeId,
//                todayAtNoon.toLocalDate()
//        );

//        assertThat(result).isNotEmpty();
//        assertThat(result.get(0).getId()).isEqualTo(price.getId());
//        assertThat(result.get(0).getPrice()).isEqualByComparingTo(BigDecimal.TEN);
//    }

//    private void createProduct(UUID productId, String name) {
//        ProductEntity p = ProductEntity.builder()
//                .id(productId)
//                .name(name)
//                .sku("SKU-" + productId)
//                .build();
//        productMapper.insert(p);
//    }

//    private UUID createPrice(UUID storeId, UUID productId) {
//        UUID priceId = UUID.randomUUID();
//        PriceEntity p = PriceEntity.builder()
//                .id(priceId)
//                .productId(productId)
//                .storeId(storeId)
//                .price(BigDecimal.ONE)
//                .currency("BGN")
//                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
//                .build();
//        priceMapper.insert(p);
//        return priceId;
//    }
//}