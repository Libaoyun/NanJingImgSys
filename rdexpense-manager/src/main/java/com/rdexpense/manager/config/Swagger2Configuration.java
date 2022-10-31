package com.rdexpense.manager.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@EnableKnife4j
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Configuration {

    private transient static final String ORDER = "9999";

    //api文档地址 http://localhost:8066/rdexpense/doc.html#/home
    @Bean(value = "公共接口")
    public Docket commonApi() {
        //在配置好的配置类中增加此段代码即可
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name("token").description("系统token")//name表示名称，description表示描述
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(true).defaultValue("").build();
        pars.add(ticketPar.build());//添加完此处一定要把下边的带***的也加上否则不生效

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName(ORDER + ".公共接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.rdexpense.manager.controller.common"))
                .paths(PathSelectors.any()) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build()
                .globalOperationParameters(pars);//************把消息头添加
    }



    @Bean(value = "系统管理")
    public Docket oneApi() {
        //在配置好的配置类中增加此段代码即可
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name("token").description("系统token(登陆除外)")//name表示名称，description表示描述
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(true).defaultValue("").build();
        pars.add(ticketPar.build());//添加完此处一定要把下边的带***的也加上否则不生效

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("1.系统基础接口")
                .select()
                .apis(Predicates.or(basePackage("com.rdexpense.manager.controller")
                ))
                .paths(Predicates.or(
                        //这里添加你需要展示的接口,同一包下根据url路径设置加入文档,忽略哪些文档
                        PathSelectors.ant("/file/**"),//附件接口
                        PathSelectors.ant("/dataDictionary/**"),//字典
                        PathSelectors.ant("/menu/**"),//获取授权功能菜单
                        PathSelectors.ant("/login/**"),//用户登录
                        PathSelectors.ant("/menuMangement/**"),//菜单管理接口
                        PathSelectors.ant("/operationLog/**"),//操作日志
                        PathSelectors.ant("/permission/**"),//用户权限
                        PathSelectors.ant("/user/**"),//员工管理
                        PathSelectors.ant("/department/**"),//部门职务管理
                        PathSelectors.ant("/organization/**"),//组织管理
                        PathSelectors.ant("/template/**"),//知识库
                        PathSelectors.ant("/rule/**"),//规则配置
                        PathSelectors.ant("/flow/**"),//流程配置
                        PathSelectors.ant("/body-part/**"), //科室人体部位配置
                        PathSelectors.ant("/dig-item/**"), //科室项目诊疗配置
                        PathSelectors.ant("/device/**"), //科室设备管理
                        PathSelectors.ant("/patient/**") //患者基本信息管理


                )) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build()
                .globalOperationParameters(pars);//************把消息头添加
    }

    @Bean(value = "研发项目实施管理")
    public Docket progressReport() {
        //在配置好的配置类中增加此段代码即可
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name("token").description("系统token(登陆除外)")//name表示名称，description表示描述
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(true).defaultValue("").build();
        pars.add(ticketPar.build());//添加完此处一定要把下边的带***的也加上否则不生效

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("3.研发项目实施管理")
                .select()
                .apis(Predicates.or(basePackage("com.rdexpense.manager.controller")
                ))
                .paths(Predicates.or(
                        //这里添加你需要展示的接口,同一包下根据url路径设置加入文档,忽略哪些文档
                        PathSelectors.ant("/projContractSign/**"),
                        PathSelectors.ant("/progressreport/**"),
                        PathSelectors.ant("/disclosure/**")



                )) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build()
                .globalOperationParameters(pars);//************把消息头添加
    }



    @Bean(value = "研发项目立项管理")
    public Docket applyApi() {
        //在配置好的配置类中增加此段代码即可
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name("token").description("系统token(登陆除外)")//name表示名称，description表示描述
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(true).defaultValue("").build();
        pars.add(ticketPar.build());//添加完此处一定要把下边的带***的也加上否则不生效

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("2.研发项目立项管理")
                .select()
                .apis(Predicates.or(basePackage("com.rdexpense.manager.controller")
                ))
                .paths(Predicates.or(
                        //这里添加你需要展示的接口,同一包下根据url路径设置加入文档,忽略哪些文档
                        PathSelectors.ant("/itemExpenses/**"),//研发项目费用支出管理
                        PathSelectors.ant("/itemClosureCheck/**"),//研发项目结题验收
                        PathSelectors.ant("/projectApply/**")//项目立项申请

                )) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build()
                .globalOperationParameters(pars);//************把消息头添加
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("系统接口服务") //设置文档的标题
                .description(" API 接口文档") // 设置文档的描述
                .version("1.0.0") // 设置文档的版本信息-> 1.0.0 Version information
                .termsOfServiceUrl("http://www.baidu.com") // 设置文档的License信息->1.3 License information
                .build();
    }


    public static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackage.split(";")) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }

}
