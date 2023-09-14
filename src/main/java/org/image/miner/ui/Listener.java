package org.image.miner.ui;

import org.image.miner.picker.FileScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import static org.image.miner.ui.JProperties.backButton;
import static org.image.miner.ui.JProperties.browseButton;
import static org.image.miner.ui.JProperties.fileModel;
import static org.image.miner.ui.JProperties.folderChooseLabel;
import static org.image.miner.ui.JProperties.folderList;
import static org.image.miner.ui.JProperties.folderModel;
import static org.image.miner.ui.JProperties.openHtmlButton;
import static org.image.miner.ui.JProperties.progressBar;
import static org.image.miner.ui.JProperties.scanButton;
import static org.image.miner.ui.JProperties.startButton;

/**
 * This class is responsible for handling the events that occur on the UI.
 */
public class Listener implements PropertyChangeListener, ActionListener, ListSelectionListener {
    private final UiFrame ui;
    private final FileScanner fileScanner;
    private final JProperties props;
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    public Listener(UiFrame ui, JProperties props) {
        this.ui = ui;
        this.props = props;
        fileScanner = new FileScanner(this);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(startButton.getActionCommand())) {
            startButtonClick();
        } else if (event.getActionCommand().equals(browseButton.getActionCommand())) {
            browseButtonClick();
        } else if (event.getActionCommand().equals(scanButton.getActionCommand())) {
            scanButtonClick();
        } else if (event.getActionCommand().equals(openHtmlButton.getActionCommand())) {
            openHtmlButtonClick();
        } else if (event.getActionCommand().equals(backButton.getActionCommand())) {
            backButtonClick();
        }
    }

    // Changing panel when button clicked
    public void startButtonClick() {
        ui.cardLayout.show(ui.panelContainer, "Folder File Container Panel");
    }

    // Opening a File chooser to allow selecting a folder/directory
    public void browseButtonClick() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            folderModel.addElement(file);
        } else {
            JOptionPane.showMessageDialog(ui, "No folder selected");
        }
    }

    // Begins scanning the folder selected
    public void scanButtonClick() {
        try {
            File folder = folderList.getSelectedValue();
            String folderPath = folder.getPath();
            fileScanner.scanFolder(folderPath);
            if (props.filesFound || fileScanner.getScannedFileCount() != 0) {
                props.done = false;
                props.worker = new Worker();
                props.worker.addPropertyChangeListener(this);
                props.worker.execute();
                startScanning(folder.getName());
            }
        } catch (Exception ed) {
            JOptionPane.showMessageDialog(ui, "Please select a folder from the list");
        }
    }

    // Redirects user to an HTML file generated
    public void openHtmlButtonClick() {
        //Show HTML doc
        File htmlFile = showHtmlDoc(props.pathToIndexFile);
        try {
            Desktop.getDesktop().open(htmlFile);
        } catch (IOException ex) {
            logger.error("Error opening HTML file: {} ", ex.getLocalizedMessage());
        }
    }

    // Redirects user to the browse folder page
    public void backButtonClick() {
        ui.cardLayout.show(ui.panelContainer, "Folder File Container Panel");
        props.counter = 10;
    }

    public void startScanning(String folderName) {
        if (props.filesFound || fileScanner.getScannedFileCount() > 0) {
            ActionListener action = e1 -> {
                if (props.counter == 0) {
                    props.timer.stop();
                    JOptionPane.showMessageDialog(ui, "Folder Scan Completed");
                    progressBar.setValue(0);
                    ui.cardLayout.show(ui.panelContainer, "Folder Choose Panel");
                    folderChooseLabel.setText("Folder Chosen : " + folderName);
                } else {
                    props.counter--;
                }
            };
            int delay = 700;
            props.timer = new Timer(delay, action);
            props.timer.setInitialDelay(0);
            props.timer.start();
        }
    }

    // Updating progress bar when the progress is changed
    public void propertyChange(PropertyChangeEvent evt) {
        if (!props.done) {
            int progress = props.worker.getProgress();
            progressBar.setValue(progress);
        }
    }

    // Getting selected folder on the JList
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == folderList) {
            File folder = folderList.getSelectedValue();
            displayFolder(folder);
        }
    }

    // Display folder if recognised file is not displayed
    public void displayFolder(File folder) {
        File[] fileList = folder.listFiles();
        if (fileList == null) {
            logger.error("No files found in: {}", folder.getAbsolutePath());
            return;
        }
        for (File file : fileList) {
            if (!fileModel.contains(file)) {
                fileModel.addElement(file);
            }
        }
    }

    public void setFoundFiles(boolean value) {
        props.filesFound = value;
    }

    public File showHtmlDoc(String docLocation) {
        props.pathToIndexFile = docLocation;
        return new File(docLocation);
    }
}
