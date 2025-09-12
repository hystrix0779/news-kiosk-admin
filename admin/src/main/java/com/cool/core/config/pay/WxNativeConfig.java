package com.cool.core.config.pay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author     : Created by niulixiong(hystrix0779@yeah.net) on 2025/08/20/10.
 * @annotation :
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.native") 
public class WxNativeConfig {
    private String appId;
    private String mchId;
    private String partnerKey;
    private String notifyUrl;
}
