package com.alibaba.springcloud.e2e.cases.nacos.naming;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.springcloud.e2e.core.SpringCloudAlibaba;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.alibaba.springcloud.e2e.cases.nacos.naming.base.NamingBase.*;

/**
 * Nacos多租户测试
 */
@Slf4j
@SpringCloudAlibaba(composeFiles = "docker/nacos-compose-test.yml", serviceName = "nacos-standalone")
public class MultiTenant_ITCase {
    
    private static NamingService naming;
    
    private static NamingService naming1;
    
    private static NamingService naming2;
    
    private volatile List<Instance> instances = Collections.emptyList();
    
    private static final String port = "8848";
    
    
    @BeforeAll
    public static void setUp() {
        
        try {
            naming = NamingFactory.createNamingService("127.0.0.1"+ ":" + port);
        }
        catch (NacosException e) {
            e.printStackTrace();
        }
    
        while (true) {
            if (!"UP".equals(naming.getServerStatus())) {
               
                continue;
            }
            break;
        }
    
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESPACE, "namespace-1");
        properties.put(PropertyKeyConst.SERVER_ADDR, "127.0.0.1" + ":" + port);
        try {
            naming1 = NamingFactory.createNamingService(properties);
        }
        catch (NacosException e) {
            e.printStackTrace();
        }
    
