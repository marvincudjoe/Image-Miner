package org.image.miner.finder;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.image.miner.picker.FileScanner;
import org.image.miner.reporter.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.image.miner.Constants.FILE_PROCESSED;
import static org.image.miner.Constants.PATH_TO_IMG_DIR;

/**
 * Extracts images from the file.
 */
public class ImageFinder {
    private final FileScanner fileScanner;
    private final ReportGenerator reportGenerator;
    private final File inFile;
    private static final Logger logger = LoggerFactory.getLogger(ImageFinder.class);
    private Path imgPath;
    private boolean nextImage = true;

    public ImageFinder(FileScanner fileScanner, ReportGenerator reportGenerator, File inFile) {
        this.fileScanner = fileScanner;
        this.reportGenerator = reportGenerator;
        this.inFile = inFile;
        reportGenerator.nextFile(this);
    }

    public void fileSent() {
        fileScanner.fileSent();
        callExtractionMethod();
        reportGenerator.finishFile();
    }

    private void callExtractionMethod() {
        String extension = inFile.getAbsolutePath();
        if (extension.endsWith(".docx")) {
            extractFromDocx();
            nextImage = false;
        } else if (extension.endsWith(".pdf")) {
            extractFromPDF();
            nextImage = false;
        } else if (extension.endsWith(".pptx")) {
            extractFromPPTX();
            nextImage = false;
        } else {
            throw new IllegalArgumentException("File extension not supported: " + extension);
        }
    }

    private void extractFromPDF() {
        imgPath = createImageDirectory();
        try (final PDDocument document = Loader.loadPDF(inFile)) {
            PDPageTree list = document.getPages();
            for (PDPage page : list) {
                PDResources pdResources = page.getResources();
                readPDFObjects(pdResources);
            }
        } catch (IOException ex) {
            logger.error("Extract from PDF error: {}", ex.getLocalizedMessage());
        }
        logger.info(FILE_PROCESSED);
    }

    private void readPDFObjects(PDResources pdResources) {
        try {
            for (COSName name : pdResources.getXObjectNames()) {
                PDXObject object = pdResources.getXObject(name);
                if ((object instanceof PDImageXObject image) && (nextImage) && (isImageValid(image.getImage()))) {
                    outputImage(image.getImage(), image.getSuffix(), image.getSuffix());
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to process image: {}", ex.getLocalizedMessage());
        }
    }

    private void extractFromDocx() {
        imgPath = createImageDirectory();
        try (XWPFDocument docx = new XWPFDocument(new FileInputStream(inFile))) {
            List<XWPFPictureData> pictureList = docx.getAllPackagePictures();
            readDocxObjects(pictureList);
        } catch (Exception ex) {
            logger.error("Extract from docx error: {}", ex.getLocalizedMessage());
        }
        logger.info(FILE_PROCESSED);
    }

    private void readDocxObjects(List<XWPFPictureData> pictureList) {
        try {
            for (XWPFPictureData picture : pictureList) {
                if (nextImage) {
                    byte[] pictureBytes = picture.getData();
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(pictureBytes));
                    if (isImageValid(bufferedImage)) {
                        outputImage(bufferedImage, picture.getFileName(), picture.suggestFileExtension());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to process image: Most likely a non bitmap image(.emf): {}", ex.getLocalizedMessage());
        }
    }

    private void extractFromPPTX() {
        imgPath = createImageDirectory();
        try (XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(inFile))) {
            readPptxObjects(ppt);
        } catch (Exception ex) {
            logger.error("Extract from pptx error: {}", ex.getLocalizedMessage());
        }
        logger.info(FILE_PROCESSED);
    }

    private void readPptxObjects(XMLSlideShow ppt) {
        try {
            for (XSLFPictureData picture : ppt.getPictureData()) {
                if (nextImage) {
                    byte[] pictureBytes = picture.getData();
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(pictureBytes));
                    if (isImageValid(bufferedImage)) {
                        outputImage(bufferedImage, picture.getFileName(), picture.suggestFileExtension());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to process PPTX image: {}", ex.getLocalizedMessage());
        }
    }

    private Path createImageDirectory() {
        String imgName = FilenameUtils.removeExtension(inFile.getName());
        Path imgDirName = PATH_TO_IMG_DIR.resolve(imgName);
        logger.info("Image directory path: {}", imgDirName);
        if (!imgDirName.toFile().exists() && !imgDirName.toFile().mkdir()) {
            logger.error("Failed to create image directory: {}", imgDirName);
        }
        return imgDirName;
    }

    private boolean isImageValid(BufferedImage img) {
        if (img.getHeight() < 20 && img.getWidth() < 20) return false;
        Color firstPixelColor = new Color(img.getRGB(0, 0));
        for (int height = 0; height < img.getHeight(); height++) {
            for (int width = 0; width < img.getWidth(); width++) {
                if (!(firstPixelColor.equals(new Color(img.getRGB(width, height))))) return true;
            }
        }
        return false;
    }

    private void outputImage(BufferedImage img, String fileName, String suggestedExtension) throws IOException {
        File imgFile = new File(imgPath.resolve(imgPath.getFileName() + "-" + fileName).toUri());
        ImageIO.write(img, suggestedExtension, imgFile);
        reportGenerator.addImage(imgFile.getAbsolutePath());
    }

    public void fileDone() {
        fileScanner.nextFile();
    }

    public void nextImage() {
        nextImage = true;
    }
}