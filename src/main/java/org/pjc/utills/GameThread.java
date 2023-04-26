package org.pjc.utills;

import org.pjc.event_handlers.ThreadCompleteListener;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class GameThread extends Thread {
    private final Set<ThreadCompleteListener> listeners
            = new CopyOnWriteArraySet<>();
    private final GameThreadRun run;

    public GameThread(GameThreadRun run) {
        this.run = run;
    }
    public final void addListener(final ThreadCompleteListener listener) {
        listeners.add(listener);
    }
    public final void removeListener(final ThreadCompleteListener listener) {
        listeners.remove(listener);
    }
    private void notifyListeners() {
        for (ThreadCompleteListener listener : listeners) {
            listener.notifyOfThreadComplete(this);
        }
    }
    @Override
    public final void run() {
        try {
            run.doRun();
        } finally {
            notifyListeners();
        }
    }
}