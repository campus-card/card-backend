package org.wlow.card.student;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import org.wlow.card.data.data.DTO.DTOCard;
import org.wlow.card.data.data.DTO.DTOPage;
import org.wlow.card.data.data.DTO.Response;
import org.wlow.card.data.data.PO.Product;
import org.wlow.card.data.data.PO.PurchaseRecord;

import java.math.BigDecimal;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Resource
    private StudentService studentService;

    /**
     * 开通校园卡
     */
    @PostMapping("/registerCard")
    public Response<DTOCard> registerCard(@RequestParam
                                     @Pattern(regexp = "^[0-9]{6}$", message = "支付密码必须为6位数字")
                                     String password) {
        return studentService.registerCard(password);
    }

    /**
     * 查询校园卡余额 (获取校园卡信息)
     */
    @GetMapping("/getCardInfo")
    public Response<DTOCard> getCardInfo() {
        return studentService.getCardInfo();
    }

    /**
     * 校园卡充值
     */
    @PostMapping("/recharge")
    public Response<String> recharge(@RequestParam
                                 @Digits(integer = 10, fraction = 2, message = "金额最多有2位小数, 整数部分最多10位")
                                 @Positive(message = "金额必须为正数")
                                 BigDecimal amount) {
        return studentService.recharge(amount);
    }

    /**
     * 校园卡消费, 购买商品
     */
    @PostMapping("/purchase")
    public Response<DTOCard> purchase(@RequestParam
                                 Integer productId,
                             @RequestParam
                                 @Positive(message = "购买数量必须为正数")
                                 @Max(value = 100, message = "购买数量最多为100")
                                 Integer count,
                             @RequestParam
                                 @Pattern(regexp = "^[0-9]{6}$", message = "支付密码必须为6位数字")
                                 String password) {
        return studentService.purchase(productId, count, password);
    }

    /**
     * 学生查询校园卡消费记录
     */
    @GetMapping("/getPurchaseRecord")
    public Response<DTOPage<PurchaseRecord>> getPurchaseRecord(@RequestParam
                                          @Positive(message = "页码必须为正数")
                                          Integer page,
                                                               @RequestParam
                                          @Positive(message = "每页数量必须为正数")
                                          @Max(value = 100, message = "每页数量最多为100")
                                          Integer pageSize) {
        return studentService.getPurchaseRecord(page, pageSize);
    }

    /**
     * 学生获取商品列表 <br>
     * order: 1->按上架时间排序 2->按价格排序 3->按库存排序
     */
    @GetMapping("/getProductList")
    public Response<DTOPage<Product>> getProductList(@RequestParam
                                       @Positive(message = "页码必须为正数")
                                       Integer page,
                                                     @RequestParam
                                       @Positive(message = "每页数量必须为正数")
                                       @Max(value = 100, message = "每页数量不能大于100")
                                       Integer pageSize,
                                                     @RequestParam(defaultValue = "1")
                                       @Positive(message = "排序字段必须为正数")
                                       Integer order,
                                                     @RequestParam(defaultValue = "false")
                                       Boolean isAsc) {
        return studentService.getProductList(page, pageSize, order, isAsc);
    }
}
