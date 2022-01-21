package cn.flowboot.service.impl;

import cn.flowboot.entity.Order;
import cn.flowboot.service.RuleService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/06
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RuleServiceImplTest {

    @Autowired
    private RuleService ruleService;

    @Test
    public void executeOrderRule() {

        Order order = new Order();
        order.setAmout(120.00);
        Order order1 = ruleService.executeOrderRule(order);
        System.out.println(order1);
    }
}
