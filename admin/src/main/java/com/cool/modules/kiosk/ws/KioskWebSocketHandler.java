package com.cool.modules.kiosk.ws;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.cool.modules.kiosk.entity.KioskMachineEntity;
import com.cool.modules.kiosk.entity.KioskMachineLogEntity;
import com.cool.modules.kiosk.entity.KioskOrderEntity;
import com.cool.modules.kiosk.mapper.KioskMachineErrMapper;
import com.cool.modules.kiosk.mapper.KioskMachineLogMapper;
import com.cool.modules.kiosk.mapper.KioskMachineMapper;
import com.cool.modules.kiosk.mapper.KioskOrderMapper;
import com.cool.modules.kiosk.pojo.KioskMessagePojo;
import com.cool.modules.order.enums.OrderStatusEnum;
import com.mybatisflex.core.query.QueryWrapper;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/07/31/15.
 * @annotation :
 */
@Slf4j
@Component
public class KioskWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, KioskMessagePojo> currentConn = new ConcurrentHashMap<>();

    private final Map<Long, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Resource
    private KioskMachineMapper KioskMachineMapper;

    @Resource
    private KioskMachineLogMapper KioskMachineLogMapper;

    @Resource
    private KioskMachineErrMapper KioskMachineErrMapper;

    @Resource
    private KioskOrderMapper kioskOrderMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri == null) {
            session.sendMessage(new TextMessage("系统异常"));
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("系统异常"));
            return;
        }
        Map<String, String> paramMap = parseQueryParams(uri.getQuery());

        Long id = Long.valueOf(paramMap.get("id"));
        String key = paramMap.get("key");

        if (id == null || key == null) {
            // 发送拒绝消息
            session.sendMessage(new TextMessage("连接拒绝：缺少id或key"));
            // 主动关闭连接
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("缺少id或key参数"));
            return;
        }
        KioskMachineEntity KioskMachine = KioskMachineMapper.selectOneById(id);
        if (KioskMachine == null) {
            session.sendMessage(new TextMessage("连接拒绝：设备不存在"));
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("设备不存在"));
            return;
        }
        if (!KioskMachine.getKey().equals(key)) {
            session.sendMessage(new TextMessage("连接拒绝：key错误"));
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("key错误"));
            return;
        }
        session.getAttributes().put("id", id);
        sessionMap.put(id, session);
        log.info("WebSocket连接建立成功，id={}, key={}", id, key);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        if ("1".equals(payload)) {
            session.sendMessage(new TextMessage("success"));
            return;
        }
        KioskMessagePojo KioskMessage = JSONUtil.toBean(payload, KioskMessagePojo.class);

        log.info("收到消息: {}", KioskMessage);

        // 保存 session
        Long id = KioskMessage.getId();
        if (id == null) {
            session.sendMessage(new TextMessage("缺少必要参数"));
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("缺少必要参数"));
            return;
        }
        // 连接信息
        if (KioskMessage.getType() == null || KioskMessage.getType() == 1) {
            if (!sessionMap.containsKey(id)) {
                session.sendMessage(new TextMessage("非法链接"));
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("非法链接"));
                return;
            }
            KioskMessage.setLoginTime(LocalDateTime.now());
            currentConn.put(id, KioskMessage);
        } else if (KioskMessage.getType() == 2) {
            KioskMachineErrMapper.insert(KioskMessage.getErr());
        } else if (KioskMessage.getType() == 4) {
            KioskOrderEntity order = new KioskOrderEntity();
            order.setStatus(OrderStatusEnum.TRANSACTION_COMPLETE.getCode());
            kioskOrderMapper.updateByQuery(order,
                    QueryWrapper.create().eq(KioskOrderEntity::getOrderNum, KioskMessage.getOrderNum()));
        } else if (KioskMessage.getType() == 5) {
            KioskOrderEntity order = new KioskOrderEntity();
            order.setStatus(OrderStatusEnum.TRANSACTION_FAILED.getCode());
            order.setRemark(KioskMessage.getRemark());
            kioskOrderMapper.updateByQuery(order,
                    QueryWrapper.create().eq(KioskOrderEntity::getOrderNum, KioskMessage.getOrderNum()));
        }
        // 错误信息
        session.sendMessage(new TextMessage("success"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long id = (Long) session.getAttributes().get("id");
        log.info("WebSocket连接关闭: sessionId={}, id={}", session.getId(), id);
        KioskMessagePojo KioskMessage = currentConn.get(id);
        KioskMachineLogEntity KioskMachineLogEntity = new KioskMachineLogEntity();
        KioskMachineLogEntity.setMachineId(id);
        KioskMachineLogEntity.setLoginTime(KioskMessage.getLoginTime());
        KioskMachineLogEntity.setLogoutTime(LocalDateTime.now());
        KioskMachineLogMapper.insert(KioskMachineLogEntity);
        sessionMap.remove(id);
        currentConn.remove(id);
    }

    @SneakyThrows
    public void sendToDevice(KioskMessagePojo pojo) {
        WebSocketSession session = sessionMap.get(pojo.getId());
        if (session != null && session.isOpen()) {
            // List<String> types =
            // currentConn.get(pojo.getId()).getInstructions().stream().map(i -> {
            // return i.getType();
            // }).toList();
            session.sendMessage(new TextMessage(JSONUtil.toJsonStr(pojo)));
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .filter(pair -> pair.length == 2)
                .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));
    }

    public KioskMessagePojo getKioskMessagePojo(Long deviceId) {
        return currentConn.get(deviceId);
    }
}
