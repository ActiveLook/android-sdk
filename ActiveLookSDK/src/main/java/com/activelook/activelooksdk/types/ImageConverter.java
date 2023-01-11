package com.activelook.activelooksdk.types;

import static com.activelook.activelooksdk.types.ImageMDP05.rgbTo8bitGrayWeightedConvertion;
import static com.activelook.activelooksdk.types.ImageMDP05.rotateBMP_180;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.util.Log;

public class ImageConverter {

    public static ImageData getImageData(Bitmap img, ImgSaveFormat fmt) {
        int[][] matrix = convert(img, fmt);
        int width = matrix[0].length;

        switch (fmt){
            case MONO_4BPP:
                return new ImageData(width, getCmd4Bpp(matrix));
            case MONO_4BPP_HEATSHRINK:
                /*let encodedImg = getCmd4Bpp(matrix: matrix)
                let matrixData = Data(bytes: encodedImg, count: encodedImg.count)
                cmds = getCmdCompress4BppHeatshrink(encodedImg: matrixData)
                return ImageData(width: UInt16(width), data: cmds, size: UInt32(encodedImg.count))*/
            case MONO_4BPP_HEATSHRINK_SAVE_COMP:
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

    public static Image1bppData getImageDataStream1bpp(Bitmap img, ImgSaveFormat fmt) {
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

    public static ImageData getImageDataStream4bpp(Bitmap img, ImgSaveFormat fmt) {
        int[][] matrix = convertStream(img, fmt);
        int width = matrix[0].length;

        switch (fmt){
            case MONO_4BPP_HEATSHRINK:
                /*let encodedImg = getCmd4Bpp(matrix: matrix)
                let matrixData = Data(bytes: encodedImg, count: encodedImg.count)
                cmds = getCmdCompress4BppHeatshrink(encodedImg: matrixData)
                return ImageData(width: UInt16(width), data: cmds, size: UInt32(encodedImg.count))*/
            default:
                Log.d("image4bppStreamFormat", "Unknown format");
        }
        return new ImageData();
    }

    private static int[][] convert(Bitmap img, ImgSaveFormat fmt) {
        switch(fmt) {
            case MONO_1BPP:
                return ImageMDP05.convert1Bpp(img);
            case MONO_4BPP:case MONO_4BPP_HEATSHRINK:case MONO_4BPP_HEATSHRINK_SAVE_COMP:
                return ImageMDP05.convertDefault(img);
            default:
                Log.d("imageConvert", "Unknown format");
        }
        return new int[][]{};
    }

    private static int[][] convertStream(Bitmap img, ImgSaveFormat fmt) {
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

        //Compresse img 4 bit per pixel
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

    /*private func getCmdCompress4BppHeatshrink(encodedImg: Data) -> [UInt8]{
        let encoder = RNHeatshrinkEncoder(windowSize: 8, andLookaheadSize: 4)
        return [UInt8](encoder.encode(encodedImg))
    }*/
}
