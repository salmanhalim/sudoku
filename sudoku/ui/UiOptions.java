package sudoku.ui;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;


/**
 * Hold option values.
 * <BR><BR>
 *
 * @author Salman Halim
 * @version $Revision:$
 */
public class UiOptions
{
    public static boolean      SHOW_DOTS              = false;
    public static boolean      SHOW_ALLOWED_VALUES    = false;
    public static boolean      SHOW_GUESSES           = false;
    public static boolean      SHOW_DOTS_ON_HOVER     = false;
    public static int          SHOW_SELECTIVE_DOT     = CellUi.EMPTY_VALUE;
    public static boolean      SHOW_ALLOWED_DOTS      = true;
    public static boolean      SHOW_PROHIBITED_DOTS   = false;
    public static boolean      SHOW_USER_PROHIBITIONS = false;
    public static boolean      SHOW_X_WING            = true;
    public static boolean      IS_KILLER              = false;
    public static boolean      BOARD_ENTRY_MODE       = false;
    public static boolean      SET_PUZZLE_SUMS_MODE   = false;
    public static StringBuffer BOARD_ENTRY_VALUES     = new StringBuffer();
    public static String       THEME_NAME             = System.getProperty( "default.theme", "C:/tmp/SuDoku/src/sudoku/ui/peachpuff.properties" );

    public enum UiSize
    {
        SMALL( 30 ),
        MEDIUM( 50 ),
        LARGE( 100 );

        private int m_size;

        private UiSize( int size )
        {
            m_size = size;
        }

        public int getSize()
        {
            return m_size;
        }
    }

    public static int CELL_SIZE = UiSize.MEDIUM.getSize();

    public static Dimension getMessageSize( Graphics g, Font f, String message )
    {
        FontRenderContext context = ( (Graphics2D) g ).getFontRenderContext();
        Rectangle2D       bounds  = f.getStringBounds( message, context );

        return new Dimension( (int) bounds.getWidth(), (int) bounds.getHeight() );
    }

    public static int getMessageAscent( Graphics g, Font f, String message )
    {
        FontRenderContext context = ( (Graphics2D) g ).getFontRenderContext();
        Rectangle2D       bounds  = f.getStringBounds( message, context );

        return (int) -bounds.getY();
    }
}
