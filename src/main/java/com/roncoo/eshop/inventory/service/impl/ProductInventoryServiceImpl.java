package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.dao.RedisDAO;
import com.roncoo.eshop.inventory.mapper.ProductInventoryMapper;
import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.ReportAsSingleViolation;

@Service("productInventoryService")
public class ProductInventoryServiceImpl implements ProductInventoryService {

    @Resource
    private ProductInventoryMapper productInventoryMapper;

    @Resource
    private RedisDAO redisDAO;

    @Override
    public void updateProductInventory(ProductInventory productInventory) {
        productInventoryMapper.updateProductInventory(productInventory);
        System.out.println("=========日志==========:已修改数据库中的库存=" + productInventory.getProductId()+",商品库存数量="+productInventory.getInventoryCnt());
    }

    @Override
    public void removeProductInventoryCache(ProductInventory productInventory) {
        String key = "product:inventory:"+productInventory.getProductId();
        System.out.println("=========日志==========:删除redis中的缓存，key="+key);
        redisDAO.delete(key);
    }

    @Override
    public ProductInventory findProductInventory(Integer productId) {
        return productInventoryMapper.findProductInventroy(productId);
    }

    @Override
    public void setProductInventoryCache(ProductInventory productInventory) {
        String key = "product:inventory:"+productInventory.getProductId();
        redisDAO.set(key,String.valueOf(productInventory.getInventoryCnt()));
    }

    @Override
    public ProductInventory getProductInventoryCache(Integer productId) {
        Long inventoryCnt = 0L;

        String key = "product:inventory:"+productId;
        String result = redisDAO.get(key);
        //如果读到了，放进缓存
        if(null != result && !"".equals(result)){
            try{
                inventoryCnt = Long.valueOf(result);
            }catch (Exception e){
                e.printStackTrace();
            }
            return new ProductInventory(productId,inventoryCnt);
        }
        //如果没读到，返回null
        return null;
    }
}
