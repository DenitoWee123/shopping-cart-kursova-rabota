package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.PriceEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper
public interface PriceMapper {

    @Select("SELECT * FROM prices")
    List<PriceEntity> getAll();

    @Insert("""
     INSERT INTO prices (id, product_id, store_id, price, timestamp)
     VALUES (#{id, typeHandler=shopping_cart.config.UUIDTypeHandler},
             #{productId, typeHandler=shopping_cart.config.UUIDTypeHandler},
             #{storeId, typeHandler=shopping_cart.config.UUIDTypeHandler},
             #{price}, #{timestamp})
     """)
    void insert(PriceEntity price);

    /**
     * Намира всички цени за даден продукт + магазин + конкретна дата (без час)
     * Използва се, за да не записваме една и съща цена по 10 пъти на ден
     */
    @Select("""
     SELECT * FROM prices
     WHERE product_id = #{productId}
       AND store_id = #{storeId}
       AND DATE(timestamp) = #{date}
     """)
    List<PriceEntity> findByProductAndStoreAndDate(
            @Param("productId") UUID productId,
            @Param("storeId") UUID storeId,
            @Param("date") LocalDate date);
}