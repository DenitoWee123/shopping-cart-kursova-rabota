package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.RetailerEntity;

import java.util.List;
import java.util.UUID;

@Mapper
public interface RetailerMapper {
    @Select("SELECT * FROM retailer")
    List<RetailerEntity> getAll();

    @Insert("""
        INSERT INTO retailer (id, name, website_url, created_at)
        VALUES (#{id}, #{name}, #{websiteUrl}, #{createdAt})
    """)
    void insert(RetailerEntity retailer);
}
