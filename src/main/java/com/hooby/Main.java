// Path: com.hooby.Main.java
package com.hooby;

import com.hooby.aop.*;
import com.hooby.db.JdbcUtils;
import com.hooby.http.*;
import com.hooby.ioc.*;
import com.hooby.servlet.*;
import com.hooby.tx.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            JdbcUtils.initSchema();

            // ApplicationContext 초기화
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
            // ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml"); // 리팩터링 할 것

            // AOP 프록시 설정
            ProxyBeanPostProcessor processor = new ProxyBeanPostProcessor();
            TransactionManager txManager = new TransactionManager();

            // pointcut -> confirm join point
            ExecutionPointcut pointcut = ExecutionPointcutParser.parse(
                    "execution(* *.*ServiceImpl.*(..))"
            );

//            ExecutionPointcut pointcut = ExecutionPointcutParser.parse(
//                    "execution(* com.hooby.service.UserServiceImpl.*(..))"
//            );

            // 트랜잭션 Advice
            processor.addAdvisor(new Advisor(pointcut, new TransactionAdvice(txManager)));

            // 로깅 Advice
            processor.addAdvisor(new Advisor(pointcut, new LoggingAdvice()));

            // PostProcessor 등록
            context.addPostProcessor(processor);

            // 서블릿 컨테이너 초기화
            ServletContainer container = (ServletContainer) context.getBean("servletContainer");
            CustomHttpServer server = new CustomHttpServer(8080, container);

            System.out.println("🟢 HTTP 서버가 8080 포트에서 시작되었습니다.");
            server.run();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("🟡 Shutdown Hook 실행 중...");
                context.close();
            }));

        } catch (Exception e) {
            logger.error("🔴 예기치 못한 에러 발생", e);
        }
    }
}