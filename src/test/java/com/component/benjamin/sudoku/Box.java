package com.component.benjamin.sudoku;

/**
 * 宫
 * @author Benjamin Zheng
 *
 */
public class Box extends Region {

    // 明文的宫号 1-9
    // String name;
    // // pos row+column;
    // String pos;
    // // 宫所在数独的行 0-2
    // int row;
    // // 宫所在数独的列 0-2
    // int column;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Box [name=").append(name).append(", units=")
                .append(units).append(", missingNums=").append(missingNums)
                .append("]");
        return builder.toString();
    }

}