        properties = new Properties();
        properties.put(PropertyKeyConst.NAMESPACE, "namespace-2");
        properties.put(PropertyKeyConst.SERVER_ADDR, "127.0.0.1" + ":" + port);
        try {
            naming2 = NamingFactory.createNamingService(properties);
        }
        catch (NacosException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @TCDescription : 多租户注册IP，port不相同实例
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(10)
    public void multipleTenant_registerInstance() throws Exception {
        String serviceName = randomDomainName();
        
        naming1.registerInstance(serviceName, "11.11.11.11", 80);
        
        naming2.registerInstance(serviceName, "22.22.22.22", 80);
        
        naming.registerInstance(serviceName, "33.33.33.33", 8888);
        
        TimeUnit.SECONDS.sleep(5L);
        
        List<Instance> instances = naming1.getAllInstances(serviceName);
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals("11.11.11.11", instances.get(0).getIp());
        Assert.assertEquals(80, instances.get(0).getPort());
        
        instances = naming2.getAllInstances(serviceName);
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals("22.22.22.22", instances.get(0).getIp());
        Assert.assertEquals(80, instances.get(0).getPort());
        
        instances = naming.getAllInstances(serviceName);
        Assert.assertEquals(1, instances.size());
    }
    
    /**
     * @TCDescription : 多Group注册实例
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(9)
    public void multipleTenant_multiGroup_registerInstance() throws Exception {
        String serviceName = randomDomainName();
        
        naming1.registerInstance(serviceName, TEST_GROUP_1,"11.11.11.11", 80);
        
        naming2.registerInstance(serviceName, TEST_GROUP_2,"22.22.22.22", 80);
        
        naming.registerInstance(serviceName, "33.33.33.33", 8888);
        
        TimeUnit.SECONDS.sleep(5L);
        
        List<Instance> instances = naming1.getAllInstances(serviceName);
        Assert.assertEquals(0, instances.size());
        
        instances = naming2.getAllInstances(serviceName, TEST_GROUP_2);
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals("22.22.22.22", instances.get(0).getIp());
        Assert.assertEquals(80, instances.get(0).getPort());
        
        instances = naming.getAllInstances(serviceName);
        Assert.assertEquals(1, instances.size());
        
        naming1.deregisterInstance(serviceName, TEST_GROUP_1,"11.11.11.11", 80);
        naming1.deregisterInstance(serviceName, TEST_GROUP_2,"22.22.22.22", 80);
    }
    
    /**
     * @TCDescription : 多租户注册IP，port相同的实例
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(8)
    public void multipleTenant_equalIP() throws Exception {
        String serviceName = randomDomainName();
        naming1.registerInstance(serviceName, "11.11.11.11", 80);
        
        naming2.registerInstance(serviceName, "11.11.11.11", 80);
        
        naming.registerInstance(serviceName, "11.11.11.11", 80);
        
        TimeUnit.SECONDS.sleep(5L);
        
        List<Instance> instances = naming1.getAllInstances(serviceName);
        
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals("11.11.11.11", instances.get(0).getIp());
        Assert.assertEquals(80, instances.get(0).getPort());
        
        instances = naming2.getAllInstances(serviceName);
        
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals("11.11.11.11", instances.get(0).getIp());
        Assert.assertEquals(80, instances.get(0).getPort());
        
        instances = naming.getAllInstances(serviceName);
        Assert.assertEquals(1, instances.size());
    }
    
    /**
     * @TCDescription : 多租户注册IP，port相同的实例
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(7)
    public void multipleTenant_selectInstances() throws Exception {
        String serviceName = randomDomainName();
        naming1.registerInstance(serviceName, TEST_IP_4_DOM_1, TEST_PORT);
        
        naming2.registerInstance(serviceName, "22.22.22.22", 80);
        
        naming.registerInstance(serviceName, TEST_IP_4_DOM_1, TEST_PORT);
        
        TimeUnit.SECONDS.sleep(5L);
        
        List<Instance> instances = naming1.selectInstances(serviceName, true);
        
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals(TEST_IP_4_DOM_1, instances.get(0).getIp());
        Assert.assertEquals(TEST_PORT, instances.get(0).getPort());
        
        instances = naming2.selectInstances(serviceName, false);
        Assert.assertEquals(0, instances.size());
        
        
        instances = naming.selectInstances(serviceName, true);
        Assert.assertEquals(1, instances.size());
    }
    
    /**
     * @TCDescription : 多租户,多Group注册IP，port相同的实例
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(6)
    public void multipleTenant_group_equalIP() throws Exception {
        String serviceName = randomDomainName();
        naming1.registerInstance(serviceName, TEST_GROUP_1,"11.11.11.11", 80);
        
        naming2.registerInstance(serviceName, TEST_GROUP_2,"11.11.11.11", 80);
        
        naming.registerInstance(serviceName, Constants.DEFAULT_GROUP,"11.11.11.11", 80);
        
        TimeUnit.SECONDS.sleep(5L);
        
        List<Instance> instances = naming1.getAllInstances(serviceName);
        
        Assert.assertEquals(0, instances.size());
        
        instances = naming2.getAllInstances(serviceName, TEST_GROUP_2);
        
        Assert.assertEquals(1, instances.size());
        Assert.assertEquals("11.11.11.11", instances.get(0).getIp());
        Assert.assertEquals(80, instances.get(0).getPort());
        
        instances = naming.getAllInstances(serviceName);
        Assert.assertEquals(1, instances.size());
    }
    
    /**
     * @TCDescription : 多租户,多Group注册IP，port相同的实例, 通过group获取实例
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(5)
    public void multipleTenant_group_getInstances() throws Exception {
        String serviceName = randomDomainName();
        System.out.println(serviceName);
        naming1.registerInstance(serviceName, TEST_GROUP_1,"11.11.11.11", 80);
        // will cover the instance before
        naming1.registerInstance(serviceName, TEST_GROUP_2,"11.11.11.11", 80);
        
        naming.registerInstance(serviceName, Constants.DEFAULT_GROUP,"11.11.11.11", 80);
        
        TimeUnit.SECONDS.sleep(5L);
        List<Instance> instances = naming1.getAllInstances(serviceName, TEST_GROUP);
        
        Assert.assertEquals(0, instances.size());
        
        instances = naming.getAllInstances(serviceName);
        Assert.assertEquals(1, instances.size());
        naming1.deregisterInstance(serviceName, TEST_GROUP_1,"11.11.11.11", 80);
        naming1.deregisterInstance(serviceName, TEST_GROUP_2,"11.11.11.11", 80);
    }
    
    /**
     * @TCDescription : 多租户同服务获取实例
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(4)
    public void multipleTenant_getServicesOfServer() throws Exception {
        
        String serviceName = randomDomainName();
        naming1.registerInstance(serviceName, "11.11.11.11", TEST_PORT, "c1");
        TimeUnit.SECONDS.sleep(5L);
        
        ListView<String> listView = naming1.getServicesOfServer(1, 200);
        
        naming2.registerInstance(serviceName, "33.33.33.33", TEST_PORT, "c1");
        TimeUnit.SECONDS.sleep(5L);
        ListView<String> listView1 = naming1.getServicesOfServer(1, 200);
        Assert.assertEquals(listView.getCount(), listView1.getCount());
    }
    
    /**
     * @TCDescription : 多租户, 多group，同服务获取实例
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(3)
    public void multipleTenant_group_getServicesOfServer() throws Exception {
        
        String serviceName = randomDomainName();
        naming1.registerInstance(serviceName, TEST_GROUP_1, "11.11.11.11",  TEST_PORT, "c1");
        // will cover the instance before
        naming1.registerInstance(serviceName, TEST_GROUP_2, "22.22.22.22",  TEST_PORT, "c1");
        TimeUnit.SECONDS.sleep(5L);
        
        //服务不会删除，实例会注销
        ListView<String> listView = naming1.getServicesOfServer(1, 20, TEST_GROUP_1);
        
        naming2.registerInstance(serviceName, "33.33.33.33", TEST_PORT, "c1");
        TimeUnit.SECONDS.sleep(5L);
        ListView<String> listView1 = naming1.getServicesOfServer(1, 20, TEST_GROUP_1);
        Assert.assertEquals(listView.getCount(), listView1.getCount());
        Assert.assertNotEquals(0, listView1.getCount());
    }
    
    /**
     * @TCDescription : 多租户订阅服务
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(2)
    public void multipleTenant_subscribe() throws Exception {
        
        String serviceName = randomDomainName();
        
        naming1.subscribe(serviceName, new EventListener() {
            @Override
            public void onEvent(Event event) {
                instances = ((NamingEvent) event).getInstances();
            }
        });
        
        naming1.registerInstance(serviceName, "11.11.11.11", TEST_PORT, "c1");
        naming2.registerInstance(serviceName, "33.33.33.33", TEST_PORT, "c1");
        
        while (instances.size() == 0) {
            TimeUnit.SECONDS.sleep(1L);
        }
        Assert.assertEquals(1, instances.size());
        
        TimeUnit.SECONDS.sleep(2L);
        Assert.assertTrue(verifyInstanceList(instances, naming1.getAllInstances(serviceName)));
    }
    
    /**
     * @TCDescription : 多租户多group订阅服务
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(2)
    public void multipleTenant_group_subscribe() throws Exception {
        
        String serviceName = randomDomainName();
        
        naming1.subscribe(serviceName, TEST_GROUP_1, new EventListener() {
            @Override
            public void onEvent(Event event) {
                instances = ((NamingEvent) event).getInstances();
            }
        });
        
        naming1.registerInstance(serviceName, "11.11.11.11", TEST_PORT, "c1");
        naming1.registerInstance(serviceName, TEST_GROUP_1,"33.33.33.33", TEST_PORT, "c1");
        
        while (instances.size() == 0) {
            TimeUnit.SECONDS.sleep(1L);
        }
        TimeUnit.SECONDS.sleep(2L);
        Assert.assertEquals(1, instances.size());
        
        TimeUnit.SECONDS.sleep(2L);
        Assert.assertTrue(verifyInstanceList(instances, naming1.getAllInstances(serviceName, TEST_GROUP_1)));
        
        naming1.deregisterInstance(serviceName, "11.11.11.11", TEST_PORT, "c1");
        naming1.deregisterInstance(serviceName, TEST_GROUP_1,"33.33.33.33", TEST_PORT, "c1");
    }
    
    /**
     * @TCDescription : 多租户取消订阅服务
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    @Order(1)
    public void multipleTenant_unSubscribe() throws Exception {
        
        String serviceName = randomDomainName();
        EventListener listener = new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(((NamingEvent)event).getServiceName());
                instances = ((NamingEvent)event).getInstances();
            }
        };
        
        naming1.subscribe(serviceName, listener);
        naming1.registerInstance(serviceName, "11.11.11.11", TEST_PORT, "c1");
        naming2.registerInstance(serviceName, "33.33.33.33", TEST_PORT, "c1");
        
        while (instances.size() == 0) {
            TimeUnit.SECONDS.sleep(1L);
        }
        Assert.assertEquals(serviceName, naming1.getSubscribeServices().get(0).getName());
        Assert.assertEquals(0, naming2.getSubscribeServices().size());
        
        naming1.unsubscribe(serviceName, listener);
        
        TimeUnit.SECONDS.sleep(5L);
        Assert.assertEquals(0, naming1.getSubscribeServices().size());
        Assert.assertEquals(0, naming2.getSubscribeServices().size());
    }
    
    /**
     * @TCDescription : 多租户,多group下, 没有对应的group订阅，取消订阅服务
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    public void multipleTenant_group_nosubscribe_unSubscribe() throws Exception {
        
        String serviceName = randomDomainName();
        EventListener listener = new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(((NamingEvent)event).getServiceName());
                instances = ((NamingEvent)event).getInstances();
            }
        };
        
        naming1.subscribe(serviceName, TEST_GROUP_1, listener);
        naming1.registerInstance(serviceName, "11.11.11.11", TEST_PORT, "c1");
        naming1.registerInstance(serviceName, TEST_GROUP_2,"33.33.33.33", TEST_PORT, "c1");
        
        TimeUnit.SECONDS.sleep(3L);
        Assert.assertEquals(serviceName, naming1.getSubscribeServices().get(0).getName());
        Assert.assertEquals(0, naming2.getSubscribeServices().size());
        
        naming1.unsubscribe(serviceName, listener);    //取消订阅服务，没有订阅group
        TimeUnit.SECONDS.sleep(3L);
        Assert.assertEquals(1, naming1.getSubscribeServices().size());
        
        naming1.unsubscribe(serviceName, TEST_GROUP_1, listener);   //取消订阅服务，有订阅group
        TimeUnit.SECONDS.sleep(3L);
        Assert.assertEquals(0, naming1.getSubscribeServices().size());
        
        Assert.assertEquals(0, naming2.getSubscribeServices().size());
    }
    
    /**
     * @TCDescription : 多租户,多group下, 多个group订阅，查看服务的个数
     * @TestStep :
     * @ExpectResult :
     */
    @Test
    public void multipleTenant_group_unSubscribe() throws Exception {
        
        String serviceName = randomDomainName();
        EventListener listener = new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(((NamingEvent)event).getServiceName());
                instances = ((NamingEvent)event).getInstances();
            }
        };
        
        naming1.subscribe(serviceName, Constants.DEFAULT_GROUP, listener);
        naming1.subscribe(serviceName, TEST_GROUP_2, listener);
        naming1.subscribe(serviceName, TEST_GROUP_1, listener);
        
        naming1.registerInstance(serviceName, "11.11.11.11", TEST_PORT, "c1");
        naming1.registerInstance(serviceName, TEST_GROUP_2,"33.33.33.33", TEST_PORT, "c1");
        
        while (instances.size() == 0) {
            TimeUnit.SECONDS.sleep(1L);
        }
        TimeUnit.SECONDS.sleep(2L);
        Assert.assertEquals(serviceName, naming1.getSubscribeServices().get(0).getName());
        Assert.assertEquals(3, naming1.getSubscribeServices().size());
        
        naming1.unsubscribe(serviceName, listener);
        naming1.unsubscribe(serviceName, TEST_GROUP_2, listener);
        TimeUnit.SECONDS.sleep(3L);
        Assert.assertEquals(1, naming1.getSubscribeServices().size());
        Assert.assertEquals(TEST_GROUP_1, naming1.getSubscribeServices().get(0).getGroupName());
        
        naming1.unsubscribe(serviceName, TEST_GROUP_1, listener);
    }
    
}
