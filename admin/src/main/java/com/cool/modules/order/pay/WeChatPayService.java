package com.cool.modules.order.pay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.cool.core.config.pay.WxNativeConfig;
import com.cool.core.exception.CoolException;
import com.cool.core.util.IPUtils;
import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.mapper.KioskOrderMapper;
import com.cool.modules.kiosk.ws.InstructionService;
import com.cool.modules.order.enums.OrderStatusEnum;
import com.cool.modules.order.pojo.H5SceneInfo;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.enums.TradeType;
import com.ijpay.core.kit.HttpKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.model.UnifiedOrderModel;
import com.mybatisflex.core.query.QueryWrapper;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import java.awt.image.BufferedImage;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/08/19/17.
 * @annotation : 微信支付服务
 */
@Service
@Slf4j
public class WeChatPayService {

    @Resource
    private HttpServletRequest request;
    @Resource
    private IPUtils ipUtils;
    @Resource
    private WxNativeConfig wxNativeConfig;
    @Resource
    private KioskOrderMapper kioskOrderMapper;
    @Resource
    private InstructionService instructionService;

    /**
     * Native支付
     * 
     * @param orderInfoEntity 订单信息
     * @return 二维码图片base64编码
     * @throws IOException 异常
     */
    public void nativePay(KioskOrderEntity orderInfoEntity) throws IOException {
        String ip = ipUtils.getIpAddr(request);
        if (StrUtil.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        Map<String, String> params = UnifiedOrderModel
                .builder()
                .appid(wxNativeConfig.getAppId())
                .mch_id(wxNativeConfig.getMchId())
                .nonce_str(WxPayKit.generateStr())
                .body(orderInfoEntity.getTitle())
                .out_trade_no(orderInfoEntity.getOrderNum())
                // .total_fee("1")
                .total_fee(orderInfoEntity.getPrice().multiply(new BigDecimal(100))
                        .setScale(0, RoundingMode.HALF_UP)
                        .intValue() + "")
                .spbill_create_ip(ip)
                .notify_url(wxNativeConfig.getNotifyUrl())
                .trade_type(TradeType.NATIVE.getTradeType())
                .build()
                .createSign(wxNativeConfig.getPartnerKey(), SignType.HMACSHA256);
        String xmlResult = WxPayApi.pushOrder(false, params);
        log.info("统一下单:" + xmlResult);

        Map<String, String> result = WxPayKit.xmlToMap(xmlResult);

        String returnCode = result.get("return_code");
        String returnMsg = result.get("return_msg");
        System.out.println(returnMsg);
        if (!WxPayKit.codeIsOk(returnCode)) {
            throw new CoolException("统一下单失败:" + returnMsg);
        }
        String resultCode = result.get("result_code");
        if (!WxPayKit.codeIsOk(resultCode)) {
            throw new CoolException("统一下单失败:" + returnMsg);
        }
        // 生成预付订单success

        String qrCodeUrl = result.get("code_url");
        QrConfig config = new QrConfig(200, 200);
        BufferedImage image = QrCodeUtil.generate(qrCodeUrl, config);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        String base64 = Base64.encode(out.toByteArray());
        String base64Img = "data:image/png;base64," + base64;
        orderInfoEntity.setQrCode(base64Img);

    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public String nativeNotify() {
        String xmlMsg = HttpKit.readData(request);
        System.out.println("支付通知=" + xmlMsg);
        Map<String, String> params = WxPayKit.xmlToMap(xmlMsg);

        String returnCode = params.get("return_code");
        String orderNum = params.get("out_trade_no");
        long count = kioskOrderMapper.selectCountByQuery(QueryWrapper.create()
                .eq(KioskOrderEntity::getOrderNum, orderNum)
                .eq(KioskOrderEntity::getStatus, OrderStatusEnum.AWAITING_DELIVERY.getCode())
                .eq(KioskOrderEntity::getPayType, 1));
        if (count > 0) {
            return null;
        }

        // 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
        // 注意此处签名方式需与统一下单的签名类型一致
        if (WxPayKit.verifyNotify(params, wxNativeConfig.getPartnerKey(), SignType.HMACSHA256)) {
            if (WxPayKit.codeIsOk(returnCode)) {
                // 更新订单信息
                KioskOrderEntity order = new KioskOrderEntity();
                order.setStatus(OrderStatusEnum.AWAITING_DELIVERY.getCode());
                order.setPayType(1);
                order.setPayTime(LocalDateTime.now());
                kioskOrderMapper.updateByQuery(order,
                        QueryWrapper.create().eq(KioskOrderEntity::getOrderNum, orderNum));
                instructionService.sendPrintInstruction(orderNum);

                // 发送通知等
                Map<String, String> xml = new HashMap<String, String>(2);
                xml.put("return_code", "SUCCESS");
                xml.put("return_msg", "OK");
                System.out.println("更新订单成功:" + xml);
                System.out.println("更新订单成功:" + orderNum);
                return WxPayKit.toXml(xml);
            }
        }
        return null;
    }

    public void wapPay(KioskOrderEntity kioskOrderEntity) {
        String ip = ipUtils.getIpAddr(request);
        if (StrUtil.isBlank(ip)) {
            ip = "127.0.0.1";
        }

        H5SceneInfo sceneInfo = new H5SceneInfo();

        H5SceneInfo.H5 h5Info = new H5SceneInfo.H5();
        h5Info.setType("Wap");
        // 此域名必须在商户平台--"产品中心"--"开发配置"中添加
        h5Info.setWap_url("https://h5-mall.xiaohogo.com/");
        h5Info.setWap_name("报纸打印");
        sceneInfo.setH5_info(h5Info);

        Map<String, String> params = UnifiedOrderModel
                .builder()
                .appid(wxNativeConfig.getAppId())
                .mch_id(wxNativeConfig.getMchId())
                .nonce_str(WxPayKit.generateStr())
                .body(kioskOrderEntity.getTitle())
                .out_trade_no(kioskOrderEntity.getOrderNum())
                // .total_fee("1")
                .total_fee(kioskOrderEntity.getPrice().multiply(new BigDecimal(100))
                        .setScale(0, RoundingMode.HALF_UP)
                        .intValue() + "")
                .spbill_create_ip(ip)
                .notify_url(wxNativeConfig.getNotifyUrl())
                .trade_type(TradeType.MWEB.getTradeType())
                .scene_info(JSON.toJSONString(sceneInfo))
                .build()
                .createSign(wxNativeConfig.getPartnerKey(), SignType.HMACSHA256);

        String xmlResult = WxPayApi.pushOrder(false, params);
        log.info(xmlResult);

        Map<String, String> result = WxPayKit.xmlToMap(xmlResult);

        String returnCode = result.get("return_code");
        String returnMsg = result.get("return_msg");
        if (!WxPayKit.codeIsOk(returnCode)) {
            throw new CoolException("统一下单失败:" + returnMsg);
        }
        String resultCode = result.get("result_code");
        if (!WxPayKit.codeIsOk(resultCode)) {
            throw new CoolException("统一下单失败:" + returnMsg);
        }
        // 以下字段在return_code 和result_code都为SUCCESS的时候有返回

        String prepayId = result.get("prepay_id");
        String webUrl = result.get("mweb_url");

        log.info("prepay_id:" + prepayId + " mweb_url:" + webUrl);
        kioskOrderEntity.setQrCode(webUrl);
    }
}
