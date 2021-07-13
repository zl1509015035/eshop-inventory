package com.roncoo.eshop.inventory.mapper;

import com.roncoo.eshop.inventory.model.ProductInventory;
import org.apache.ibatis.annotations.Param;

/**
 * 库存数量DAO
 */
public interface ProductInventoryMapper {
    /**
     * 更新库存数量
     */
    void updateProductInventory(ProductInventory productInventory);

    /**
     * 根据商品id查询商品库存信息
     * @param productId
     * @return
     */
    ProductInventory findProductInventroy(@Param("productId") Integer productId);
}
