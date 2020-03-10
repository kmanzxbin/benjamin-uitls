package com.component.benjamin.sudoku;

public class Palace extends GridGroup {

    // 明文的宫号 1-9
    String name;
    // // pos row+line;
    // String pos;
    // // 宫所在数独的行 0-2
    // int row;
    // // 宫所在数独的列 0-2
    // int line;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Palace [name=").append(name).append(", grids=")
                .append(grids).append(", missingNums=").append(missingNums)
                .append("]");
        return builder.toString();
    }

}
