package org.image.miner.ui;

import javax.swing.*;
import java.awt.*;

import static org.image.miner.Constants.APP_NAME;

/**
 * This class is responsible for setting up the UI Frame.
 */
public class UiFrame extends JFrame {
    final JPanel panelContainer = new JPanel();
    final CardLayout cardLayout = new CardLayout();

    public UiFrame() {
        setSize(600, 600);
        setTitle(APP_NAME);
        //Adding panel container to the frame
        add(new PanelContainer(this).setPanelContainer());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
