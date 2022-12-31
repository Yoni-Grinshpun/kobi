package org.example;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.util.concurrent.ThreadLocalRandom;

public class LongRunningProcess {

    int firstStageMinRunTimeSeconds = Integer.parseInt(System.getProperty("firstStageMinRunTimeSeconds", "20"));
    int firstStageMaxRunTimeSeconds = Integer.parseInt(System.getProperty("firstStageMaxRunTimeSeconds", "40"));
    int secondStageMinRunTimeSeconds = Integer.parseInt(System.getProperty("secondStageMinRunTimeSeconds", "20"));
    int secondStageMaxRunTimeSeconds = Integer.parseInt(System.getProperty("secondStageMaxRunTimeSeconds", "40"));
    int thirdStageMinRunTimeSeconds = Integer.parseInt(System.getProperty("thirdStageMinRunTimeSeconds", "20"));
    int thirdStageMaxRunTimeSeconds = Integer.parseInt(System.getProperty("thirdStageMaxRunTimeSeconds", "40"));

    public void firstStage() {
        int randomNum = ThreadLocalRandom.current().nextInt(firstStageMinRunTimeSeconds, firstStageMaxRunTimeSeconds + 1);
        try {
            Thread.sleep(randomNum * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void secondStage() {
        int randomNum = ThreadLocalRandom.current().nextInt(secondStageMinRunTimeSeconds, secondStageMaxRunTimeSeconds + 1);
        try {
            Thread.sleep(randomNum * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void thirdStage() {
        int randomNum = ThreadLocalRandom.current().nextInt(thirdStageMinRunTimeSeconds, thirdStageMaxRunTimeSeconds + 1);
        try {
            Thread.sleep(randomNum * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void runAllStagesContinuously(PrometheusMeterRegistry prometheusMeterRegistry, String environmentName) {
        // Counters for Counting Method Invocations
        Counter firstStageInvocationsCounter
                = prometheusMeterRegistry.counter("firstStageInvocations", "environment", environmentName);
        Counter secondStageInvocationsCounter
                = prometheusMeterRegistry.counter("secondStageInvocations", "environment", environmentName);
        Counter thirdStageInvocationsCounter
                = prometheusMeterRegistry.counter("thirdStageInvocations", "environment", environmentName);

        LongTaskTimer firstStageTimer
                = prometheusMeterRegistry.more().longTaskTimer("firstStageTimer", "environment", environmentName);
        LongTaskTimer secondStageTimer
                = prometheusMeterRegistry.more().longTaskTimer("secondStageTimer", "environment", environmentName);
        LongTaskTimer thirdStageTimer
                = prometheusMeterRegistry.more().longTaskTimer("thirdStageTimer", "environment", environmentName);

        // Run Multi-Staged Process
        while (true) {
            try {
                // First Stage
                firstStageInvocationsCounter.increment();
                firstStageTimer.record(() -> this.firstStage());

                // Second Stage
                secondStageInvocationsCounter.increment();
                secondStageTimer.record(() -> this.secondStage());

                // Third
                thirdStageInvocationsCounter.increment();
                thirdStageTimer.record(() -> this.thirdStage());

                // Wait Between Iterations
                Thread.sleep(5000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
