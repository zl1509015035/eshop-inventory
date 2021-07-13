package com.roncoo.eshop.inventory.request;


import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;

/**
 * 一个商品发生了交易，那么就要修改这个商品对应的库存
 * <p>
 * 此时就会发送请求过来，要求修改库存，那么这个可能就是所谓的 data update request ，数据更新请求
 * <p>
 * cache aside pattern
 * <p>
 * （1）删除缓存
 * （2）更新数据
 */
public class InventoryCntDBUpdateRequest implements Request {

    /**
     * 商品库存
     */
    private ProductInventory productInventory;
    /**
     * 商品库存Service
     */
    private ProductInventoryService productInventoryService;


    public InventoryCntDBUpdateRequest(ProductInventory productInventory,
                                       ProductInventoryService productInventoryService) {
        this.productInventory = productInventory;
        this.productInventoryService = productInventoryService;
    }

    @Override
    public void process() {
        // 删除redis中的缓存
        productInventoryService.removeProductInventoryCache(productInventory);
        // 修改数据库中的库存
        productInventoryService.updateProductInventory(productInventory);
    }
}
