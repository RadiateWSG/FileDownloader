package me.spirittalk.library;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.spirittalk.library.DownloadTask.DownloadRunnable;

/**
 * Created by spirit on 2017/11/26.
 */

public class Dispatcher {
    private int maxTask;
    private ExecutorService executorService;
    private final Deque<DownloadRunnable> readyQueue = new ArrayDeque<>();
    private final Deque<DownloadRunnable> runningQueue = new ArrayDeque<>();

    public Dispatcher(int maxTask) {
        this.maxTask = maxTask;
    }

    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60,
                    TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        }
        return executorService;
    }

    synchronized void enqueue(DownloadRunnable runnable) {
        if (runningQueue.size() < maxTask) {
            runningQueue.add(runnable);
            executorService().execute(runnable);
        } else {
            readyQueue.add(runnable);
        }
    }

    /**
     * 执行 tasks
     */
    private void executeTasks() {
        if (runningQueue.size() >= maxTask) return; // 执行队列已为最大值
        if (readyQueue.isEmpty()) return; // 等待队列为空

        for (Iterator<DownloadRunnable> i = readyQueue.iterator(); i.hasNext(); ) {
            DownloadRunnable task = i.next();
            i.remove();
            runningQueue.add(task);
            executorService().execute(task);
            if (runningQueue.size() >= maxTask) return;
        }
    }

    /**
     * 执行结束，尝试执行下一个
     *
     * @param runnable 当前 runnable
     */
    synchronized void fininshed(DownloadRunnable runnable) {
        if (!runningQueue.remove(runnable))
            throw new AssertionError("DownloadRunnable wasn't in queue!");
        executeTasks();
    }

    public synchronized void cancleAll() {
        for (DownloadRunnable runnable : readyQueue) {
            runnable.get().cancel();
        }
        for (DownloadRunnable runnable : runningQueue) {
            runnable.get().cancel();
        }
    }
}
