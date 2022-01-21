


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
