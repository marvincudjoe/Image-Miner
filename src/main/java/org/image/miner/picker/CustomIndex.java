package org.image.miner.picker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import static org.image.miner.Constants.FILE_MODIFIED;
import static org.image.miner.Constants.FILE_NOT_MODIFIED;
import static org.image.miner.Constants.PATH_TO_IMG_DIR;

/**
 * Creates and updates a csv file of known/previously scanned files.
 */
public class CustomIndex {
    private static final Logger logger = LoggerFactory.getLogger(CustomIndex.class);
    private final ArrayList<String[]> index = new ArrayList<>();
    private Path csv;

    public void start() {
        if (!new File(PATH_TO_IMG_DIR.toUri()).exists()) {
            logger.info("Creating images directory at: {}", PATH_TO_IMG_DIR);
            new File(PATH_TO_IMG_DIR.toUri()).mkdir();
        }
        this.csv = PATH_TO_IMG_DIR.resolve("img_indexing.csv");
        createIndexCSV(csv.toFile());
        parseIndex();
    }

    public void createIndexCSV(File indexCSV) {
        if (!indexCSV.isFile()) {
            try {
                if (indexCSV.createNewFile()) {
                    logger.info("New img_indexing.csv file created");
                } else {
                    logger.info("img_indexing.csv file creation failed");
                }
            } catch (IOException ex) {
                logger.error("Error creating img_indexing.csv file: {}", ex.getMessage());
            }
        }
    }

    public void parseIndex() {
        try (Scanner sc = new Scanner(csv.toFile())) {
            sc.useDelimiter(", ");

            while (sc.hasNextLine()) {
                String[] currentLine = sc.nextLine().split(", ");
                index.add(currentLine);
            }
        } catch (Exception ex) {
            logger.error("Error parsing index: {}", ex.getMessage());
        }
    }

    public boolean addFileToIndex(String file) {
        boolean foundInIndex = false;
        for (String[] currentEntry : index) {
            if (currentEntry[0].equals(file)) {
                foundInIndex = true;
                break;
            }
        }
        if (!foundInIndex) {
            logger.info("NEW FILE: appending to index");
            appendToIndex(file);
            return false;
        }

        if (!compareMetadata(file)) {
            logger.info("{}: updating index", FILE_MODIFIED);
            updateIndex(file);
            return true;
        } else {
            logger.info("{}: skipped", FILE_NOT_MODIFIED);
            return false;
        }
    }

    public boolean compareMetadata(String file) {
        String csvFileDate = "";

        for (String[] currentEntry : index) {
            if (currentEntry[0].equals(file)) {
                csvFileDate = currentEntry[1];
                break;
            }
        }
        return Long.toString(new File(file).lastModified()).equals(csvFileDate);
    }

    public void appendToIndex(String file) {
        File ourFile = new File(file);
        String fileName = ourFile.getName();
        File imgFolderDir = PATH_TO_IMG_DIR.resolve(FilenameUtils.removeExtension(fileName)).toFile();

        String[] entry = {file, String.valueOf(ourFile.lastModified()), imgFolderDir.getAbsolutePath()};
        index.add(entry);
    }

    public void updateIndex(String file) {
        for (String[] currentEntry : index) {
            if (currentEntry[0].equals(file)) {
                File dir = new File(currentEntry[2]);
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException ex) {
                    logger.error("Error deleting directory: {}", ex.getMessage());
                }
                dir.mkdir();
                currentEntry[1] = String.valueOf(new File(file).lastModified());
                break;
            }
        }
    }

    public void outputToCSV() throws IOException {
        FileWriter writer = new FileWriter(csv.toFile());
        try (BufferedWriter outFile = new BufferedWriter(writer)) {
            StringBuilder sb = new StringBuilder();
            for (String[] entry : index) {
                for (String str : entry) {
                    sb.append(str);
                    if (!str.equals(entry[2])) {
                        sb.append(", ");
                    }
                }
                sb.append("\n");
            }
            outFile.write(String.valueOf(sb));
        }

    }
}
