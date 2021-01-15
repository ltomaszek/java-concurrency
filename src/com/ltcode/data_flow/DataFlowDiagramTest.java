package com.ltcode.data_flow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test of the class DataFlowDiagram
 */
class DataFlowDiagramTest {

    private static DataFlowDiagram dfd;

    @BeforeAll
    static void setUp() {
        dfd = new DataFlowDiagram();
    }

    @Test
    void runDiagramPar() {
        long runtime = dfd.runDiagramPar();
        long maxExpectedRuntime = (long) (dfd.getCPL() * 1.1);

        assert runtime < maxExpectedRuntime :
                String.format("Max expected runtime was: %d, but actual runtime was: %d",
                        maxExpectedRuntime,
                        runtime);
    }

    @Test
    void runDiagramSeq() {
        long runtime = dfd.runDiagramSeq();
        long maxExpectedRuntime = (long) (dfd.getTotalWork() * 1.1);

        assert runtime < maxExpectedRuntime :
                String.format("Max expected runtime was: %d, but actual runtime was: %d",
                        maxExpectedRuntime,
                        runtime);
    }
}