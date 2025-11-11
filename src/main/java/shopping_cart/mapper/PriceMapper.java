package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.PriceEntity;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PriceMapper {
    @Select("SELECT * FROM price")
    List<PriceEntity> getAll();

    @Insert("""
        INSERT INTO price (id, product_id, store_id, price, timestamp)
        VALUES (#{id}, #{productId}, #{storeId}, #{price}, #{timestamp})
    """)
    void insert(PriceEntity price);
}

