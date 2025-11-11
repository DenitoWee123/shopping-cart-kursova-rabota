package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.ProductEntity;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM product")
    List<ProductEntity> getAll();

    @Insert("""
        INSERT INTO product (id, name, category, description, sku, created_at)
        VALUES (#{id}, #{name}, #{category}, #{description}, #{sku}, #{createdAt})
    """)
    void insert(ProductEntity product);
}
