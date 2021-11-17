package com.rdexpense.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/4/1 18:40
 */
@Configuration
public class BcryptPasswordConfiguration {
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
