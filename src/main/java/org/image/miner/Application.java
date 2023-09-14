package org.image.miner;

import org.image.miner.ui.UiFrame;

import javax.swing.SwingUtilities;

class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(UiFrame::new);
    }
}