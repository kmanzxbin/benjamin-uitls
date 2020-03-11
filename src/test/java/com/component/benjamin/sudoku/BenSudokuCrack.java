package com.component.benjamin.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Benjamin Zheng
 *
 */
@Slf4j
public class BenSudokuCrack {

    // 行row A到I
    // 列column 1-9

    // 行缺一个数
    // 列缺一个数
    // 宫缺一个数

    public static void main(String[] args) {

        int[][] matrix = {

                // { 1, 0, 0, 8, 3, 0, 0, 0, 2 }, { 5, 7, 0, 0, 0, 1, 0, 0, 0 },
                // { 0, 0, 0, 5, 0, 9, 0, 6, 4 }, { 7, 0, 4, 0, 0, 8, 5, 9, 0 },
                // { 0, 0, 3, 0, 1, 0, 4, 0, 0 }, { 0, 5, 1, 4, 0, 0, 3, 0, 6 },
                // { 3, 6, 0, 7, 0, 4, 0, 0, 0 }, { 0, 0, 0, 6, 0, 0, 0, 7, 9 },
                // { 8, 0, 0, 0, 5, 2, 0, 0, 3 },

                { 8, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 3, 6, 0, 0, 0, 0, 0 },
                { 0, 7, 0, 0, 9, 0, 2, 0, 0 }, { 0, 5, 0, 0, 0, 7, 0, 0, 0 },
                { 0, 0, 0, 0, 4, 5, 7, 0, 0 }, { 0, 0, 0, 1, 0, 0, 0, 3, 0 },
                { 0, 0, 1, 0, 0, 0, 0, 6, 0 }, { 0, 0, 8, 5, 0, 0, 0, 1, 0 },
                { 0, 9, 0, 0, 0, 0, 0, 0, 0 },

                // { 0, 0, 5, 0, 2, 0, 0, 9, 0 }, { 3, 0, 0, 6, 0, 7, 0, 0, 0 },
                // { 0, 0, 1, 0, 4, 0, 7, 0, 8 }, { 0, 3, 0, 0, 0, 0, 0, 7, 0 },
                // { 2, 0, 8, 0, 0, 0, 3, 0, 4 }, { 0, 4, 0, 0, 0, 0, 0, 2, 0 },
                // { 8, 0, 7, 0, 6, 0, 1, 0, 0 }, { 0, 0, 0, 7, 0, 5, 0, 0, 9 },
                // { 0, 9, 0, 0, 1, 0, 5, 0, 0 },

        };
        BenSudokuCrack mySudokuCrack = new BenSudokuCrack();
        mySudokuCrack.crack(matrix);
    }

    Grid grid;

    int[][] matrix;

    public void crack(int[][] matrix) {
        checking(matrix, false);
        this.matrix = matrix;
        parseGrid(matrix);
        int blankGridCountBeforeCrack = grid.blankUnits.size();

        int round = 0;
        while (blankGridCountBeforeCrack > 0) {
            round++;
            log.info("round {}", round);
            basicExclude();
            hiddenOne();
            excludeByBoxSameQueueHints();
            if (grid.blankUnits.size() == blankGridCountBeforeCrack) {
                if (excludeHintsByNumberGroup()) {
                    continue;
                }
                log.warn("can not crack this grid!\n{}",
                        StringUtils.join(grid.blankUnits, "\n"));
                System.exit(1);
            }
            blankGridCountBeforeCrack = grid.blankUnits.size();
        }
        log.info("crack finished!");

        grid.units.forEach(t -> matrix[t.row][t.column] = t.num);
        checking(matrix, true);

        print();
    }

    void checking(int[][] matrix, boolean finished) {
        for (int i = 0; i < 9; i++) {
            Set<Integer> nums = new HashSet<>();
            for (int j = 0; j < 9; j++) {
                int num = matrix[i][j];
                if (num == 0) {
                    if (finished) {
                        Assert.fail("have 0 when finished!");
                    }
                } else {
                    Assert.assertTrue("num " + num + " at " + i + j,
                            nums.add(num));
                }
            }
            if (finished) {
                Assert.assertEquals(9, nums.size());
            }
        }

        for (int i = 0; i < 9; i++) {
            Set<Integer> nums = new HashSet<>();
            for (int j = 0; j < 9; j++) {
                int num = matrix[j][i];
                if (num == 0) {
                    if (finished) {
                        Assert.fail("have 0 when finished!");
                    }
                } else {
                    Assert.assertTrue("num " + num + " at " + i + j,
                            nums.add(num));
                }
            }
            if (finished) {
                Assert.assertEquals(9, nums.size());
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                Set<Integer> nums = new HashSet<>();
                for (int r = 0; r < 3; r++) {

                    for (int c = 0; c < 3; c++) {
                        int x = i * 3 + r;
                        int y = j * 3 + c;
                        int num = matrix[x][y];
                        if (num == 0) {
                            if (finished) {
                                Assert.fail("have 0 when finished!");
                            }
                        } else {
                            Assert.assertTrue("num " + num + " at " + (x) + (y),
                                    nums.add(num));
                        }
                    }
                }
                if (finished) {
                    Assert.assertEquals(9, nums.size());
                }
            }

        }

    }

