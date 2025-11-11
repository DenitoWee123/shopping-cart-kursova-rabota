package shopping_cart.mapper;

import org.apache.ibatis.annotations.*;
import shopping_cart.entity.PriceReportEntity;
import java.util.List;
import java.util.UUID;

@Mapper
public interface PriceReportMapper {
    @Select("SELECT * FROM price_report")
    List<PriceReportEntity> getAll();

    @Insert("""
        INSERT INTO price_report (id, user_id, product_id, store_id, price, created_at)
        VALUES (#{id}, #{userId}, #{productId}, #{storeId}, #{price}, #{createdAt})
    """)
    void insert(PriceReportEntity report);
}
