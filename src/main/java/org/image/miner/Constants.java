package org.image.miner;

import javax.swing.filechooser.FileSystemView;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Constants used in the project.
 */
public class Constants {
    public static final String APP_NAME = "Image-Miner";
    public static final String DIR_NAME = "image-miner-generated";
    public static final Path PATH_TO_GENERATED_DIR =
            Paths.get(FileSystemView.getFileSystemView().getDefaultDirectory().getPath()).resolve(DIR_NAME);
    public static final Path PATH_TO_IMG_DIR = PATH_TO_GENERATED_DIR.resolve("images");
    public static final String FILE_PROCESSED = "FILE PROCESSED";
    public static final String FILE_NOT_MODIFIED = "FILE NOT MODIFIED";
    public static final String FILE_MODIFIED = "FILE MODIFIED";

    private Constants() {
    }
}
