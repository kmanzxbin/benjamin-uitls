package com.component.benjamin.sudoku;

import java.util.Set;

import org.junit.Assert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Grid {

    // 明文的格子名称 使用坐标标识 X:A-I Y:1-9 示例 A1
    String name;
    // pos row+line;
    String pos;
    // 行 0-9
    int row;
    // 列 0-9
    int line;
    // 数字 0 表示未确定
    int num;
    // 候选数
    Set<Integer> candis;

    Sudoku sudoku;
    // 所在宫
    Palace palace;
    // 所在行
    Array rowArray;
    // 所在列
    Array lineArray;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Grid [name=").append(name).append(", pos=").append(pos)
                .append(", num=").append(num).append(", candis=").append(candis)
                .append("]");
        return builder.toString();
    }

    public void fillNum(int num, String method) {
        this.num = num;
        sudoku.blankGrids.remove(this);
        candis = null;

        Assert.assertTrue(palace.missingNums.remove(num));
        Assert.assertTrue(rowArray.missingNums.remove(num));
        Assert.assertTrue(lineArray.missingNums.remove(num));
        log.info("fill num {} to {} by {}, blank grid remain {}", num, name,
                method, sudoku.blankGrids.size());
    }

}