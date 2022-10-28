package com.rdexpense.manager.config.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;


/**
 * 为第三方应用系统提供接口，并进行认证
 * 第一步：根据grant_type、scope、client_id、client_secret参数，获取token
 * http://127.0.0.1:8888/urequipment/oauth/token?grant_type=client_credentials&scope=select&client_id=client_1&client_secret=123456
 * 第二步：根据access_token，请求接口方法
 * http://127.0.0.1:8888/urequipment/api/product/1
 * 参数：
 * Authorization：bearer ced08599-0edd-4feb-ba77-bbeacc493c0c
 * Content-Type：application/json
 * 注意：
 * 1、ced08599-0edd-4feb-ba77-bbeacc493c0c为根据第一步获取到的access_token
 * 2、bearer后面有一个空格
 *
 * @author rdexpense
 */
@Configuration
public class OAuth2ServerConfig {
    private static final String DEMO_RESOURCE_ID = "api/public/";

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(DEMO_RESOURCE_ID).stateless(true);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/api/public/**").authenticated();//配置api访问控制，必须认证过后才可以访问

        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        AuthenticationManager authenticationManager;
        @Autowired
        RedisConnectionFactory redisConnectionFactory;
//        @Autowired
//        DataSource dataSource;


        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        	System.out.println(clientId+"=====");
//        	System.out.println(clientSecret+"=====");
            String clientId = "mctech";
            String client_secret = new BCryptPasswordEncoder().encode("JaPEdO9X0JE0Qb5JFsTlLkNlx3rqi9sCCjU5104J");
            clients.inMemory()
                    //客户端模式
                    .withClient(clientId)
                    .secret(client_secret)
                    .resourceIds(DEMO_RESOURCE_ID)
                    .authorizedGrantTypes("client_credentials", "refresh_token")
                    .scopes("all", "select", "read", "write")
                    .authorities("client")
                    .accessTokenValiditySeconds(60 * 60 * 2);
//                    .refreshTokenValiditySeconds(3600 * 24 * 2); //2天过期


            // 密码模式的客户端
//                    .and().withClient("client_2")
//                    .resourceIds(DEMO_RESOURCE_ID)
//                    .authorizedGrantTypes("password", "refresh_token")
//                    .scopes("select")
//                    .authorities("client")
//                    .secret(finalSecret);
            // 这一块可以持久化到数据库【参考数据库脚本：oauth_client_details.sql】
//            clients.jdbc(dataSource);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
            endpoints
                    // 缓存到redis中
//                    .tokenStore(new RedisTokenStore(redisConnectionFactory))
                    // 缓存到内存中
//                    .tokenStore(new InMemoryTokenStore())
                    // 缓存到mysql中【参考数据库脚本：oauth_access_token.sql】
//                    .tokenStore(new JdbcTokenStore(dataSource))
                    .authenticationManager(authenticationManager)
                    .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
//                    .reuseRefreshTokens(true);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) {

            oauthServer
                    .tokenKeyAccess("permitAll()")
                    .checkTokenAccess("isAuthenticated()")
                    //允许表单认证
                    .allowFormAuthenticationForClients();
        }
    }
}
