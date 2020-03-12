package com.component.benjamin.sudoku;

import java.util.Arrays;

public class DfsSodokuCrack {

    static int count = 0;// 记录该数独的解法个数

    public static void main(String[] args) {
        find(Example.matrix, null, 0, 0);
        System.out.println("try count: " + tryCount);
    }

    public static int[][] initHints(int[][] map) {

        // 行已经出现的数字 用int的一位表示该值有没有出现 下同
        int[] rowNums = new int[9];
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (map[x][y] != 0) {
                    rowNums[x] = rowNums[x] | 1 << (map[x][y] - 1);
                }
            }
        }
        System.out.println(Arrays.toString(rowNums));

        // 列已经出现的数字
        int[] colNums = new int[9];
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (map[y][x] != 0) {
                    colNums[x] = colNums[x] | 1 << (map[y][x] - 1);
                }
            }
        }
        System.out.println(Arrays.toString(colNums));

        // 宫已经出现的数字
        int[] boxNums = new int[9];
        int boxIdx = 0;
        for (int bx = 0; bx < 3; bx++) {
            for (int by = 0; by < 3; by++) {
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        if (map[bx * 3 + x][by * 3 + y] != 0) {
                            boxNums[bx * 3 + by] = boxNums[bx * 3 + by]
                                    | 1 << (map[bx * 3 + x][by * 3 + y] - 1);
                        }
                    }
                }
            }
        }
        System.out.println(Arrays.toString(boxNums));

        int[][] hints = new int[9][];
        for (int x = 0; x < 9; x++) {
            hints[x] = new int[9];
            for (int y = 0; y < 9; y++) {
                if (map[x][y] == 0) {
                    int hint = rowNums[x] | colNums[y]
                            | boxNums[x / 3 * 3 + y / 3];
                    hints[x][y] = 511 ^ hint;
                }
            }
        }
        for (int x = 0; x < 9; x++) {
            System.out.println(Arrays.toString(hints[x]));
        }

        return hints;
    }

    public static void find(int[][] map, int[][] hints, int x, int y) {
        if (hints == null) {
            hints = initHints(map);
        }
        if (x > 8) {
            System.out.println("第" + ++count + "种解法:");
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++)
                    System.out.print(map[i][j] + " ");
                System.out.println();
            }
            return;
        }
        // 这一行已经填满，填写下一行
        if (y > 8) {
            find(map, hints, x + 1, 0);
            return;
        }
        // 0表示没有填数
        if (map[x][y] == 0) {
            for (int k = 1; k <= 9; k++) {
                if ((hints[x][y] >> (k - 1) & 1) == 1) {
                    map[x][y] = k;
                    if (checking(map, x, y))
                        find(map, hints, x, y + 1);
                }
            }
            map[x][y] = 0;
        } else
            find(map, hints, x, y + 1);

    }

    static int tryCount = 0;

    private static boolean checking(int[][] map, int x, int y) {
        ++tryCount;
        // 判断列
        for (int i = 0; i < 9; i++)
            if (map[i][y] == map[x][y] && i != x)
                return false;
        // 判断行
        for (int i = 0; i < 9; i++)
            if (map[x][i] == map[x][y] && i != y)
                return false;

        // 判断九宫格，找到当前判断格子 所属的九宫格
        int startX = x / 3 * 3;// lextx为目前所在的九宫格的起始x坐标，即九宫格左上角的x坐标
        int startY = y / 3 * 3;// lexty为目前所在的九宫格的起始y坐标
        int endX = startX + 3;// endx为目前所在的九宫格的末尾x坐标，即九宫格右下角的x坐标
        int endY = startY + 3;// endx为目前所在的九宫格的末尾x坐标，即九宫格右下角的y坐标
        for (int i = startX; i < endX; i++)
            for (int j = startY; j < endY; j++) {
                if (i == x && j == y)
                    continue;
                if (map[i][j] == map[x][y])
                    return false;
            }
        // 通过检测
        return true;
    }

}
