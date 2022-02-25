package sudoku.ui;


import static sudoku.ui.UiOptions.BOARD_ENTRY_MODE;
import static sudoku.ui.UiOptions.BOARD_ENTRY_VALUES;
import static sudoku.ui.UiOptions.IS_KILLER;
import static sudoku.ui.UiOptions.SET_PUZZLE_SUMS_MODE;
import static sudoku.ui.UiOptions.SHOW_SELECTIVE_DOT;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

import sudoku.SuDoku;


/**
 * Presents the contents of a Sudoku board.
 * <BR><BR>
 *
 * @author Salman Halim
 * @version $Revision:$
 */
public class BoardUi
    extends JPanel
{
    public static int GRID_SIZE       = 9;
    public static int CELL_BLOCK_SIZE = 3;

    protected static final String SUM_SOLUTION_PREFIX = "  ";

    private CellBlockUi[][] m_cellBlocks =
            new CellBlockUi[ GRID_SIZE / CELL_BLOCK_SIZE ][ GRID_SIZE / CELL_BLOCK_SIZE ];

    private List<CellSum> m_cellSums         = new ArrayList<>();
    private Set<CellUi>   m_currentSumCells;

    protected CellSum m_selectedCellSum;

    protected SuDoku m_mainContainer;

    public BoardUi(SuDoku mainContainer)
    {
        m_mainContainer = mainContainer;

        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        JPanel[] row = new JPanel[ CELL_BLOCK_SIZE ];
        for ( int i = 0; i < CELL_BLOCK_SIZE; i++ )
        {
            row[ i ] = new JPanel();

            row[ i ].setLayout( new BoxLayout( row[ i ], BoxLayout.X_AXIS ) );

            for ( int j = 0; j < CELL_BLOCK_SIZE; j++ )
            {
                m_cellBlocks[ i ][ j ] = new CellBlockUi( this, i, j );

                row[ i ].add( m_cellBlocks[ i ][ j ] );
            }

            add( row[ i ] );
        }

        Color  c1 = new Color( 86, 86, 86 );
        Color  c2 = new Color( 192, 192, 192 );
        Color  c3 = new Color( 204, 204, 204 );
        Border b1 = new BevelBorder( EtchedBorder.RAISED, c3, c1 );
        Border b2 = new MatteBorder( 5, 5, 5, 5, c2 );
        Border b3 = new BevelBorder( EtchedBorder.LOWERED, c3, c1 );

        setBorder( new CompoundBorder( new CompoundBorder( b1, b2 ), b3 ) );

        // SALMAN: remove
        // m_cellSums.add(new CellSum(18, GRID_SIZE).add(getCell(0, 0)).add(getCell(0, 1)).add(getCell(1, 0)).add(getCell(1, 1)));
        // m_cellSums.add(new CellSum(17, GRID_SIZE).add(getCell(0, 2)).add(getCell(0, 3)));
        // m_cellSums.add(new CellSum(9,  GRID_SIZE).add(getCell(0, 4)).add(getCell(0, 5)));
        // m_cellSums.add(new CellSum(8,  GRID_SIZE).add(getCell(0, 6)).add(getCell(0, 7)));
        // m_cellSums.add(new CellSum(5,  GRID_SIZE).add(getCell(0, 8)).add(getCell(1, 8)));
        // m_cellSums.add(new CellSum(20, GRID_SIZE).add(getCell(1, 2)).add(getCell(1, 3)).add(getCell(2, 2)).add(getCell(2, 3)).add(getCell(3, 3)));
        // m_cellSums.add(new CellSum(7,  GRID_SIZE).add(getCell(1, 4)).add(getCell(1, 5)));
        // m_cellSums.add(new CellSum(13, GRID_SIZE).add(getCell(1, 6)).add(getCell(1, 7)));
        // m_cellSums.add(new CellSum(10, GRID_SIZE).add(getCell(2, 0)).add(getCell(2, 1)));
        // m_cellSums.add(new CellSum(31, GRID_SIZE).add(getCell(2, 4)).add(getCell(3, 4)).add(getCell(4, 4)).add(getCell(5, 4)).add(getCell(6, 4)));
        // m_cellSums.add(new CellSum(16, GRID_SIZE).add(getCell(2, 5)).add(getCell(2, 6)).add(getCell(3, 5)).add(getCell(3, 6)));
        // m_cellSums.add(new CellSum(14, GRID_SIZE).add(getCell(2, 7)).add(getCell(3, 7)));
        // m_cellSums.add(new CellSum(16, GRID_SIZE).add(getCell(2, 8)).add(getCell(3, 8)));
        // m_cellSums.add(new CellSum(15, GRID_SIZE).add(getCell(3, 0)).add(getCell(3, 1)).add(getCell(4, 0)));
        // m_cellSums.add(new CellSum(27, GRID_SIZE).add(getCell(3, 2)).add(getCell(4, 1)).add(getCell(4, 2)).add(getCell(4, 3)));
        // m_cellSums.add(new CellSum(17, GRID_SIZE).add(getCell(4, 5)).add(getCell(4, 6)).add(getCell(4, 7)).add(getCell(5, 6)));
        // m_cellSums.add(new CellSum(11, GRID_SIZE).add(getCell(4, 8)).add(getCell(5, 7)).add(getCell(5, 8)));
        // m_cellSums.add(new CellSum(3,  GRID_SIZE).add(getCell(5, 0)).add(getCell(6, 0)));
        // m_cellSums.add(new CellSum(16, GRID_SIZE).add(getCell(5, 1)).add(getCell(6, 1)));
        // m_cellSums.add(new CellSum(20, GRID_SIZE).add(getCell(5, 2)).add(getCell(5, 3)).add(getCell(6, 2)).add(getCell(6, 3)));
        // m_cellSums.add(new CellSum(34, GRID_SIZE).add(getCell(5, 5)).add(getCell(6, 5)).add(getCell(6, 6)).add(getCell(7, 5)).add(getCell(7, 6)));
        // m_cellSums.add(new CellSum(4,  GRID_SIZE).add(getCell(6, 7)).add(getCell(6, 8)));
        // m_cellSums.add(new CellSum(9,  GRID_SIZE).add(getCell(7, 0)).add(getCell(8, 0)));
        // m_cellSums.add(new CellSum(9,  GRID_SIZE).add(getCell(7, 1)).add(getCell(7, 2)));
        // m_cellSums.add(new CellSum(5,  GRID_SIZE).add(getCell(7, 3)).add(getCell(7, 4)));
        // m_cellSums.add(new CellSum(21, GRID_SIZE).add(getCell(7, 7)).add(getCell(7, 8)).add(getCell(8, 7)).add(getCell(8, 8)));
        // m_cellSums.add(new CellSum(9,  GRID_SIZE).add(getCell(8, 1)).add(getCell(8, 2)));
        // m_cellSums.add(new CellSum(10, GRID_SIZE).add(getCell(8, 3)).add(getCell(8, 4)));
        // m_cellSums.add(new CellSum(11, GRID_SIZE).add(getCell(8, 5)).add(getCell(8, 6)));
    }

    public Dimension getPreferredSize()
    {
        Dimension size      = new Dimension();
        Dimension blockSize = getCellBlock( 0, 0 ).getPreferredSize();

        size.width  = CELL_BLOCK_SIZE * blockSize.width;
        size.height = CELL_BLOCK_SIZE * blockSize.height;

        return size;
    }

    public void setCellBlocks( CellBlockUi[][] val )
    {
        m_cellBlocks = val;
    }

    public CellBlockUi[][] getCellBlocks()
    {
        return m_cellBlocks;
    }

    public SuDoku getMainContainer() {
        return m_mainContainer;
    }

    public void setMainContainer(SuDoku val) {
        m_mainContainer = val;
    }

    /**
     * Gets the specific cell block for this board.
     *
     * @param row the row on the board
     * @param col the column on the board
     *
     * @return the cell block
     */
    public CellBlockUi getCellBlock( int row, int col )
    {
        return m_cellBlocks[ row ][ col ];
    }

    /**
     * Gets the cell block for the absolute row and column (across the entire board) for the specific cell.
     *
     * @param row the row
     * @param col the column
     *
     * @return the cell block
     */
    protected CellBlockUi getCellBlockFromRowAndCol( int row, int col )
    {
        int blockRow = row / CELL_BLOCK_SIZE;
        int blockCol = col / CELL_BLOCK_SIZE;

        return getCellBlock( blockRow, blockCol );
    }

    public CellUi getCell( int row, int col )
    {
        return getCellBlockFromRowAndCol( row, col ).getCell( row % CELL_BLOCK_SIZE, col % CELL_BLOCK_SIZE );
    }

    public void updateCellBlock( CellUi cell, int row, int col )
    {
        getCellBlockFromRowAndCol( row, col ).updateAllowances( cell );
    }

    public void updateCellSum(CellUi originalCell) {
        CellSum cellSum = originalCell.getCellSum();

        if (cellSum == null) {
            return;
        }

        int subscript = originalCell.getValue() - 1;

        for (CellUi cell : cellSum.getCells()) {
            if (cell != originalCell) {
                cell.prohibit(subscript);
            }
        }
    }

    public void updateRow( CellUi originalCell, int row )
    {
        int subscript = originalCell.getValue() - 1;

        for ( int col = 0; col < GRID_SIZE; col++ )
        {
            CellUi cell = getCell( row, col );

            if ( cell != originalCell )
            {
                cell.prohibit( subscript );
            }
        }
    }

    public void updateCol( CellUi originalCell, int col )
    {
        int subscript = originalCell.getValue() - 1;

        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            CellUi cell = getCell( row, col );

            if ( cell != originalCell )
            {
                cell.prohibit( subscript );
            }
        }
    }

    public void refresh()
    {
        refresh( true );
    }

    public void refresh( boolean redraw )
    {
        // Go over all the cells, resetting them (allowing everything).  Then, for each cell that is not empty, work on
        // the entire row, column and block.
        //
        // Also reset the X-Wing; we'll recalculate it later.
        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            for ( int col = 0; col < GRID_SIZE; col++ )
            {
                getCell( row, col ).resetAllowances();
                getCell( row, col ).setXwing( false );
            }
        }

        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            for ( int col = 0; col < GRID_SIZE; col++ )
            {
                CellUi cell = getCell( row, col );

                if ( !cell.isEmpty() )
                {
                    updateCellBlock( cell, row, col );
                    updateRow( cell, row );
                    updateCol( cell, col );
                    updateCellSum(cell);
                }
            }
        }

        // Calculate X-Wing here
        if ( SHOW_SELECTIVE_DOT == CellUi.EMPTY_VALUE )
        {
            for ( int i = 1; i < GRID_SIZE + 1; i++ )
            {
                SHOW_SELECTIVE_DOT = i;

                determineXWing();
            }

            SHOW_SELECTIVE_DOT = CellUi.EMPTY_VALUE;
        }
        else
        {
            determineXWing();
        }

        if ( redraw )
        {
            getParent().repaint();
        }
    }

    /**
     * Highlights all X-Wings for the given number.
     */
    protected void determineXWing()
    {
        determineXWingForRows();
        determineXWingForCols();
    }

    protected void determineXWingForRows()
    {
        List<Integer> possibleRows = new ArrayList<>();

        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            int numberOfEntries = determineNumberOfEntriesForRow( row );

            // SALMAN:  Change this to a <= instead of a ==
            if ( numberOfEntries == 2 )
            {
                possibleRows.add( row );
            }
        }

        // Now, go over the list of possible rows, comparing the matching columns for each with subsequent ones.  We need only go to the last - 1 because the
        // very last row will have nothing to compare against.
        for ( int i = 0; i < possibleRows.size() - 1; i++ )
        {
            final int row1 = ( (Number) possibleRows.get( i ) ).intValue();
            List<Integer> cols = determineAllowedColumns( row1 );

            for ( int j = i + 1; j < possibleRows.size(); j++ )
            {
                final int row2 = ( (Number) possibleRows.get( j ) ).intValue();

                // SALMAN:  Change this to check that the columns that are present are the same and not that ALL columns are indeed present and the same
                if ( cols.equals( determineAllowedColumns( row2 ) ) )
                {
                    // Set the X-Wing flag for the rows and columns here and get out
                    getCell( row1, ( (Number) cols.get( 0 ) ).intValue() ).setXwing( true );
                    getCell( row1, ( (Number) cols.get( 1 ) ).intValue() ).setXwing( true );
                    getCell( row2, ( (Number) cols.get( 0 ) ).intValue() ).setXwing( true );
                    getCell( row2, ( (Number) cols.get( 1 ) ).intValue() ).setXwing( true );
                }
            }
        }
    }

    protected void determineXWingForCols()
    {
        List<Integer> possibleCols = new ArrayList<>();

        for ( int col = 0; col < GRID_SIZE; col++ )
        {
            int numberOfEntries = determineNumberOfEntriesForCol( col );

            if ( numberOfEntries == 2 )
            {
                possibleCols.add( col );
            }
        }

        // Now, go over the list of possible cols, comparing the matching rows for each with subsequent ones.  We need only go to the last - 1 because the
        // very last col will have nothing to compare against.
        for ( int i = 0; i < possibleCols.size() - 1; i++ )
        {
            final int col1 = ( (Number) possibleCols.get( i ) ).intValue();
            List<Integer> rows = determineAllowedRows( col1 );

            for ( int j = i + 1; j < possibleCols.size(); j++ )
            {
                final int col2 = ( (Number) possibleCols.get( j ) ).intValue();

                if ( rows.equals( determineAllowedRows( col2 ) ) )
                {
                    // Set the X-Wing flag for the cols and rows here and get out
                    getCell( ( (Number) rows.get( 0 ) ).intValue(), col1 ).setXwing( true );
                    getCell( ( (Number) rows.get( 1 ) ).intValue(), col1 ).setXwing( true );
                    getCell( ( (Number) rows.get( 0 ) ).intValue(), col2 ).setXwing( true );
                    getCell( ( (Number) rows.get( 1 ) ).intValue(), col2 ).setXwing( true );
                }
            }
        }
    }

    protected int determineNumberOfEntriesForRow( int row )
    {
        if ( SHOW_SELECTIVE_DOT == CellUi.EMPTY_VALUE )
        {
            return 0;
        }

        int count = 0;

        for ( int col = 0; col < GRID_SIZE; col++ )
        {
            CellUi cell = getCell( row, col );

            if ( cell.getLock() == CellUi.Lock.NONE && cell.isAllowed( SHOW_SELECTIVE_DOT ) )
            {
                count++;
            }
        }

        return count;
    }

    protected int determineNumberOfEntriesForCol( int col )
    {
        if ( SHOW_SELECTIVE_DOT == CellUi.EMPTY_VALUE )
        {
            return 0;
        }

        int count = 0;

        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            CellUi cell = getCell( row, col );

            if ( cell.getLock() == CellUi.Lock.NONE && cell.isAllowed( SHOW_SELECTIVE_DOT ) )
            {
                count++;
            }
        }

        return count;
    }

    protected List<Integer> determineAllowedColumns( int row )
    {
        List<Integer> result = new ArrayList<>( 2 );

        for ( int col = 0; col < GRID_SIZE; col++ )
        {
            CellUi cell = getCell( row, col );

            if ( cell.getLock() == CellUi.Lock.NONE && cell.isAllowed( SHOW_SELECTIVE_DOT ) )
            {
                result.add( col );
            }
        }

        return result;
    }

    protected List<Integer> determineAllowedRows( int col )
    {
        List<Integer> result = new ArrayList<>( 2 );

        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            CellUi cell = getCell( row, col );

            if ( cell.getLock() == CellUi.Lock.NONE && cell.isAllowed( SHOW_SELECTIVE_DOT ) )
            {
                result.add( row );
            }
        }

        return result;
    }

    /**
     * Clears values placed by the user in the board.
     *
     * @param userLocks if true, values with user locks are cleared -- otherwise, USER lock values aren't cleared (the
     * GIVEN ones are never cleared)
     */
    public void reset( boolean userLocks )
    {
        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            for ( int col = 0; col < GRID_SIZE; col++ )
            {
                CellUi      cell = getCell( row, col );
                CellUi.Lock lock = cell.getLock();

                if ( lock != CellUi.Lock.GIVEN )
                {
                    if ( userLocks || cell.getLock() != CellUi.Lock.USER )
                    {
                        cell.clear();
                        cell.setGuess("");
                        cell.resetUserAllowances();
                        cell.setLock( CellUi.Lock.NONE );
                    }
                }
            }
        }
        
        for (CellSum cellSum : m_cellSums) {
        	cellSum.clearSolutions();
        }
    }

    /**
     * Clears the board of ALL values.
     */
    public void clear()
    {
        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            for ( int col = 0; col < GRID_SIZE; col++ )
            {
                CellUi cell = getCell( row, col );

                cell.clear();
                cell.setGuess( "" );
                cell.resetUserAllowances();
                cell.setLock( CellUi.Lock.NONE );
            }
        }
    }

    /**
     * Converts all non-empty NONE values to USER lock.
     */
    public void lockValues()
    {
        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            for ( int col = 0; col < GRID_SIZE; col++ )
            {
                CellUi cell = getCell( row, col );

                if ( !cell.isEmpty() && cell.getLock() == CellUi.Lock.NONE )
                {
                    cell.setLock( CellUi.Lock.USER );
                }
            }
        }
    }

    public void quickImportPuzzle()
    {
        String values = JOptionPane.showInputDialog( this, "Enter values", BOARD_ENTRY_VALUES );

        if ( values != null )
        {
            BOARD_ENTRY_MODE = true;
            BOARD_ENTRY_VALUES.setLength( 0 );
            BOARD_ENTRY_VALUES.append( values );
        }
    }

    public void importPuzzle()
    {
        clear();

        for ( int i = 0; i < GRID_SIZE; i++ )
        {
            String rowValues = JOptionPane.showInputDialog( this, "Enter row " + ( i + 1 ) );

            if ( rowValues == null )
            {
                break;
            }

            for ( int c = 0; c < rowValues.length(); c++ )
            {
                int value = CellUi.EMPTY_VALUE;

                try
                {
                    value = Integer.parseInt( String.valueOf( rowValues.charAt( c ) ) );
                }
                catch ( NumberFormatException e )
                {
                    // Do nothing; the default value will suffice.
                }

                CellUi cell = getCell(i, c);

                cell.setValue(value);

                if (value != CellUi.EMPTY_VALUE) {
                    cell.setLock(CellUi.Lock.GIVEN);
                }
            }

            refresh();
        }
    }

    public void toggleAddPuzzleSumCell(CellUi cell) {
        if (m_currentSumCells == null) {
            m_currentSumCells = new HashSet<>();
        }

        if (m_currentSumCells.contains(cell)) {
            m_currentSumCells.remove(cell);
        } else {
            m_currentSumCells.add(cell);
        }

        m_mainContainer.setStatus("Number of cells selected: " + m_currentSumCells.size());
    }

    public boolean cellSelectedForInclusionInSum(CellUi cell) {
        return m_currentSumCells != null && m_currentSumCells.contains(cell);
    }

    public void closePuzzleBlock(int sum) {
        CellSum cellSum = new CellSum(sum, GRID_SIZE);

        for (CellUi cell : m_currentSumCells) {
            cellSum.add(cell);
        }

        int numCells = m_currentSumCells.size();

        m_cellSums.add(cellSum);
        m_currentSumCells = null;

        m_mainContainer.setStatus("Set " + sum + " as sum for " + numCells + " cell" + (numCells == 1 ? "" : "s"));
    }

    public void refreshCombinationContainer(CellSum cellSum) {
        m_mainContainer.clearCombinationContainer();

        if (cellSum != null) {
            for (CellSolution combination : cellSum.getSolutions()) {
                m_mainContainer.addCombinationToDisplay(combination, cellSum);
            }

            m_mainContainer.setStatus("Showing combinations for " + cellSum.getSum());
        }

        refresh();
    }

    public void toggleSelectedCell(CellUi cell) {
        if (IS_KILLER) {
            CellSum cellSum = cell.getCellSum();

            // If it really is the same cell sum, then we just clicked elsewhere in the same block; deselect it.
            if (cellSum == m_selectedCellSum) {
                m_selectedCellSum.setSelected(false);

                m_selectedCellSum = null;
            } else if (cellSum != m_selectedCellSum) {
                if (m_selectedCellSum != null) {
                    m_selectedCellSum.setSelected(false);
                }

                m_selectedCellSum = cellSum;

                cellSum.setSelected(true);
            }

            refreshCombinationContainer(m_selectedCellSum);
        }
    }

    // SALMAN: Have an option to clear existing sums.
    // SALMAN:
    // SALMAN: Add validation to ensure contiguous cells and to clean up duplicates.
    public void setKillerPuzzleSums () {
        SET_PUZZLE_SUMS_MODE = true;
        m_currentSumCells    = null;

        // SALMAN: Better indication for the user that they're in puzzle sum setting mode. (Different background colours.)
        System.out.println("-=-=-=-=-= SET_PUZZLE_SUMS_MODE: " + (SET_PUZZLE_SUMS_MODE) + " (" + new java.util.Date() + " " + (new Exception().getStackTrace()[0]) + ")"); // SALMAN: remove
    }

    /**
     * Converts all non-empty values to GIVEN lock.
     */
    public void markValuesAsGiven()
    {
        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            for ( int col = 0; col < GRID_SIZE; col++ )
            {
                CellUi cell = getCell( row, col );

                if ( !cell.isEmpty() )
                {
                    cell.setLock( CellUi.Lock.GIVEN );
                }
            }
        }
    }

    /**
     * Removes all guesses without touching the puzzle values.
     */
    public void clearGuesses()
    {
        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            for ( int col = 0; col < GRID_SIZE; col++ )
            {
                CellUi cell = getCell( row, col );

                cell.setGuess( CellUi.EMPTY_GUESS );
                cell.resetUserAllowances();
            }
        }
    }

    /**
     * Fills in all cells that can only contain one value (the others are all prohibited).
     */
    public void autoFill()
    {
        boolean changed = true;

        while ( changed )
        {
            changed = false;

outer:
            for ( int row = 0; row < GRID_SIZE; row++ )
            {
                for ( int col = 0; col < GRID_SIZE; col++ )
                {
                    CellUi cell = getCell( row, col );

                    // If a cell is empty and only one value is allowed, fill it in.
                    if ( cell.isEmpty() && cell.getProhibitionCount() == GRID_SIZE - 1 )
                    {
                        for ( int i = 1; i <= GRID_SIZE; i++ )
                        {
                            if ( cell.isAllowed( i ) )
                            {
                                cell.setValueAndUpdateCellSums( i );
                                changed = true;

                                break outer;
                            }
                        }
                    }
                }
            }

            refresh( false );
        }
    }

    public String getAsString()
    {
        StringBuffer result = new StringBuffer();

        for ( int row = 0; row < CELL_BLOCK_SIZE; row++ )
        {
            for ( int col = 0; col < CELL_BLOCK_SIZE; col++ )
            {
                result.append( getCellBlock( row, col ).getAsString() );
                result.append( "\n" );
            }
            result.append( "\n" );
        }

        for (CellSum cellSum : m_cellSums) {
            result.append(cellSum.getSum());
            result.append(" ");

            // Write out the row and column of each cell.
            for (CellUi cell : cellSum.getCells()) {
                result.append(cell.getBoardRow());
                result.append(" ");
                result.append(cell.getBoardColumn());
                result.append(" ");
            }

            // Write out individual solutions, including whether they're enabled.
            for (CellSolution combination : cellSum.getSolutions()) {
                result.append("\n");
                result.append(SUM_SOLUTION_PREFIX);

                for (Integer number : combination.getSolution()) {
                    result.append(number);
                }

                result.append(" ");
                result.append(combination.getEnabled());
            }

            result.append("\n");
        }

        return result.toString();
    }

    public String getSimpleString()
    {
        StringBuffer result = new StringBuffer();

        for ( int row = 0; row < GRID_SIZE; row++ )
        {
            for ( int col = 0; col < GRID_SIZE; col++ )
            {
                int value = getCell( row, col ).getValue();

                result.append( value == CellUi.EMPTY_VALUE ? "." : String.valueOf( value ) );
            }
            result.append( "\n" );
        }

        return result.toString();
    }

    public void loadFromReader( Reader reader )
    {
        try
        {
            BufferedReader in    = new BufferedReader( reader );
            String         aline = null;

            for ( int row = 0; row < CELL_BLOCK_SIZE; row++ )
            {
                for ( int col = 0; col < CELL_BLOCK_SIZE; col++ )
                {
                    aline = in.readLine();

                    while ( aline.trim().length() == 0 )
                    {
                        aline = in.readLine();
                    }

                    getCellBlock( row, col ).loadFromString( aline );
                }
            }

            m_cellSums.clear();

            // Load cell sums.
            aline = in.readLine();

            CellSum cellSum = null;

            while (aline != null) {
                if (aline.trim().length() != 0) {
                    try (Scanner tokens = new Scanner(aline)) {
                        // If it starts with the prefix for a solution and we are looking at a cell sum, add this as a solution to that.
                        if (aline.indexOf(SUM_SOLUTION_PREFIX) == 0 && cellSum != null) {
                            cellSum.addSolution(tokens.next(), tokens.next());
                        } else {
                            cellSum = new CellSum(tokens.nextInt(), GRID_SIZE);

                            while (tokens.hasNextInt()) {
                                cellSum.add(getCell(tokens.nextInt(), tokens.nextInt()));
                            }

                            m_cellSums.add(cellSum);
                        }
                    }
                }

                aline = in.readLine();
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
