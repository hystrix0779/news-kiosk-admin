package com.cool.modules.kiosk.controller.app;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cool.core.annotation.CoolRestController;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.request.R;
import com.cool.modules.kiosk.pojo.KioskMessagePojo;
import com.cool.modules.kiosk.ws.KioskWebSocketHandler;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.mapper.PluginInfoMapper;
import com.mybatisflex.core.query.QueryWrapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author     : Created by niulixiong(hystrix0779@yeah.net) on 2025/09/12/17.
 * @annotation :
 */
@Tag(name = "kiosk 信息", description = "kiosk 信息")
@CoolRestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
public class AppKioskInfoController {

    private final KioskWebSocketHandler kioskWebSocketHandler;
    private final PluginInfoMapper pluginInfoMapper;

    @GetMapping(value = "/oss/paramet")
    @ResponseBody
    @TokenIgnore
    public R ossParameter(@RequestParam Long machineId, @RequestParam String key) {
          KioskMessagePojo pojo = kioskWebSocketHandler.getKioskMessagePojo(machineId);
            if (pojo != null) {
                PluginInfoEntity plu = pluginInfoMapper.selectOneByQuery(QueryWrapper.create().eq(PluginInfoEntity::getKey, "upload-oss"));
                if (plu == null) {
                    return R.error("插件不存在");
                }
               return R.ok(plu.getConfig());
            } else {
               return R.error("设备未连接，请先连接设备");
            }
    }

    
}
