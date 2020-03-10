package com.component.benjamin.sudoku;

import java.util.ArrayList;
import java.util.List;

public class Sudoku {

    // 所有的格子
    List<Grid> grids;

    // 空格子
    List<Grid> blankGrids;

    // 宫
    List<Palace> palaces;

    // 行
    List<Array> rowArrays;

    // 列
    List<Array> lineArrays;

    public Sudoku() {
        grids = new ArrayList<>();
        blankGrids = new ArrayList<>();
        palaces = new ArrayList<>();
        rowArrays = new ArrayList<>();
        lineArrays = new ArrayList<>();
    }
}
