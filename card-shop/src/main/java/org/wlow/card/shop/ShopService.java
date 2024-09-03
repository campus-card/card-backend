package org.wlow.card.shop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.wlow.card.data.data.DTO.DTOPage;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.Product;
import org.wlow.card.data.data.PO.PurchaseRecord;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.data.mapper.ProductMapper;
import org.wlow.card.data.mapper.PurchaseRecordMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ShopService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private PurchaseRecordMapper purchaseRecordMapper;

    public Response addProduct(String name, String description, BigDecimal price, Integer store) {
        Product product = Product.builder()
                .name(name)
                .shopId(CurrentUser.getId())
                .description(description)
                .price(price)
                .store(store)
                .build();
        int res = productMapper.insert(product);
        if (res == 1) {
            return Response.ok();
        } else {
            return Response.failure(500, "添加商品失败");
        }
    }

    public Response deleteProduct(Integer id) {
        Product target = productMapper.selectById(id);
        if (target == null) {
            return Response.failure(404, "商品不存在");
        }
        if (!Objects.equals(target.getShopId(), CurrentUser.getId())) {
            return Response.failure(403, "不能删除其他商家的商品");
        }
        int res = productMapper.deleteById(id);
        if (res == 1) {
            return Response.ok();
        } else {
            return Response.failure(500, "删除商品失败");
        }
    }

    public Response modifyProduct(Integer id, String name, String description, BigDecimal price, Integer store) {
        Product target = productMapper.selectById(id);
        if (target == null) {
            return Response.failure(404, "商品不存在");
        }
        if (!Objects.equals(target.getShopId(), CurrentUser.getId())) {
            return Response.failure(403, "不能修改其他商家的商品信息");
        }

        UpdateWrapper<Product> update = new UpdateWrapper<>();
        update.eq("id", id);
        if (name != null) {
            if (name.isBlank()) {
                return Response.failure(400, "商品名称不能为空");
            }
            update.set("name", name);
        }
        update.set(description != null, "description", description);
        update.set(price != null, "price", price);
        update.set(store != null, "store", store);
        update.set("modify_time", LocalDateTime.now());
        int res = productMapper.update(update);
        if (res == 1) {
            return Response.ok();
        } else {
            return Response.failure(500, "修改商品信息失败");
        }
    }

    public Response getProductList(Integer page, Integer pageSize, Integer order, Boolean isAsc) {
        Page<Product> productPage = Page.of(page, pageSize);
        QueryWrapper<Product> query = new QueryWrapper<>();
        query.eq("shop_id", CurrentUser.getId());
        query.orderBy(true, isAsc, switch (order) {
            case 1 -> "upload_time";
            case 2 -> "price";
            case 3 -> "store";
            default -> "id";
        });
        productMapper.selectPage(productPage, query);
        return Response.success(new DTOPage<>(
                productPage.getTotal(),
                productPage.getPages(),
                productPage.getRecords()
        ));
    }

    public Response getSalesRecord(Integer page, Integer pageSize) {
        int shopId = CurrentUser.getId();
        IPage<PurchaseRecord> recordPage = Page.of(page, pageSize);
        recordPage = purchaseRecordMapper.selectByShopId(shopId, recordPage);
        return Response.success(new DTOPage<>(
                recordPage.getTotal(),
                recordPage.getPages(),
                recordPage.getRecords()
        ));
    }
}
