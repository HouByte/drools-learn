@[TOC](【Drools规则引擎】基础入门案例二)


# 前言
## 介绍

> **java语言开发的开源业务规则引擎**
[DROOLS](http://www.drools.org.cn/)（JBOSS RULES ）具有一个易于访问企业策略、易于调整以及易于管理的开源业务规则引擎，符合业内标准，速度快、效率高。业务分析师或审核人员可以利用它轻松查看业务规则，从而检验是否已编码的规则执行了所需的业务规则。

# 宏函数
insert  、update 和  retract 可在java代码和规则结果中执行，执行时会重新执行匹配一次所有可以被匹配的规则，no-loop 默认 false，未设置true在规则文件中使用insert 和update 和可能存在死循环的情况，根据业务规则决定是否设置。
函数名称| 介绍
-------- | -----
insert | 将一个 Fact 对象插入到当前的工作内存
update | 对当前工作内存中的 Fact 进行更新
retract | 从工作内存中删除某个 Fact 对象

# insert 
规则文件src/test/resources/rules/customer-rules-*.drl
```java
rule "customer-rules-5"
    when
        eval(true)
    then
    Customer cus = new Customer();
    cus.setName("张三");
    insert(cus);
    System.out.println("insert....");
end

rule "customer-rules-6"
    when
        $customer:Customer(name == "张三")
    then
        System.out.println("insert 获取名字为张三的对象 $customer  name :"+$customer.getName());

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

# update  
规则文件src/test/resources/rules/customer-rules-*.drl

```java
rule "customer-rules-7"
    no-loop true
    when
       $customer:Customer(name == "李四")
    then
	    $customer.setName("张三");
	    update($customer);
	    System.out.println("update....");
end

rule "customer-rules-8"
    when
        $customer:Customer(name == "张三")
    then
        System.out.println("update 获取名字为张三的对象$customer  name :"+$customer.getName());

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

# retract 
规则文件src/test/resources/rules/customer-rules-*.drl

```java
rule "customer-rules-9"
    no-loop true
    when
       $customer:Customer(name == "李四")
    then

        retract($customer);
        System.out.println("retract....");
end

rule "customer-rules-10"
    when
        $customer:Customer()
    then
        System.out.println("retract 获取名字为张三的对象 $customer  name :"+$customer.getName());

end
```
测试方法（同update一致）
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

本文学习Drools个人学习笔记
