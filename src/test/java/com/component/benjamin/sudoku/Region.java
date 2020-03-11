package com.component.benjamin.sudoku;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 行、列、宫统称区
 * @author Benjamin Zheng
 *
 */
public class Region {

    Grid grid;
    List<Unit> units;
    Set<Integer> missingNums;

    public List<Unit> getBlankUnits() {
        return units.stream().filter(t -> t.num == 0)
                .collect(Collectors.toList());
    }
}
