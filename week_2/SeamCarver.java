import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

/**
 * @author Dima Pasieka
 */
public class SeamCarver {
    private static final double BORDER_ENERGY = 1000;

    private Picture picture;
    private int width;
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }

        this.picture = picture;
        width = picture.width();
        height = picture.height();
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException();
        }

        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
            return BORDER_ENERGY;
        }

        return Math.sqrt(deltaX(x, y) + deltaY(x, y));
    }

    private double deltaX(int x, int y) {
        Color left = picture.get(x - 1, y);
        Color right = picture.get(x + 1, y);

        int redDiff = right.getRed() - left.getRed();
        int greenDiff = right.getGreen() - left.getGreen();
        int blueDiff = right.getBlue() - left.getBlue();

        return Math.pow(redDiff, 2) + Math.pow(greenDiff, 2) + Math.pow(blueDiff, 2);
    }

    private double deltaY(int x, int y) {
        Color top = picture.get(x, y - 1);
        Color bottom = picture.get(x, y + 1);

        int redDiff = bottom.getRed() - top.getRed();
        int greenDiff = bottom.getGreen() - top.getGreen();
        int blueDiff = bottom.getBlue() - top.getBlue();

        return Math.pow(redDiff, 2) + Math.pow(greenDiff, 2) + Math.pow(blueDiff, 2);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[][] edgeTo = new int[height][width];
        double[][] distTo = new double[height][width];
        resetDist(distTo);

        for (int row = 0; row < height; row++) {
            distTo[row][0] = 1000;
        }

        for (int col = 0; col < width - 1; col++) {
            for (int row = 0; row < height; row++) {
                relaxHorizontal(row, col, edgeTo, distTo);
            }
        }

        double minDist = Double.POSITIVE_INFINITY;
        int minRow = 0;
        for (int row = 0; row < height; row++)
            if (minDist > distTo[row][width - 1]) {
                minDist = distTo[row][width - 1];
                minRow = row;
            }

        int[] seam = new int[width];
        for (int col = width - 1, row = minRow; col >= 0; col--) {
            seam[col] = row;
            row -= edgeTo[row][col];
        }

        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[][] edgeTo = new int[height][width];
        double[][] distTo = new double[height][width];
        resetDist(distTo);

        for (int col = 0; col < width; col++) {
            distTo[0][col] = 1000;
        }

        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {
                relaxVertical(row, col, edgeTo, distTo);
            }
        }

        double minDist = Double.POSITIVE_INFINITY;
        int minCol = 0;
        for (int col = 0; col < width; col++) {
            if (minDist > distTo[height - 1][col]) {
                minDist = distTo[height - 1][col];
                minCol = col;
            }
        }

        int[] seam = new int[height];
        for (int row = height - 1, col = minCol; row >= 0; row--) {
            seam[row] = col;
            col -= edgeTo[row][col];
        }

        return seam;
    }

    private void relaxHorizontal(int row, int col, int[][] edgeTo, double[][] distTo) {
        int nextCol = col + 1;
        for (int i = -1; i <= 1; i++) {
            int nextRow = row + i;
            if (nextRow < 0 || nextRow >= height) {
                continue;
            }
            if (distTo[nextRow][nextCol] > distTo[row][col] + energy(nextCol, nextRow)) {
                distTo[nextRow][nextCol] = distTo[row][col] + energy(nextCol, nextRow);
                edgeTo[nextRow][nextCol] = i;
            }
        }
    }

    private void relaxVertical(int row, int col, int[][] edgeTo, double[][] distTo) {
        int nextRow = row + 1;
        for (int i = -1; i <= 1; i++) {
            int nextCol = col + i;
            if (nextCol < 0 || nextCol >= width) {
                continue;
            }
            if (distTo[nextRow][nextCol] > distTo[row][col] + energy(nextCol, nextRow)) {
                distTo[nextRow][nextCol] = distTo[row][col] + energy(nextCol, nextRow);
                edgeTo[nextRow][nextCol] = i;
            }
        }
    }

    private void resetDist(double[][] distTo) {
        for (int i = 0; i < distTo.length; i++) {
            for (int j = 0; j < distTo[i].length; j++) {
                distTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }

        if (seam.length != width || height <= 1) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height) {
                throw new IllegalArgumentException();
            }

            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }

        Picture newPicture = new Picture(width, height - 1);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height - 1; row++) {
                if (row >= seam[col]) {
                    newPicture.set(col, row, picture.get(col, row + 1));
                } else {
                    newPicture.set(col, row, picture.get(col, row));
                }
            }
        }
        picture = newPicture;
        height = height() - 1;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException();
        }

        if (seam.length != height || width <= 1) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width) {
                throw new IllegalArgumentException();
            }

            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }

        Picture newPicture = new Picture(width - 1, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width - 1; col++) {
                if (col >= seam[row]) {
                    newPicture.set(col, row, picture.get(col + 1, row));
                } else {
                    newPicture.set(col, row, picture.get(col, row));
                }
            }
        }

        picture = newPicture;
        width = width() - 1;
    }
}