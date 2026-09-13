package com.activelook.activelooksdk.types;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import heatshrink.HsOutputStream;

public class ImageConverter {

    public static ImageData getImageData(Bitmap img, ImgSaveFormat fmt) {
        int[][] matrix = convert(img, fmt);
        int width = matrix[0].length;

        switch (fmt){
            case MONO_4BPP:
                return new ImageData(width, getCmd4Bpp(matrix));
            case MONO_4BPP_HEATSHRINK:
            case MONO_4BPP_HEATSHRINK_SAVE_COMP: {
                byte[] raw = getCmd4Bpp(matrix);
                byte[] compressed = getCmdCompress4BppHeatshrink(raw);
                return new ImageData(width, compressed, raw.length);
            }
            case MONO_4BPP_ALPHA: {
                byte[] raw = new byte[width * matrix.length];
                int ai = 0;
                for (int[] row : matrix) for (int px : row) raw[ai++] = (byte) px;
                return new ImageData(width, raw);
            }
            case RG_COLOR_8BPP: {
                byte[] raw = new byte[width * matrix.length];
                int i = 0;
                for (int[] row : matrix) for (int px : row) raw[i++] = (byte) px;
                byte[] compressed = getCmdCompress4BppHeatshrink(raw);
                return new ImageData(width, compressed, raw.length);
            }
            case RG_COLOR_8BPP_ALPHA: {
                byte[] raw = getCmdRgColor8BppAlpha(matrix);
                byte[] compressed = getCmdCompress4BppHeatshrink(raw);
                return new ImageData(width, compressed, raw.length);
            }
            default:
                Log.d("imageFormat", "Unknown format");
        }
        return new ImageData();
    }

    public static Image1bppData getImage1bppData(Bitmap img, ImgSaveFormat fmt) {
        int[][] matrix = convert(img, fmt);
        int width = matrix[0].length;

        switch (fmt){
            case MONO_1BPP:
                byte[][] cmds = getCmd1Bpp(matrix);
                return new Image1bppData(width, cmds);
            default:
                Log.d("image1bppFormat", "Unknown format");
        }
        return new Image1bppData();
    }

    public static Image1bppData getImageDataStream1bpp(Bitmap img, ImgStreamFormat fmt) {
        int[][] matrix = convertStream(img, fmt);
        int width = matrix[0].length;

        switch (fmt){
            case MONO_1BPP:
                return new Image1bppData(width, getCmd1Bpp(matrix));
            default:
                Log.d("image1bppStreamFormat", "Unknown format");
        }
        return new Image1bppData();
    }

    public static ImageData getImageDataStream4bpp(Bitmap img, ImgStreamFormat fmt) {
        int[][] matrix = convertStream(img, fmt);
        int width = matrix[0].length;

        switch (fmt){
            case MONO_4BPP_HEATSHRINK:
                byte[] encodedImg = getCmd4Bpp(matrix);
                byte[] cmds = getCmdCompress4BppHeatshrink(encodedImg);
                return new ImageData(width,cmds, encodedImg.length);
            default:
                Log.d("image4bppStreamFormat", "Unknown format");
        }
        return new ImageData();
    }

    private static int[][] convert(Bitmap img, ImgSaveFormat fmt) {
        switch(fmt) {
            case MONO_1BPP:
                return ImageMDP05.convert1Bpp(img);
            case MONO_4BPP:
            case MONO_4BPP_HEATSHRINK:
            case MONO_4BPP_HEATSHRINK_SAVE_COMP:
                return ImageMDP05.convertDefault(img);
            case MONO_4BPP_ALPHA:
                return ImageMDP05.convert4bppAlpha(img);
            case RG_COLOR_8BPP:
                return ImageMDP08.convertRgColor8bpp(img);
            case RG_COLOR_8BPP_ALPHA:
                return ImageMDP08.convertRgColor8bppAlpha(img);
            default:
                Log.d("imageConvert", "Unknown format");
        }
        return new int[][]{};
    }

    private static int[][] convertStream(Bitmap img, ImgStreamFormat fmt) {
        switch(fmt) {
            case MONO_1BPP:
                return ImageMDP05.convert1Bpp(img);
            case MONO_4BPP_HEATSHRINK:
                return ImageMDP05.convertDefault(img);
            default:
                Log.d("imageConvert", "Unknown format");
        }
        return new int[][]{};
    }

