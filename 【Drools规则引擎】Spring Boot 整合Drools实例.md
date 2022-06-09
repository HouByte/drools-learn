@[TOC](【Drools规则引擎】Spring Boot 整合Drools实例)
# 前言

> **java语言开发的开源业务规则引擎**
[DROOLS](http://www.drools.org.cn/)（JBOSS RULES ）具有一个易于访问企业策略、易于调整以及易于管理的开源业务规则引擎，符合业内标准，速度快、效率高。业务分析师或审核人员可以利用它轻松查看业务规则，从而检验是否已编码的规则执行了所需的业务规则。

	
# 添加Maven依赖
添加Drools的相关依赖

```html
<drools.version>7.63.0.Final</drools.version>

...

<dependency>
   <groupId>org.drools</groupId>
   <artifactId>drools-compiler</artifactId>
   <version>${drools.version}</version>
</dependency>
<dependency>
   <groupId>org.drools</groupId>
   <artifactId>drools-mvel</artifactId>
   <version>${drools.version}</version>
</dependency>
<dependency>
   <groupId>org.drools</groupId>
   <artifactId>drools-core</artifactId>
   <version>${drools.version}</version>
</dependency>
<dependency>
   <groupId>org.drools</groupId>
   <artifactId>drools-templates</artifactId>
   <version>${drools.version}</version>
</dependency>
<dependency>
   <groupId>org.kie</groupId>
   <artifactId>kie-api</artifactId>
   <version>${drools.version}</version>
</dependency>
<dependency>
   <groupId>org.kie</groupId>
   <artifactId>kie-spring</artifactId>
   <version>${drools.version}</version>
</dependency>
```
demo中为了简便使用了lombok,可以根据自己的实际需要添加

```html
<dependency>
     <groupId>org.projectlombok</groupId>
     <artifactId>lombok</artifactId>
     <version>1.18.20</version>
 </dependency>
 ```

# 编写配置类
DroolsConfig.java

```java
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import java.io.IOException;

/**
 * <h1>规则引擎配置类</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/06
 */
 
@Configuration
public class DroolsConfig {

    //指定规则文件存放的目录
    private static final String RULES_PATH = "rules/";
    private final KieServices kieServices = KieServices.Factory.get();
    @Bean
    @ConditionalOnMissingBean
    public KieFileSystem kieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        ResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver();
        Resource[] files =
                resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "*.*");
        String path = null;
        for (Resource file : files) {
            path = RULES_PATH + file.getFilename();
            kieFileSystem.write(ResourceFactory.newClassPathResource(path, "UTF-8"));
        }
        return kieFileSystem;
    }
    @Bean
    @ConditionalOnMissingBean
    public KieContainer kieContainer() throws IOException {
        KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem());
        kieBuilder.buildAll();
        return kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
    }
    @Bean
    @ConditionalOnMissingBean
    public KieBase kieBase() throws IOException {
        return kieContainer().getKieBase();
    }
    @Bean
    @ConditionalOnMissingBean
    public KModuleBeanFactoryPostProcessor kiePostProcessor() {
        return new KModuleBeanFactoryPostProcessor();
    }
}

```

# 编写规则文件
rules/score-rules.drl

```java
package rules;

import cn.flowboot.entity.Order;

//100元以下，不加分
rule "score_1"
when
    $order:Order(amout<100)
then
    $order.setScore(0);
    System.out.println("触发规则：100元不加分");
end

//100元- 500元加100分
rule "score_2"
when
    $order:Order(amout>=100&&amout < 500)
then
    $order.setScore(100);
    System.out.println("触发规则：100元-500元加100分");
end

//500元- 1000元加500分
rule "score_3"
when
    $order:Order(amout>=500&&amout < 1000)
then
    $order.setScore(500);
    System.out.println("触发规则：500元-1000元加500分");
end

//1000元以上加1000分
rule "score_4"
when
    $order:Order(amout>1000)
then
    $order.setScore(0);
    System.out.println("触发规则：1000元以上加1000分");
end

```
> PS: idea 有Drools插件，可以提供代码高亮

# 编写测试业务
实体类**Order** 

```java
@Data
public class Order {
    private Double amout;
    private Integer score;
}
```
服务接口**RuleService** 

```java
public interface RuleService {

    /**
     * 订单通过规则引擎处理
     */
    Order executeOrderRule(Order order);
}
```
服务实现**RuleServiceImpl** 

```java
@RequiredArgsConstructor
@Service
public class RuleServiceImpl implements RuleService {

    private final KieBase kieBase;


    /**
     * 订单通过规则引擎处理
     *
     * @param order
     * @return
     */
    @Override
    public Order executeOrderRule(Order order) {
        KieSession kieSession = kieBase.newKieSession();
        kieSession.insert(order);
        kieSession.fireAllRules();
        kieSession.dispose();
        return order;
    }
}
```

控制类

```java
@RequiredArgsConstructor
@RestController
public class RuleController {

    private final RuleService ruleService;

    @GetMapping("order")
    public Order executeOrderRule(@RequestParam(value = "price",defaultValue = "200") Double price){

        Order order = new Order();
        order.setAmout(price);
        return ruleService.executeOrderRule(order);
    }
}
```
配置文件（配置文件仅为Spring最基本配置内容，不涉及Drools）

```yaml
server:
  port: 8088

spring:
  application:
    name: Drools-demo


```

> 运行项目，打开浏览器访问地址测试效果
> http://localhost:8088/order

本文仅为快速集成案例，个人学习笔记
