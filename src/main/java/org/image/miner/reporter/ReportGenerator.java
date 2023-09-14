package org.image.miner.reporter;

import org.image.miner.finder.ImageFinder;
import org.image.miner.picker.FileScanner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.image.miner.Constants.PATH_TO_GENERATED_DIR;

/**
 * Generates the HTML file.
 */
public class ReportGenerator {
    private final FileScanner fileScanner;
    private final StringBuilder outputHtml = new StringBuilder();
    private String folderName;
    private ImageFinder imageFinder;

    public ReportGenerator(FileScanner fileScanner) {
        this.fileScanner = fileScanner;
    }

    public void start(String folderName) {
        this.folderName = folderName;
        outputHtml.append(HTML);
        addFileNameInSideBar(fileScanner.getDocumentSet());
        outputHtml.append("<div class='content' id='imgContent'>\n");
        fileScanner.startSending();
    }

    private void addFileNameInSideBar(Set<String> documentSet) {
        for (String file : documentSet) {
            Path path = Paths.get(file);
            Path fileName = path.getFileName();
            String newFile = file.replace("'", "&#39;");
            if (fileName.toString().length() > 20) {
                fileName = Paths.get(fileName.toString().substring(0, 20));
            }
            outputHtml.append("<a href='" + "file:///").append(newFile).append("' id='").append(fileName).append("'>").append(fileName).append("</a>\n<br>\n");
        }
        outputHtml.append("</div>\n");
    }

    public void nextFile(ImageFinder imageFinder) {
        this.imageFinder = imageFinder;
        imageFinder.fileSent();
    }

    public void addImage(String imagePath) {
        String newImagePath = imagePath.replace("'", "&#39;");
        outputHtml.append("<a href='").append(newImagePath).append("' id='").append(newImagePath).append("'>\n").append("<img src='").append(newImagePath).append("'>\n").append("</a>\n");
        imageFinder.nextImage();
    }

    public void finishFile() {
        imageFinder.fileDone();
    }

    public void endDocument() throws IOException {
        File htmlFile = PATH_TO_GENERATED_DIR.resolve(getFolderName() + ".html").toFile();
        FileWriter writer = new FileWriter(htmlFile);
        try (BufferedWriter outFile = new BufferedWriter(writer)) {
            outputHtml.append("</div>\n</body>\n</html>");

            outFile.write(outputHtml.toString());
        }

        fileScanner.htmlGeneratorDone(htmlFile.getAbsolutePath());
    }

    public String getFolderName() {
        return folderName;
    }

    private static final String HTML =
            """
                        <!DOCTYPE html>
                        <html>
                                
                        <head>
                        <meta charset='utf-8'/>
                        <title>Image-Miner" folderName "</title>
                        <style>
                        .header {
                            margin-left: 600px;
                            top: 0;
                            position: relative;
                        }
                        .header img {
                            width: 500px;
                            height: 200px;
                        }
                        .sidebar {
                            height: 100%;
                            position: fixed;
                            left: 0;
                            top: 0;
                            width: 270px;
                            z-index: 1;
                            padding-top: 10px;
                            background-color: grey;
                        }
                        .sidebar p {
                            margin-left: 30px;
                            font-size: 30px;
                            color: white;
                            font-family: "Arial";
                        }
                        .sidebar a {
                            margin-left: 30px;
                            font-size: 20px;
                            font-family: "Arial";
                            text-decoration: none;
                            line-height: 150%;
                        }
                        .sidebar a:link { color: blue; }
                        .sidebar a:hover { color: aqua; }
                        .content {
                            margin-left: 350px;
                            padding-top: 5px;
                        }
                        .content img {
                           padding: 5px;
                           width: 150px;
                           height: 100px;
                           border: 3px solid #ddd;
                           border-radius: 6px;
                           margin-left: 2px;
                        }
                        .content img:hover { box-shadow: 0 0 5px 5px rgba(0, 191, 255, 0.5);}
                        </style>
                        </head>
                        <body style = 'background-color:black'>
                        <h1 class='header'>
                        </h1>
                        <div class='sidebar'>
                        <p>Files: </p>"
                    """;
}
