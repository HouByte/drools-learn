package cn.flowboot.controller;

import cn.flowboot.entity.Order;
import cn.flowboot.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/06
 */
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
