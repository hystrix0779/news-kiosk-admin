package com.cool.modules.kiosk.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.enums.Apis;
import com.cool.modules.kiosk.entity.KioskOrderRefundEntity;
import com.cool.modules.kiosk.service.KioskOrderRefundService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 订单信息
 */
@Tag(name = "订单信息", description = "订单信息")
@CoolRestController(api = {Apis.ADD, Apis.DELETE, Apis.UPDATE, Apis.PAGE, Apis.LIST, Apis.INFO},cname = "orderRefund")
public class AdminKioskOrderRefundController extends BaseController<KioskOrderRefundService, KioskOrderRefundEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}