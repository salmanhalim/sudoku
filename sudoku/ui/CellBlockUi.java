package sudoku.ui;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JPanel;


/**
 * Presents the contents of a CellUi block.
 * <BR><BR>
 *
 * @author Salman Halim
 * @version $Revision:$
 */
public class CellBlockUi
    extends JPanel
{
    private CellUi[][] m_cells = new CellUi[ BoardUi.CELL_BLOCK_SIZE ][ BoardUi.CELL_BLOCK_SIZE ];
    private BoardUi    m_parentBoard;

    public CellBlockUi(BoardUi parentBoard, int parentRow, int parentColumn) {
        int rowOffset    = parentRow * BoardUi.CELL_BLOCK_SIZE;
        int columnOffset = parentColumn * BoardUi.CELL_BLOCK_SIZE;

        m_parentBoard = parentBoard;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel[] row = new JPanel[BoardUi.CELL_BLOCK_SIZE];

        for (int i = 0; i < BoardUi.CELL_BLOCK_SIZE; i++) {
            row[i] = new JPanel();

            row[i].setLayout(new BoxLayout(row[i], BoxLayout.X_AXIS));

            for (int j = 0; j < BoardUi.CELL_BLOCK_SIZE; j++) {
                // Pass in the row and column so the cell can save it.
                m_cells[i][j] = new CellUi(this, rowOffset + i, columnOffset + j);

                row[i].add(m_cells[i][j]);
            }

            add(row[i]);
        }
    }

    public BoardUi getParentBoard()
    {
        return m_parentBoard;
    }

    public void setParentBoard( BoardUi parentBoard )
    {
        m_parentBoard = parentBoard;
    }

    public void setCells( CellUi[][] val )
    {
        m_cells = val;
    }

    public CellUi[][] getCells()
    {
        return m_cells;
    }

    public CellUi getCell( int row, int col )
    {
        return m_cells[ row ][ col ];
    }

    protected void paintBorder( Graphics g )
    {
        Dimension size = getSize();

        g.setColor( Theme.getInstance().getColor( Theme.Themable.CELL_BLOCK_BORDER ) );
        g.fillRect( 0, 0, size.width, size.height );
    }

    public Insets getInsets()
    {
        Insets insets = new Insets( 2, 2, 2, 2 );
        return insets;
    }

    public Dimension getPreferredSize()
    {
        Dimension size     = new Dimension();
        Dimension cellSize = getCell( 0, 0 ).getPreferredSize();

        size.width  = BoardUi.CELL_BLOCK_SIZE * cellSize.width;
        size.height = BoardUi.CELL_BLOCK_SIZE * cellSize.height;

        return size;
    }

    public void refresh( CellUi cell )
    {
        m_parentBoard.refresh();
    }

    /**
     * Goes over all the cells in the cell block, except for the one specified, and prohibits the specific value in the
     * specified cell.
     *
     * @param originalCell the cell whose values are to be prohibited for the other cells in the block
     */
    public void updateAllowances( CellUi originalCell )
    {
        int subscript = originalCell.getValue() - 1;

        for ( int row = 0; row < BoardUi.CELL_BLOCK_SIZE; row++ )
        {
            for ( int col = 0; col < BoardUi.CELL_BLOCK_SIZE; col++ )
            {
                CellUi cell = getCell( row, col );

                if ( !cell.equals( originalCell ) )
                {
                    cell.prohibit( subscript );
                }
            }
        }
    }

    public String getAsString()
    {
        StringBuffer result = new StringBuffer();

        for ( int row = 0; row < BoardUi.CELL_BLOCK_SIZE; row++ )
        {
            for ( int col = 0; col < BoardUi.CELL_BLOCK_SIZE; col++ )
            {
                final CellUi cell = getCell( row, col );

                result.append( cell.getValue() );
                result.append( " " );
                result.append( cell.getGuess() );
                result.append( " " );

                String prohibitions = cell.getUserProhibitions();

                if ( prohibitions.length() == 0 )
                {
                    prohibitions = "0";
                }

                result.append( prohibitions );
                result.append( " " );
                result.append( cell.getLock() );
                result.append( " " );
            }
        }

        return result.toString();
    }

    public void loadFromString(String str) {
        try (Scanner tokens = new Scanner(str)) {
            for (int row = 0; row < BoardUi.CELL_BLOCK_SIZE; row++) {
                for (int col = 0; col < BoardUi.CELL_BLOCK_SIZE; col++) {
                    CellUi cell = getCell(row, col);

                    cell.setValue(tokens.nextInt());
                    cell.setGuess(tokens.next());
                    cell.setUserProhibitions(tokens.next());
                    cell.setLock(Enum.valueOf(CellUi.Lock.class, tokens.next()));
                }
            }
        }
    }

    public String toString()
    {
        return "CellBlockUi={"
                + "m_cells=" + m_cells
                + '}';
    }
}
