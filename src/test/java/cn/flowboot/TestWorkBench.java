package cn.flowboot;

import cn.flowboot.entity.Order;
import org.drools.core.io.impl.UrlResource;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.InputStream;

/**
 * <h1></h1>
 *
 * @version 1.0
 * @author: Vincent Vic
 * @since: 2022/01/09
 */
public class TestWorkBench {

    @Test
    public void test1() throws Exception {
        //通过此URL可以访问到maven仓库中的jar包
        //URL地址构成：http://ip地址:Tomcat端⼝号/WorkBench⼯程名/maven2/坐标/版本号/xxx.jar
        String url = "http://119.29.196.142:8080/order-rules/maven2/cn/flowboot/order-rules/1.0.0-SNAPSHOT/order-rules-1.0.0-SNAPSHOT.jar";
        KieServices kieServices = KieServices.Factory.get();
        //通过Resource资源对象加载jar包
        UrlResource resource = (UrlResource)
                kieServices.getResources().newUrlResource(url);
        //通过Workbench提供的服务来访问maven仓库中的jar包资源，需要先进⾏Workbench的认证
        resource.setUsername("kieserver");
        resource.setPassword("kieserver1!");
        resource.setBasicAuthentication("enabled");
        //将资源转换为输⼊流，通过此输⼊流可以读取jar包数据
        InputStream inputStream = resource.getInputStream();
        //创建仓库对象，仓库对象中保存Drools的规则信息
        KieRepository repository = kieServices.getRepository();
        //通过输⼊流读取maven仓库中的jar包数据，包装成KieModule模块添加到仓库中
        KieModule kieModule = repository.addKieModule(kieServices.getResources().newInputStreamResource(inputStream));
        //基于KieModule模块创建容器对象，从容器中可以获取session会话
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        KieSession session = kieContainer.newKieSession();
        Order order = new Order();
        order.setAmout(1000.0);
        session.insert(order);
        session.fireAllRules();
        session.dispose();

    }

}
