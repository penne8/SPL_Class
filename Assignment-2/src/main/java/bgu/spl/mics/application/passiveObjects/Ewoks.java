package bgu.spl.mics.application.passiveObjects;

import java.util.Collections;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton. You must not alter
 * any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private final Object initLock = new Object();
    private Ewok[] ewoks;

    private Ewoks() {
    }

    public static Ewoks getInstance() {
        return Ewoks.SingeltonHolder.INSTANCE;
    }

    public void initEwoks(int totalEwoks) {
        ewoks = new Ewok[totalEwoks];
        for (int i = 0; i < totalEwoks; i++) {
            ewoks[i] = new Ewok(i + 1);
        }
    }

    // Acquiring the ewoks in a sorted manner, this will assure a correct synchronization between the threads
    public void acquire(List<Integer> requiredEwoks) {
        Collections.sort(requiredEwoks);

        for (Integer serialNumber : requiredEwoks) {
            synchronized (ewoks[serialNumber - 1]) {
                while (!ewoks[serialNumber - 1].isAvailable()) {
                    try {
                        ewoks[serialNumber - 1].wait();
                    } catch (InterruptedException ignore) {
                    }
                }
                ewoks[serialNumber - 1].acquire();
            }
        }
    }

    public void release(List<Integer> requiredEwoks) {
        Collections.sort(requiredEwoks);
        Collections.reverse(requiredEwoks);

        for (Integer serialNumber : requiredEwoks) {
            synchronized (ewoks[serialNumber - 1]) {
                ewoks[serialNumber - 1].release();
                ewoks[serialNumber - 1].notifyAll();
            }
        }
    }

    private static class SingeltonHolder {
        private static final Ewoks INSTANCE = new Ewoks();
    }
}
