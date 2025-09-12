package com.cool.modules.order.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author : Created by niulixiong(hystrix0779@yeah.net) on 2025/08/25/17.
 * @annotation :
 */
@Getter
@Setter
public class H5SceneInfo {

    private H5 h5_info;

    @Getter
    @Setter
    public static class H5 {
        private String type;
        private String app_name;
        private String bundle_id;
        private String package_name;
        private String wap_url;
        private String wap_name;
    }

}
