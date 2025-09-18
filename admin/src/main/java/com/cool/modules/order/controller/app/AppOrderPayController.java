package com.cool.modules.order.controller.app;

import cn.hutool.core.net.URLDecoder;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.request.R;
import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.pojo.KioskOrderPojo;
import com.cool.modules.kiosk.service.KioskOrderService;
import com.cool.modules.kiosk.ws.InstructionService;
import com.cool.modules.order.pay.AliPayService;
import com.cool.modules.order.pay.WeChatPayService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 订单信息
 */
@Tag(name = "订单信息", description = "订单信息")
@CoolRestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class AppOrderPayController {

    private final WeChatPayService weChatPayService;

    private final AliPayService aliPayService;

    private final KioskOrderService kioskOrderService;

    private final InstructionService instructionService;

    @PostMapping("/create")
    @ResponseBody
    @TokenIgnore
    public R<KioskOrderEntity> create(@RequestBody KioskOrderPojo pojo) {
        return R.ok(kioskOrderService.create(pojo));
    }

    @PostMapping("/updateOrder")
    @ResponseBody
    @TokenIgnore
    public R<KioskOrderEntity> updateOrder(@RequestBody KioskOrderPojo pojo) {
        return R.ok(kioskOrderService.updateOrder(pojo));
    }

    @RequestMapping(value = "/native/notify_url", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    @TokenIgnore
    public String nativeNotify() {
        return weChatPayService.nativeNotify();
    }

    @RequestMapping(value = "/ali/h5/notify_url", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    @TokenIgnore
    public String aliH5Notify(HttpServletRequest request) throws IOException {
        Map<String, String> params = new HashMap<>();
        try {
            String body = request.getReader().lines().collect(Collectors.joining("&"));
            System.out.println("支付宝回调原始报文：" + body);

            // 手动解析 key=value&key=value 格式
            for (String kv : body.split("&")) {
                String[] pair = kv.split("=", 2);
                if (pair.length == 2) {
                    String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                }
            }
            System.out.println("支付宝回调参数：" + params);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return aliPayService.aliH5Notify(params);
    }

    // 发送测试
    @GetMapping("/sendTest")
    @ResponseBody
    @TokenIgnore
    public R<String> sendTest(@RequestParam String orderNum) {
        instructionService.sendPrintInstruction(orderNum);
        return R.ok("success");
    }
}