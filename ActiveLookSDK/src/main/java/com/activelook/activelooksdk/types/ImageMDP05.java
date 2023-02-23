package com.activelook.activelooksdk.types;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

public class ImageMDP05 {

    ///convert image to MDP05 default format
     public static int[][] convertDefault(Bitmap img){
        img = rotateBMP_180(img);

        int height = img.getHeight();
        int width = img.getWidth();

        int[][] encodedImg = new int[height][width];

        //reduce to 4bpp
        for (int y=0; y < height; y++){
            for (int x=0; x < width; x++){
                int pxl =  rgbTo8bitGrayWeightedConvertion(img.getPixel(x,y));
                //convert gray8bit to gray4bit
                encodedImg[y][x] =  Math.round(pxl/16);
            }
        }
        return encodedImg;
    }

    ///convert image to MDP05 1bpp format
     public  static int[][] convert1Bpp(Bitmap img) {
        img = rotateBMP_180(img);

        int height = img.getHeight();
        int width = img.getWidth();

        int[][] encodedImg = new int[height][width];

        //reduce to 1 bpp
        for (int y=0; y < height; y++){
            for (int x=0; x < width; x++){
                //convert gray8bit in gray1bit
                if (rgbTo8bitGrayWeightedConvertion(img.getPixel(x,y)) > 0){
                    encodedImg[y][x] = 1;
                }else{
                    encodedImg[y][x] = 0;
                }
            }
        }

        return encodedImg;
    }
    //convert image to mdp05 8bpp format
    public static int[][] convert8Bpp(Bitmap img) {
        img = rotateBMP_180(img);

        int height = img.getHeight();
        int width = img.getWidth();

        int[][] encodedImg = new int[height][width];

        // Convert image to grayscale
        Bitmap grayImg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayImg);
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(0);
        Paint paint = new Paint();
        ColorFilter filter = new ColorMatrixColorFilter(grayMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(img, 0, 0, paint);

        // Encode image as 8bpp
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayVal = Color.red(grayImg.getPixel(x, y));
                int alphaVal = 15;
                if (img.hasAlpha()) {
                    alphaVal = Color.alpha(img.getPixel(x, y));
                }
                int encodedVal = (grayVal / 16) << 4 + alphaVal / 16;
                encodedImg[y][x] = encodedVal;
            }
        }

        return encodedImg;
    }




    public static  int rgbTo8bitGrayDirectConvertion(int pxl){
        return (Color.red(pxl) + Color.green(pxl) + Color.blue(pxl)) / 3;
    }

    public static  int rgbTo8bitGrayWeightedConvertion(int pxl){
        return (int) ((Color.red(pxl) * 0.299) + (Color.green(pxl) * 0.587) + (Color.blue(pxl) * 0.114));
    }

    public static Bitmap rotateBMP_180(Bitmap img){
        Matrix matrix = new Matrix();
        matrix.setScale(-1,-1);
        matrix.postTranslate(img.getWidth(), img.getHeight());
        return Bitmap.createBitmap(
                img, 0, 0, img.getWidth(), img.getHeight(), matrix, true
        );
    }
}
