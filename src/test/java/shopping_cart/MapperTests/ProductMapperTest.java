package shopping_cart.MapperTests;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import shopping_cart.entity.ProductEntity;
import shopping_cart.mapper.ProductMapper;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    void testInsertAndFindBySku() {
        UUID id = UUID.randomUUID();
        String sku = "TEST-PROD-001";
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
        ProductEntity newProduct = ProductEntity.builder()
                .id(id)
                .name("Тестов Продукт")
                .category("Печива")
                .description("Описание на продукта")
                .sku(sku)
                .createdAt(now)
                .build();

        productMapper.insert(newProduct);

        ProductEntity foundProduct = productMapper.findBySku(sku);

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(id);
        assertThat(foundProduct.getName()).isEqualTo("Тестов Продукт");
        assertThat(foundProduct.getCategory()).isEqualTo("Печива");
        assertThat(foundProduct.getDescription()).isEqualTo("Описание на продукта");
        assertThat(foundProduct.getSku()).isEqualTo(sku);

        assertThat(foundProduct.getCreatedAt()).isNotNull();
    }

    @Test
    void testFindBySku_NotFound() {
        ProductEntity result = productMapper.findBySku("NON_EXISTING_SKU");

        assertThat(result).isNull();
    }
}