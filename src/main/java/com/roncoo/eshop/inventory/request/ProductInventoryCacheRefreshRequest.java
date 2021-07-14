package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;

/**
 * 重新加载商品库存的缓存
 */
public class ProductInventoryCacheRefreshRequest implements Request {

    /**
     * 商品库存
     */
    private Integer productId;
    /**
     * 商品库存Service
     */
    private ProductInventoryService productInventoryService;

    /**
     * 是否强制刷新缓存
     */
    private boolean forceRefresh;


    public ProductInventoryCacheRefreshRequest(Integer productId,
                                               ProductInventoryService productInventoryService,
                                                boolean forceRefresh) {
        this.productId = productId;
        this.productInventoryService = productInventoryService;
        this.forceRefresh = forceRefresh;
    }

    @Override
    public void process() {
        //从数据库中查询最新商品库存数量
        ProductInventory productInventory = productInventoryService.findProductInventory(productId);
        //将最新的商品库存数量刷新到redis缓存中去
        productInventoryService.setProductInventoryCache(productInventory);
    }

    @Override
    public Integer getProductId() {
        return productId;
    }

    public boolean isForceRefresh() {
        return forceRefresh;
    }
}
