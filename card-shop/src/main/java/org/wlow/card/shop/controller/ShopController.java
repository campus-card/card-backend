package org.wlow.card.shop.controller;

import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;
import org.wlow.card.shop.service.ShopService;
import org.wlow.card.data.data.DTO.Response;

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
                                   Double price,
                               @RequestParam
                                   @PositiveOrZero(message = "商品库存不能为负数")
                                   Integer store) {
        return shopService.addProduct(name, description, price, store);
    }

    /**
     * 删除商品
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
                                      Double price,
                                  @RequestParam(required = false)
                                      @Nullable
                                      @PositiveOrZero(message = "商品库存不能为负数")
                                      Integer store) {
        return shopService.modifyProduct(id, name, description, price, store);
    }

    /**
     * 查询自己的商品列表 <br>
     * order: 1->按上架时间排序 2->按价格排序 3->按库存排序
     */
    @GetMapping("/getProductList")
    public Response getProductList(@RequestParam
                                       @Min(value = 1, message = "页码不能小于1")
                                       Integer page,
                                   @RequestParam
                                       @Min(value = 1, message = "每页数量不能小于1")
                                       @Max(value = 100, message = "每页数量不能大于100")
                                       Integer pageSize,
                                   @RequestParam(defaultValue = "false")
                                       Boolean isAsc,
                                   @RequestParam(defaultValue = "1")
                                       Integer order) {
        return shopService.getProductList(page, pageSize, order, isAsc);
    }
}
