package com.cool.modules.kiosk.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.enums.Apis;
import com.cool.modules.kiosk.entity.KioskTemplateEntity;
import com.cool.modules.kiosk.service.KioskTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 打印模板
 */
@Tag(name = "打印模板", description = "打印模板")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO})
public class AdminKioskTemplateController extends BaseController<KioskTemplateService, KioskTemplateEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}