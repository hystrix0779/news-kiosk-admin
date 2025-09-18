package com.cool.modules.order.pay;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.cool.core.config.pay.AliPayConfig;
import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.mapper.KioskOrderMapper;
import com.cool.modules.kiosk.ws.InstructionService;
import com.cool.modules.order.enums.OrderStatusEnum;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import com.mybatisflex.core.query.QueryWrapper;
import com.alipay.api.AlipayApiException;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/08/25/11.
 * @annotation :
 */
@Service
@Slf4j
public class AliPayService {

    @Resource
    private AliPayConfig aliPayConfig;

    @Resource
    private KioskOrderMapper kioskOrderMapper;

    @Resource
    private InstructionService instructionService;

    @SneakyThrows
    AliPayApiConfig getApiConfig() {
        // File appCertFile = copyToTempFile(new
        // ClassPathResource(aliPayConfig.getAppCertPath()));
        // File aliPayCertFile = copyToTempFile(new
        // ClassPathResource(aliPayConfig.getAliPayCertPath()));
        // File aliPayRootCertFile = copyToTempFile(new
        // ClassPathResource(aliPayConfig.getAliPayRootCertPath()));
        AliPayApiConfig aliPayApiConfig;
        try {
            aliPayApiConfig = AliPayApiConfigKit.getApiConfig(aliPayConfig.getAppId());
        } catch (Exception e) {
            aliPayApiConfig = AliPayApiConfig.builder()
                    .setAppId(aliPayConfig.getAppId())
                    .setAliPayPublicKey(aliPayConfig.getPublicKey())
                    // .setAppCertPath(appCertFile.getAbsolutePath())
                    // .setAliPayCertPath(aliPayCertFile.getAbsolutePath())
                    // .setAliPayRootCertPath(aliPayRootCertFile.getAbsolutePath())
                    .setCharset("UTF-8")
                    .setPrivateKey(aliPayConfig.getPrivateKey())
                    .setServiceUrl(aliPayConfig.getServerUrl())
                    .setSignType("RSA2")
                    // 普通公钥方式
                    .build();
            // 证书模式
            // .buildByCert();

        }
        return aliPayApiConfig;
    }

    @SneakyThrows
    public void wapPay(KioskOrderEntity kioskOrderEntity) {
        String body = kioskOrderEntity.getTitle();
        String totalAmount = kioskOrderEntity.getPrice() + "";
        String passBackParams = "1";
        String returnUrl = aliPayConfig.getReturnUrl() + "?orderNum=" + kioskOrderEntity.getOrderNum() + "&price="
                + totalAmount;
        String notifyUrl = aliPayConfig.getNotifyUrl();
        AliPayApiConfigKit.putApiConfig(getApiConfig());

        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setBody(body);
        model.setSubject(body + "业务支付");
        model.setTotalAmount(totalAmount);
        model.setPassbackParams(passBackParams);
        String outTradeNo = kioskOrderEntity.getOrderNum();
        System.out.println("wap outTradeNo>" + outTradeNo);
        model.setOutTradeNo(outTradeNo);
        model.setProductCode("QUICK_WAP_PAY");

        String payStr = AliPayApi.wapPayStr(model, returnUrl, notifyUrl);
        System.out.println("payStr>" + payStr);
        kioskOrderEntity.setQrCode(payStr);
    }

    public String aliH5Notify(Map<String, String> params) {
        try {
            // 获取支付宝POST过来反馈信息
            AliPayApiConfigKit.putApiConfig(getApiConfig());
            AliPayApiConfig config = AliPayApiConfigKit.getAliPayApiConfig();
            // System.out.println(JSON.toJSONString(request.getParameterMap()));

            // Map<String, String> params = AliPayApi.toMap(request);
            // System.out.println("params>" + params);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
            AliPayApiConfigKit.putApiConfig(getApiConfig());
            boolean verifyResult = AlipaySignature.rsaCheckV1(params, config.getAliPayPublicKey(), "UTF-8", "RSA2");

            if (verifyResult) {
                String outTradeNo = params.get("out_trade_no");
                long count = kioskOrderMapper.selectCountByQuery(QueryWrapper.create()
                        .eq(KioskOrderEntity::getOrderNum, outTradeNo)
                        .eq(KioskOrderEntity::getStatus, OrderStatusEnum.AWAITING_DELIVERY.getCode())
                        .eq(KioskOrderEntity::getPayType, 2));
                if (count > 0) {
                    return "success";
                }
                System.out.println("notify_url 验证成功succcess");

                KioskOrderEntity order = new KioskOrderEntity();
                order.setStatus(OrderStatusEnum.AWAITING_DELIVERY.getCode());
                order.setPayType(2);
                order.setPayTime(LocalDateTime.now());
                kioskOrderMapper.updateByQuery(order,
                        QueryWrapper.create().eq(KioskOrderEntity::getOrderNum, outTradeNo));
                instructionService.sendPrintInstruction(outTradeNo);
                return "success";
            } else {
                System.out.println("notify_url 验证失败");
                return "failure";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "failure";
        }
    }

    // private static File copyToTempFile(ClassPathResource resource) throws
    // IOException {
    // File tempFile = File.createTempFile("alipay-", ".crt");
    // tempFile.deleteOnExit();
    // try (InputStream in = resource.getInputStream();
    // OutputStream out = new FileOutputStream(tempFile)) {
    // byte[] buffer = new byte[1024];
    // int len;
    // while ((len = in.read(buffer)) != -1) {
    // out.write(buffer, 0, len);
    // }
    // }
    // return tempFile;
    // }
}
