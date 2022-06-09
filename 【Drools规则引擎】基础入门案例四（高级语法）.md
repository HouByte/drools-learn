@[TOC](【Drools规则引擎】基础入门案例四（高级语法）)

# 介绍

> **java语言开发的开源业务规则引擎**
[DROOLS](http://www.drools.org.cn/)（JBOSS RULES ）具有一个易于访问企业策略、易于调整以及易于管理的开源业务规则引擎，符合业内标准，速度快、效率高。业务分析师或审核人员可以利用它轻松查看业务规则，从而检验是否已编码的规则执行了所需的业务规则。

# 关键字
关键字 | 描述
-------- | -----
package | 包名，只限于逻辑上的管理，同⼀个包名下的查询或者函数可以直接调⽤
import  | ⽤于导⼊类或者静态⽅法
global  | 全局变量
function  | ⾃定义函数
query |  查询
rule end  | 规则体

# 全局变量
Global 全局变量
```java
global java.util.List globalList;

rule "high-rules-1"
    when
        eval(true)
    then
        globalList.add("high-rules-1");
end

rule "high-rules-2"
    when
        eval(true)
    then
        globalList.add("high-rules-2");
        System.out.println("globalList size: "+globalList.size() +" toString "+globalList);
end
```
测试方法
```java
@Test
public void test10() throws InterruptedException {
    //获取服务
    KieSession kieSession = getKieSession();
    List list = new ArrayList();
    //设置全局变量，名称和类型必须和规则⽂件中定义的全局变量名称对应
    kieSession.setGlobal( "globalList", list );
    //执行且关闭
    run(kieSession);
    System.out.println(list);
}
```
> 测试：
> agenda-group 10 ...
globalList size: 2 toString [high-rules-1, high-rules-2]
[high-rules-1, high-rules-2]

# 自定义函数
定义格式： function 返回类型 函数名称(参数...){}

```java
function String format(String name) {
	return "Hi " + name;
}

rule "high-rules-3"
    when
      eval(true)
    then
        //调⽤上⾯定义的函数
        String ret = format("jack");
        System.out.println(ret);
end
```
测试方法
```java
@Test
public void test4(){
    //获取服务
    KieSession kieSession = getKieSession();
    //执行且关闭
    run(kieSession);
}
```
> 测试：Hi jack

# 查询
query 查询

```java
query 查询名称[(参数)]
	变量:条件
end
```

规则文件
```java
import cn.flowboot.entity.Order;

//不带参数的查询
//当前query⽤于查询Working Memory中amout > 100的Person对象
query "high-rules-query-1"
    $order : Order( amout > 100)
end
//带有参数的查询
////当前query⽤于查询Working Memory中amout > 参数amoutParam
query "high-rules-query-2"(Double amoutParam)
    $order : Order(amout > amoutParam)
end
```
测试方法
```java
@Test
public void test11() {
    //获取服务
    KieSession kieSession = getKieSession();
    //创建测试数据
    Order o1 = new Order();
    o1.setAmout(122.0);
    Order o2 = new Order();
    o2.setAmout(522.0);
    //添加内容
    kieSession.insert(o1);
    kieSession.insert(o2);
    //激活规则引擎，如果匹配成功则执⾏规则
    kieSession.fireAllRules();

    //查询
    QueryResults results = kieSession.getQueryResults("high-rules-query-1");
    System.out.println("results size is " + results.size());
    for(QueryResultsRow row : results){
        Order order = (Order) row.get("$order");
        System.out.println("Order 无参数获取 : " + order.getAmout());
    }

    QueryResults results2 = kieSession.getQueryResults("high-rules-query-2", 500.0);
    for(QueryResultsRow row : results2){
        Order order = (Order) row.get("$order");
        System.out.println("Order 带参数获取 : " + order.getAmout());
    }
    kieSession.dispose();
}
```

个人学习笔记，部分代码会在前面部分。
