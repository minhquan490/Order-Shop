package com.bachlinh.order.core.enums;

import java.awt.Image;

public enum ImageScaleOption implements Cloneable {
    DEFAULT(300, 300, Image.SCALE_SMOOTH);

    private int width;
    private int height;
    private int hint;

    private ImageScaleOption(int width, int height, int hint) {
        this.width = width;
        this.height = height;
        this.hint = hint;
    }

    public int getHeight() {
        return height;
    }

    public int getHint() {
        return hint;
    }

    public int getWidth() {
        return width;
    }

    public static ImageScaleOption of(int width, int height, int hint) {
        ImageScaleOption result = DEFAULT;
        result.width = width;
        result.height = height;
        result.hint = hint;
        return result;
    }

    public static ImageScaleOption of(int width, int height) {
        return of(width, height, DEFAULT.getHint());
    }
}
