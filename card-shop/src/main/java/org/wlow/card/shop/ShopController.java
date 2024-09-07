package org.wlow.card.shop;

import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wlow.card.data.data.DTO.Response;

import java.math.BigDecimal;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Resource
    private ShopService shopService;

    /**
     * 添加商品
     */
    @PostMapping("/addProduct")
    public Response addProduct(@RequestParam
                                   @NotBlank(message = "商品名称不能为空")
                                   String name,
                               @RequestParam
                                   @Length(min = 10, message = "商品描述不得少于10字")
                                   String description,
                               @RequestParam
                                   @PositiveOrZero(message = "商品价格不能为负数")
                                   @Digits(fraction = 2, integer = 10, message = "商品价格最多有两位小数, 整数部分最多10位")
                                   BigDecimal price,
                               @RequestParam
                                   @PositiveOrZero(message = "商品库存不能为负数")
                                   Integer store,
                               @RequestParam(required = false)
                                   MultipartFile cover) {
        return shopService.addProduct(name, description, price, store, cover);
    }

    /**
     * 删除商品. 如果商品有图片则删除对应图片
     */
    @DeleteMapping("/deleteProduct")
    public Response deleteProduct(@RequestParam Integer id) {
        return shopService.deleteProduct(id);
    }

    /**
     * 修改商品信息
     */
    @PatchMapping("/modifyProduct")
    public Response modifyProduct(@RequestParam Integer id,
                                  @RequestParam(required = false)
                                      String name,
                                  @RequestParam(required = false)
                                      @Nullable
                                      @Length(min = 10, message = "商品描述不得少于10字")
                                      String description,
                                  @RequestParam(required = false)
                                      @Nullable
                                      @PositiveOrZero(message = "商品价格不能为负数")
                                      @Digits(fraction = 2, integer = 10, message = "商品价格最多有两位小数, 整数部分最多10位")
                                      BigDecimal price,
                                  @RequestParam(required = false)
                                      @Nullable
                                      @PositiveOrZero(message = "商品库存不能为负数")
                                      Integer store,
                                  @RequestParam(required = false)
                                      MultipartFile cover) {
        return shopService.modifyProduct(id, name, description, price, store, cover);
    }

    /**
     * 查询自己的商品列表 <br>
     * order: 1->按上架时间排序 2->按价格排序 3->按库存排序
     */
    @GetMapping("/getProductList")
    public Response getProductList(@RequestParam
                                       @Positive(message = "页码必须为正数")
                                       Integer page,
                                   @RequestParam
                                       @Positive(message = "每页数量必须为正数")
                                       @Max(value = 100, message = "每页数量不能大于100")
                                       Integer pageSize,
                                   @RequestParam(defaultValue = "false")
                                       Boolean isAsc,
                                   @RequestParam(defaultValue = "1")
                                       Integer order) {
        return shopService.getProductList(page, pageSize, order, isAsc);
    }

    /**
     * 商家查询商品销售记录
     */
    @GetMapping("/getSalesRecord")
    public Response getSalesRecord(@RequestParam
                                       @Positive(message = "页码必须为正数")
                                       Integer page,
                                   @RequestParam
                                       @Positive(message = "每页数量必须为正数")
                                       @Max(value = 100, message = "每页数量不能大于100")
                                       Integer pageSize) {
        return shopService.getSalesRecord(page, pageSize);
    }
}
