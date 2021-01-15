package com.ltcode.data_flow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * Class shows the use of class Phaser in running the below Diagram
 *
 *         Data Flow Diagram
 *             A      B       TOTAL_WORK = 7 * 1000 = 7000 milliseconds
 *            / \  / | \             CPL = 3 * 3000 = 3000 milliseconds  (critical path length A -> D -> G)
 *           C   D   E   F
 *              /
 *             G            Each task can run only if the 'parent' task has finished
 */
public class DataFlowDiagram {

    private final int NUM_WORKERS = 7;
    private final int WORKLOAD = 1000;    // milliseconds

    private final Worker A = new Worker("A", WORKLOAD);
    private final Worker B = new Worker("B", WORKLOAD);
    private final Worker C = new Worker("C", WORKLOAD, A);
    private final Worker D = new Worker("D", WORKLOAD, A, B);
    private final Worker E = new Worker("E", WORKLOAD, B);
    private final Worker F = new Worker("F", WORKLOAD, B);
    private final Worker G = new Worker("G", WORKLOAD, D);

    /**
     * Runs the Data Flow Diagram parallel
     *
     * @return Running time in milliseconds
     */
    public long runDiagramPar() {
        ExecutorService es = Executors.newFixedThreadPool(NUM_WORKERS);

        long startTime = System.currentTimeMillis();
        es.execute(A);
        es.execute(B);
        es.execute(C);
        es.execute(D);
        es.execute(E);
        es.execute(F);
        es.execute(G);
        es.shutdown();
        try {
            es.awaitTermination(NUM_WORKERS * WORKLOAD / 1000 + 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long timeInMillis = System.currentTimeMillis() - startTime;

        return timeInMillis;
    }

    /**
     * Runs the Data Flow Diagram sequentially
     *
     * @return Running time in milliseconds
     */
    public long runDiagramSeq()  {
        long startTime = System.currentTimeMillis();
        A.runWithoutWaiting();
        B.runWithoutWaiting();
        C.runWithoutWaiting();
        D.runWithoutWaiting();
        E.runWithoutWaiting();
        F.runWithoutWaiting();
        G.runWithoutWaiting();
        long timeInMillis = System.currentTimeMillis() - startTime;

        return timeInMillis;
    }

    /**
     * Returns total work in milliseconds, where
     * total work = number of workers * workload of each worker
     *
     * @return Total work in milliseconds
     */
    public int getTotalWork() {
        return NUM_WORKERS * WORKLOAD;
    }

    /**
     * Returns Critical Path Length - in this Diagram CPL = 3000
     *
     * @return Critical Path Length in milliseconds
     */
    public int getCPL() {
        return 3000;
    }

    private static class Worker implements Runnable {

        private final String name;
        private final int workLoad;
        private final Worker[] workersToWaitFor;
        private final Phaser phaser;

        public Worker(String name, int workLoad, Worker... workersToWaitFor) {
            this.name = name;
            this.workLoad = workLoad;
            this.workersToWaitFor = workersToWaitFor;
            this.phaser = new Phaser(1);
        }

        @Override
        public void run() {
            // Wait for 'parent' workers to complete their work
            for (Worker workerToWaitFor : workersToWaitFor) {
                workerToWaitFor.getPhaser().awaitAdvance(phaser.getPhase());
            }
            doWork();
            // Signal end of work
            phaser.arrive();
        }

        public void runWithoutWaiting() {
            doWork();
        }

        private void doWork() {
            System.out.println(this.name + " starts working");
            try {
                Thread.sleep(workLoad);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(this.name + " ends working");
        }

        public Phaser getPhaser() {
            return this. phaser;
        }
    }
}
