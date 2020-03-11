package com.component.benjamin.sudoku;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * 格子
 * @author Benjamin Zheng
 *
 */
@Slf4j
public class Unit {

    // 明文的格子名称 使用坐标标识 X:A-I Y:1-9 示例 A1
    String name;
    // pos row+column;
    String pos;
    // 行 0-9
    int row;
    // 列 0-9
    int column;
    // 数字 0 表示未确定
    int num;
    // 候选数
    Set<Integer> hints = new HashSet<>();

    public Set<Integer> getHints() {
        return hints;
    }

    public String getName() {
        return name;
    }

    Grid grid;
    // 所在宫
    Box box;
    // 所在行
    Queue rowQueue;
    // 所在列
    Queue columnQueue;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Unit [name=").append(name).append(", pos=").append(pos)
                .append(", num=").append(num).append(", hints=").append(hints)
                .append("]");
        return builder.toString();
    }

    public void fillNum(int num, String method) {
        this.num = num;
        grid.blankUnits.remove(this);
        hints.clear();

        log.info("filling num {} to {} by {}, {} blank unit remaining", num,
                name, method, grid.blankUnits.size());

        Assert.assertTrue(box.missingNums.remove(num));
        Assert.assertTrue(rowQueue.missingNums.remove(num));
        Assert.assertTrue(columnQueue.missingNums.remove(num));

        removeHint(num);
    }

    public void removeHint(int num) {
        removeHint(box, num);
        removeHint(rowQueue, num);
        removeHint(columnQueue, num);
    }

    public void removeHint(Region region, int num) {
        region.units.forEach(t -> {
            if (t.hints != null && t != this)
                if (t.hints.contains(num)) {
                    log.info("remove hint {} from unit {} in {}", num, t,
                            region.name);
                    t.hints.remove(num);
                }
            ;
        });
    }

}