    /**
     * 如果一个宫里有三个或者两个相同行或者列的空格子，则可以排除其他列的这些值的候选数
     */
    boolean excludeByBoxSameQueueHints() {
        Set<Box> boxs = grid.boxes.stream()
                .filter(t -> t.missingNums.size() <= 3)
                .collect(Collectors.toSet());
        if (boxs.size() == 0) {

            return false;
        }
        AtomicInteger counter = new AtomicInteger(0);

        boxs.forEach(t -> {
            List<Unit> grids = t.getBlankUnits();
            if (grids.size() < 2) {
                return;
            }
            if (grids.size() != t.missingNums.size()) {
                log.error(
                        "logical error! missing numbers is not equals blank grids in {}",
                        t);
                System.exit(1);
            }
            if (grids.get(0).row == grids.get(1).row && ((grids.size() > 2)
                    ? grids.get(1).row == grids.get(2).row
                    : true)) {
                // 行相同 将其他同一行其他宫格子的候选数移除这三个格子的候选数
                grid.blankUnits.stream().filter(
                        g -> g.row == grids.get(0).row && !grids.contains(g))
                        .forEach(g -> g.hints.removeAll(t.missingNums));
                counter.incrementAndGet();
                log.info("remove hint num {} from same row {} by box {}",
                        t.missingNums, grids.get(0).row, t.name);

            } else if (grids.get(0).column == grids.get(1).column
                    && ((grids.size() > 2)
                            ? grids.get(1).column == grids.get(2).column
                            : true)) {
                // 列相同 将其他同一列其他宫格子的候选数移除这三个格子的候选数
                grid.blankUnits.stream()
                        .filter(g -> g.column == grids.get(0).column
                                && !grids.contains(g))
                        .forEach(g -> g.hints.removeAll(t.missingNums));
                counter.incrementAndGet();
                log.info("remove hint num {} from same column {} by box {}",
                        t.missingNums, grids.get(0).row, t.name);
            }
        });

        return counter.get() > 0;
    }

    /**
     * 数组排除法
     * @return
     */
    boolean excludeHintsByNumberGroup() {
        AtomicInteger counter = new AtomicInteger(0);

        grid.boxes.forEach(t -> counter.addAndGet(numberGroupExcludeOthers(t)));
        if (counter.get() > 0) {
            return true;
        }
        grid.rowQueues
                .forEach(t -> counter.addAndGet(numberGroupExcludeOthers(t)));
        if (counter.get() > 0) {
            return true;
        }
        grid.columnQueues
                .forEach(t -> counter.addAndGet(numberGroupExcludeOthers(t)));
        return counter.get() > 0;
    }

    /**
     * 候选数组排除其他格子的候选数
     */
    static int numberGroupExcludeOthers(Region region) {
        // 从候选数中抽取两个数据一样的
        // grid.rowQueues.forEach(t -> t.units.stream().filter(predicate));
        // grid.units.stream().collect(Collectors.groupingBy(Unit::getHints,
        // Collectors.counting()));
        // log.debug("numberGroupExcludeOthers: {}", region.name);
        // 获取候选数是2个或者3个的
        Map<Set<Integer>, List<Unit>> unitsGroupByHints = region.units.stream()
                .filter(t -> t.hints.size() == 2 || t.hints.size() == 3)
                .collect(Collectors.groupingBy(Unit::getHints));
        // log.debug("unitsGroupByHints: \n{}",
        // StringUtils.join(unitsGroupByHints.entrySet(), "\n"));

        AtomicInteger counter = new AtomicInteger(0);

        for (Entry<Set<Integer>, List<Unit>> entry : unitsGroupByHints
                .entrySet()) {

            // 如果对应的格子数和候选数的个数一样，说明 这些格子把这些数都占住了，清除其关联的其他格子的这些候选数
            if (entry.getKey().size() == entry.getValue().size()) {
                log.info("the units {} have same hints {} in {}!",
                        entry.getValue().stream().map(Unit::getName)
                                .collect(Collectors.toList()),
                        entry.getKey(), region.name);
                counter.incrementAndGet();
                region.units.stream().filter(t -> !entry.getValue().contains(t))
                        .collect(Collectors.toList()).forEach(t -> {
                            entry.getKey().forEach(num -> {
                                if (t.hints.contains(num)) {
                                    log.info(
                                            "remove hint {} from unit {} in {}",
                                            num, t, region.name);
                                    t.hints.remove(num);
                                }
                            });

                        });
            }
        }
        return counter.get();
    }

