package sudoku.ui;


import static sudoku.ui.Theme.Themable.ALLOWED_DOT;
import static sudoku.ui.Theme.Themable.ALLOWED_VALUES;
import static sudoku.ui.Theme.Themable.ALL_BLOCKED;
import static sudoku.ui.Theme.Themable.ALL_BLOCKED_CELL_BACKGROUND;
import static sudoku.ui.Theme.Themable.ALL_BUT_ONE;
import static sudoku.ui.Theme.Themable.ALL_BUT_ONE_CELL_BACKGROUND;
import static sudoku.ui.Theme.Themable.CELL_BACKGROUND;
import static sudoku.ui.Theme.Themable.CELL_BACKGROUND_PUZZLE_SUMS_CELL_SELECTED;
import static sudoku.ui.Theme.Themable.CELL_BACKGROUND_PUZZLE_SUMS_CELL_SELECTED_HOVER;
import static sudoku.ui.Theme.Themable.CELL_BACKGROUND_PUZZLE_SUMS_MODE;
import static sudoku.ui.Theme.Themable.CELL_BORDER;
import static sudoku.ui.Theme.Themable.CELL_BORDER_SELECTED_CELL_SUM;
import static sudoku.ui.Theme.Themable.DEFAULT_BACKGROUND;
import static sudoku.ui.Theme.Themable.EMPTY_CELL_BACKGROUND;
import static sudoku.ui.Theme.Themable.GIVEN_VALUE;
import static sudoku.ui.Theme.Themable.GUESS;
import static sudoku.ui.Theme.Themable.PROHIBITED_DOT;
import static sudoku.ui.Theme.Themable.SELECTED_CELL_SUM_CELL_BACKGROUND;
import static sudoku.ui.Theme.Themable.SELECTED_DOT_BACKGROUND;
import static sudoku.ui.Theme.Themable.USER_LOCK_VALUE;
import static sudoku.ui.Theme.Themable.USER_PROHIBITIONS;
import static sudoku.ui.Theme.Themable.VALUE;
import static sudoku.ui.Theme.Themable.X_WING_CELL_BACKGROUND;
import static sudoku.ui.UiOptions.BOARD_ENTRY_MODE;
import static sudoku.ui.UiOptions.BOARD_ENTRY_VALUES;
import static sudoku.ui.UiOptions.CELL_SIZE;
import static sudoku.ui.UiOptions.IS_KILLER;
import static sudoku.ui.UiOptions.SET_PUZZLE_SUMS_MODE;
import static sudoku.ui.UiOptions.SHOW_ALLOWED_DOTS;
import static sudoku.ui.UiOptions.SHOW_ALLOWED_VALUES;
import static sudoku.ui.UiOptions.SHOW_DOTS;
import static sudoku.ui.UiOptions.SHOW_DOTS_ON_HOVER;
import static sudoku.ui.UiOptions.SHOW_GUESSES;
import static sudoku.ui.UiOptions.SHOW_PROHIBITED_DOTS;
import static sudoku.ui.UiOptions.SHOW_SELECTIVE_DOT;
import static sudoku.ui.UiOptions.SHOW_USER_PROHIBITIONS;
import static sudoku.ui.UiOptions.SHOW_X_WING;
import static sudoku.ui.UiOptions.getMessageAscent;
import static sudoku.ui.UiOptions.getMessageSize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 * Presents the contents of a cell.
 * <BR><BR>
 *
 * @author Salman Halim
 * @version $Revision:$
 */
