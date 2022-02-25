package sudoku.ui;


import static sudoku.ui.Theme.ThemableType.FOREGROUND;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;


/**
 * UI theme (colours and fonts).
 * <BR><BR>
 *
 * @author Salman Halim
 * @version $Revision:$
 */
public class Theme
{
    public static final String RANDOM_THEME = "randomTheme";

    public enum ThemableType
    {
        FOREGROUND,
        BACKGROUND,
        BORDER
    };

    public enum Themable
    {
        ALL_BLOCKED_CELL_BACKGROUND( ThemableType.BACKGROUND ),
        ALL_BUT_ONE_CELL_BACKGROUND( ThemableType.BACKGROUND ),
        CELL_BACKGROUND_PUZZLE_SUMS_CELL_SELECTED(ThemableType.BACKGROUND),
        CELL_BACKGROUND_PUZZLE_SUMS_CELL_SELECTED_HOVER(ThemableType.BACKGROUND),
        CELL_BACKGROUND_PUZZLE_SUMS_MODE(ThemableType.BACKGROUND),
        CELL_BACKGROUND( ThemableType.BACKGROUND ),
        SELECTED_CELL_SUM_CELL_BACKGROUND( ThemableType.BACKGROUND ),
        DEFAULT_BACKGROUND( ThemableType.BACKGROUND ),
        EMPTY_CELL_BACKGROUND( ThemableType.BACKGROUND ),
        SELECTED_DOT_BACKGROUND( ThemableType.BACKGROUND ),
        X_WING_CELL_BACKGROUND( ThemableType.BACKGROUND ),

        ALLOWED_DOT( FOREGROUND ),
        ALLOWED_VALUES( FOREGROUND ),
        ALL_BLOCKED( FOREGROUND ),
        ALL_BUT_ONE( FOREGROUND ),
        DEFAULT_COLOR( FOREGROUND ),
        GIVEN_VALUE( FOREGROUND ),
        GUESS( FOREGROUND ),
        PROHIBITED_DOT( FOREGROUND ),
        USER_LOCK_VALUE( FOREGROUND ),
        USER_PROHIBITIONS( FOREGROUND ),
        VALUE( FOREGROUND ),

        CELL_BLOCK_BORDER( ThemableType.BORDER ),
        CELL_BORDER( ThemableType.BORDER ),
        CELL_BORDER_SELECTED_CELL_SUM( ThemableType.BORDER );

        ThemableType m_type;

        Themable( ThemableType type )
        {
            m_type = type;
        }

        public ThemableType getType()
        {
            return m_type;
        }
    }

    public static final Theme DEFAULT_THEME = new Theme();

    private static HashMap<String, Theme> THEMES = new HashMap<String, Theme>();

    static
    {
        DEFAULT_THEME.setColor( Themable.DEFAULT_COLOR, Color.BLACK );

        // Cadet blue 3.
        DEFAULT_THEME.setColor( Themable.ALL_BLOCKED, new Color( 122, 197, 205 ) );

        DEFAULT_THEME.setColor( Themable.CELL_BORDER, Color.BLUE );
        DEFAULT_THEME.setColor( Themable.CELL_BORDER_SELECTED_CELL_SUM, Color.WHITE );
        DEFAULT_THEME.setColor( Themable.CELL_BACKGROUND, Color.WHITE );

        DEFAULT_THEME.setColor(Themable.SELECTED_CELL_SUM_CELL_BACKGROUND, new Color(80, 80, 80, 80));

        // Pale turquoise 1.
        DEFAULT_THEME.setColor( Themable.ALL_BUT_ONE_CELL_BACKGROUND, new Color( 187, 255, 255 ) );

        // Vim's Lucius theme's Visual background.
        DEFAULT_THEME.setColor(Themable.CELL_BACKGROUND_PUZZLE_SUMS_MODE, new Color(32, 80, 112));

        // Light grey.
        DEFAULT_THEME.setColor(Themable.CELL_BACKGROUND_PUZZLE_SUMS_CELL_SELECTED, new Color(200, 200, 200));

        // Maroon.
        DEFAULT_THEME.setColor(Themable.CELL_BACKGROUND_PUZZLE_SUMS_CELL_SELECTED_HOVER, new Color(176, 48, 96));

        // Yellow 1.
        DEFAULT_THEME.setColor( Themable.ALL_BLOCKED_CELL_BACKGROUND, new Color( 255, 255, 224 ) );

        // Bisque.
        DEFAULT_THEME.setColor( Themable.EMPTY_CELL_BACKGROUND, new Color( 255, 228, 196 ) );

        // Light green with R and G +60
        DEFAULT_THEME.setColor( Themable.SELECTED_DOT_BACKGROUND, new Color( 204, 238, 204 ) );

        DEFAULT_THEME.setColor( Themable.X_WING_CELL_BACKGROUND, Color.RED );

        DEFAULT_THEME.setColor( Themable.GIVEN_VALUE, Color.RED );
        DEFAULT_THEME.setColor( Themable.USER_LOCK_VALUE, Color.BLUE );
        DEFAULT_THEME.setColor( Themable.GUESS, Color.BLACK );
        DEFAULT_THEME.setColor( Themable.USER_PROHIBITIONS, Color.RED );
        DEFAULT_THEME.setColor( Themable.ALLOWED_VALUES, Color.BLUE );

        // Cyan 4.
        DEFAULT_THEME.setColor( Themable.ALL_BUT_ONE, new Color( 0, 139, 139 ) );

        DEFAULT_THEME.setColor( Themable.ALLOWED_DOT, Color.BLACK );
        DEFAULT_THEME.setColor( Themable.PROHIBITED_DOT, Color.RED );

        DEFAULT_THEME.setColor( Themable.CELL_BLOCK_BORDER, Color.RED );

        DEFAULT_THEME.setColor( Themable.DEFAULT_BACKGROUND, new Color( 240, 240, 240 ) );
    }

