import java.util.concurrent.atomic.AtomicLong;

public class Counter {
    private static AtomicLong count = new AtomicLong(0);

    public static long incrementAndGet() {
        return count.incrementAndGet();
    }

    public static long get() {
        return count.get();
    }
}
