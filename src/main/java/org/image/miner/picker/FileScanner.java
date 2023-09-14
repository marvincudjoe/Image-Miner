package org.image.miner.picker;

import org.apache.commons.compress.utils.FileNameUtils;
import org.image.miner.finder.ImageFinder;
import org.image.miner.reporter.ReportGenerator;
import org.image.miner.ui.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for scanning folder.
 * The csv file to store the index of known files is initiated here.
 */
public class FileScanner {
    private static final List<String> SUPPORTED_FILE_EXTENSIONS = Arrays.asList("doc", "docx", "pdf", "ppt", "pptx");
    private final CustomIndex customIndex = new CustomIndex();
    private final Set<String> documentSet = new HashSet<>();
    private final Listener listener;
    private static final Logger logger = LoggerFactory.getLogger(FileScanner.class);
    ReportGenerator reportGenerator;
    int scannedFileCount;
    private File currentFile;

    public FileScanner(Listener listener) {
        this.listener = listener;
        customIndex.start();
    }

    public void scanFolder(String directory) {
        File folder = new File(directory);
        try {
            File[] files = folder.listFiles();
            if (files == null) {
                listener.setFoundFiles(false);
                logger.warn("No files found in: {}", directory);
                return;
            }
            for (File file : files) {
                String path = file.getAbsolutePath();
                readThisFile(file, path);
            }
            if (!documentSet.isEmpty()) {
                listener.setFoundFiles(true);
            } else {
                logger.warn("No file in {} contains a valid extension in group {}",
                                                                directory, SUPPORTED_FILE_EXTENSIONS);
                listener.setFoundFiles(false);
            }
        } catch (Exception ex) {
            logger.error("Error scanning folder: {}", ex.getMessage());
            listener.setFoundFiles(false);
            System.exit(1); // adding kill
        }
        String folderName = folder.getName();
        reportGenerator = new ReportGenerator(this);
        reportGenerator.start(folderName);
    }

    public void nextFile() {
        // once set is empty
        if (documentSet.isEmpty()) {
            try {
                reportGenerator.endDocument();
            } catch (IOException ex) {
                logger.error("Error ending document: {}", ex.getMessage());
            }
        } else {
            startSending();
        }
    }

    public void startSending() {
        for (String temp : documentSet) {
            currentFile = new File(temp);
            if (!customIndex.addFileToIndex(currentFile.getAbsolutePath())) {
                new ImageFinder(this, reportGenerator, currentFile);
            }
            break;
        }
    }

    public void fileSent() {
        documentSet.remove(currentFile.getAbsolutePath());
        scannedFileCount += 1;
    }

    public void htmlGeneratorDone(String docLocation) {
        try {
            customIndex.outputToCSV();
        } catch (IOException ex) {
            logger.error("Error outputting to CSV: {}", ex.getMessage());
        }
        listener.showHtmlDoc(docLocation);
    }

    public void readThisFile(File file, String path) {
        // if it's a file lets read it
        if (!file.isDirectory()
                && SUPPORTED_FILE_EXTENSIONS.contains(FileNameUtils.getExtension(file.getName()))) {
            documentSet.add(path);
        } else if (file.isDirectory()) {
            // recursive search for sub directories
            scanFolder(path);
        }
    }

    public Set<String> getDocumentSet() {
        return documentSet;
    }

    public int getScannedFileCount() {
        return scannedFileCount;
    }
}