    private HashMap<Themable, Color> m_colors = new HashMap<Themable, Color>();
    private long                     m_timestamp;

    public static Theme getInstance()
    {
        return getInstance( UiOptions.THEME_NAME );
    }

    public static Theme getInstance( String name )
    {
        Theme theme  = THEMES.get( name );
        File  infile = new File( name );

        // Internal theme; not saved to disk
        if ( theme != null && !infile.exists() )
        {
            return theme;
        }

        if ( !infile.exists() )
        {
            return DEFAULT_THEME;
        }

        if ( theme == null || theme.m_timestamp != infile.lastModified() )
        {
            try
            {
                BufferedInputStream in         = new BufferedInputStream( new FileInputStream( infile ) );
                Properties          properties = new Properties();

                properties.load( in );

                in.close();

                theme = new Theme();

                Enumeration<Object> keys  = properties.keys();

                while ( keys.hasMoreElements() )
                {
                    String key = (String) keys.nextElement();
                    theme.setColor( Enum.valueOf( Themable.class, key ), properties.getProperty( key ) );
                }

                theme.m_timestamp = infile.lastModified();

                THEMES.put( name, theme );
            }
            catch ( IOException e )
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return theme == null ? DEFAULT_THEME : theme;
    }

    /**
     * Returns a random number between the specified range -- both numbers are inclusive.
     *
     * @param start the lowest number allowed
     * @param end the largest number allowed
     *
     * @return a random number between the specified inclusive range
     */
    public static int getRandomNumber( int start, int end )
    {
        return (int) ( Math.random() * ( end - start + 1 ) ) + start;
    }

    public static String getRandomTheme( boolean light )
    {
        Theme theme = new Theme();

        for ( Themable item : Themable.values() )
        {
            switch ( item.getType() )
            {
                case FOREGROUND:
                    if ( light )
                    {
                        theme.setColor( item, new Color( getRandomNumber( 0, 100 ),
                                                         getRandomNumber( 0, 100 ),
                                                         getRandomNumber( 0, 100 ) ) );
                    }
                    else
                    {
                        theme.setColor( item, new Color( getRandomNumber( 155, 255 ),
                                                         getRandomNumber( 155, 255 ),
                                                         getRandomNumber( 155, 255 ) ) );
                    }
                    break;
                case BACKGROUND:
                    if ( light )
                    {
                        theme.setColor( item, new Color( getRandomNumber( 155, 255 ),
                                                         getRandomNumber( 155, 255 ),
                                                         getRandomNumber( 155, 255 ) ) );
                    }
                    else
                    {
                        theme.setColor( item, new Color( getRandomNumber( 0, 100 ),
                                                         getRandomNumber( 0, 100 ),
                                                         getRandomNumber( 0, 100 ) ) );
                    }
                    break;
                case BORDER:
                    theme.setColor( item, new Color( getRandomNumber( 0, 255 ),
                                                     getRandomNumber( 0, 255 ),
                                                     getRandomNumber( 0, 255 ) ) );
                    break;
            }
        }

        THEMES.put( RANDOM_THEME, theme );

        return RANDOM_THEME;
    }

    public Color getColor( Themable key )
    {
        Color result = m_colors.get( key );

        if ( result == null )
        {
            result = getDefaultColor();
        }

        return result;
    }

    public Color getDefaultColor()
    {
        return m_colors.get( Themable.DEFAULT_COLOR );
    }

    public void setColor( Themable key, Color color )
    {
        if ( color == null )
        {
            m_colors.remove( key );
        }

        m_colors.put( key, color );
    }

    public void setColor( Themable key, String colorValues )
    {
        if ( colorValues == null || colorValues.trim().length() == 0 )
        {
            m_colors.remove( key );

            return;
        }
        
        int numValues = colorValues.split(" ").length;

        try (Scanner tokens = new Scanner(colorValues)) {
        	if (numValues == 4) {
                setColor(key, new Color(tokens.nextInt(), tokens.nextInt(), tokens.nextInt(), tokens.nextInt()));
            } else {
                setColor(key, new Color(tokens.nextInt(), tokens.nextInt(), tokens.nextInt()));
            }
        }
    }
}
