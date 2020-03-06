package com.component.benjamin.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MySudokuCrack {

    // 行row;
    int[] row;
    // 列column
    int[] column;

    int[] smallSquare;

    

    // todo 首先根据小宫格计算每个位置的候选数
    // 根据行和列排除已经有的，
    // 再根据小宫格每个位置重复的数字筛选出唯一候选数

    // 不行的话用最少的一个数去试

    Map<String, Grid> grids;
    
    public static void main(String[] args) {
        
        int[][] sudoku = {

                 { 1, 0, 0, 8, 3, 0, 0, 0, 2 },
                 { 5, 7, 0, 0, 0, 1, 0, 0, 0 },
                 { 0, 0, 0, 5, 0, 9, 0, 6, 4 },
                 { 7, 0, 4, 0, 0, 8, 5, 9, 0 },
                 { 0, 0, 3, 0, 1, 0, 4, 0, 0 },
                 { 0, 5, 1, 4, 0, 0, 3, 0, 6 },
                 { 3, 6, 0, 7, 0, 4, 0, 0, 0 },
                 { 0, 0, 0, 6, 0, 0, 0, 7, 9 },
                 { 8, 0, 0, 0, 5, 2, 0, 0, 3 },
                
//         { 0, 0, 5, 0, 2, 0, 0, 9, 0 },
//         { 3, 0, 0, 6, 0, 7, 0, 0, 0 },
//         { 0, 0, 1, 0, 4, 0, 7, 0, 8 },
//         { 0, 3, 0, 0, 0, 0, 0, 7, 0 },
//         { 2, 0, 8, 0, 0, 0, 3, 0, 4 },
//         { 0, 4, 0, 0, 0, 0, 0, 2, 0 },
//         { 8, 0, 7, 0, 6, 0, 1, 0, 0 },
//         { 0, 0, 0, 7, 0, 5, 0, 0, 9 },
//         { 0, 9, 0, 0, 1, 0, 5, 0, 0 },
        };
        new MySudokuCrack().convert2Grids(sudoku);
    }

    class Grid {
        // 坐标标识 使用下标 0-8 xy格式
        String pos;
        // 数字 0 表示未确定
        int num;
        // 候选数
        Set<Integer> candis;
        @Override
        public String toString() {
            return "Grid [pos=" + pos + ", num=" + num + ", candis=" + candis
                    + "]";
        }
        
        
    }

    /**
     * 每个数字出现的次数
     */
    Map<Integer, Integer> numAppearTimes;

    /**
     * 获取一行的数字
     * @param sudoku
     * @param rowId
     * @return
     */
    Set<Integer> getNumOnRow(int[][] sudoku, int rowId) {
        Set<Integer> nums = new HashSet<>();
        for (int i = 0; i < sudoku[rowId].length; i++) {
            int num = sudoku[rowId][i];
            if (num != 0) {
                nums.add(num);
            }
        }
        return nums;
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
     * 把原始数据转成对象的映射
     * @param sudoku
     * @return
     */
    Map<String, Grid> convert2Grids(int[][] sudoku) {
        
        Map<String, Set<Integer>> squareCandis = getSquareCandis(sudoku);

        Map<String, Grid> grids = new HashMap<String, Grid>();
        for (int x = 0; x < sudoku.length; x++) {
            for (int y = 0; y < sudoku[x].length; y++) {
                Grid grid = new Grid();
//                grid.pos = "" + ((char)(65 + i)) + (j + 1);
                grid.pos = "" + x + y;
                grid.num = sudoku[x][y];
                grids.put(grid.pos, grid);
                
                if (grid.num == 0) {
                Set<Integer> crossCandis = getCandisByCross(sudoku, x, y);
                System.out.println(crossCandis);
                Set<Integer> gridCandis = new HashSet<Integer>(squareCandis.get(getSmallSquareKey(x, y)));
                gridCandis.retainAll(crossCandis);
                grid.candis = gridCandis;
                    if (gridCandis.size() == 1) {
                        int value = gridCandis.toArray(new Integer[0])[0];
                        System.out.println("you can put " + value + " to " + (x + 1) + (y + 1));
                    }
                }

//                System.out.println(grid);
            }
        }
        return grids;
    }
    
    /**
     * 一条线路上的三个小方块关联排除，可以直接判定某个方格的值
     * 两个方块中第一第二行出现的数，才能出现在另一个方块的第三行 以此类推所有的
     */
    
    
    
    /**
             * 排除一个格子十字坐标上的数字来获取候选
     * @param sudoku
     * @param x
     * @param y
     * @return
     */
    Set<Integer> getCandisByCross(int[][] sudoku, int x, int y) {
        Set<Integer> candis = new HashSet<Integer>(defaultCandis);
        for(int i = 0; i < sudoku[x].length; i++) {
            candis.remove(sudoku[x][i]);
        }
        for(int i = 0; i < sudoku.length; i++) {
            candis.remove(sudoku[x][y]);
        }
        
        return candis;
    }
    
    String getSmallSquareKey(int x, int y) {
        return "" + (x / 3) + (y / 3);
    }

    /**
     * 
     * @param sudoku
     * @param x 第一个格子的纵坐标索引
     * @param y 第一个格子的横坐标索引
     * @return
     */
    int[][] cutSmallSquare(int[][] sudoku, int x, int y) {
        int[][] smallSquare = new int[3][];
        for (int i = 0; i < 3; i++) {
            smallSquare[i] = new int[3];
            for (int j = 0; j < 3; j++) {
                smallSquare[i][j] = sudoku[x][y];
            }
        }
        return smallSquare;
    }

    Map<String, Set<Integer>> smallSquareCandis;

    Map<String, Set<Integer>> getSquareCandis(int[][] square) {
        Map<String, Set<Integer>> smallSquareCandis = new HashMap<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int[][] smallSquare = cutSmallSquare(square, x * 3, y * 3);
                smallSquareCandis.put("" + (x + 1) + (y + 1),
                        getSmallCandis(smallSquare));
            }
        }
        return smallSquareCandis;
    }

    Set<Integer> defaultCandis = new HashSet<>();
    {
        for (int i = 1; i <= 9; i++) {
            defaultCandis.add(i);
        }
    }
    
    /**
     * 获取小方块的候选数
     * @param square
     * @return
     */
    Set<Integer> getSmallCandis(int[][] square) {
        Set<Integer> candis = new HashSet<Integer>(defaultCandis);
        
        for (int[] nums : square) {
            for (int num : nums) {
                if (num != 0) {
                    candis.remove(num);
                }
            }
        }
        return candis;
    }

}
