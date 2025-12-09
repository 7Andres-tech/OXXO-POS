package com.oxxo.reniec;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "reniec")
public class ReniecProperties {
    private String url;
    private String token;
}
