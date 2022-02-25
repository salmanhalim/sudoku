package sudoku.ui;

import java.util.List;

public class CellSolution implements Comparable<CellSolution> {
    public enum Enabled {
        ENABLED,
        DISABLED_AUTOMATICALLY,
        DISABLED_BY_USER;
    }

    protected List<Integer> m_solution;
    protected Enabled       m_enabled;

    public CellSolution(List<Integer> solution) {
        this(solution, Enabled.ENABLED);
    }

    public CellSolution(List<Integer> solution, Enabled enabled) {
        m_solution = solution;
        m_enabled  = enabled;
    }

    public List<Integer> getSolution() {
        return m_solution;
    }

    public void setSolution(List<Integer> val) {
        m_solution = val;
    }

    public boolean isEnabled() {
        return m_enabled == Enabled.ENABLED;
    }

    public Enabled getEnabled() {
        return m_enabled;
    }

    public void setEnabled(Enabled val) {
        m_enabled = val;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof CellSolution) {
            return m_solution.equals(((CellSolution) other).m_solution);
        }

        return false;
    }

    @Override
    public int hashCode() {
    	return m_solution.hashCode();
    }

	@Override
    public int compareTo(CellSolution other) {
        if (other == null) {
            return 1;
        }

        // If the solution numbers aren't sorted, this will break.
        return m_solution.toString().compareTo(other.m_solution.toString());
    }
}
