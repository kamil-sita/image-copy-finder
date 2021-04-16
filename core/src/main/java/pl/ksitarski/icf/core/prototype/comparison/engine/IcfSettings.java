package pl.ksitarski.icf.core.prototype.comparison.engine;

import java.util.Objects;

public class IcfSettings {
    private boolean aggressiveOptimizations = false; //might improve performance, but won't compare some files in extreme cases (for example mismatched images ratio, way too different colors)
    private int threadCount = 1;
    private int batchSize = 1024;
    private boolean includeErrors = true; //whether to include comparisons that failed in the results
    private CutoffStrategy cutoffStrategy = ACCEPT_ALL;
    private int jobBatchSize = 1024;
    private boolean jobsSingleThreaded = false;
    private boolean exceptionsStackTrace = false;

    public static final CutoffStrategy ACCEPT_ALL = (value, suggested) -> true;
    public static final CutoffStrategy ACCEPT_SUGGESTED = (value, suggested) -> value >= suggested;

    public int getBatchSize() {
        return batchSize;
    }

    public IcfSettings setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public IcfSettings setAggressiveOptimizations(boolean aggressiveOptimizations) {
        this.aggressiveOptimizations = aggressiveOptimizations;
        return this;
    }

    public boolean isAggressiveOptimizations() {
        return aggressiveOptimizations;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public IcfSettings setThreadCount(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("thread count must be at 1 or higher");
        }
        this.threadCount = threadCount;
        return this;
    }

    public CutoffStrategy getCutoffStrategy() {
        return cutoffStrategy;
    }

    public IcfSettings setCutoffStrategy(CutoffStrategy cutoffStrategy) {
        Objects.requireNonNull(cutoffStrategy);
        this.cutoffStrategy = cutoffStrategy;
        return this;
    }

    public boolean isIncludeErrors() {
        return includeErrors;
    }

    public IcfSettings setIncludeErrors(boolean includeErrors) {
        this.includeErrors = includeErrors;
        return this;
    }


    public int getJobBatchSize() {
        return jobBatchSize;
    }

    public IcfSettings setJobBatchSize(int jobBatchSize) {
        this.jobBatchSize = jobBatchSize;
        return this;
    }

    public boolean isJobsSingleThreaded() {
        return jobsSingleThreaded;
    }

    public IcfSettings setJobsSingleThreaded(boolean jobsSingleThreaded) {
        this.jobsSingleThreaded = jobsSingleThreaded;
        return this;
    }

    public boolean isExceptionsStackTrace() {
        return exceptionsStackTrace;
    }

    public IcfSettings setExceptionsStackTrace(boolean exceptionsStackTrace) {
        this.exceptionsStackTrace = exceptionsStackTrace;
        return this;
    }

    @Override
    public String toString() {
        return "IcfSettings{" +
                "aggressiveOptimizations=" + aggressiveOptimizations +
                ", threadCount=" + threadCount +
                ", batchSize=" + batchSize +
                ", includeErrors=" + includeErrors +
                ", cutoffStrategy=" + cutoffStrategy +
                '}';
    }

    public ExceptionsBehaviour toExceptionsBehaviour() {
        return new ExceptionsBehaviour().setExceptionsStackTrace(isExceptionsStackTrace()).setIncludeErrors(isIncludeErrors());
    }
}
