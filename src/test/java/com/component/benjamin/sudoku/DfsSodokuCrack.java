package com.component.benjamin.sudoku;

public class DfsSodokuCrack {

    static int count = 0;// 记录该数独的解法个数

    public static void main(String[] args) {
        f(Example.matrix, 0, 0);
    }

    public static void f(int[][] map, int x, int y) {
        if (x > 8) {
            System.out.println("第" + ++count + "种解法:");
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++)
                    System.out.print(map[i][j] + " ");
                System.out.println();
            }
            return;
        }
        if (y > 8) {
            f(map, x + 1, 0);
            return;
        }
        // 0表示没有填数
        if (map[x][y] == 0) {
            for (int k = 1; k <= 9; k++) {
                map[x][y] = k;
                if (OK(map, x, y))
                    f(map, x, y + 1);
            }
            map[x][y] = 0;
        } else
            f(map, x, y + 1);

    }

    private static boolean OK(int[][] map, int x, int y) {
        // 判断列
        for (int i = 0; i < 9; i++)
            if (map[i][y] == map[x][y] && i != x)
                return false;
        // 判断行
        for (int i = 0; i < 9; i++)
            if (map[x][i] == map[x][y] && i != y)
                return false;

        // 判断九宫格，找到当前判断格子 所属的九宫格
        int leftx = x / 3 * 3;// lextx为目前所在的九宫格的起始x坐标，即九宫格左上角的x坐标
        int lefty = y / 3 * 3;// lexty为目前所在的九宫格的起始y坐标
        int endx = leftx + 2;// endx为目前所在的九宫格的末尾x坐标，即九宫格右下角的x坐标
        int endy = lefty + 2;// endx为目前所在的九宫格的末尾x坐标，即九宫格右下角的y坐标
        for (int i = leftx; i <= endx; i++)
            for (int j = lefty; j <= endy; j++) {
                if (i == x && j == y)
                    continue;
                if (map[i][j] == map[x][y])
                    return false;
            }
        // 通过检测
        return true;
    }

}
