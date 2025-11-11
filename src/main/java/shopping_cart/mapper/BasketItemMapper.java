package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.BasketItemEntity;

import java.util.List;
import java.util.UUID;

@Mapper
public interface BasketItemMapper {
    @Select("SELECT * FROM basket_item WHERE basket_id = #{basketId}")
    List<BasketItemEntity> findByBasketId(UUID basketId);

    @Insert("""
        INSERT INTO basket_item (id, basket_id, product_id, quantity, added_by, created_at)
        VALUES (#{id}, #{basketId}, #{productId}, #{quantity}, #{addedBy}, #{createdAt})
    """)
    void insert(BasketItemEntity item);
}