    void print() {
        grid.units.forEach(t -> matrix[t.row][t.column] = t.num);
        for (int i = 0; i < 9; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
    }

    /**
     * 把原始数据转成对象的映射
     * @param matrix
     * @return
     */
    void parseGrid(int[][] matrix) {

        grid = new Grid();

        parseUnits();

        // 解析所有的宫 设置每个空格子的候选数
        parseBoxs();

        // 解析所有的行和列 设置设置每个格子的候选数
        parseRows();
        parseLines();

    }

    void parseUnits() {

        // 先处理每个格子
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {

                Unit unit = new Unit();
                unit.name = getName(row, column);
                unit.row = row;
                unit.column = column;
                unit.pos = "" + row + column;
                unit.num = matrix[row][column];
                if (unit.num == 0) {
                    unit.hints = new HashSet<>(defaultHints);
                }
                unit.grid = grid;

                grid.units.add(unit);

            }
        }

        grid.blankUnits = grid.units.stream().filter(t -> t.num == 0)
                .collect(Collectors.toList());

        int variableUnit = 81 - grid.blankUnits.size();
        if (variableUnit < 17) {
            throw new RuntimeException("sudoku variable unit less than 17!");
        }
        log.info("sudoku variable unit: {}", variableUnit);
    }

    void parseBoxs() {

        for (int row = 0; row < 3; row++) {
            int rowFinal = row;
            for (int column = 0; column < 3; column++) {
                int columnFinal = column;
                Box box = new Box();
                // box.row = row;
                // box.column = column;
                // box.pos = "" + row + column;
                box.name = "B" + ((row * 3) + (column + 1)) + "";

                // 得到属于这个宫的格子
                box.units = grid.units.stream()
                        .filter(t -> ((t.row >= rowFinal * 3
                                && t.row < (rowFinal + 1) * 3)
                                && (t.column >= columnFinal * 3
                                        && t.column < (columnFinal + 1) * 3)))
                        .collect(Collectors.toList());
                box.units.forEach(t -> t.box = box);

                handleGridGroup(box);
                log.info("add box {}", box);
                grid.boxes.add(box);
            }
        }

    }

    void parseRows() {
        for (int row = 0; row < 9; row++) {
            Queue rowQueue = new Queue();
            rowQueue.pos = row;
            rowQueue.name = getRowName(row);
            final int rowFinal = row;
            rowQueue.units = grid.units.stream().filter(t -> t.row == rowFinal)
                    .collect(Collectors.toList());

            handleGridGroup(rowQueue);

            rowQueue.units.forEach(t -> t.rowQueue = rowQueue);
            grid.rowQueues.add(rowQueue);
            log.info("add row {}", rowQueue);
        }
    }

    void parseLines() {
        for (int column = 0; column < 9; column++) {
            Queue columnQueue = new Queue();
            columnQueue.pos = column;
            columnQueue.name = getColumnName(column);
            final int columnFinal = column;
            columnQueue.units = grid.units.stream()
                    .filter(t -> t.column == columnFinal)
                    .collect(Collectors.toList());

            handleGridGroup(columnQueue);
            columnQueue.units.forEach(t -> t.columnQueue = columnQueue);
            grid.columnQueues.add(columnQueue);
            log.info("add column {}", columnQueue.toString());
        }
    }

    Set<Integer> getFilledNums(Region gridGroup) {
        return gridGroup.units.stream().filter(t -> t.num > 0).map(t -> t.num)
                .collect(Collectors.toSet());
    }

    /**
     * 设置格子组缺少的数
     * 删除每个格子多余的候选数
     * @param region
     */
    void handleGridGroup(Region region) {

        Set<Integer> filledNums = getFilledNums(region);
        region.missingNums = getMissingNums(filledNums);
        // 从每个格子的候选数里移除已经填好的数
        region.units.stream().filter(t -> t.num == 0).forEach(t -> {
            t.hints.removeAll(filledNums);
        });
    }

    Set<Integer> getFullHints() {
        return new HashSet<>(defaultHints);
    }

    Set<Integer> getMissingNums(Set<Integer> filledNums) {
        Set<Integer> missingNum = getFullHints();
        missingNum.removeAll(filledNums);
        return missingNum;
    }

    /**
     * 获取一列的数字
     * @param grid
     * @param columnId
     * @return
     */
    Set<Integer> getNumOnColumn(int[][] grid, int columnId) {
        Set<Integer> nums = new HashSet<>();
        for (int i = 0; i < grid.length; i++) {
            int num = grid[i][columnId];
            if (num != 0) {
                nums.add(num);
            }
        }
        return nums;
    }

