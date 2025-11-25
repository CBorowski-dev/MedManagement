package de.medmanagement.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Component
public class CheckDrugConsumptionTask implements Tasklet {

    @Value( "${perform.job.check_drug_consumption}" )
    private String performJob;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if ( performJob.equals("true") ) {
            System.out.println("--> Task CheckDrugConsumptionTask execute starts");
            System.out.printf("java.library.path: %s%n", System.getProperty("java.library.path"));

            if ( performJob.equals("true") ) {
                //Reading images
                BufferedImage img1 = ImageIO.read(new File("C:/Users/borowski/Pictures/Camera Roll/xyz_halbvoll.jpg"));
                BufferedImage img2 = ImageIO.read(new File("C:/Users/borowski/Pictures/Camera Roll/xyz_leer.jpg"));

                // Crop images
                Rectangle rectCrop = new Rectangle(454, 214, 363, 580);
                img1 = cropImage(img1, rectCrop);
                img2 = cropImage(img2, rectCrop);

                showDedectedDrugs(img1, img2, 150, 3, 2);
            }

            System.out.println("<-- Task CheckDrugConsumptionTask execute done");
        }
        return RepeatStatus.FINISHED;
    }

    private BufferedImage showDedectedDrugs(BufferedImage img1, BufferedImage img2, int edgeLength, int rows, int columns) {
        int height = img1.getHeight();
        int width = img1.getWidth();

        for (int x=0; x<columns; x++) {
            for (int y=0; y<rows; y++) {
                // Extract regions
                int x_rect = width / (columns * 2) + (x * width / columns) - edgeLength/2;
                int y_rect = height / (rows * 2) + (y * height / rows) - edgeLength / 2;
                Rectangle rectCrop = new Rectangle(x_rect, y_rect, edgeLength, edgeLength);
                // Calculate difference
                Double diff = getDifferencePercent(cropImage(img1, rectCrop), cropImage(img2, rectCrop));
                System.out.println("["+x+"]["+y+"] : " + diff + " -> " + (diff > 4.0 ? "filled":"notfilled"));

                // Mark region on origin Mat
                // Scalar color = (averageColor > -0.9 ? new Scalar(0,0,255):new Scalar(0,255,0));
                // Draw rectangle
                // Imgproc.line(matColor, new Point(x_rect, y_rect), new Point(x_rect + edgeLength, y_rect), color, 3);
                // Imgproc.line(matColor, new Point(x_rect + edgeLength, y_rect), new Point(x_rect + edgeLength, y_rect + edgeLength), color, 3);
                // Imgproc.line(matColor, new Point(x_rect + edgeLength, y_rect + edgeLength), new Point(x_rect, y_rect + edgeLength), color, 3);
                // Imgproc.line(matColor, new Point(x_rect, y_rect + edgeLength), new Point(x_rect, y_rect), color, 3);
            }
        }

        return null;
    }

    private BufferedImage cropImage(BufferedImage src, Rectangle rect) {
        BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width, rect.height);
        return dest;
    }

    private double getDifferencePercent(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();
        if (width != width2 || height != height2) {
            throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
        }

        long diff = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
            }
        }
        long maxDiff = 3L * 255 * width * height;

        return 100.0 * diff / maxDiff;
    }

    private int pixelDiff(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >>  8) & 0xff;
        int b1 =  rgb1        & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >>  8) & 0xff;
        int b2 =  rgb2        & 0xff;
        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

}
