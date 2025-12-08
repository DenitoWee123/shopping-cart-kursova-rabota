package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.BasketItemEntity;

import java.util.List;
import java.util.UUID;

@Mapper
public interface BasketItemMapper {
    @Select("SELECT * FROM basket_item WHERE basket_id = #{basketId, typeHandler=shopping_cart.config.UUIDTypeHandler}")
    List<BasketItemEntity> findByBasketId(UUID basketId);

    @Insert("""
        INSERT INTO basket_item (id,, basket_id, product_id, quantity, added_by, created_at)
        VALUES (#{id, typeHandler=shopping_cart.config.UUIDTypeHandler}, 
        #{basketId, typeHandler=shopping_cart.config.UUIDTypeHandler}, 
        #{productId, typeHandler=shopping_cart.config.UUIDTypeHandler}, 
        #{quantity}, 
        #{addedBy, typeHandler=shopping_cart.config.UUIDTypeHandler}, #{createdAt})
    """)
    void insert(BasketItemEntity item);
}

