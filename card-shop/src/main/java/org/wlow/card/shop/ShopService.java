package org.wlow.card.shop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.DTO.DTOPage;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.FileEntry;
import org.wlow.card.data.data.PO.Product;
import org.wlow.card.data.data.PO.PurchaseRecord;
import org.wlow.card.data.data.constant.CurrentUser;
import org.wlow.card.data.mapper.FileEntryMapper;
import org.wlow.card.data.mapper.ProductMapper;
import org.wlow.card.data.mapper.PurchaseRecordMapper;
import org.wlow.card.file.FileService;
import org.wlow.card.file.exception.FileSystemException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ShopService {
    @Value("${file-service.server-url}")
    private String serverUrl;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${file-service.virtual-path.image}")
    private String imageVirtualPath;
    @Value("${file-service.local-path.dir.image}")
    private String imageLocalDir;

    @Resource
    private ProductMapper productMapper;
    @Resource
    private PurchaseRecordMapper purchaseRecordMapper;
    @Resource
    private FileService fileService;
    @Resource
    private FileEntryMapper fileEntryMapper;

    public Response addProduct(String name, String description, BigDecimal price, Integer store, MultipartFile cover) {
        Product product = Product.builder()
                .name(name)
                .shopId(CurrentUser.getId())
                .description(description)
                .price(price)
                .store(store)
                .build();
        // 如果有图片则保存图片
        if (cover != null) {
            FileEntry coverFile = fileService.putImageEntry(cover);
            product.setCoverId(coverFile.getId());
        }
        int res = productMapper.insert(product);
        if (res == 1) {
            return Response.ok();
        } else {
            return Response.failure(500, "添加商品失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Response deleteProduct(Integer id) {
        Product target = productMapper.selectById(id);
        if (target == null) {
            return Response.failure(404, "商品不存在");
        }
        if (!Objects.equals(target.getShopId(), CurrentUser.getId())) {
            return Response.failure(403, "不能删除其他商家的商品");
        }
        // 先删除商品对应的图片
        FileEntry cover = fileEntryMapper.selectById(target.getCoverId());
        // 如果商品有图片则删除图片
        if (cover != null) {
            boolean delete = fileService.deleteFile(cover);
            if (!delete) {
                throw new FileSystemException("删除本地商品图片文件失败");
            }
        }
        int res = productMapper.deleteById(id);
        if (res == 1) {
            return Response.ok();
        } else {
            return Response.failure(500, "删除商品失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Response modifyProduct(Integer id, String name, String description, BigDecimal price, Integer store, MultipartFile cover) {
        Product target = productMapper.selectById(id);
        if (target == null) {
            return Response.failure(404, "商品不存在");
        }
        if (!Objects.equals(target.getShopId(), CurrentUser.getId())) {
            return Response.failure(403, "不能修改其他商家的商品信息");
        }

        UpdateWrapper<Product> update = new UpdateWrapper<>();
        update.eq("id", id);

        // 如果有图片则修改图片
        if (cover != null) {
            // 如果已经有旧的封面图片的话得先删除
            Integer oldCoverId = target.getCoverId();
            if (oldCoverId != null) {
                FileEntry oldCover = fileEntryMapper.selectById(oldCoverId);
                if (!fileService.deleteFile(oldCover)) {
                    throw new FileSystemException("删除本地商品旧图片文件失败");
                }
            }
            FileEntry coverFile = fileService.putImageEntry(cover);
            target.setCoverId(coverFile.getId());
            update.set("cover_id", coverFile.getId());
        }

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
        IPage<Product> productPage = Page.of(page, pageSize);
        QueryWrapper<Product> query = new QueryWrapper<>();
        query.eq("shop_id", CurrentUser.getId());
        query.orderBy(true, isAsc, switch (order) {
            case 1 -> "upload_time";
            case 2 -> "price";
            case 3 -> "store";
            default -> "id";
        });
        // 通过application.yml配置内容将图片url的前缀拼接传入
        productPage = productMapper.selectPageWithCoverUrl(productPage, query,
                // 拼接图片url的前缀
                serverUrl + contextPath + imageVirtualPath + imageLocalDir + "/"
        );

        return Response.success(new DTOPage<>(
                productPage.getTotal(),
                productPage.getPages(),
                productPage.getRecords()
        ));
    }

    public Response getSalesRecord(Integer page, Integer pageSize) {
        int shopId = CurrentUser.getId();
        IPage<PurchaseRecord> recordPage = Page.of(page, pageSize);
        QueryWrapper<PurchaseRecord> query = new QueryWrapper<>();
        query.eq("shop.id", shopId);
        query.orderByDesc("purchase_time");
        recordPage = purchaseRecordMapper.selectPurchaseRecord(recordPage, query);
        return Response.success(new DTOPage<>(
                recordPage.getTotal(),
                recordPage.getPages(),
                recordPage.getRecords()
        ));
    }
}
