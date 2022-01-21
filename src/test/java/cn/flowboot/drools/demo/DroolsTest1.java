package cn.flowboot.drools.demo;

import cn.flowboot.entity.Customer;
import cn.flowboot.entity.Order;
import org.drools.core.base.RuleNameEndsWithAgendaFilter;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import java.util.ArrayList;
import java.util.List;


/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/03
 */

public class DroolsTest1 {

    @Test
    public void test1(){
        KieSession kieSession = getKieSession();

        //测试数据
        Order order = new Order();
        order.setAmout(234.1);

        //提交数据
        kieSession.insert(order);

        //执行且关闭
        run(kieSession);
        System.out.println(order);
    }

    private KieSession getKieSession() {
        //获取服务
        KieServices kieService = KieServices.Factory.get();
        //服务获取容器
        KieContainer container = kieService.getKieClasspathContainer();
        //获取session和规则引擎打交道
        return container.newKieSession();
    }

    @Test
    public void test2(){
        //获取服务
        KieSession kieSession = getKieSession();

        //测试数据
        Order order = new Order();
        order.setAmout(234.1);
        Customer customer = new Customer();
        List<Order> orderList = new ArrayList<>();
       // orderList.add(order);
        customer.setOrderList(orderList);

        //提交数据
        kieSession.insert(order);
        kieSession.insert(customer);
        //执行且关闭
        run(kieSession);

        System.out.println(order);
    }

    /**
     * 执行且关闭
     * @param kieSession
     */
    private void run(KieSession kieSession) {
        //执行
        kieSession.fireAllRules();
        //关闭
        kieSession.dispose();
    }

    /**
     * 执行且关闭
     * @param kieSession
     */
    private void run(KieSession kieSession,String name) {
        //执行
        kieSession.fireAllRules(new RuleNameEqualsAgendaFilter(name));
        //关闭
        kieSession.dispose();
    }

    @Test
    public void test3(){
        //获取服务
        KieSession kieSession = getKieSession();

        //测试数据
        Order order = new Order();
        order.setAmout(234.1);
        Customer customer = new Customer();
        List<Order> orderList = new ArrayList<>();
       // orderList.add(order);
        customer.setOrderList(orderList);
        customer.setName("张三");
        //customer.setName("李四");
        //提交数据
        kieSession.insert(order);
        kieSession.insert(customer);

        //执行
        //kieSession.fireAllRules(new RuleNameEqualsAgendaFilter("customer-rules-3"));
//        kieSession.fireAllRules(new RuleNameEndsWithAgendaFilter("-rules-3"));
       // kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("customer-rules-"));
        kieSession.fireAllRules(new RuleNameMatchesAgendaFilter("customer-.*-3"));
        kieSession.dispose();
        System.out.println(order);
    }

    @Test
    public void test4(){
        //获取服务
        KieSession kieSession = getKieSession();

        //执行且关闭
        run(kieSession);
    }

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


    /**
     * attrubutes-rules-1 ~ 2
     */
    @Test
    public void test6(){
        //获取服务
        KieSession kieSession = getKieSession();

        //执行且关闭
        run(kieSession);
    }

    /**
     * attrubutes-rules-3
     * 设置时间格式
     */
    @Test
    public void test7(){
        System.setProperty("drools.dateformat","yyyy-MM-dd");
        //获取服务
        KieSession kieSession = getKieSession();

        //执行且关闭
        run(kieSession);
    }

    @Test
    public void test8(){
        //获取服务
        KieSession kieSession = getKieSession();
        //获得执⾏焦点
        kieSession.getAgenda().getAgendaGroup("008").setFocus();

        //执行且关闭
        run(kieSession);
        //指定规则名称
        //run(kieSession,"attrubutes-rules-8");
    }

    @Test
    public void test9() throws InterruptedException {
        //设置修改默认的时间格式
        System.setProperty("drools.dateformat","yyyy-MM-dd");
        //获取服务
        KieSession kieSession = getKieSession();
        //启动规则引擎进⾏规则匹配，直到调⽤halt⽅法才结束规则引擎
        new Thread(kieSession::fireUntilHalt).start();
        Thread.sleep(10000);
        //结束规则引擎
        kieSession.halt();
        kieSession.dispose();
    }

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

}
