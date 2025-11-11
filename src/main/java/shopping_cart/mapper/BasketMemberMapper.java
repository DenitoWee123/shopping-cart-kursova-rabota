package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.BasketMemberEntity;

import java.util.List;
import java.util.UUID;

@Mapper
public interface BasketMemberMapper {
    @Select("SELECT * FROM basket_member WHERE basket_id = #{basketId}")
    List<BasketMemberEntity> findByBasketId(UUID basketId);

    @Insert("""
        INSERT INTO basket_member (id, basket_id, user_id, role, created_at)
        VALUES (#{id}, #{basketId}, #{userId}, #{role}, #{createdAt})
    """)
    void insert(BasketMemberEntity member);
}


