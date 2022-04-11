package edu.tcu.cs.hogwartsartifactsonline.utils;

public class IdWorker {
    private final static long twepoch = 1288834974657L;
    private final static long workerIdBits = 5L;
    private final static long datacenterIdBits = 5L;
    private final static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final static long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final static long sequenceBits = 12L;
    private final static long workerIdShift = sequenceBits;
    private final static long datacenterIdShift = sequenceBits + workerIdBits;
    private final static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);
    private static long lastTimestamp = -1L;
    private long sequence = 0L;
    private final long workerId;
    private final long datacenterId;

    public IdWorker() {
        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);
    }

    public IdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater... "));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater..."));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to..."));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextHillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
    }
}
