package io.girish.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * Graph object that can be used to draw graphs from a list of points.
 * It is able to produce images in PPM (Portable Pixmap) format.
 */
public class Graph {
    private String lineColor = PPMColor.RED;
    private String backgroundColor = PPMColor.WHITE;

    private final int width, height;
    private final List<List<Integer>> points = new ArrayList<>();

    String ppmHeader;

    public Graph(int width, int height) {
        this.width = width;
        this.height = height;

         this.ppmHeader = """
            P3
            %s %s
            255
            """.formatted(width, height);
    }

    public void addPoint(int x, int y) {
        List<Integer> pair = new ArrayList<>();
        pair.add(x);
        pair.add(y);

        this.points.add(pair);
    }

    private int findMinX() {
        int minX = this.points.get(0).get(0);

        for (List<Integer> point : this.points) {
            if (point.get(0) < minX) {
                minX = point.get(0);
            }
        }

        return minX;
    }

    private int findMaxX() {
        int maxX = this.points.get(0).get(0);

        for (List<Integer> point : this.points) {
            if (point.get(0) > maxX) {
                maxX = point.get(0);
            }
        }

        return maxX;
    }

    private int findMinY() {
        int minY = this.points.get(0).get(1);

        for (List<Integer> point : this.points) {
            if (point.get(1) < minY) {
                minY = point.get(1);
            }
        }

        return minY;
    }

    private int findMaxY() {
        int maxY = this.points.get(0).get(1);

        for (List<Integer> point : this.points) {
            if (point.get(1) > maxY) {
                maxY = point.get(1);
            }
        }

        return maxY;
    }

    /*
     * Writes points for graph to an image file in PPM format.
     * https://en.wikipedia.org/wiki/Netpbm#PPM_example
     */
    public void writeImage(String imageLocation) {
        StringBuilder sb = new StringBuilder();

        // Image mapped by height, then width
        List<List<String>> pixels = new ArrayList<>();

        // Draw a blank canvas
        for (int y = 0; y < this.height; y++) {
            List<String> wp = new ArrayList<>();
            for (int x = 0; x < this.width; x++) {
                wp.add(this.getBackgroundColor());
            }
            pixels.add(wp);
        }

        // Calculate translation values to center the graph in our image
        int xTranslation = (this.width / 2) + Math.abs(findMinX()) - (((findMaxX() - findMinX()) / 2));
        int yTranslation = (this.height / 2) + Math.abs(findMinY()) - (((findMaxY() - findMinY()) / 2));

        // Draw calculated coordinates on our image
        for (List<Integer> point : this.points) {
            int posX = point.get(0);
            int posY = point.get(1);

            // Apply translation to center graph horizontally and vertically
            posX += xTranslation;
            posY += yTranslation;

            // Only draw the pixel if point is within image bounds
            if (posY >= 0 && posY < pixels.size() && posX >= 0 && posX < pixels.get(posY).size()) {
                pixels.get(posY).set(posX, this.getLineColor());
            }
        }

        // Construct header for PPM image format
        sb.append(ppmHeader);

        // Create pixels from our graph in PPM format
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                String pixel = pixels.get(y).get(x);
                sb.append(pixel);
            }
        }

        // Write constructed PPM to file
        try {
            FileWriter fw = new FileWriter(imageLocation);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getLineColor() {
        return lineColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }
}
