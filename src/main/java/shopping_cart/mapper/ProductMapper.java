package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.ProductEntity;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM products")
    List<ProductEntity> getAll();

    @Insert("""
        INSERT INTO products (id, name, category, description, sku, created_at)
        VALUES (#{id, typeHandler=shopping_cart.config.UUIDTypeHandler}, #{name}, #{category}, #{description}, #{sku}, #{createdAt})
    """)
    void insert(ProductEntity product);
    @Select("SELECT * FROM products WHERE sku = #{sku} LIMIT 1")
    ProductEntity findBySku(@Param("sku") String sku);
}
