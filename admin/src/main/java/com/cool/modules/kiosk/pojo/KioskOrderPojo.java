package com.cool.modules.kiosk.pojo;

import lombok.Data;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/09/16/17.
 * @annotation :
 */

@Data
public class KioskOrderPojo {


    private Long templateId;
    private Long machineId;
    private Integer count;
    private Integer payType;
    private String orderNum;
    private String pic;
    private String filePath;
}
