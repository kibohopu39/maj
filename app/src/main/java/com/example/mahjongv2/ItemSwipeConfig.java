package com.example.mahjongv2;

public final class ItemSwipeConfig {
    /**
     * 顯示可見的item數量
     */
    public static final int DEFAULT_SHOW_ITEM = 2;
    /**
     * 縮放比例
     */
    public static final float DEFAULT_SCALE = 0.5f;
    /**
     * item的Y軸偏移量
     */
    public static final int DEFAULT_TRANSLATE_Y = 6;
    /**
     * item的X軸偏移量
     */
    public static final int DEFAULT_TRANSLATE_X = 2;
    /**
     * 卡片滑动时不偏左也不偏右
     */
    public static final int SWIPING_NONE = 1;
    /**
     * 卡片向左滑动时
     */
    public static final int SWIPING_LEFT = 1 << 2;
    /**
     * 向右滑動
     */
    public static final int SWIPING_RIGHT = 1 << 3;
    /**
     * 卡片从左边滑出
     */
    public static final int SWIPED_LEFT = 1;
    /**
     * 卡片从右边滑出
     */
    public static final int SWIPED_RIGHT = 1 << 2;


}
