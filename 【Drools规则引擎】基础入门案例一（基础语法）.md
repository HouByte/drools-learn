@[TOC](【Drools规则引擎】基础入门案例一)
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
   <groupId>org.kie</groupId>
   <artifactId>kie-api</artifactId>
   <version>${drools.version}</version>
</dependency>
```
demo中为了简便使用了lombok和junit,可以根据自己的实际需要添加

```html
<dependency>
     <groupId>org.projectlombok</groupId>
     <artifactId>lombok</artifactId>
     <version>1.18.20</version>
 </dependency>
 <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
 ```

# 项目配置
src/test/resources/META-INF/kmodule.xml
```java
<?xml version="1.0" encoding="UTF-8"?>
<kmodule xmlns="http://jboss.org/kie/6.0.0/kmodule">
    <kbase name="hello" packages="rules" default="true">
        <ksession name="ksession-rule" default="true"/>
    </kbase>
</kmodule>
```

# 规则定义
规则文件中，一个规则的标准结构如下：
```java
rule "name"
    attributes
    when
        LHS
    then
        RHS
end
```

# 公共代码
案例使用Junit 测试，本办法未集成Spring依赖管理，其中这部分代码为公共部分，
获取KieSession
```java
private KieSession getKieSession() {
    //获取服务
    KieServices kieService = KieServices.Factory.get();
    //服务获取容器
    KieContainer container = kieService.getKieClasspathContainer();
    //获取session和规则引擎打交道
    return container.newKieSession();
}
```
执行全部且关闭会话
```java
private void run(KieSession kieSession) {
    //执行
    kieSession.fireAllRules();
    //关闭
    kieSession.dispose();
}
```
实体类Order 

```java
@Data
public class Order {

    private Double amout;
    private Integer score;

}
```
实体类Customer 
```java
@Data
public class Customer {

    private String name;
    private List<Order> orderList;
}
```

# 案例1：订单积分
规则文件：rules/score-rules.drl

```java
package rules;

import Order;

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
测试方法

```java
@Test
public void test1(){
    KieSession kieSession = getKieSession();

    //测试数据
    Order order = new Order();
    order.setAmout(234.1);
    //order.setAmout(534.1);
    //order.setAmout(1234.1);

    //提交数据
    kieSession.insert(order);

    //执行且关闭
    run(kieSession);
    System.out.println(order);
}
```

> 输出：
> 触发规则：100元-500元加100分
> Order(amout=234.1, score=100)



# 运算符
常规运算符与Java语言一致，如下为Drools特殊运算符

运算符| 描述
-------- | -----
contains | 检查一个Fact对象的某个属性是否包含一个指定的对象值
not contains | 检查一个Fact对象的某个属性是否不包含一个指定的对象值
memberOf | 判断一个Fact对象的某个属性是否在一个或者多个集合中
not memberOf | 判断一个Fact对象的某个属性是否不在一个或者多个集合中
matches | 判断一个Fact对象的某个属性是否与提供标准的Java正则表达式进行匹配
not matches | 判断一个Fact对象的某个属性是否不与提供标准的Java正则表达式进行匹配

# 案例2： 客户中包含和不包含订单
contains 和 not contains 
规则文件src/test/resources/rules/customer-rules-*.drl
```java
rule "customer-rules-1"
    when
    $order:Order();
    $customer:Customer(orderList contains $order)
    then
    System.out.println("orderList 包含 $order");
end

rule "customer-rules-2"
    when
    $order:Order();
    $customer:Customer(orderList not contains $order)
    then
    System.out.println("orderList 不包含 $order");
end
```

测试方法
```java
@Test
public void test2(){
    //获取服务
    KieSession kieSession = getKieSession();

    //测试数据
    Order order = new Order();
    order.setAmout(234.1);
    Customer customer = new Customer();
    List<Order> orderList = new ArrayList<>();
    //注释下行切换执行不包含案例
    orderList.add(order);
    customer.setOrderList(orderList);

    //提交数据
    kieSession.insert(order);
    kieSession.insert(customer);
    //执行且关闭
    run(kieSession);

    System.out.println(order);
}
```

> 输出：
> orderList 包含 $order
触发规则：100元-500元加100分 //本条由于未注释上文规则，全局执行时被一起执行了

# 案例3： 客户名字匹配
matches 和 not matches 
规则文件src/test/resources/rules/customer-rules-*.drl
```java
rule "customer-rules-3"
    when
    Customer(name matches "张.*")
    then
    System.out.println("name matches 张.*");
end

rule "customer-rules-4"
    when
    Customer(name not matches "张.*")
    then
    System.out.println("name not matches 张.*");
end
```
测试方法

```java
@Test
public void test3(){
    //获取服务
    KieSession kieSession = getKieSession();

    //测试数据
    Customer customer = new Customer();
    //下行切换注释执行不匹配案例
    //customer.setName("李四");
    customer.setName("张三");
    //提交数据
    kieSession.insert(customer);

    //执行且关闭
    run(kieSession);
}
```
> 输出：
> name not matches 张.*
# 指定规则名称
上述代码执行可以采用指定规则名称，上文未指定时，只要满足条件即可执行
```java
 kieSession.fireAllRules(new RuleNameEqualsAgendaFilter("规则名称匹配"));
 kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("规则名称结尾匹配"));
 kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("规则名称开头匹配"));
 kieSession.fireAllRules(new RuleNameMatchesAgendaFilter("规则名称正则表达式匹配"));
```


本文学习Drools个人学习笔记
