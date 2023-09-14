package org.image.miner.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static java.awt.Component.LEFT_ALIGNMENT;

/**
 * This class is responsible for setting up the UI Container.
 */
public class PanelContainer extends JProperties {
    private static final Logger logger = LoggerFactory.getLogger(PanelContainer.class);
    private final UiFrame ui;
    private static final String FONT_NAME = "Arial";

    public PanelContainer(UiFrame ui) {
        this.ui = ui;
    }

    /**
     * Setting up the UI Container.
     *
     * @return JPanel
     */
    public JPanel setPanelContainer() {
        logger.info("Setting up UI Container");
        // Setting up Panel Container
        ui.panelContainer.setLayout(ui.cardLayout);

        // Menu Panel, setting up the menu panel
        menuPanel.setName("menuPanel");
        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setBackground(Color.black);

        GridBagConstraints gbc = new GridBagConstraints();

        // Setting up Menu Label
        gbc.gridx = 3;
        gbc.gridy = 2;
        menuPanel.add(menuLabel, gbc);

        // Setting up Start Button
        startButton.setName("startButton");
        startButton.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
        startButton.setFocusable(false);

        // Initiate Listener class
        Listener listener = new Listener(ui, this);

        // Action Listener for start button
        startButton.addActionListener(listener);
        gbc.gridx = 3;
        gbc.gridy = 3;
        menuPanel.add(startButton, gbc);

        // Folder File Container Panel
        // Setting up folder list and folder model
        folderList.setModel(folderModel);
        folderList.setName("folderList");
        folderList.addListSelectionListener(listener);

        // Setting up file list and file model
        fileList.setModel(fileModel);

        // Setting up Folder List Scroller
        folderListScroller.setPreferredSize(new Dimension(250, 300));
        folderListScroller.setAlignmentX(LEFT_ALIGNMENT);

        // Setting up File List Scroller
        folderListScroller.setPreferredSize(new Dimension(250, 300));
        folderListScroller.setAlignmentX(LEFT_ALIGNMENT);

        // Setting up Folder Label
        folderLabel.setName("folderLabel");
        folderLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
        folderLabel.setForeground(Color.white);

        // Setting up File Label
        fileLabel.setName("fileLabel");
        fileLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
        fileLabel.setForeground(Color.white);

        // Setting up Folder Panel
        addComponentsToLayout(folderPanel, folderLabel, folderList, folderListScroller);
        folderPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 3));

        // Setting up File Panel
        filePanel.setName("filePanel");
        addComponentsToLayout(filePanel, fileLabel, fileList, fileListScroller);
        filePanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 10, 5));

        // Setting up Folder File Panel
        folderFilePanel.setLayout(new BoxLayout(folderFilePanel, BoxLayout.X_AXIS));
        folderFilePanel.setBackground(Color.black);
        folderFilePanel.add(folderPanel);
        folderFilePanel.add(filePanel);
        folderFilePanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // Setting up Browse More Button
        browseButton.setName("browseButton");
        browseButton.setFont(new Font(FONT_NAME, Font.PLAIN, 15));
        browseButton.setFocusable(false);
        browseButton.addActionListener(listener);

        // Setting up Scan Button
        scanButton.setName("scanButton");
        scanButton.setFont(new Font(FONT_NAME, Font.PLAIN, 15));
        scanButton.setFocusable(false);
        scanButton.addActionListener(listener);

        //Setting up the JProgress panel
        progressBar.setName("progressBar");
        progressBar.setStringPainted(true);
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.LINE_AXIS));
        progressPanel.setBounds(0, 50, 300, 200);
        progressPanel.setBackground(Color.red);
        progressPanel.add(progressBar);
        progressText.setFont(new Font("Serif", Font.BOLD, 20));
        progressText.setForeground(Color.white);
        progressPanel.add(progressText);
        buttonPanel.add(progressPanel);

        // Setting up Button Panel
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBackground(Color.black);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(browseButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(scanButton);

        // Setting up Folder File Container Panel
        folderFileContainerPanel.setLayout(new BoxLayout(folderFileContainerPanel, BoxLayout.Y_AXIS));
        folderFileContainerPanel.setBackground(Color.black);
        folderFileContainerPanel.add(folderFilePanel);
        folderFileContainerPanel.add(buttonPanel);
        folderFileContainerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        //Folder Choose Panel
        //Setting up Folder Choose Panel
        folderChoosePanel.setLayout(new GridBagLayout());
        folderChoosePanel.setBackground(Color.black);
        folderChoosePanel.setBounds(0, 50, 300, 200);

        GridBagConstraints gbcFolderChoosePanel = new GridBagConstraints();

        //Setting up Folder Choose Label
        folderChooseLabel.setForeground(Color.white);
        folderChooseLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 25));
        gbcFolderChoosePanel.gridx = 3;
        gbcFolderChoosePanel.gridy = 3;
        folderChoosePanel.add(folderChooseLabel, gbcFolderChoosePanel);

        //Setting up open HTML Button
        openHtmlButton.setName("openHtmlButton");
        openHtmlButton.setFont(new Font(FONT_NAME, Font.PLAIN, 17));
        openHtmlButton.setFocusable(false);
        gbcFolderChoosePanel.gridx = 3;
        gbcFolderChoosePanel.gridy = 4;
        openHtmlButton.addActionListener(listener);
        folderChoosePanel.add(openHtmlButton, gbcFolderChoosePanel);

        //Setting up Back Button
        backButton.setName("backButton");
        backButton.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
        gbcFolderChoosePanel.gridx = 3;
        gbcFolderChoosePanel.gridy = 6;
        backButton.setBackground(Color.black);
        backButton.setBorder(new LineBorder(Color.BLACK));
        backButton.setForeground(Color.white);
        backButton.addActionListener(listener);
        folderChoosePanel.add(backButton, gbcFolderChoosePanel);

        // Adding Mouse listener event on the button
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                backButton.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                backButton.setBackground(Color.black);
                backButton.setBorder(new LineBorder(Color.BLACK));
                backButton.setForeground(Color.white);
            }
        });

        //Adding panels to panel container
        ui.panelContainer.add(menuPanel, "Menu Panel");
        ui.panelContainer.add(folderFileContainerPanel, "Folder File Container Panel");
        ui.panelContainer.add(folderChoosePanel, "Folder Choose Panel");
        ui.cardLayout.show(ui.panelContainer, "Menu Panel");

        //Adding panel container to the frame
        return ui.panelContainer;
    }

    // Function to add different panel, lists inside the main frame
    private void addComponentsToLayout(JPanel folderPanel, JLabel folderLabel, JList<File> folderList,
                                        JScrollPane folderListScroller) {
        folderPanel.setLayout(new BoxLayout(folderPanel, BoxLayout.PAGE_AXIS));
        folderPanel.setBackground(Color.black);
        folderLabel.setLabelFor(folderList);
        folderLabel.setName("folderLabel");
        folderPanel.add(folderLabel);
        folderPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        folderPanel.add(folderListScroller);
    }
}
