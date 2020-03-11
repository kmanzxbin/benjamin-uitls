package com.component.benjamin.sudoku;

/**
 * 行、列共用的队列
 * @author Benjamin Zheng
 *
 */
public class Queue extends Region {

    String name;
    int pos;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Queue [name=").append(name).append(", pos=").append(pos)
                .append(", units=").append(units).append(", missingNums=")
                .append(missingNums).append("]");
        return builder.toString();
    }

}
