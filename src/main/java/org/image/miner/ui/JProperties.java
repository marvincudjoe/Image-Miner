package org.image.miner.ui;

import javax.swing.*;
import java.io.File;

/**
 * This class contains all the properties for the UI.
 */
public class JProperties {
    // Progress Panel components
    Worker worker;
    Timer timer;
    int counter = 10;
    String pathToIndexFile = "";
    static final JProgressBar progressBar = new JProgressBar();
    boolean done;
    boolean filesFound = false;
    static final JLabel progressText = new JLabel();

    // Components - Menu Panel
    static final JPanel menuPanel = new JPanel();
    static final JLabel menuLabel = new JLabel();
    static final JButton startButton = new JButton("Start");

    // Components - Folder File Container Panel
    static final JPanel folderFileContainerPanel = new JPanel();
    static final JPanel folderFilePanel = new JPanel();
    static final JPanel folderPanel = new JPanel();
    static final JPanel filePanel = new JPanel();
    static final JPanel buttonPanel = new JPanel();
    static final JPanel progressPanel = new JPanel();

    static final JList<File> folderList = new JList<>();
    static final DefaultListModel<File> folderModel = new DefaultListModel<>();
    static final JList<File> fileList = new JList<>();
    static final DefaultListModel<File> fileModel = new DefaultListModel<>();

    static final JScrollPane folderListScroller = new JScrollPane(folderList);
    static final JScrollPane fileListScroller = new JScrollPane(fileList);
    static final JLabel folderLabel = new JLabel("Folder List:");
    static final JLabel fileLabel = new JLabel("File List:");
    static final JButton browseButton = new JButton("Browse...");
    static final JButton scanButton = new JButton("Scan");

    // Components - Folder Choose Panel
    static final JPanel folderChoosePanel = new JPanel();
    static final JLabel folderChooseLabel = new JLabel();
    static final JButton openHtmlButton = new JButton("Open HTML");
    static final JButton backButton = new JButton("Back");
}