    static void singleInRegin(Region region, String regin) {

        for (int i = 1; i <= 9; i++) {
            int iFinal = i;
            List<Unit> iUnits = region.units.stream()
                    .filter(u -> u.hints.contains(iFinal))
                    .collect(Collectors.toList());
            if (iUnits.size() == 1) {
                iUnits.get(0).fillNum(i, "single-in-" + (regin != null ? regin
                        : region.getClass().getSimpleName()));
            }
        }

    }

    /**
     * 基本排除法
     */
    void basicExclude() {

        grid.boxes.forEach(t -> singleInRegin(t, "Box"));
        grid.rowQueues.forEach(t -> singleInRegin(t, "Row"));
        grid.columnQueues.forEach(t -> singleInRegin(t, "Column"));

        // 格子唯一
        new ArrayList<>(grid.blankUnits).forEach(t -> {
            if (t.hints.size() == 1) {
                t.fillNum(t.hints.iterator().next(), "GridSingleHint");
            }
        });

        // 行唯一
        crackReginSingle(grid.rowQueues, "row single");

        // 列唯一
        crackReginSingle(grid.columnQueues, "column single");

        // 宫唯一
        crackReginSingle(grid.boxes, "box single");
    }

    /**
     * 
     * @param gridGroups
     * @param method
     */
    void crackReginSingle(List<? extends Region> gridGroups, String method) {
        gridGroups.forEach(t -> {
            if (t.missingNums.size() == 1) {
                int num = t.missingNums.iterator().next();
                List<Unit> grids = t.units.stream().filter(g -> g.num == 0)
                        .collect(Collectors.toList());
                if (grids.size() > 1) {
                    throw new RuntimeException("missing num is only one " + num
                            + " but blank grid is more than one! " + grids);
                }
                grids.get(0).fillNum(num, method);
            }
        });
    }

    String getName(int row, int column) {
        return getRowName(row) + getColumnName(column);
    }

    String getRowName(int row) {
        return "R" + (row + 1);
    }

    String getColumnName(int column) {
        return "C" + (column + 1);
    }

    // if (grid.num == 0) {
    // blankGrids.add(grid);
    // Set<Integer> crossHints = getHintsByCross(matrix, row,
    // column);
    // Set<Integer> gridHints = new HashSet<Integer>(
    // box.missingNums);
    // gridHints.retainAll(crossHints);
    // grid.hints = gridHints;
    // if (gridHints.size() == 1) {
    // int value = gridHints.iterator().next();
    // log.info("you can put " + value + " to " + grid.pos);
    // }
    // }

    String getBoxPos(int x, int y) {
        return "" + (x / 3) + (y / 3);
    }

    Set<Integer> defaultHints = new HashSet<>();
    {
        for (int i = 1; i <= 9; i++) {
            defaultHints.add(i);
        }
    }

    /**
     *  遍历数字，将每个数字在每个宫可能出现的位置给列出来，如果确定在宫内只出现一次，则说明摒除得到结果
     */
    void hiddenOne() {
        for (int num = 1; num <= 9; num++) {
            Set<Integer> rowSets = new HashSet<>();
            Set<Integer> columnSets = new HashSet<>();
            int numFinal = num;
            grid.units.stream().filter(t -> t.num == numFinal).forEach(t -> {
                rowSets.add(t.row);
                columnSets.add(t.column);
            });

            // 获取并遍历没有这个数的宫
            for (Box box : grid.boxes.stream()
                    .filter(t -> t.missingNums.contains(numFinal))
                    .collect(Collectors.toSet())) {

                // 获取可以填写这个数的格子
                Set<Unit> gridSet = getBlankGridInBoxByNum(box, num);

                // 从行里过滤出剩余可能存在的格子
                for (Integer row : rowSets) {
                    gridSet = gridSet.stream()
                            .filter(t -> !t.pos.startsWith("" + row))
                            .collect(Collectors.toSet());
                }

                // 从列里过滤出剩余可能存在的格子
                for (Integer column : columnSets) {
                    gridSet = gridSet.stream()
                            .filter(t -> !t.pos.endsWith("" + column))
                            .collect(Collectors.toSet());
                }

                if (gridSet.size() == 0) {
                    throw new RuntimeException(
                            "no grid for " + num + " in box " + box.name + "!");
                }

                // 找到唯一位置
                if (gridSet.size() == 1) {
                    Unit grid = gridSet.iterator().next();
                    grid.fillNum(num, "hiddenSingle");
                }
            }
        }
    }

    Set<Unit> getBlankGridInBoxByNum(Box box, int num) {

        return box.units.stream()
                .filter(t -> t.num == 0 && t.hints.contains(num))
                .collect(Collectors.toSet());
    }

}
