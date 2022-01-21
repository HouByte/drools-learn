package cn.flowboot.service.impl;

import cn.flowboot.entity.Order;
import cn.flowboot.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/06
 */
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
