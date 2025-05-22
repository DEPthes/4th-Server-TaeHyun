package com.hooby.monitor;

import java.lang.management.*;
import java.time.Duration;

public class SystemResourceMonitor implements Runnable {

    private final Runtime runtime = Runtime.getRuntime();
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    private final GarbageCollectorMXBean gcBean = ManagementFactory.getGarbageCollectorMXBeans().get(0);

    private final int availableProcessors = runtime.availableProcessors();
    private long lastCpuTime = 0;
    private long lastTime = System.nanoTime();
    private long lastGcCount = gcBean.getCollectionCount();
    private long lastGcTime = gcBean.getCollectionTime();

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(3000);

                // 현재 시간
                long now = System.nanoTime();

                // JVM 메모리 사용량
                long totalMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                long usedMemory = totalMemory - freeMemory;
                long maxMemory = runtime.maxMemory();

                // CPU 사용 시간 (이 JVM 프로세스의 누적 시간)
                long cpuTime = ProcessHandle.current()
                        .info()
                        .totalCpuDuration()
                        .orElse(Duration.ZERO)
                        .toNanos();

                // GC 정보
                long currentGcCount = gcBean.getCollectionCount();
                long currentGcTime = gcBean.getCollectionTime();
                long gcCountDelta = currentGcCount - lastGcCount;
                long gcTimeDelta = currentGcTime - lastGcTime;

                // CPU 사용률 계산 (전체 시스템 대비)
                double cpuUsage = 0;
                if (lastCpuTime > 0) {
                    long cpuDelta = cpuTime - lastCpuTime;
                    long timeDelta = now - lastTime;
                    cpuUsage = (double) cpuDelta / timeDelta * 100 / availableProcessors;
                }

                // 현재 스레드 수
                int liveThreads = threadBean.getThreadCount();

                // 출력
                System.out.printf("[JVM 모니터] CPU: %.2f%% | 메모리 사용: %.2f MB / %.2f MB (%.1f%%) | 스레드: %d | GC: %d회, 총 %dms%n",
                        cpuUsage,
                        usedMemory / 1024.0 / 1024.0,
                        maxMemory / 1024.0 / 1024.0,
                        (double) usedMemory / maxMemory * 100,
                        liveThreads,
                        gcCountDelta,
                        gcTimeDelta
                );

                // 다음 샘플 기준 갱신
                lastCpuTime = cpuTime;
                lastTime = now;
                lastGcCount = currentGcCount;
                lastGcTime = currentGcTime;

            } catch (InterruptedException e) {
                System.out.println("모니터링 종료");
                break;
            }
        }
    }
}