@[TOC](【Drools规则引擎】基础入门案例五（Kie-Server+WorkBench）)

# 介绍

> **java语言开发的开源业务规则引擎**
[DROOLS](http://www.drools.org.cn/)（JBOSS RULES ）具有一个易于访问企业策略、易于调整以及易于管理的开源业务规则引擎，符合业内标准，速度快、效率高。业务分析师或审核人员可以利用它轻松查看业务规则，从而检验是否已编码的规则执行了所需的业务规则。

# 搭建WorkBench
本文采用Docker部署

```shell
docker pull jboss/drools-workbench-showcase
docker run -p 8080:8080 -p 8001:8001 -d --name drools-workbench jboss/drools-workbench-showcase:latest
```

# 搭建Kie-Server

```shell
docker pull jboss/kie-server-showcase
docker run -p 8180:8080 -d --name kie-server --link drools-workbench:kie_wb jboss/kie-server-showcase:latest
```

# 使用WorkBench
## 访问登入
> 浏览器访问：http://ip:port/business-central
> 输入账号 admin 密码 admin 登入

![WorkBench登入](https://img-blog.csdnimg.cn/e4546c6b6ac94ce584eab3a5ce796c74.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
点击 design 设计
![在这里插入图片描述](https://img-blog.csdnimg.cn/c58b1feadc7c47d2aab06d151d30ccdb.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
创建项目
![在这里插入图片描述](https://img-blog.csdnimg.cn/2002676d54964195827364555517aebf.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
填写项目信息
![填写项目信息](https://img-blog.csdnimg.cn/0c67081db72947c09c45b6a2f32b910c.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
添加包 (后续添加都是 Create New Asset 或者 Add Asset)
![添加包](https://img-blog.csdnimg.cn/05e90e56b48c40c7905a7af62b7008e3.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
添加数据对象
![添加数据对象](https://img-blog.csdnimg.cn/99b04b66da29449bb90e97cdb17e3e60.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
创建属性
![创建属性](https://img-blog.csdnimg.cn/efdef986f25c4a9bb7789fca51e47038.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
创建规则文件并编辑保存
![创建规则文件](https://img-blog.csdnimg.cn/fb12d5306a7d4e958cb94ce7316c2702.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
规则文件代码
```java
package cn.flowboot;


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
然后需要设置Kie Base
![设置Kie Base](https://img-blog.csdnimg.cn/d1b65947875a420da45d8a752379c90c.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
添加Kie Session
![添加session](https://img-blog.csdnimg.cn/ce497de4b9974e299281d7dcc967828e.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
构建和部署
![构建和部署](https://img-blog.csdnimg.cn/185e8344107f47bba4c4a5391238fd36.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
查看 kie-server
![查看 kie-server](https://img-blog.csdnimg.cn/d3cc5ea25ad44bb49b1d39a22e71e996.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)
配置进程保存
![在这里插入图片描述](https://img-blog.csdnimg.cn/0deab9997d754ad99e1170b029e77220.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAVmluY2VudCBWaWM=,size_20,color_FFFFFF,t_70,g_se,x_16)

# 客户端测试

```java
import cn.flowboot.entity.Order;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/09
 */
public class WorkBenchTest {
    public static final String SERVER_URL = "http://119.29.196.142:8180/kie-server/services/rest/server";
    public static final String PASSWORD = "kieserver1!";
    public static final String USERNAME = "kieserver";
    public static final String KIE_CONTAINER_ID = "order-relus";

    @Test
    public void workBenchTest() {
        // KisService 配置信息设置
        KieServicesConfiguration kieServicesConfiguration =
                KieServicesFactory.newRestConfiguration(SERVER_URL, USERNAME, PASSWORD, 10000L);
        kieServicesConfiguration.setMarshallingFormat(MarshallingFormat.JSON);

        // 创建规则服务客户端
        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(kieServicesConfiguration);
        RuleServicesClient ruleServicesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);

        // 规则输入条件
        Order order = new Order();
        order.setAmout(234.1);


        // 命令定义，包含插入数据，执行规则
        KieCommands kieCommands = KieServices.Factory.get().getCommands();
        List<Command<?>> commands = new LinkedList<Command<?>>();
        commands.add(kieCommands.newInsert(order, "order"));
        commands.add(kieCommands.newFireAllRules());
        ServiceResponse<ExecutionResults> results = ruleServicesClient.executeCommandsWithResults(KIE_CONTAINER_ID,
                kieCommands.newBatchExecution(commands,"kiesession1"));

        // 返回值读取
        Order value = (Order) results.getResult().getValue("order");
        System.out.println(JSONObject.toJSON(value).toString());
    }


}

```

> 输出 {"score":100,"amout":234.1}

修改规则，重新构建部署
```java
package cn.flowboot;


import cn.flowboot.entity.Order;

//100元以下，不加分
rule "score_1"
when
    $order:Order(amout<300)
then
    $order.setScore(0);
    System.out.println("触发规则：100元不加分");
end

//100元- 500元加100分
rule "score_2"
when
    $order:Order(amout>=300&&amout < 500)
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
重新运行，此时规则已经被动态修改
> 输出 ：{"score":0,"amout":234.1}
