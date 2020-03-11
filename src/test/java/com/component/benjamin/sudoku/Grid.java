package com.component.benjamin.sudoku;

import java.util.ArrayList;
import java.util.List;

/**
 * 九宫格
 * @author Benjamin Zheng
 *
 */
public class Grid {

    // 所有的格子
    List<Unit> units;

    // 空格子
    List<Unit> blankUnits;

    // 宫
    List<Box> boxes;

    // 行
    List<Queue> rowQueues;

    // 列
    List<Queue> columnQueues;

    public Grid() {
        units = new ArrayList<>();
        blankUnits = new ArrayList<>();
        boxes = new ArrayList<>();
        rowQueues = new ArrayList<>();
        columnQueues = new ArrayList<>();
    }
}
