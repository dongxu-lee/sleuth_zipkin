package com.ldx.config;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 这个类的作用是在jwt里增加或删除一些属性
 */
@Component
public class LdxAccessTokenConvertor extends DefaultAccessTokenConverter {
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        // 在jwt里增加客户端ip
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();

        String remoteAddr = request.getRemoteAddr();
        Map<String, String> stringMap = (Map<String, String>) super.convertAccessToken(token,authentication);
        stringMap.put("clientIp", remoteAddr);
        return stringMap;

    }
}
