package com.cool.modules.kiosk.ws;

import org.springframework.stereotype.Service;

import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.mapper.KioskOrderMapper;
import com.cool.modules.kiosk.pojo.KioskMessagePojo;

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
    public KioskMessagePojo.Instruction createPrintOrderInstruction(String filePath, String orderNum) {
        KioskMessagePojo.Instruction instruction = new KioskMessagePojo.Instruction();
        instruction.setType("printOrder");
        instruction.setContent("打印订单");
        JSONObject params = new JSONObject();
        params.set("filePath", filePath);
        params.set("orderNum", orderNum);
        instruction.setParams(params);
        return instruction;
    }

    /**
     * 发送打印命令
     */
    @SneakyThrows
    public void sendPrintInstruction(Long orderId) {
        KioskOrderEntity order = kioskOrderMapper.selectOneById(orderId);
        KioskMessagePojo pojo = kioskWebSocketHandler.getKioskMessagePojo(order.getMachineId());
        pojo.setType(3);
        pojo.setInstruction(createPrintOrderInstruction(order.getFilePath(), order.getOrderNum()));
        kioskWebSocketHandler.sendToDevice(pojo);
    }

}