public class CellUi
    extends JPanel
    implements MouseListener,
               KeyListener
{
    /**
     * The small font used to display extra information on the cell.
     */
    public static final Font FONT_12 = new Font( "SansSerif", Font.PLAIN, 12 );

    /**
     * The font size to use to display a single "dot" (if only one is being shown).
     */
    public static final Font SELECTIVE_DOT_FONT = new Font( "SansSerif", Font.PLAIN, 20 );

    /**
     * Font for a regular cell value.
     */
    public static final Font FONT_24 = new Font( "Serif", Font.BOLD, 24 );

    /**
     * Font for a GIVEN cell value.
     */
    public static final Font FONT_26 = new Font( "Serif", Font.BOLD, 26 );


    public static int          EMPTY_VALUE = 0;
    public static final String EMPTY_GUESS = String.valueOf( EMPTY_VALUE );

    public enum CellEntry
    {
        ALLOWED,
        PROHIBITED
    };

    public enum Lock
    {
        NONE,
        USER,
        GIVEN
    }

    private int         m_value            = EMPTY_VALUE;
    private CellEntry[] m_prohibitions     = new CellEntry[ BoardUi.GRID_SIZE ];
    private CellEntry[] m_userProhibitions = new CellEntry[ BoardUi.GRID_SIZE ];
    private CellBlockUi m_parentCellBlock;
    private Lock        m_lock             = Lock.NONE;
    private String      m_guess            = EMPTY_GUESS;
    private Theme       m_theme;
    private boolean     m_xwing;

    protected int     m_sum;
    protected Color   m_sumColor;
    protected CellSum m_cellSum;
    protected int     m_boardRow;
    protected int     m_boardColumn;

    boolean m_mouseEntered = false;

    public CellUi(CellBlockUi parentCellBlock, int boardRow, int boardColumn) {
        m_parentCellBlock = parentCellBlock;
        m_boardRow        = boardRow;
        m_boardColumn     = boardColumn;

        resetAllowances();
        resetUserAllowances();

        addMouseListener(this);
        addKeyListener(this);
    }

    public void setValue( int val )
    {
        m_value = val;
    }

    public void setValueAndUpdateCellSums(int val) {
        setValue(val);

        if (m_cellSum != null) {
            m_cellSum.updateCellSumCombinations();

            m_parentCellBlock.getParentBoard().refreshCombinationContainer(m_cellSum);
        }
    }

    public int getValue()
    {
        return m_value;
    }

    public String getDisplayValue()
    {
        return getDisplayValue( m_value );
    }

    public String getDisplayValue( int value )
    {
        final String[] values = new String[]
        {
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
        };

        return values[ value ];
    }

    public void clear()
    {
        m_value = EMPTY_VALUE;
    }

    public void setParentCellBlock( CellBlockUi val )
    {
        m_parentCellBlock = val;
    }

    public CellBlockUi getParentCellBlock()
    {
        return m_parentCellBlock;
    }

    public void setLock( Lock val )
    {
        m_lock = val;
    }

    public Lock getLock()
    {
        return m_lock;
    }

    public void setGuess( String val )
    {
        if ( val == null || val.trim().length() == 0 )
        {
            val = EMPTY_GUESS;
        }

        m_guess = val;
    }

    public String getGuess()
    {
        return m_guess;
    }

    public void setXwing( boolean val )
    {
        m_xwing = val;
    }

    public boolean isXwing()
    {
        return m_xwing;
    }

    public void prohibit( int subscript )
    {
        m_prohibitions[ subscript ] = CellEntry.PROHIBITED;
    }

    public void allow( int subscript )
    {
        m_prohibitions[ subscript ] = CellEntry.ALLOWED;
    }

    public void setUserProhibitions( String prohibitions )
    {
        resetUserAllowances();

        for ( int i = 0; i < prohibitions.length(); i++ )
        {
            try
            {
                int subscript = Integer.parseInt( String.valueOf( prohibitions.charAt( i ) ) );

                m_userProhibitions[ subscript - 1 ] = CellEntry.PROHIBITED;
            }
            catch ( Exception e )
            {
                // Ignore it silently -- don't care about invalid values
            }
        }

        if ( getUserProhibitions().length() > 0 )
        {
            setToolTipText( "User prohibitions:  " + getUserProhibitions() );
        }
    }

    public String getUserProhibitions()
    {
        StringBuffer result = new StringBuffer();

        for ( int i = 0; i < m_userProhibitions.length; i++ )
        {
            if ( m_userProhibitions[ i ] == CellEntry.PROHIBITED )
            {
                result.append( getDisplayValue( i + 1 ) );
            }
        }

        return result.toString();
    }

    public boolean isAllowed( int value )
    {
        boolean allowed = m_userProhibitions[value - 1].equals(CellEntry.ALLOWED) && m_prohibitions[value - 1].equals(CellEntry.ALLOWED);

        if (allowed && IS_KILLER && m_cellSum != null) {
            allowed = m_cellSum.isAllowed(value);
        }

        return allowed;
    }

    public boolean isEmpty()
    {
        return m_value == EMPTY_VALUE;
    }

    public Dimension getPreferredSize()
    {
        return new Dimension( CELL_SIZE, CELL_SIZE );
    }

    protected void paintBorder( Graphics g )
    {
        Dimension size = getSize();

        if (m_cellSum != null && m_cellSum.isSelected()) {
            setColor( g, CELL_BORDER_SELECTED_CELL_SUM );
        } else {
            setColor( g, CELL_BORDER );
        }

        g.drawRect( 0, 0, size.width, size.height );
        setDefaultColor( g );
    }

    protected String getAllowedValues()
    {
        StringBuffer result = new StringBuffer();

        for ( int i = 1; i <= BoardUi.GRID_SIZE; i++ )
        {
            if ( isAllowed( i ) )
            {
                result.append( getDisplayValue( i ) );
            }
        }

        return result.toString();
    }

    protected int getDotSize( int cellWidth )
    {
        int result = cellWidth / 10;

        return ( result < 1 ) ? 1 : ( ( result > 8 ) ? 8 : result );
    }

    public void paint( Graphics g )
    {
        // Refresh the theme variable in case things have changed
        m_theme = Theme.getInstance();
        super.paint( g );
    }

    public void setColor( Graphics g, Theme.Themable name )
    {
        g.setColor( m_theme.getColor( name ) );
    }

    public void setColor(Graphics g, Color color) {
        g.setColor(color);
    }

    public void setDefaultColor( Graphics g )
    {
        g.setColor( m_theme.getDefaultColor() );
    }

    public void paintComponent( Graphics g )
    {
        Dimension size              = getSize();
        int       halfWidth         = size.width / 2;
        int       halfHeight        = size.height / 2;
        int       dotSize           = getDotSize( size.width );
        boolean   defaultBackground = true;

        if ( m_lock == Lock.NONE )
        {
            defaultBackground = false;

            if ( m_mouseEntered )
            {
                if (SET_PUZZLE_SUMS_MODE && m_parentCellBlock.getParentBoard().cellSelectedForInclusionInSum(this)) {
                    setColor( g, CELL_BACKGROUND_PUZZLE_SUMS_CELL_SELECTED_HOVER );
                } else {
                    setColor( g, CELL_BACKGROUND );
                }
            }
            else if ( getValue() != EMPTY_VALUE )
            {
                defaultBackground = true;
            } else if (SET_PUZZLE_SUMS_MODE) {
                if (m_parentCellBlock.getParentBoard().cellSelectedForInclusionInSum(this)) {
                    setColor(g, CELL_BACKGROUND_PUZZLE_SUMS_CELL_SELECTED);
                } else if (IS_KILLER && (m_sum != 0 && m_sumColor != null)) {
                    setColor(g, m_sumColor);
                } else {
                    setColor(g, CELL_BACKGROUND_PUZZLE_SUMS_MODE);
                }
            } else {
                int prohibitionCount = getProhibitionCount();

                if ( prohibitionCount == BoardUi.GRID_SIZE - 1 )
                {
                    setColor( g, ALL_BUT_ONE_CELL_BACKGROUND );
                }
                else if ( prohibitionCount == BoardUi.GRID_SIZE )
                {
                    setColor( g, ALL_BLOCKED_CELL_BACKGROUND );
                }
                else if ( m_xwing && SHOW_X_WING )
                {
                    setColor( g, X_WING_CELL_BACKGROUND );
                }
                else if (IS_KILLER && (m_sum != 0 && m_sumColor != null))
                {
                    setColor(g, m_sumColor);
                }
                else
                {
                    setColor( g, EMPTY_CELL_BACKGROUND );
                }
            }
        }
        else if ( SHOW_SELECTIVE_DOT == getValue() )
        {
            defaultBackground = false;

            setColor( g, SELECTED_DOT_BACKGROUND );
        }

        if ( defaultBackground )
        {
            setColor( g, DEFAULT_BACKGROUND );
        }

        g.fill3DRect( 0, 0, size.width, size.height, true );

        if (m_cellSum != null && m_cellSum.isSelected()) {
            setColor(g, SELECTED_CELL_SUM_CELL_BACKGROUND);
            g.fill3DRect( 0, 0, size.width, size.height, true );
        }

        setDefaultColor( g );

        if ( m_lock == Lock.NONE )
        {
            drawDots( g );

            if ( SHOW_GUESSES && ( m_guess != null && m_guess.length() > 0 && !m_guess.equals( "0" ) ) )
            {
                g.setFont( FONT_12 );
                setColor( g, GUESS );
                g.drawString( m_guess, dotSize + 1, halfHeight - 8 );
            }

            if ( SHOW_USER_PROHIBITIONS )
            {
                g.setFont( FONT_12 );
                setColor( g, USER_PROHIBITIONS );
                g.drawString( getUserProhibitions(), halfWidth, halfHeight - 8 );
            }

            // Show allowed values for this square.
            if ( SHOW_ALLOWED_VALUES )
            {
                g.setFont( FONT_12 );
                setColor( g, ALLOWED_VALUES );
                g.drawString( getAllowedValues(), dotSize + 1, halfHeight + 16 );
                setDefaultColor( g );
            }
        }

        if ( !isEmpty() )
        {
            if ( m_lock == Lock.GIVEN )
            {
                setColor( g, GIVEN_VALUE );
                g.setFont( FONT_26 );
            }
            else
            {
                if ( m_lock == Lock.USER )
                {
                    setColor( g, USER_LOCK_VALUE );
                }
                else
                {
                    setColor( g, VALUE );
                }

                g.setFont( FONT_24 );
            }

            g.drawString( getDisplayValue(), halfWidth - 6, halfHeight + 8 );
            setDefaultColor( g );
        }

        if (IS_KILLER && m_sum != 0 && m_cellSum.isTopLeftCell(this)) {
            g.setFont(FONT_12);
            setColor(g, GIVEN_VALUE);
            g.drawString("" + m_sum, 2, 12);
            setDefaultColor(g);
        }
    }


    /**
     * Gets the number of prohibited values for this cell.
     *
     * @return the number of prohibited values for this cell
     */
    public int getProhibitionCount()
    {
        int prohibitedCount = 0;

        for ( int i = 1; i <= BoardUi.GRID_SIZE; i++ )
        {
            if ( !isAllowed( i ) )
            {
                prohibitedCount++;
            }
        }

        return prohibitedCount;
    }

    protected void drawDots( Graphics g )
    {
        if ( SHOW_DOTS )
        {
            if ( m_mouseEntered || !SHOW_DOTS_ON_HOVER )
            {
                int prohibitionCount = getProhibitionCount();

                if ( SHOW_SELECTIVE_DOT == EMPTY_VALUE )
                {
                    for ( int i = 1; i <= BoardUi.GRID_SIZE; i++ )
                    {
                        drawDot( g, i, isAllowed( i ), prohibitionCount, true );
                    }
                }
                else
                {
                    drawDot( g, SHOW_SELECTIVE_DOT, isAllowed( SHOW_SELECTIVE_DOT ),
                             prohibitionCount );
                }
            }
        }
    }

    /**
     * Draws a dot in the specified location (1 through the total size of the grid).
     *
     * @param g the Graphics contxt
     * @param dotLocation the dot number to draw
     * @param allowed whether or not the current dot corresponds to an allowed cell -- if so, it will be displayed in a
     * different color if allowed dots are being displayed
     * @param prohibitedCount the number of total prohibitions for this cell; used to color different cells differently
     */
    protected void drawDot( Graphics g, int dotLocation, boolean allowed, int prohibitedCount )
    {
        drawDot( g, dotLocation, allowed, prohibitedCount, false );
    }

    /**
     * If drawingAllDots is true, then it will place a dot around the perimeter of the grid; otherwise, the dot will always go in the position allocated for dot
     * number 3 (bottom-left).
     *
     * @param drawingAllDots whether or not dots should occupy their positions around the grid
     */
    protected void drawDot( Graphics g, int dotLocation, boolean allowed, int prohibitedCount, boolean drawingAllDots )
    {
        if ( ( allowed && !SHOW_ALLOWED_DOTS )
             || ( !allowed && !SHOW_PROHIBITED_DOTS ) )
        {
            return;
        }

        int       x            = 0;
        int       y            = 0;
        Dimension size         = getSize();
        String    displayValue = getDisplayValue( dotLocation );
        // int       dotSize      = getDotSize( size.width );
        Dimension dotBoxSize   = getMessageSize( g, SHOW_SELECTIVE_DOT == EMPTY_VALUE ? FONT_12 : SELECTIVE_DOT_FONT, displayValue );
        int       dotBoxAscent = getMessageAscent( g, SHOW_SELECTIVE_DOT == EMPTY_VALUE ? FONT_12 : SELECTIVE_DOT_FONT, displayValue );
        int       xOffset      = dotBoxSize.width + 2;
        int       yOffset      = dotBoxSize.height + 2;

        if ( !drawingAllDots )
        {
            dotLocation = 3;
        }

        // Horizontal offsets
        switch ( dotLocation )
        {
            case 1:
            case 7:
            case 8:
                x = size.width - xOffset;
                break;
            case 2:
            case 6:
            case 9:
                x = ( size.width - dotBoxSize.width ) / 2;
                break;
            case 3:
            case 4:
            case 5:
                x = 2;
                break;
        }

        // Vertical Offsets
        switch ( dotLocation )
        {
            case 1:
            case 2:
            case 3:
                y = size.height - 2;
                break;
            case 4:
            case 8:
            case 9:
                y = ( size.height + dotBoxSize.height ) / 2 - ( dotBoxSize.height - dotBoxAscent );
                break;
            case 5:
            case 6:
            case 7:
                y = yOffset - ( dotBoxSize.height - dotBoxAscent );
                break;
        }

        if ( prohibitedCount == BoardUi.GRID_SIZE - 1 && !allowed )
        {
            setColor( g, ALL_BUT_ONE );
        }
        else if ( prohibitedCount == BoardUi.GRID_SIZE )
        {
            setColor( g, ALL_BLOCKED );
        }
        else
        {
            setColor( g, allowed ? ALLOWED_DOT : PROHIBITED_DOT );
        }

        // g.fillRect( x, y, dotSize, dotSize );
        if ( SHOW_SELECTIVE_DOT == EMPTY_VALUE )
        {
            g.setFont( FONT_12 );
        }
        else
        {
            g.setFont( SELECTIVE_DOT_FONT );
        }
        g.drawString( displayValue, x, y );

        setDefaultColor( g );
    }

    public void resetAllowances()
    {
        for ( int i = 0; i < BoardUi.GRID_SIZE; i++ )
        {
            m_prohibitions[ i ] = CellEntry.ALLOWED;
        }
    }

    public void resetUserAllowances()
    {
        for ( int i = 0; i < BoardUi.GRID_SIZE; i++ )
        {
            m_userProhibitions[ i ] = CellEntry.ALLOWED;
        }

        setToolTipText( null );
    }

    protected void setNextLock()
    {
        switch ( m_lock )
        {
            case NONE:
                m_lock = Lock.USER;
                break;
            case USER:
                m_lock = Lock.GIVEN;
                break;
            case GIVEN:
                m_lock = Lock.NONE;
                break;
        }
    }

    protected String promptUserForGuess( String guess )
    {
        String result = JOptionPane.showInputDialog( this, "Enter guess", guess );

        if ( result == null )
        {
            result = guess;
        }

        return result;
    }

    protected String promptUserForProhibitions( String prohibitions )
    {
        String result = JOptionPane.showInputDialog( this, "Enter prohibitions", prohibitions );

        if ( result == null )
        {
            result = prohibitions;
        }

        return result;
    }

    protected void setAllowedEntriesOnly() {
        StringBuilder alloweds     = new StringBuilder();

        for (int i = 0; i < BoardUi.GRID_SIZE; i++) {
            if (m_userProhibitions[i] != CellEntry.PROHIBITED) {
                alloweds.append(getDisplayValue(i + 1));
            }
        }

        String result = JOptionPane.showInputDialog( this, "Enter allowed values (all others will be prohibited)", alloweds );

        if (result != null) {
            StringBuilder notAllowed = new StringBuilder();

            for (int i = 0; i < BoardUi.GRID_SIZE; i++) {
                try {
                    String entry = getDisplayValue(i + 1);

                    if (result.indexOf(entry) < 0) {
                        notAllowed.append(entry);
                    }
                } catch (Exception e) {
                    // Quietly ignore invalid values.
                }
            }

            setUserProhibitions(notAllowed.toString());
        }
    }

    public void mouseClicked( MouseEvent e )
    {
        int newValue = m_value;

        switch ( e.getButton() )
        {
            case MouseEvent.BUTTON1:
                if ( ( e.getModifiersEx() & InputEvent.ALT_DOWN_MASK ) == InputEvent.ALT_DOWN_MASK )
                {
                    // SALMAN:  Enable this AFTER we have figured out how to enable the checkbox, too.
                    // Enable the display of guesses here
                    // SHOW_GUESSES = true;

                    // Update the guess here; they had Alt down
                    setGuess( promptUserForGuess( m_guess ) );
                }
                else if ( ( e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK ) == InputEvent.CTRL_DOWN_MASK )
                {
                    // // Enable the display of user prohibitions
                    // SHOW_USER_PROHIBITIONS = true;

                    // If shift was also down or there is no current dot value, THEN prompt the user; otherwise, automatically toggle the inclusion of the current
                    // dot value in the prohibitions directly.
                    if (SET_PUZZLE_SUMS_MODE) {
                        m_parentCellBlock.getParentBoard().toggleAddPuzzleSumCell(this);

                        String sum = JOptionPane.showInputDialog(this, "Enter sum for block");

                        if (sum != null) {
                            m_parentCellBlock.getParentBoard().closePuzzleBlock(Integer.parseInt(sum));
                        }
                    } else if ( e.isShiftDown() || SHOW_SELECTIVE_DOT == EMPTY_VALUE )
                    {
                        setUserProhibitions( promptUserForProhibitions( getUserProhibitions() ) );
                    }
                    else
                    {
                        setUserProhibitions( getUserProhibitions() + SHOW_SELECTIVE_DOT );
                    }
                }
                else
                {
                    // Let BUTTON1 (left-mouse click) be used to cycle through the valid values (including empty).
                    if ( m_lock != Lock.NONE )
                    {
                        break;
                    }

                    if (SET_PUZZLE_SUMS_MODE) {
                        m_parentCellBlock.getParentBoard().toggleAddPuzzleSumCell(this);
                    } else if (BOARD_ENTRY_MODE && BOARD_ENTRY_VALUES.length() > 0) {
                        newValue = Integer.parseInt(String.valueOf(BOARD_ENTRY_VALUES.charAt(0)));

                        BOARD_ENTRY_VALUES.deleteCharAt(0);
                    } else if (IS_KILLER) {
                        // Clicking on a cell in killer mode doesn't go to the next value unless the cell can only HAVE one value.
                        m_parentCellBlock.getParentBoard().toggleSelectedCell(this);

                        // If only one value is allowed, just set it.
                        boolean onlyOne      = false;
                        int     allowedValue = 0;

                        for (int i = 1; i <= BoardUi.GRID_SIZE; i++) {
                            if (isAllowed(i)) {
                                if (!onlyOne) {
                                    onlyOne = true;
                                    allowedValue = i;
                                } else {
                                    onlyOne = false;

                                    break;
                                }
                            }
                        }

                        if (onlyOne) {
                            newValue = allowedValue;
                        }
                    } else {
                        newValue = m_value + 1;

                        while (newValue <= BoardUi.GRID_SIZE && !isAllowed(newValue)) {
                            newValue++;
                        }

                        if (newValue > BoardUi.GRID_SIZE) {
                            newValue = EMPTY_VALUE;
                        }
                    }
                }
                break;
            case MouseEvent.BUTTON3:
                if ( ( e.getModifiersEx() & InputEvent.ALT_DOWN_MASK ) == InputEvent.ALT_DOWN_MASK )
                {
                    // Reset the value; they had Alt down
                    if ( m_lock != Lock.NONE )
                    {
                        break;
                    }

                    newValue = EMPTY_VALUE;
                } else if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
                    // Take in a set of values and set the prohibitions to everything but those.
                    setAllowedEntriesOnly();
                } else if (SET_PUZZLE_SUMS_MODE) {
                    String sum = JOptionPane.showInputDialog(this, "Enter sum for block");

                    if (sum != null) {
                        m_parentCellBlock.getParentBoard().closePuzzleBlock(Integer.parseInt(sum));
                    }
                } else {
                    // Let BUTTON3 (right-mouse click) be used to lock a value.
                    if ( isEmpty() )
                    {
                        break;
                    }

                    setNextLock();
                }

                break;
        }

        if ( newValue != m_value )
        {
            setValueAndUpdateCellSums( newValue );
        }

        m_parentCellBlock.refresh( this );
    }

    public void mousePressed( MouseEvent e )
    {
    }

    public void mouseReleased( MouseEvent e )
    {
    }

    public void mouseEntered( MouseEvent e )
    {
        if ( BOARD_ENTRY_MODE && BOARD_ENTRY_VALUES.length() > 0 )
        {
            setToolTipText( "Click to place a '" + BOARD_ENTRY_VALUES.charAt( 0 ) + "'." );
        // SALMAN: remove?
        // } else if (IS_KILLER) {
        //     // SALMAN: Get combinations and display them.
        //     // SALMAN: Finish this
        //     if (m_cellSum != null) {
        //         setToolTipText("Combinations : " + m_cellSum.getCombinations());
        //     }
        }

        requestFocusInWindow();
        m_mouseEntered = true;
        getParent().repaint();
    }

    public void mouseExited( MouseEvent e )
    {
        if ( BOARD_ENTRY_MODE )
        {
            setToolTipText( null );
        }

        m_mouseEntered = false;
        getParent().repaint();
    }

    public int getSum() {
        return m_sum;
    }

    public void setSum(int val) {
        m_sum = val;
    }

    public Color getSumColor() {
        return m_sumColor;
    }

    public void setSumColor(Color val) {
        m_sumColor = val;
    }

    public CellSum getCellSum() {
        return m_cellSum;
    }

    public void setCellSum(CellSum val) {
        m_cellSum = val;
    }

    public int getBoardRow() {
        return m_boardRow;
    }

    public void setBoardRow(int val) {
        m_boardRow = val;
    }

    public int getBoardColumn() {
        return m_boardColumn;
    }

    public void setBoardColumn(int val) {
        m_boardColumn = val;
    }

    public String toString() {
        return "CellUi={"
            + "m_value=" + m_value
            + ", m_prohibitions=" + m_prohibitions
            + ", m_userProhibitions=" + m_userProhibitions
            + ", m_parentCellBlock=" + m_parentCellBlock
            + ", m_lock=" + m_lock
            + ", m_guess=" + m_guess
            + ", m_theme=" + m_theme
            + ", m_xwing=" + m_xwing
            + ", m_sum=" + m_sum
            + ", m_sumColor=" + m_sumColor
            + ", m_cellSum=" + m_cellSum
            + ", m_boardRow=" + m_boardRow
            + ", m_boardColumn=" + m_boardColumn
            + ", " + super.toString()
            + "}";
    }

    public void keyPressed( KeyEvent e )
    {
    }

    public void keyTyped( KeyEvent e )
    {
    }

    public void keyReleased( KeyEvent event )
    {
        char key = event.getKeyChar();

        if ( key == KeyEvent.VK_ESCAPE )
        {
            BOARD_ENTRY_MODE     = false;
            SET_PUZZLE_SUMS_MODE = false;

            m_parentCellBlock.refresh(this);

            getParent().repaint();
        }
        else if ( m_lock == Lock.NONE )
        {
            // We will only consider input over cells that aren't locked.
            int modifiers = event.getModifiersEx();

            if ( modifiers == 0 || modifiers == KeyEvent.CTRL_DOWN_MASK )
            {
                try
                {
                    int value = Integer.parseInt( String.valueOf( event.getKeyChar() ) );

                    if ( value == EMPTY_VALUE || ( value <= BoardUi.GRID_SIZE && isAllowed( value ) ) )
                    {
                        setValueAndUpdateCellSums( value );

                        // If the user was holding the control key down while hitting the key, we'll lock the cell as well
                        if ( value != EMPTY_VALUE && event.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK )
                        {
                            m_lock = Lock.USER;
                        }

                        // Every time a cell value changes, it's easiest to recalculate the entire grid.
                        m_parentCellBlock.refresh( this );

                        getParent().repaint();
                    }
                }
                catch ( Exception e )
                {
                    // Do nothing here; we'll just ignore keys we don't like.
                }
            }
        }
    }
}
