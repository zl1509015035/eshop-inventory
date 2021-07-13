package com.roncoo.eshop.inventory.service;

import com.roncoo.eshop.inventory.model.ProductInventory;

/**
 * 商品库存service接口
 */
public interface ProductInventoryService {

    /**
     * 更新商品库存
     * @param productInventory
     */
    void updateProductInventory(ProductInventory productInventory);

    /**
     * 删除redis中的商品库存缓存
     * @param productInventory
     */
    void removeProductInventoryCache(ProductInventory productInventory);
}
