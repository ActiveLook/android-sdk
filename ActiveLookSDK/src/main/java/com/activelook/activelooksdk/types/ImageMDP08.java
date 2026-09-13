package com.activelook.activelooksdk.types;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Image formats specific to the MDP08 display module (Engo 3 and other color-capable glasses) --
 * as opposed to {@link ImageMDP05}, whose formats target the mono MDP05 module.
 */
public class ImageMDP08 {

    /**
     * RG (red-green) color palette, doc section 5.5.5: color glasses have no blue subpixel, so
     * the display's 81 colors are the product of 9 red intensities x 9 green intensities
     * (0=off .. 8=max each), packed into disjoint bitfields of the color byte
     * (red: bits 2-5, green: bits 0-1+6-7).
     */
    private static final int[] RG_RED_LEVELS   = {0x00, 0x04, 0x20, 0x14, 0x18, 0x28, 0x1C, 0x38, 0x3C};
    private static final int[] RG_GREEN_LEVELS = {0x00, 0x01, 0x02, 0x41, 0x81, 0x82, 0x43, 0x83, 0xC3};

    /** Scales an 8-bit channel (0-255) to a 0-8 intensity level, rounding half up -- the integer
     * equivalent of Math.round(channel / 255f * 8f), avoiding float math per-pixel.
     */
    private static int scaleTo8Levels(int channel) {
        return (channel * 16 + 255) / 510;
    }

    /**
     * Approximates a standard 24-bit RGB pixel as an RG color byte. There's no blue subpixel to
     * map to, so blue is folded into green -- taking max(g, b) rather than just g -- since blue
     * tones (e.g. water in aerial/nautical imagery) would otherwise be rendered as black and lose
     * the most contrast; this is a lossy brightness-preserving approximation, not a hue-accurate
     * one. The red/green intensities (each 0 off .. 8 max) are combined into an RG color byte via
     * RG_RED_LEVELS/RG_GREEN_LEVELS; there's no blue subpixel, so red=green=8 is the closest this
     * display gets to white (a yellow-ish mix), not true white.
     */
    private static int rgbToRgColor(int r, int g, int b) {
        int redLevel = scaleTo8Levels(r);
        int greenLevel = scaleTo8Levels(Math.max(g, b));
        return RG_RED_LEVELS[redLevel] | RG_GREEN_LEVELS[greenLevel];
    }

    /**
     * Convert image to MDP08 RG 8bpp color format (doc section 5.5.5): each cell is already the
     * final on-wire byte (see rgbToRgColor()) -- one byte per pixel, no bit-packing needed. The
     * 180-degree optical-mirroring correction is ImageConverter.getPixelsReflected()'s reversed scan,
     * not a physical bitmap rotation.
     */
    public static int[][] convertRgColor8bpp(Bitmap img) {
        int height = img.getHeight();
        int width = img.getWidth();

        int[][] encodedImg = new int[height][width];

        int[] pixels = ImageConverter.getPixelsReflected(img);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pxl = pixels[y * width + x];
                encodedImg[y][x] = rgbToRgColor(Color.red(pxl), Color.green(pxl), Color.blue(pxl));
            }
        }
        return encodedImg;
    }

    /**
     * Convert image to MDP08 RG 8bpp color + 4-bit alpha format (doc section 5.5.6): each cell
     * packs that pixel's RG color byte (see rgbToRgColor()) into bits 4-11 and its alpha nibble
     * into bits 0-3 -- firmware only supports boolean alpha (0 or 15; any nonzero source alpha
     * reads as fully opaque), and ImageConverter.getCmdRgColor8BppAlpha() regroups these packed
     * cells into the doc's color/color/alpha pixel-pair layout. Same reversed-scan 180-degree
     * correction as convertRgColor8bpp().
     */
    public static int[][] convertRgColor8bppAlpha(Bitmap img) {
        int height = img.getHeight();
        int width = img.getWidth();

        int[][] encodedImg = new int[height][width];

        int[] pixels = ImageConverter.getPixelsReflected(img);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pxl = pixels[y * width + x];
                int color = rgbToRgColor(Color.red(pxl), Color.green(pxl), Color.blue(pxl));
                int alpha = Color.alpha(pxl) == 0 ? 0x0 : 0xF;
                encodedImg[y][x] = (color << 4) | alpha;
            }
        }
        return encodedImg;
    }
}
