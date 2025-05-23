package com.hooby;

import com.hooby.aop.*;
import com.hooby.db.JdbcUtils;
import com.hooby.http.*;
import com.hooby.ioc.*;
import com.hooby.monitor.SystemResourceMonitor;
import com.hooby.servlet.*;
import com.hooby.tx.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            JdbcUtils.initSchema();

            // 모니터링 스레드 실행
            new Thread(new SystemResourceMonitor()).start();

            // ApplicationContext 초기화
            ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

            // AOP 프록시 설정
            BeanPostProcessor processor = new BeanPostProcessor();
            TransactionManager txManager = new TransactionManager();

            // pointcut -> confirm join point
            ExecutionPointcut pointcut = ExecutionPointcutParser.parse(
                    "execution(* com.hooby.service.UserServiceImpl.*(..))"
            );

            processor.addAdvisor(new Advisor(pointcut, new TransactionAdvice(txManager))); // 트랜잭션 Advice
            processor.addAdvisor(new Advisor(pointcut, new LoggingAdvice())); // 로깅 Advice
            context.addPostProcessor(processor); // PostProcessor 등록

            // 서블릿 컨테이너 초기화
            ServletContainer container = (ServletContainer) context.getBean("servletContainer");
            CustomHttpServer server = new CustomHttpServer(8080, container);

            try {
                server.run();
            } finally {
                context.close();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutdown Hook 실행 중...");
                context.close();
            }));

        } catch (Exception e) {
            logger.error("🔴 예기치 못한 에러 발생", e);
        }
    }
}