package com.activelook.activelooksdk.types;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Image formats specific to the MDP05 monochrome display module --
 * as opposed to {@link ImageMDP08}, whose formats target the RG color MDP08 module.
 */
public class ImageMDP05 {

    ///convert image to MDP05 default format
    public static int[][] convertDefault(Bitmap img){
        int height = img.getHeight();
        int width = img.getWidth();

        int[][] encodedImg = new int[height][width];

        int[] pixels = ImageConverter.getPixelsReflected(img);

        //reduce to 4bpp
        for (int y=0; y < height; y++){
            for (int x=0; x < width; x++){
                int pxl =  rgbTo8bitGrayWeightedConvertion(pixels[y * width + x]);
                //convert gray8bit to gray4bit
                encodedImg[y][x] =  pxl/16;
            }
        }
        return encodedImg;
    }

    ///convert image to MDP05 1bpp format
    public  static int[][] convert1Bpp(Bitmap img) {
        int height = img.getHeight();
        int width = img.getWidth();

        int[][] encodedImg = new int[height][width];

        int[] pixels = ImageConverter.getPixelsReflected(img);

         //reduce to 1 bpp
        for (int y=0; y < height; y++){
            for (int x=0; x < width; x++){
                //convert gray8bit in gray1bit
                if ((rgbTo8bitGrayWeightedConvertion(pixels[y * width + x])) > 0){
                    encodedImg[y][x] = 1;
                } else {
                    encodedImg[y][x] = 0;
                }
            }
        }

        return encodedImg;
    }

    /**
     * Convert image to MDP05 8bpp grey+alpha format (doc section 5.5.4): each cell is already the
     * final on-wire byte, (grey << 4) | alpha, both 0-15 -- unlike convertDefault()'s cells, which
     * are just the grey level and still need packing 2-per-byte by getCmd4Bpp().
     */
    public static int[][] convert4bppAlpha(Bitmap img) {
        int height = img.getHeight();
        int width = img.getWidth();

        int[][] encodedImg = new int[height][width];

        int[] pixels = ImageConverter.getPixelsReflected(img);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pxl = pixels[y * width + x];
                int grey = scaleTo15Levels(rgbTo8bitGrayWeightedConvertion(pxl));
                int alpha = scaleTo15Levels(Color.alpha(pxl));
                encodedImg[y][x] = ((grey & 0x0F) << 4) | (alpha & 0x0F);
            }
        }
        return encodedImg;
    }

    /** Scales an 8-bit channel (0-255) to a 0-15 level, rounding half up -- the integer
     * equivalent of Math.round(channel / 255f * 15f), avoiding float math per-pixel.
     */
    private static int scaleTo15Levels(int channel) {
        return (channel * 30 + 255) / 510;
    }

    public static  int rgbTo8bitGrayDirectConvertion(int pxl){
        return (Color.red(pxl) + Color.green(pxl) + Color.blue(pxl)) / 3;
    }

    public static  int rgbTo8bitGrayWeightedConvertion(int pxl){
        return (int) ((Color.red(pxl) * 0.299) + (Color.green(pxl) * 0.587) + (Color.blue(pxl) * 0.114));
    }
}
