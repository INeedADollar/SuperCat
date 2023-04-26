package org.pjc.event_handlers;

import org.pjc.utills.GameThread;

public interface ThreadCompleteListener {
    void notifyOfThreadComplete(GameThread thread);
}
