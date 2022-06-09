@[TOC](【Drools规则引擎】基础入门案例三（属性）)

# 介绍

> **java语言开发的开源业务规则引擎**
[DROOLS](http://www.drools.org.cn/)（JBOSS RULES ）具有一个易于访问企业策略、易于调整以及易于管理的开源业务规则引擎，符合业内标准，速度快、效率高。业务分析师或审核人员可以利用它轻松查看业务规则，从而检验是否已编码的规则执行了所需的业务规则。

# 关键字
属性名 |  说明
-------- | -----
salience | 指定规则执⾏优先级
dialect | 指定规则使⽤的语⾔类型，取值为java和mvel (默认java)
enabled | 指定规则是否启⽤ (默认true)
date-effective | 指定规则⽣效时间
date-expires | 指定规则失效时间
activation-group | 激活分组，具有相同分组名称的规则只能有⼀个规则触发
agenda-group | 议程分组，只有获取焦点的组中的规则才有可能触发
auto-focus | ⾃动获取焦点，⼀般结合agenda-group⼀起使⽤
timer | 定时器，指定规则触发的时间
no-loop | 防⽌死循环


# 优先级
salience  [数字] :指定规则执⾏优先级,数字越大优先级越高
```java
rule "attrubutes-rules-1"
salience 1 //设置执行顺序
    when
        eval(true)
    then
        System.out.println("salience 1 ....");
end

rule "attrubutes-rules-2"
salience 2 //设置执行顺序
    when
        eval(true)
    then
        System.out.println("salience 2 ....");
end
```
测试方法

```java
public void test4(){
    //获取服务
    KieSession kieSession = getKieSession();
    //执行且关闭
    run(kieSession);
}
```
> 输出：
> salience 2 ....
salience 1 ....

#  生效时间
date-effective [时间字符串]： 指定规则⽣效时间，默认情况下可接受的⽇期格式为“dd-MMM-yyyy”，可以通过修改系统属性drools.dateformat来修改日期格式。

```java
rule "attrubutes-rules-3"
    date-effective "2022-1-10" //设置有效时间
    when
        eval(true)
    then
        System.out.println("date-effective ....");
end
```
测试方法

```java
/**
* attrubutes-rules-3
* 设置时间格式
*/
@Test
public void test7(){
	//设置系统属性修改时间格式
   System.setProperty("drools.dateformat","yyyy-MM-dd");
   //获取服务
   KieSession kieSession = getKieSession();
   //执行且关闭
   run(kieSession);
}
```

> 当前时间2022年1月9日 则无任何输出，未触发条件
> 当前时间2022年1月10日 则输出 date-effective ....

# 失效时间
date-expires  [时间字符串]： 指定规则失效时间，时间格式同有效时间格式一致
```java
rule "attrubutes-rules-4"
    date-expires "2022-1-10" //设置有效时间
    when
        eval(true)
    then
        System.out.println("date-expires ....");
end
```
测试方法同有效时间一致(test7)
 
> 当前时间2022年1月9日 则输出 date-expires ....
> 当前时间2022年1月10日 则无任何输出，未触发条件

# 激活分组
activation-group [分组名称] ：激活分组，具有相同分组名称的规则只能有⼀个规则触发
```java
rule "attrubutes-rules-5"
    activation-group "group"
    when
        eval(true)
    then
        System.out.println("activation-group 5 ...");
    end

rule "attrubutes-rules-6"
    activation-group "group"
    when
        eval(true)
    then
        System.out.println("activation-group 6 ...");
end
```
测试方法同优先级一致（test4）
> 输出：activation-group 5 ...

# 议程分组
agenda-group [分组字符串]: 议程分组，只有获取焦点的组中的规则才有可能触发，这样位于该 Agenda Group 当中的规则才会触发执⾏，否则将不执⾏。
```java
rule "attrubutes-rules-7"
    agenda-group "007"
    when
        eval(true)
    then
        System.out.println("agenda-group 7 ...");
    end

rule "attrubutes-rules-8"
    agenda-group "008"
    when
        eval(true)
    then
        System.out.println("agenda-group 8 ...");
end
```

测试方法

```java
@Test
public void test8(){
    //获取服务
    KieSession kieSession = getKieSession();
    //获得执⾏焦点
    kieSession.getAgenda().getAgendaGroup("008").setFocus();

    //执行且关闭
    run(kieSession);
    //指定规则名称
    //run(kieSession,"attrubutes-rules-6");
}

private void run(KieSession kieSession,String name) {
    //执行
    kieSession.fireAllRules(new RuleNameEqualsAgendaFilter(name));
    //关闭
    kieSession.dispose();
}
```
> 输出：agenda-group 8 ...

# ⾃动获取焦点

```java
rule "attrubutes-rules-9"
    agenda-group "009"
    when
        eval(true)
    then
        System.out.println("agenda-group 9 ...");
    end

rule "attrubutes-rules-10"
    agenda-group "010"
    auto-focus
    when
        eval(true)
    then
        System.out.println("agenda-group 10 ...");
end
```
测试方法同优先级一致（test4）
> 输出：agenda-group 10 ...

# 定时器
timer属性可以通过定时器的⽅式指定规则执⾏的时间，使⽤⽅式有两种：
## ⽅式⼀：timer (int: ?)
  此种⽅式遵循java.util.Timer对象的使⽤⽅式，第⼀个参数表示⼏秒后执⾏，第⼆个参数表示		每隔⼏秒
执⾏⼀次，第⼆个参数为可选。
## ⽅式⼆：timer(cron: )
此种⽅式使⽤标准的unix cron表达式的使⽤⽅式来定义规则执⾏的时间。
规则文件：
```java
import java.text.SimpleDateFormat
import java.util.Date
/*
测试timer属性
*/
rule "attrubutes-rules-11"
    //含义：5秒后触发，然后每隔2秒触发⼀次
    timer (5s 2s)
    when
    then
        System.out.println("attrubutes-rules-11 timer " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
end
rule "attrubutes-rules-12"
    //cron 语法 含义：每隔1秒触发⼀次
    timer (cron:0/1 * * * * ?)
    when
    then
        System.out.println("attrubutes-rules-12 timer " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
end
```
测试方法

```java
@Test
public void test9() throws InterruptedException {
    //设置修改默认的时间格式
    System.setProperty("drools.dateformat","yyyy-MM-dd");
    //获取服务
    KieSession kieSession = getKieSession();
    //启动规则引擎进⾏规则匹配，直到调⽤halt⽅法才结束规则引擎
    new Thread(kieSession::fireUntilHalt).start();
    //睡眠主线程，阻止结束规则引擎
    Thread.sleep(10000);
    //结束规则引擎
    kieSession.halt();
    kieSession.dispose();
}
```
> 输出：
> attrubutes-rules-12 timer 2022-01-09 11:30:51
attrubutes-rules-12 timer 2022-01-09 11:30:52
attrubutes-rules-12 timer 2022-01-09 11:30:53
attrubutes-rules-12 timer 2022-01-09 11:30:54
attrubutes-rules-12 timer 2022-01-09 11:30:55
attrubutes-rules-11 timer 2022-01-09 11:30:55
attrubutes-rules-12 timer 2022-01-09 11:30:56
attrubutes-rules-12 timer 2022-01-09 11:30:57
attrubutes-rules-11 timer 2022-01-09 11:30:57
attrubutes-rules-12 timer 2022-01-09 11:30:58
attrubutes-rules-12 timer 2022-01-09 11:30:59
attrubutes-rules-11 timer 2022-01-09 11:30:59
attrubutes-rules-12 timer 2022-01-09 11:31:00

这边两个规则都执行了，如需分开执行，指定规则名称或直接注释后执行

# 防⽌死循环
no-loop [布尔]： 防止死循环，如果未设置，会产生死循环
```java
import cn.flowboot.entity.Customer;

rule "attrubutes-rules-13"
    no-loop true
    when
       $customer:Customer(name == "李四")
    then
        $customer.setName("李四");
        update($customer);
        System.out.println("no-loop....");
end
```
测试方法

```java
 @Test
public void test5(){
    //获取服务
    KieSession kieSession = getKieSession();
    //测试数据
    Customer customer = new Customer();
    customer.setName("李四");
    //提交数据
    kieSession.insert(customer);
    //执行且关闭
    run(kieSession);
}
```
> 设置no-loop:true后输出：no-loop....

个人学习笔记，部分代码会在前面部分。
