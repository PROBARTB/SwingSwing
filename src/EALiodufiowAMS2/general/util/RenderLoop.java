package EALiodufiowAMS2.general.util;

public class RenderLoop {

    private final RenderLoopListener listener;

    private volatile int maxFps;

    private volatile boolean running = false;
    private Thread loopThread;

    private volatile double currentFps = 0.0;

    public RenderLoop(RenderLoopListener listener, int maxFps) {
        if (listener == null) throw new IllegalArgumentException("listener cannot be null");
        this.listener = listener;
        this.maxFps = maxFps;
    }

    public synchronized void start() {
        if (running) return;
        running = true;

        loopThread = new Thread(this::runLoop, "RenderLoopThread");
        loopThread.setDaemon(true);
        loopThread.start();
    }

    public synchronized void stop() {
        running = false;
        if (loopThread != null) {
            try {
                loopThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            loopThread = null;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
    }

    public int getMaxFps() {
        return maxFps;
    }

    public double getCurrentFps() {
        return currentFps;
    }

    private void runLoop() {
        final long NANOS_IN_SECOND = 1_000_000_000L;

        long lastTime = System.nanoTime();
        long fpsCounterStart = lastTime;
        int framesInCurrentSecond = 0;

        while (running) {
            long now = System.nanoTime();
            long deltaNanos = now - lastTime;
            if (deltaNanos <= 0) deltaNanos = 1;

            lastTime = now;

            double deltaSeconds = deltaNanos / (double) NANOS_IN_SECOND;

            try {
                listener.onFrame(deltaSeconds);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            framesInCurrentSecond++;
            long elapsedSinceFpsStart = now - fpsCounterStart;
            if (elapsedSinceFpsStart >= NANOS_IN_SECOND) {
                currentFps = framesInCurrentSecond * (NANOS_IN_SECOND / (double) elapsedSinceFpsStart);
                framesInCurrentSecond = 0;
                fpsCounterStart = now;
            }

            int localMaxFps = this.maxFps;
            if (localMaxFps > 0) {
                double targetFrameTimeSeconds = 1.0 / localMaxFps;
                long targetFrameTimeNanos = (long) (targetFrameTimeSeconds * NANOS_IN_SECOND);

                long frameEnd = System.nanoTime();
                long frameDuration = frameEnd - now;
                long sleepNanos = targetFrameTimeNanos - frameDuration;

                if (sleepNanos > 0) {
                    long sleepMillis = sleepNanos / 1_000_000L;
                    int sleepExtraNanos = (int) (sleepNanos % 1_000_000L);
                    try {
                        if (sleepMillis > 0 || sleepExtraNanos > 0) {
                            Thread.sleep(sleepMillis, sleepExtraNanos);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        running = false;
    }
}
