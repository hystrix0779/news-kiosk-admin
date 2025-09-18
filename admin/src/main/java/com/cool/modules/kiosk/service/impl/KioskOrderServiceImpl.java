package com.cool.modules.kiosk.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.core.exception.CoolException;
import com.cool.core.util.MappingAlgorithm;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.cool.modules.kiosk.entity.KioskMachineEntity;
import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.entity.KioskTemplateEntity;
import com.cool.modules.kiosk.entity.table.KioskMachineEntityTableDef;
import com.cool.modules.kiosk.entity.table.KioskOrderEntityTableDef;
import com.cool.modules.kiosk.entity.table.KioskTemplateEntityTableDef;
import com.cool.modules.kiosk.mapper.KioskMachineMapper;
import com.cool.modules.kiosk.mapper.KioskOrderMapper;
import com.cool.modules.kiosk.mapper.KioskTemplateMapper;
import com.cool.modules.kiosk.pojo.KioskOrderPojo;
import com.cool.modules.kiosk.service.KioskOrderService;
import com.cool.modules.order.enums.OrderStatusEnum;
import com.cool.modules.order.pay.AliPayService;
import com.cool.modules.order.pay.WeChatPayService;
import com.mybatisflex.core.query.QueryWrapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
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
    @Resource
    private KioskMachineMapper kioskMachineMapper;

    @Override
    public void queryBefore(JSONObject requestParams, QueryWrapper queryWrapper) {
        // 查询之前过滤掉创建订单前删除本机器超过1小时没有支付的订单
        // queryWrapper
        // .eq(KioskOrderEntity::getStatus, OrderStatusEnum.AWAITING_PAYMENT.getCode())
        // .gt(KioskOrderEntity::getCreateTime, DateUtil.offsetHour(new Date(), -1));
        queryWrapper.notIn(KioskOrderEntity::getId,
                QueryWrapper.create().select(KioskOrderEntityTableDef.KIOSK_ORDER_ENTITY.ID)
                        .eq(KioskOrderEntity::getStatus, OrderStatusEnum.AWAITING_PAYMENT.getCode())
                        .lt(KioskOrderEntity::getCreateTime, DateUtil.offsetHour(new Date(), -1)));
        // 按时间段查询创建时间
        String createTimeRange = requestParams.getStr("createTimeRange");
        if (StrUtil.isNotBlank(createTimeRange)) {
            String[] timeRange = createTimeRange.split(",");
            if (timeRange.length == 2) {
                queryWrapper.between(KioskOrderEntity::getCreateTime, timeRange[0], timeRange[1]);
            }
        }
    }

    @Override
    public void completion(List<KioskOrderEntity> list) {
        // 补全设备信息
        List<Long> machineIds = list.stream().map(KioskOrderEntity::getMachineId).distinct()
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(machineIds)) {
            List<KioskMachineEntity> machines = kioskMachineMapper
                    .selectListByQuery(QueryWrapper.create().select(
                            KioskMachineEntityTableDef.KIOSK_MACHINE_ENTITY.ID,
                            KioskMachineEntityTableDef.KIOSK_MACHINE_ENTITY.NAME)
                            .in(KioskMachineEntity::getId, machineIds));
            Map<Long, KioskMachineEntity> machineMap = machines.stream()
                    .collect(Collectors.toMap(KioskMachineEntity::getId, y -> y));
            for (KioskOrderEntity order : list) {
                KioskMachineEntity machine = machineMap.get(order.getMachineId());
                if (machine != null) {
                    order.setMachineName(machine.getName());
                }
            }
        }

        // 补全模板信息
        List<Long> templateIds = list.stream().map(KioskOrderEntity::getTemplateId).distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(templateIds)) {
            return;
        }
        List<KioskTemplateEntity> templates = kioskTemplateMapper
                .selectListByQuery(QueryWrapper.create().select(
                        KioskTemplateEntityTableDef.KIOSK_TEMPLATE_ENTITY.ID,
                        KioskTemplateEntityTableDef.KIOSK_TEMPLATE_ENTITY.NAME)
                        .in(KioskTemplateEntity::getId, templateIds));
        Map<Long, KioskTemplateEntity> templateMap = templates.stream()
                .collect(Collectors.toMap(KioskTemplateEntity::getId, y -> y));
        for (KioskOrderEntity order : list) {
            KioskTemplateEntity template = templateMap.get(order.getTemplateId());
            if (template != null) {
                order.setTemplateName(template.getName());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public KioskOrderEntity create(KioskOrderPojo pojo) {

        if (Objects.isNull(pojo.getMachineId()) || Objects.isNull(pojo.getTemplateId())) {
            throw new CoolException("参数错误");
        }

        // 查询模板
        KioskTemplateEntity template = kioskTemplateMapper.selectOneById(pojo.getTemplateId());
        if (template == null) {
            throw new CoolException("模板不存在");
        }
        if (template.getMachineId() != pojo.getMachineId()) {
            throw new CoolException("模板与机器不匹配");
        }
        // 创建订单前删除本机器超过1小时没有支付的订单
        this.mapper.deleteByQuery(QueryWrapper.create()
                .eq(KioskOrderEntity::getMachineId, pojo.getMachineId())
                .eq(KioskOrderEntity::getStatus, OrderStatusEnum.AWAITING_PAYMENT.getCode())
                .lt(KioskOrderEntity::getCreateTime, DateUtil.offsetHour(new Date(), -1)));
        KioskOrderEntity order = new KioskOrderEntity();
        if (pojo.getCount() == null) {
            order.setCount(1);
        } else {
            order.setCount(pojo.getCount());
        }
        order.setMachineId(pojo.getMachineId());
        order.setTemplateId(pojo.getTemplateId());
        order.setTitle(template.getName());
        order.setOrderNum(generateOrderNum());
        order.setPayType(pojo.getPayType());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SneakyThrows
    public KioskOrderEntity updateOrder(KioskOrderPojo pojo) {
        if (Objects.isNull(pojo.getOrderNum())) {
            throw new CoolException("参数错误");
        }
        KioskOrderEntity kioskOrderEntity = this.mapper.selectOneByQuery(QueryWrapper.create()
                .eq(KioskOrderEntity::getOrderNum, pojo.getOrderNum()));
        if (kioskOrderEntity == null) {
            throw new CoolException("订单不存在");
        }
        if (kioskOrderEntity.getStatus() != OrderStatusEnum.AWAITING_DELIVERY.getCode()) {
            throw new CoolException("订单状态错误");
        }
        KioskOrderEntity updateOrder = new KioskOrderEntity();
        updateOrder.setPic(pojo.getPic());
        updateOrder.setFilePath(pojo.getFilePath());
        this.mapper.updateByQuery(updateOrder,
                QueryWrapper.create().eq(KioskOrderEntity::getOrderNum, pojo.getOrderNum()));
        return kioskOrderEntity;
    }
}