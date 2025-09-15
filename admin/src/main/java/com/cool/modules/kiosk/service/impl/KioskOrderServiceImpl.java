package com.cool.modules.kiosk.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.core.exception.CoolException;
import com.cool.core.util.MappingAlgorithm;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.entity.KioskTemplateEntity;
import com.cool.modules.kiosk.mapper.KioskOrderMapper;
import com.cool.modules.kiosk.mapper.KioskTemplateMapper;
import com.cool.modules.kiosk.service.KioskOrderService;
import com.cool.modules.order.enums.OrderStatusEnum;
import com.cool.modules.order.pay.AliPayService;
import com.cool.modules.order.pay.WeChatPayService;

import cn.hutool.core.date.DateUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单信息
 */
@Service
public class KioskOrderServiceImpl extends BaseServiceImpl<KioskOrderMapper, KioskOrderEntity>
        implements KioskOrderService {

    @Resource
    private KioskTemplateMapper kioskTemplateMapper;
    @Resource
    private WeChatPayService weChatPayService;
    @Resource
    private AliPayService aliPayService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public KioskOrderEntity create(KioskOrderEntity order) {

        if (Objects.isNull(order.getMachineId())||Objects.isNull(order.getTemplateId())) {
            throw new CoolException("参数错误");
        }
        
        // 查询模板
        KioskTemplateEntity template = kioskTemplateMapper.selectOneById(order.getTemplateId());
        if (template == null) {
            throw new CoolException("模板不存在");
        }
        if (template.getMachineId() != order.getMachineId()) {
            throw new CoolException("模板与机器不匹配");
        }
        if (order.getCount() == null) {
            order.setCount(1);
        }
        order.setTitle(template.getName());
        order.setOrderNum(generateOrderNum());
        // 模板价格*数量
        order.setPrice(template.getPrice().multiply(new BigDecimal(order.getCount())));
        order.setStatus(OrderStatusEnum.AWAITING_PAYMENT.getCode());
        this.mapper.insert(order);
        if (order.getPayType() == 1) {
            weChatPayService.nativePay(order);
        } else if (order.getPayType() == 2) {
            aliPayService.wapPay(order);
        }
        return order;
    }

    private String generateOrderNum() {
        // 随机数
        int random = (int) (Math.random() * 10000);
        return "U" + DateUtil.format(new Date(), "YYMMDDHHmm") + MappingAlgorithm.encrypt(random);
    }
}