package sudoku.ui;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import sudoku.ui.CellSolution.Enabled;


public class CellSum {
    protected int          m_sum;
    protected List<CellUi> m_cells;
    protected Color        m_color;
    protected int          m_max;
    protected boolean m_selected;

    protected CellUi m_topLeftCell;

    private Set<CellSolution>  m_solutions;
    private int                m_solutionSize;

    public CellSum(int sum, int max) {
        m_sum = sum;
        m_max = max;

        m_cells = new ArrayList<>();
        m_color = new Color(Theme.getRandomNumber(150, 255), Theme.getRandomNumber(150, 255), Theme.getRandomNumber(150, 255));
    }

    public CellSum add(CellUi cell) {
        m_cells.add(cell);

        cell.setCellSum(this);
        cell.setSum(m_sum);
        cell.setSumColor(m_color);

        if (m_topLeftCell == null) {
            m_topLeftCell = cell;
        } else if (cell.getBoardRow() < m_topLeftCell.getBoardRow()) {
            m_topLeftCell = cell;
        } else if (cell.getBoardRow() == m_topLeftCell.getBoardRow() && cell.getBoardColumn() < m_topLeftCell.getBoardColumn()) {
            m_topLeftCell = cell;
        }

        m_solutions = null;

        return this;
    }

    public boolean isTopLeftCell(CellUi cell) {
        return m_topLeftCell.equals(cell);
    }

    public boolean isAllowed(int value) {
        if (m_solutions == null) {
            getCombinations();
        }

        // Go over each cell and if it has a value, remove all combinations that DON'T contain that number.
        Set<Integer> currentlySetValues = new HashSet<>();

        for (CellUi cell : m_cells) {
            int cellValue = cell.getValue();

            if (cellValue != CellUi.EMPTY_VALUE) {
                currentlySetValues.add(Integer.valueOf(cellValue));
            }
        }

        Set<Integer> solutionSet = new HashSet<>();

        for (CellSolution cellSolution : m_solutions) {
            // Skip solutions that aren't enabled.
            if (!cellSolution.isEnabled()) {
                continue;
            }

        	List<Integer> solution = cellSolution.getSolution();
            // Only process this solution if the solution contains all the currently set values.
            boolean allFound = true;

            for (Integer cellValue : currentlySetValues) {
                if (!solution.contains(cellValue)) {
                    allFound = false;

                    break;
                }
            }

            if (allFound) {
                solutionSet.addAll(solution);
            }
        }

        return solutionSet.contains(Integer.valueOf(value));
    }

    /**
     * Goes through all combinations and disables any that don't contain ALL currently set cell values.
     */
    public void updateCellSumCombinations() {
        Set<Integer> currentlySetValues = new HashSet<>();

        for (CellUi cell : m_cells) {
            int cellValue = cell.getValue();

            if (cellValue != CellUi.EMPTY_VALUE) {
                currentlySetValues.add(Integer.valueOf(cellValue));
            }
        }

        for (CellSolution cellSolution : m_solutions) {
            // Skip solutions that were disabled explicitly by the user.
            if (cellSolution.getEnabled() == Enabled.DISABLED_BY_USER) {
                continue;
            }

        	List<Integer> solution = cellSolution.getSolution();
            // Only process this solution if the solution contains all the currently set values.
            boolean allFound = true;

            for (Integer cellValue : currentlySetValues) {
                if (!solution.contains(cellValue)) {
                    allFound = false;

                    break;
                }
            }

            cellSolution.setEnabled(allFound ? Enabled.ENABLED : Enabled.DISABLED_AUTOMATICALLY);
        }
    }

    /**
     * Returns all the combinations that, when added, give the sum.
     *
     *
     * @param max The maximum allowed (for a 3x3 Sudoku, it would be 9, for a 4x4, it would be 16)
     */
    public Set<CellSolution> getCombinations() {
    	if (m_solutions != null) {
            return m_solutions;
        }

        m_solutions    = new TreeSet<>();
        m_solutionSize = m_cells.size();

        // SALMAN: The recursive solution stops as soon as the first sequence is found; for example, for 20 with 5 cells, this finds 12359, but then stops so never
        // SALMAN: finds 12368.
        // SALMAN:
        // SALMAN: It would be better to create an array of m_solutionSize and go over each number until the sum gets too high.

        m_runningCounter = new int[m_solutionSize];

        permute(0, 1);

        return m_solutions;
    }

    private int[] m_runningCounter;

    protected void permute(int depth, int start) {
        int runningTotal = 0;

        for (int i = 0; i < depth; i++) {
            runningTotal += m_runningCounter[i];
        }

        for (int i = start; i <= m_max; i++) {
            int newSum = runningTotal + i;

            if (newSum == m_sum) {
                // SALMAN: Save if at max depth. Abort if not.
                if (depth == m_solutionSize - 1) {
                    m_runningCounter[depth] = i;

                    // SALMAN: Save.
                    List<Integer> solution = new ArrayList<>();

                    for (int entry : m_runningCounter) {
                        solution.add(Integer.valueOf(entry));
                    }

                    Collections.sort(solution);

                    m_solutions.add(new CellSolution(solution));
                }

                m_runningCounter[depth] = 0;

                break;
            } else if (newSum < m_sum) {
                m_runningCounter[depth] = i;

                // SALMAN: Got the total needed? Save the solution if at maximum depth. Break out of this iteration after setting the running counter back to 0.
                // SALMAN: Not got the total, but not at maximum depth? Go in one more.

                if (depth < m_solutionSize - 1) {
                    permute(depth + 1, i + 1);
//                 } else {
//                     // Got all the way to the end, but the total wasn't reached. Get out.
//                     m_runningCounter[depth] = 0;
//
//                     break;
                }
            } else {
                // The sum is too high; get out.
                m_runningCounter[depth] = 0;

                break;
            }
        }
    }

    // SALMAN: Product a working algorithm for solution generation that doesn't abort after one solution.
    // SALMAN: Write the algorithm first.

    public int getSum() {
        return m_sum;
    }

    public void setSum(int val) {
        m_sum = val;
    }

    public List<CellUi> getCells() {
        return m_cells;
    }

    public void setCells(List<CellUi> val) {
        m_cells = val;
    }

    public Color getColor() {
        return m_color;
    }

    public void setColor(Color val) {
        m_color = val;
    }

    public int getMax() {
        return m_max;
    }

    public void setMax(int val) {
        m_max = val;
    }

    public boolean isSelected() {
        return m_selected;
    }

    public void setSelected(boolean val) {
        m_selected = val;
    }

    public void addSolution(String numbers, String enabled) {
        if (m_solutions == null) {
            m_solutions = new TreeSet<>();
        }

        List<Integer> combination = new ArrayList<>();

        for (char c : numbers.toCharArray()) {
            combination.add(Integer.valueOf(String.valueOf(c)));
        }

        m_solutions.add(new CellSolution(combination, Enum.valueOf(Enabled.class, enabled)));
    }

    public Set<CellSolution> getSolutions() {
        return m_solutions;
    }

    public void clearSolutions() {
        m_solutions = null;
    }
}
