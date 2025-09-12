package com.cool.core.config.pay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "ali.cert")
@Configuration
public class AliPayConfig {
    private String appId;
    private String privateKey;
    private String publicKey;
    private String appCertPath;
    private String aliPayCertPath;
    private String aliPayRootCertPath;
    private String serverUrl;
    private String doMain;
    private String returnUrl;
    private String notifyUrl;
}
