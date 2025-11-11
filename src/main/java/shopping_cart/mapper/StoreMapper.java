package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.StoreEntity;

import java.util.List;
import java.util.UUID;

@Mapper
public interface StoreMapper {
    @Select("SELECT * FROM store")
    List<StoreEntity> getAll();

    @Insert("""
        INSERT INTO store (id, retailer_id, address, latitude, longitude, created_at)
        VALUES (#{id}, #{retailerId}, #{address}, #{latitude}, #{longitude}, #{createdAt})
    """)
    void insert(StoreEntity store);
}

