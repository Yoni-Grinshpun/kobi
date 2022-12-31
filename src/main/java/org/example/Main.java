package org.example;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        // Load Application Context
        ApplicationContext context = new AnnotationConfigApplicationContext(BeanConfiguration.class);

        // Resolve Beans
        ClassLoaderMetrics classLoaderMetrics = context.getBean("classLoaderMetrics", ClassLoaderMetrics.class);
        JvmMemoryMetrics jvmMemoryMetrics = context.getBean("jvmMemoryMetrics", JvmMemoryMetrics.class);
        JvmGcMetrics jvmGcMetrics = context.getBean("jvmGcMetrics", JvmGcMetrics.class);
        ProcessorMetrics processorMetrics = context.getBean("processorMetrics", ProcessorMetrics.class);
        JvmThreadMetrics jvmThreadMetrics = context.getBean("jvmThreadMetrics", JvmThreadMetrics.class);
        PrometheusMeterRegistry prometheusMeterRegistry = context.getBean("prometheusMeterRegistry", PrometheusMeterRegistry.class);
        LongRunningProcess longRunningProcess = context.getBean("longRunningProcess", LongRunningProcess.class);
        HttpServer httpServer = context.getBean("httpServer", HttpServer.class);

        // Bind Generic Metrics Providers to Prometheus Registry
        classLoaderMetrics.bindTo(prometheusMeterRegistry);
        jvmMemoryMetrics.bindTo(prometheusMeterRegistry);
        jvmGcMetrics.bindTo(prometheusMeterRegistry);
        processorMetrics.bindTo(prometheusMeterRegistry);
        jvmThreadMetrics.bindTo(prometheusMeterRegistry);

        // Instantiate And Start an Http Endpoint for Prometheus Metrics Scraping
        new Thread(httpServer::start).start();

        new Thread(() -> {
            System.out.println("longRunningProcess.runAllStagesContinuously -> Invoking");
            longRunningProcess.runAllStagesContinuously(prometheusMeterRegistry, "development");
        }).start();

        new Thread(() -> {
            System.out.println("longRunningProcess.runAllStagesContinuously -> Invoking");
            longRunningProcess.runAllStagesContinuously(prometheusMeterRegistry, "test");
        }).start();

        new Thread(() -> {
            System.out.println("longRunningProcess.runAllStagesContinuously -> Invoking");
            longRunningProcess.runAllStagesContinuously(prometheusMeterRegistry, "production");
        }).start();

    }
}