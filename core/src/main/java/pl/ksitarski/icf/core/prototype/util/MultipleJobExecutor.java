package pl.ksitarski.icf.core.prototype.util;

import pl.ksitarski.icf.core.prototype.exc.UnexpectedError;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultipleJobExecutor extends AbstractExecutorService {

    private final boolean singleThread;
    private final ExecutorService executorService;
    private final Semaphore semaphore;
    private final int jobCount;
    private final AtomicInteger atomicInteger = new AtomicInteger(0);
    private final int jobBatchSize;
    private final List<Runnable> jobCreator;
    private boolean isShutdown = false;

    public MultipleJobExecutor(int jobCount, int threadCount, int jobBatchSize, boolean singleThread) {
        executorService = Executors.newFixedThreadPool(threadCount);
        this.singleThread = singleThread;
        semaphore = new Semaphore(0);
        this.jobCount = jobCount;
        this.jobBatchSize = jobBatchSize;
        this.jobCreator = new ArrayList<>(jobBatchSize);
    }


    @Override
    public void shutdown() {
        finishJobs();
        isShutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        finishJobs();
        return List.of();
    }

    @Override
    public boolean isShutdown() {
        return isShutdown;
    }

    @Override
    public boolean isTerminated() {
        return isShutdown;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit)  {
        finishJobs();
        return false;
    }

    @Override
    public void execute(Runnable runnable) {
        if (runnable == null) throw new NullPointerException();
        if (singleThread) {
            atomicInteger.incrementAndGet();
            try {
                runnable.run();
            } finally {
                semaphore.release();
            }
        } else {
            if (jobCreator.size() >= jobBatchSize) {
                submitJobMultithreaded();
            }
            jobCreator.add(runnable);
        }
    }

    private void submitJobMultithreaded() {
        if (jobCreator.size() == 0) {
            return;
        }

        List<Runnable> copy = new ArrayList<>(jobCreator);
        jobCreator.clear();
        executorService.submit(() -> {
            atomicInteger.addAndGet(copy.size());
            for (int i = 0; i < copy.size(); i++) {
                try {
                    copy.get(i).run();
                } catch (Exception e) {
                    semaphore.release(copy.size() - i - 1);
                    throw new UnexpectedError("Exceptions should not reach MultipleJobExecutor! This is a programmer error!");
                } finally {
                    semaphore.release();
                }
            }
        });
    }


    private void finishJobs() {
        if (!singleThread) {
            submitJobMultithreaded();
        }

        semaphore.acquireUninterruptibly(jobCount);
        executorService.shutdown();
        if (jobCount != atomicInteger.get()) {
            throw new IllegalStateException("Ran more jobs that expected!");
        }
    }

}
