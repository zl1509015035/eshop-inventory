package com.roncoo.eshop.inventory.mapper;

import com.roncoo.eshop.inventory.model.ProductInventory;

/**
 * 库存数量DAO
 */
public interface ProductInventoryMapper {
    /**
     * 更新库存数量
     */
    public void updateProductInventory(ProductInventory productInventory);
}
