package com.component.benjamin.sudoku;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

                { 0, 0, 5, 0, 2, 0, 0, 9, 0 }, { 3, 0, 0, 6, 0, 7, 0, 0, 0 },
                { 0, 0, 1, 0, 4, 0, 7, 0, 8 }, { 0, 3, 0, 0, 0, 0, 0, 7, 0 },
                { 2, 0, 8, 0, 0, 0, 3, 0, 4 }, { 0, 4, 0, 0, 0, 0, 0, 2, 0 },
                { 8, 0, 7, 0, 6, 0, 1, 0, 0 }, { 0, 0, 0, 7, 0, 5, 0, 0, 9 },
                { 0, 9, 0, 0, 1, 0, 5, 0, 0 }, };
        BenSudokuCrack mySudokuCrack = new BenSudokuCrack();
        mySudokuCrack.crack(matrix);
    }

    Sudoku sudoku;

    int[][] matrix;

    public void crack(int[][] matrix) {
        sudoku = parseSudoku(matrix);
        int blankGridCountBeforeCrack = sudoku.blankGrids.size();
        while (blankGridCountBeforeCrack > 0) {
            basicExclude();
            crackTheHiddenOne();
            if (sudoku.blankGrids.size() == blankGridCountBeforeCrack) {
                // 尝试查询有两个候选数的格子
                guess();
                throw new RuntimeException("no grid for cracked!");
            }
            blankGridCountBeforeCrack = sudoku.blankGrids.size();
        }
        log.info("crack finished!");

        print();
    }

    void guess() {
        // 获取一个已经填入的最多的数字
        Map<Integer, Long> hotNumbers = sudoku.grids.stream()
                .filter(t -> t.num > 0).map(t -> t.num)
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.counting()));

        Map<Integer, Long> sortMap = new LinkedHashMap<>();
        hotNumbers.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((a, b) -> (int) (b - a)))
                .forEachOrdered(e -> sortMap.put(e.getKey(), e.getValue()));
        int num = sortMap.keySet().iterator().next();
        log.info("hot num: {}", num);
        // TODO 尝试一个可以放7的位置，然后继续

    }

    void print() {
        sudoku.grids.forEach(t -> this.matrix[t.row][t.line] = t.num);
        for (int i = 0; i < 9; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
    }

    /**
     * 把原始数据转成对象的映射
     * @param matrix
     * @return
     */
    Sudoku parseSudoku(int[][] matrix) {

        this.matrix = matrix;
        sudoku = new Sudoku();

        parseGrids();

        // 解析所有的宫 设置每个空格子的候选数
        parsePalaces();

        // 解析所有的行和列 设置设置每个格子的候选数
        parseRows();
        parseLines();

        return sudoku;
    }

    void parseGrids() {

        // 先处理每个格子
        for (int row = 0; row < 9; row++) {
            for (int line = 0; line < 9; line++) {

                Grid grid = new Grid();
                grid.name = getName(row, line);
                grid.row = row;
                grid.line = line;
                grid.pos = "" + row + line;
                grid.num = matrix[row][line];
                if (grid.num == 0) {
                    grid.candis = new HashSet<>(defaultCandis);
                }
                grid.sudoku = sudoku;

                sudoku.grids.add(grid);

            }
        }

        sudoku.blankGrids = sudoku.grids.stream().filter(t -> t.num == 0)
                .collect(Collectors.toList());
    }

    void parsePalaces() {

        for (int row = 0; row < 3; row++) {
            int rowFinal = row;
            for (int line = 0; line < 3; line++) {
                int lineFinal = line;
                Palace palace = new Palace();
                // palace.row = row;
                // palace.line = line;
                // palace.pos = "" + row + line;
                palace.name = (row * 3) + (line + 1) + "";
                // palace.missingNums = new HashSet<>(defaultCandis);
                sudoku.palaces.add(palace);

                // 得到属于这个宫的格子
                palace.grids = sudoku.grids.stream()
                        .filter(t -> ((t.row >= rowFinal * 3
                                && t.row < (rowFinal + 1) * 3)
                                && (t.line >= lineFinal * 3
                                        && t.line < (lineFinal + 1) * 3)))
                        .collect(Collectors.toList());
                palace.grids.forEach(t -> t.palace = palace);

                handleGridGroup(palace);
                log.info("add palace {}", palace);
                sudoku.palaces.add(palace);
            }
        }

    }

    void parseRows() {
        for (int row = 0; row < 9; row++) {
            Array rowArray = new Array();
            rowArray.pos = row;
            rowArray.name = getRowName(row);
            final int rowFinal = row;
            rowArray.grids = sudoku.grids.stream()
                    .filter(t -> t.row == rowFinal)
                    .collect(Collectors.toList());

            handleGridGroup(rowArray);

            rowArray.grids.forEach(t -> t.rowArray = rowArray);
            sudoku.rowArrays.add(rowArray);
            log.info("add row {}", rowArray);
        }
    }

    void parseLines() {
        for (int line = 0; line < 9; line++) {
            Array lineArray = new Array();
            lineArray.pos = line;
            lineArray.name = getLineName(line);
            final int lineFinal = line;
            lineArray.grids = sudoku.grids.stream()
                    .filter(t -> t.line == lineFinal)
                    .collect(Collectors.toList());

            handleGridGroup(lineArray);
            lineArray.grids.forEach(t -> t.lineArray = lineArray);
            sudoku.lineArrays.add(lineArray);
            log.info("add line {}", lineArray.toString());
        }
    }

    Set<Integer> getFilledNums(GridGroup gridGroup) {
        return gridGroup.grids.stream().filter(t -> t.num > 0).map(t -> t.num)
                .collect(Collectors.toSet());
    }

    /**
     * 设置格子组缺少的数
     * 删除每个格子多余的候选数
     * @param gridGroup
     */
    void handleGridGroup(GridGroup gridGroup) {

        Set<Integer> filledNums = getFilledNums(gridGroup);
        gridGroup.missingNums = getMissingNums(filledNums);
        // 从每个格子的候选数里移除已经填好的数
        gridGroup.grids.stream().filter(t -> t.num == 0).forEach(t -> {
            t.candis.removeAll(filledNums);
        });
    }

    Set<Integer> getFullCandis() {
        return new HashSet<>(defaultCandis);
    }

    Set<Integer> getMissingNums(Set<Integer> filledNums) {
        Set<Integer> missingNum = getFullCandis();
        missingNum.removeAll(filledNums);
        return missingNum;
    }

    /**
     * 获取一列的数字
     * @param sudoku
     * @param columnId
     * @return
     */
    Set<Integer> getNumOnColumn(int[][] sudoku, int columnId) {
        Set<Integer> nums = new HashSet<>();
        for (int i = 0; i < sudoku.length; i++) {
            int num = sudoku[i][columnId];
            if (num != 0) {
                nums.add(num);
            }
        }
        return nums;
    }

    /**
     * 基本排除法
     */
    void basicExclude() {

        // 格子唯一
        sudoku.blankGrids.forEach(t -> {
            if (t.candis.size() == 1) {
                t.fillNum(t.candis.iterator().next(),
                        "grid only candidate num");
            }
        });

        // 行唯一
        crackGroupSingle(sudoku.rowArrays, "row single");

        // 列唯一
        crackGroupSingle(sudoku.lineArrays, "line single");

        // 宫唯一
        crackGroupSingle(sudoku.palaces, "palace single");
    }

    void crackGroupSingle(List<? extends GridGroup> gridGroups, String method) {
        gridGroups.forEach(t -> {
            if (t.missingNums.size() == 1) {
                int num = t.missingNums.iterator().next();
                List<Grid> grids = t.grids.stream().filter(g -> g.num == 0)
                        .collect(Collectors.toList());
                if (grids.size() > 1) {
                    throw new RuntimeException("missing num is only one " + num
                            + " but blank grid is more than one! " + grids);
                }
                grids.get(0).fillNum(num, method);
            }
        });
    }

    String getName(int row, int line) {
        return getRowName(row) + getLineName(line);
    }

    String getRowName(int row) {
        return ((char) (65 + row)) + "";
    }

    String getLineName(int line) {
        return line + 1 + "";
    }

    // if (grid.num == 0) {
    // blankGrids.add(grid);
    // Set<Integer> crossCandis = getCandisByCross(matrix, row,
    // line);
    // Set<Integer> gridCandis = new HashSet<Integer>(
    // palace.missingNums);
    // gridCandis.retainAll(crossCandis);
    // grid.candis = gridCandis;
    // if (gridCandis.size() == 1) {
    // int value = gridCandis.iterator().next();
    // log.info("you can put " + value + " to " + grid.pos);
    // }
    // }

    String getPalacePos(int x, int y) {
        return "" + (x / 3) + (y / 3);
    }

    Set<Integer> defaultCandis = new HashSet<>();
    {
        for (int i = 1; i <= 9; i++) {
            defaultCandis.add(i);
        }
    }

    // 遍历数字，将每个数字在每个宫可能出现的位置给列出来，如果确定在宫内只出现一次，则说明摒除得到结果
    void crackTheHiddenOne() {
        for (int num = 1; num <= 9; num++) {
            Set<Integer> rowSets = new HashSet<>();
            Set<Integer> lineSets = new HashSet<>();
            int numFinal = num;
            sudoku.grids.stream().filter(t -> t.num == numFinal).forEach(t -> {
                rowSets.add(t.row);
                lineSets.add(t.line);
            });

            // 获取并遍历没有这个数的宫
            for (Palace palace : sudoku.palaces.stream()
                    .filter(t -> t.missingNums.contains(numFinal))
                    .collect(Collectors.toSet())) {

                // 获取可以填写这个数的格子
                Set<Grid> gridSet = getBlankGridInPalaceByNum(palace, num);

                // 从行里过滤出剩余可能存在的格子
                for (Integer row : rowSets) {
                    gridSet = gridSet.stream()
                            .filter(t -> !t.pos.startsWith("" + row))
                            .collect(Collectors.toSet());
                }

                // 从列里过滤出剩余可能存在的格子
                for (Integer line : lineSets) {
                    gridSet = gridSet.stream()
                            .filter(t -> !t.pos.endsWith("" + line))
                            .collect(Collectors.toSet());
                }

                if (gridSet.size() == 0) {
                    throw new RuntimeException("no grid for " + num
                            + " in palace " + palace.name + "!");
                }

                // 找到唯一位置
                if (gridSet.size() == 1) {
                    Grid grid = gridSet.iterator().next();
                    grid.fillNum(num, "hiddenSingle");
                }
            }
        }
    }

    Set<Grid> getBlankGridInPalaceByNum(Palace palace, int num) {

        return palace.grids.stream()
                .filter(t -> t.num == 0 && t.candis.contains(num))
                .collect(Collectors.toSet());
    }

}