    //prepare command to save image
    private static byte[] getCmd4Bpp(int[][] matrix){
        int height = matrix.length;
        int width = matrix[0].length;
        int arraySize = height * ((int) Math.ceil((float) width/2.0));

        //Compress img 4 bit per pixel
        byte[] encodedImg = new byte[arraySize];
        int count = 0;

        for (int i=0; i < height; i++){
            byte b = 0;
            byte shift = 0;
            for (int j=0; j < width; j++){
                byte pxl = (byte) matrix[i][j];

                //compress 4 bit per pixel
                b += pxl << shift;
                shift += 4;
                if (shift == 8){
                    encodedImg[count] = b;
                    b = 0;
                    shift = 0;
                    count++;
                }
            }
            if (shift != 0){
                encodedImg[count] = b;
                count++;
            }
        }
        return  encodedImg;
    }

    // Regroups ImageMDP08.convertRgColor8bppAlpha()'s packed per-pixel cells (color in bits 4-11,
    // alpha nibble in bits 0-3) into doc section 5.5.6's pixel-pair layout: color, color, alpha
    // byte (low nibble = first pixel's alpha, high nibble = second's). An odd-width row's final
    // unpaired pixel still costs a full 3-byte slot -- the doc allows the second color/alpha to
    // be arbitrary there, so it's zeroed rather than reading past the row.
    private static byte[] getCmdRgColor8BppAlpha(int[][] matrix) {
        int height = matrix.length;
        int width = matrix[0].length;
        int pairsPerRow = (width + 1) / 2;
        byte[] encodedImg = new byte[height * pairsPerRow * 3];
        int count = 0;
        for (int[] row : matrix) {
            for (int j = 0; j < width; j += 2) {
                int cell1 = row[j];
                int color1 = (cell1 >> 4) & 0xFF;
                int alpha1 = cell1 & 0x0F;
                int color2 = 0;
                int alpha2 = 0;
                if (j + 1 < width) {
                    int cell2 = row[j + 1];
                    color2 = (cell2 >> 4) & 0xFF;
                    alpha2 = cell2 & 0x0F;
                }
                encodedImg[count++] = (byte) color1;
                encodedImg[count++] = (byte) color2;
                encodedImg[count++] = (byte) ((alpha2 << 4) | alpha1);
            }
        }
        return encodedImg;
    }

    private static byte[][] getCmd1Bpp(int[][] matrix){
        int height = matrix.length;
        int width = matrix[0].length;
        int subArraySize = getArraySize(matrix);

        //Compress img 1 bit per pixel
        byte[][] encodedImg = new byte[height][subArraySize];
        for (int y=0; y < height; y++){
            byte b = 0;
            byte shift = 0;
            byte[] encodedLine = new byte[subArraySize];
            int lineCount = 0;
            for (int x=0; x < width; x++){
                byte pxl = (byte) matrix[y][x];

                //compress 1 bit per pixel
                b += pxl << shift;
                shift += 1;
                if (shift == 8){
                    encodedLine[lineCount] = b;
                    b = 0;
                    shift = 0;
                    lineCount++;
                }
            }
            if (shift != 0){
                encodedLine[lineCount] = b;
                lineCount++;
            }
            encodedImg[y] = encodedLine;
        }
        return  encodedImg;
    }

    private static int getArraySize(int[][] matrix){
        int height = matrix.length;
        int width = matrix[0].length;
        int arraySize = 0;

        for (int i=0; i < height; i++){
            byte shift = 0;
            int lineCount = 0;
            for (int j=0; j < width; j++){
                shift += 1;
                if (shift == 8){
                    shift = 0;
                    lineCount++;
                }
            }
            if (shift != 0){
                lineCount++;
            }
            arraySize = lineCount;
        }
        return  arraySize;
    }

    /** Reads img's pixels into a row-major int[] with the 180-degree optical-mirroring correction
     * every format on this display needs already applied. Rotating an image 180 degrees maps
     * pixel (x,y) to (W-1-x, H-1-y), which in a row-major buffer is exactly index (total-1-i) for
     * the original index i = y*W+x, since (H-1-y)*W + (W-1-x) = H*W-1 - (y*W+x) -- so the
     * correction is just reading the pixels back to front, no Matrix/Canvas/Bitmap-copy needed
     * (this replaces the old rotateBMP_180(), which built a whole second rotated Bitmap first). */
    public static int[] getPixelsReflected(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int total = width * height;
        int[] pixels = new int[total];
        img.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0, j = total - 1; i < j; i++, j--) {
            int tmp = pixels[i];
            pixels[i] = pixels[j];
            pixels[j] = tmp;
        }
        return pixels;
    }

    public static byte[] getCmdCompress4BppHeatshrink(byte[] encodedImg){
        int windowSize = 8;
        int lookaheadSize = 4;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try(HsOutputStream out = new HsOutputStream(baos, windowSize, lookaheadSize)) {
             out.write(encodedImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}
