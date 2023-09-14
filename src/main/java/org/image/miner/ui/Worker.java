package org.image.miner.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * This class utilises the SwingWorker to execute a task in the background.
 */
public class Worker extends SwingWorker<Void, Void> {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    JProperties props = new JProperties();

    @Override
    public Void doInBackground() {
        int progress = 0;
        while (progress < 100) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                logger.error("Error: {}", ex.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
            progress += 10;
            setProgress(progress);
        }
        return null;
    }

    @Override
    public void done() {
        props.done = true;
        Toolkit.getDefaultToolkit().beep();
    }
}