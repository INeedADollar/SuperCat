package org.pjc.event_handlers;

import javax.swing.*;
import java.awt.image.BufferedImage;

public interface SkinChangeEventHandler {
    void handleEvent(String tabName, BufferedImage icon);
}
