package cn.flowboot.service;

import cn.flowboot.entity.Order;

/**
 * <h1>RuleService</h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/06
 */
public interface RuleService {

    /**
     * 订单通过规则引擎处理
     */
    Order executeOrderRule(Order order);
}
