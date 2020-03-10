package com.component.benjamin.sudoku;

public class Array extends GridGroup {

    String name;
    int pos;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Array [name=").append(name).append(", pos=").append(pos)
                .append(", grids=").append(grids).append(", missingNums=")
                .append(missingNums).append("]");
        return builder.toString();
    }

}
