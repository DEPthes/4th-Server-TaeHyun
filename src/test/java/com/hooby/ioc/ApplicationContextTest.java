package com.hooby.ioc;

import com.hooby.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationContextTest {

    ApplicationContext context = new ApplicationContext("test-beans.xml");

    @AfterEach
    void tearDown() {
        context.close(); // destroy-method 실행 검증
    }

    @Test
    void testGetBeanAndDI() {
        Object bean = context.getBean("userService");
        assertNotNull(bean);
        assertTrue(bean instanceof UserService);

        UserService service = (UserService) bean;
        String result = service.serve();
        assertEquals("UserService is serving → hooby", result);
    }

    @Test
    void testSingletonScope() {
        Object b1 = context.getBean("userService");
        Object b2 = context.getBean("userService");
        System.out.println("싱글톤을 만족하는가? : " + b1.equals(b2));
        assertSame(b1, b2); // 싱글톤 보장
    }

    @Test
    void testMissingBeanThrowsException() {
        assertThrows(RuntimeException.class, () -> {
            context.getBean("noSuchBean");
        });
    }
}