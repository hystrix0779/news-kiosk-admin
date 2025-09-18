package com.cool.modules.kiosk.ws;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.mapper.KioskOrderMapper;
import com.cool.modules.kiosk.pojo.KioskMessagePojo;
import com.cool.modules.order.enums.OrderStatusEnum;
import com.mybatisflex.core.query.QueryWrapper;

import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/09/12/17.
 * @annotation :
 */
@Service
public class InstructionService {

    @Resource
    private KioskWebSocketHandler kioskWebSocketHandler;
    @Resource
    private KioskOrderMapper kioskOrderMapper;

    /**
     * 创建直接打印图片指令
     */
    public KioskMessagePojo.Instruction createPrintImageInstruction() {
        KioskMessagePojo.Instruction instruction = new KioskMessagePojo.Instruction();
        instruction.setType("printImage");
        instruction.setContent("打印图片");
        JSONObject params = new JSONObject();
        params.set("imageUrl", "");
        instruction.setParams(params);
        return instruction;
    }

    /**
     * 创建直接打印订单命令
     */
    public KioskMessagePojo.Instruction createPrintOrderInstruction(String filePath, String orderNum ,Integer count) {
        KioskMessagePojo.Instruction instruction = new KioskMessagePojo.Instruction();
        instruction.setType("printOrder");
        instruction.setContent("打印订单");
        JSONObject params = new JSONObject();
        params.set("filePath", filePath);
        params.set("orderNum", orderNum);
        params.set("count", count);
        instruction.setParams(params);
        return instruction;
    }

    /**
     * 发送打印命令
     */
    @SneakyThrows
    public void sendPrintInstruction(String orderNum) {
        KioskOrderEntity order = kioskOrderMapper.selectOneByQuery(
                QueryWrapper.create().eq(KioskOrderEntity::getOrderNum, orderNum));
        KioskMessagePojo pojo = kioskWebSocketHandler.getKioskMessagePojo(order.getMachineId());
        if (Objects.isNull(pojo)) {
            KioskOrderEntity updateOrder = new KioskOrderEntity();
            updateOrder.setStatus(OrderStatusEnum.TRANSACTION_FAILED.getCode());
            updateOrder.setPayType(2);
            updateOrder.setRemark("设备离线");
            kioskOrderMapper.updateByQuery(updateOrder,
                    QueryWrapper.create().eq(KioskOrderEntity::getOrderNum, order.getOrderNum()));
            return;
        }
        pojo.setType(3);
        pojo.setInstruction(createPrintOrderInstruction(order.getFilePath(), order.getOrderNum(), order.getCount()));
        kioskWebSocketHandler.sendToDevice(pojo);
    }

}
