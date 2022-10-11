package com.fc.project.api.config.dev;

import com.fc.project.api.config.AuthUserResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Profile("dev")
@Configuration
public class DevWebConfig implements WebMvcConfigurer {

    /**
     * AuthUserResolver 추가
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new FakeAuthUserResolver());
    }
    
}
