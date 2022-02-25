package sudoku;


import static sudoku.ui.UiOptions.CELL_SIZE;
import static sudoku.ui.UiOptions.IS_KILLER;
import static sudoku.ui.UiOptions.SHOW_ALLOWED_DOTS;
import static sudoku.ui.UiOptions.SHOW_ALLOWED_VALUES;
import static sudoku.ui.UiOptions.SHOW_DOTS;
import static sudoku.ui.UiOptions.SHOW_DOTS_ON_HOVER;
import static sudoku.ui.UiOptions.SHOW_GUESSES;
import static sudoku.ui.UiOptions.SHOW_PROHIBITED_DOTS;
import static sudoku.ui.UiOptions.SHOW_SELECTIVE_DOT;
import static sudoku.ui.UiOptions.SHOW_USER_PROHIBITIONS;
import static sudoku.ui.UiOptions.SHOW_X_WING;
import static sudoku.ui.UiOptions.THEME_NAME;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import sudoku.ui.BoardUi;
import sudoku.ui.CellSolution;
import sudoku.ui.CellSolution.Enabled;
import sudoku.ui.CellSum;
import sudoku.ui.CellUi;
import sudoku.ui.SuDokuFileChooser;
import sudoku.ui.Theme;
import sudoku.ui.UiOptions.UiSize;
import sudoku.ui.WrapLayout;

/**
 * The Sudoku game presentation.
 *
 * Realized on Friday, April 19, 2019, that the D in the middle shouldn't be capitalized.
 * <BR><BR>
 *
 * @author Salman Halim
 * @version $Revision:$
 */
public class SuDoku
    implements ListSelectionListener,
               MouseListener
{
    public enum WindowPosition
    {
        TopLeft( 'Q' ),
        TopRight( '\\' ),
        BottomLeft( 'Z' ),
        BottomRight( '/' ),
        Center( 'Y' );

        char m_key;

        WindowPosition( char key )
        {
            m_key = key;
        }

        public char getKey()
        {
            return m_key;
        }
    }

    public static String join(List<? extends Object> coll, CharSequence separator) {
        return coll.stream()
            .map(i -> i == null ? "null" : i.toString())
            .collect(Collectors.joining(separator));
    }

    private JFrame                          m_frame;
    private BoardUi                         m_board;
    private File                            m_filename     = null;
    private HashMap<String, String>         m_states       = new HashMap<>();
    private JLabel                          m_status;
    private JPanel                          m_combinationsContainer;
    private JPanel                          m_southPanel;
    private DefaultListModel<String>        m_listModel;
    private JList<String>                   m_stateList;
    private String                          m_currentState = "Autosave";
    private JMenu                           m_themeMenu;

    public void setBoard( BoardUi val )
    {
        m_board = val;
    }

    public BoardUi getBoard()
    {
        return m_board;
    }

    public SuDoku()
    {
        try
        {
            // UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
        }
        catch ( Exception e )
        {
            // Ignore it silently and go with the default look and feel instead.
        }

        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated( true );

        // Create and set up the window.
        m_frame = new JFrame( "SuDoku" );

        m_frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        // Set up the content pane.
        Container contentPane = m_frame.getContentPane();
        JMenuBar  menuBar     = new JMenuBar();
        JMenu     fileMenu    = new JMenu( "File" );
        JMenu     editMenu    = new JMenu( "Edit" );
        JMenu     puzzleMenu  = new JMenu( "Puzzle" );
        JMenu     sizeMenu    = new JMenu( "Board Size" );

        m_themeMenu = new JMenu( "Theme" );

        menuBar.add( fileMenu );
        fileMenu.setMnemonic( KeyEvent.VK_F );
        fileMenu.add( new JMenuItem( new ClearPuzzleAction() ) );
        fileMenu.addSeparator();
        fileMenu.add( new JMenuItem( new LoadAction() ) );
        fileMenu.add( new JMenuItem( new SaveAction() ) );
        fileMenu.add( new JMenuItem( new SaveAsAction() ) );

        menuBar.add( editMenu );
        editMenu.setMnemonic( KeyEvent.VK_E );
        editMenu.add( new JMenuItem( new CopyPuzzleAction() ) );

        menuBar.add( puzzleMenu );
        puzzleMenu.setMnemonic( KeyEvent.VK_P );
        puzzleMenu.add( new JMenuItem( new LockBoardAction() ) );
        puzzleMenu.add( new JMenuItem( new MarkBoardAsGivenAction() ) );
        puzzleMenu.add( new JMenuItem( new AutoFillAction() ) );
        puzzleMenu.addSeparator();
        puzzleMenu.add( new JMenuItem( new ClearGuessesAction() ) );
        puzzleMenu.add( new JMenuItem( new ResetUnlockedPuzzleAction() ) );
        puzzleMenu.add( new JMenuItem( new ResetPuzzleAction() ) );
        puzzleMenu.addSeparator();
        puzzleMenu.add( new JMenuItem( new QuickImportPuzzleAction() ) );
        puzzleMenu.add( new JMenuItem( new ImportPuzzleAction() ) );
        puzzleMenu.add( new JMenuItem( new SetKillerPuzzleSumsAction() ) );

        menuBar.add( sizeMenu );
        sizeMenu.setMnemonic( KeyEvent.VK_D );
        for ( UiSize size : UiSize.values() )
        {
            sizeMenu.add( new JMenuItem( new ResizeBoardAction( size.toString() ) ) );
        }

        menuBar.add( m_themeMenu );
        m_themeMenu.setMnemonic( KeyEvent.VK_M );
        m_themeMenu.add( new JMenuItem( new LoadThemeAction() ) );
        m_themeMenu.add( new JMenuItem( new DarkRandomThemeAction() ) );
        m_themeMenu.add( new JMenuItem( new LightRandomThemeAction() ) );
        m_themeMenu.addSeparator();
        m_themeMenu.add( new JMenuItem( new SelectThemeAction( THEME_NAME ) ) );

        JToolBar positionToolBar = new JToolBar();
        positionToolBar.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );

        positionToolBar.add( new JButton( new ChangeBoardSizeAction( true ) ) );
        positionToolBar.add( new JButton( new ChangeBoardSizeAction( false ) ) );
        positionToolBar.add( new JToggleButton( new ToggleAlwaysOnTopAction() ) );

        positionToolBar.add( Box.createHorizontalStrut( 20 ) );

        addPositionButtons( positionToolBar );

        positionToolBar.add( Box.createHorizontalGlue() );

        JToggleButton tempToggleButton;
        JToolBar      toolBar = new JToolBar();
        toolBar.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );

        tempToggleButton = new JCheckBox( new ToggleAllowancesButtonAction() );
        tempToggleButton.setSelected( SHOW_ALLOWED_VALUES );
        toolBar.add( tempToggleButton );

        tempToggleButton = new JCheckBox( new ToggleGuessesButtonAction() );
        tempToggleButton.setSelected( SHOW_GUESSES );
        toolBar.add( tempToggleButton );

        tempToggleButton = new JCheckBox( new ToggleUserProhibitionsButtonAction() );
        tempToggleButton.setSelected( SHOW_USER_PROHIBITIONS );
        toolBar.add( tempToggleButton );

        tempToggleButton = new JCheckBox( new ToggleXWingButtonAction() );
        tempToggleButton.setSelected( SHOW_X_WING );
        toolBar.add( tempToggleButton );

        tempToggleButton = new JCheckBox( new ToggleIsKillerButtonAction() );
        tempToggleButton.setSelected( IS_KILLER );
        toolBar.add( tempToggleButton );

        toolBar.add( Box.createHorizontalStrut( 20 ) );

        toolBar.add( new JButton( new SaveStateAction() ) );

        toolBar.add( Box.createHorizontalGlue() );

        // Show the filename in the title bar
        setFilename( null );

        // SALMAN:  Switch the options to JSpinner types
        m_frame.setJMenuBar( menuBar );

        ButtonGroup dotButtons = new ButtonGroup();
        JPanel      dotBar     = new JPanel();

        dotBar.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        dotBar.setLayout( new GridLayout( 0, 1 ) );

        JLabel dots = new JLabel( "DOTS" );
        dots.setFont( dots.getFont().deriveFont( Font.BOLD, 18 ) );
        dotBar.add( dots );

        tempToggleButton = new JCheckBox( new ToggleDotsButtonAction() );
        tempToggleButton.setSelected( SHOW_DOTS );
        dotBar.add( tempToggleButton );

        tempToggleButton = new JCheckBox( new ToggleHoverDotsButtonAction() );
        tempToggleButton.setSelected( SHOW_DOTS_ON_HOVER );
        dotBar.add( tempToggleButton );

        tempToggleButton = new JRadioButton( new ShowSelectiveDotsAction( "All" ) );
        tempToggleButton.setSelected( true );
        dotBar.add( tempToggleButton );
        dotButtons.add( tempToggleButton );

        for ( int i = 1; i <= 9; i++ )
        {
            tempToggleButton = new JRadioButton( new ShowSelectiveDotsAction( String.valueOf( i ) ) );
            dotBar.add( tempToggleButton );
            dotButtons.add( tempToggleButton );
        }

        tempToggleButton = new JCheckBox( new ToggleShowAllowedDotsAction() );
        tempToggleButton.setSelected( SHOW_ALLOWED_DOTS );
        dotBar.add( tempToggleButton );

        tempToggleButton = new JCheckBox( new ToggleShowProhibitedDotsAction() );
        tempToggleButton.setSelected( SHOW_PROHIBITED_DOTS );
        dotBar.add( tempToggleButton );

        contentPane.setLayout( new BorderLayout() );

        m_board = new BoardUi(this);

        m_southPanel = new JPanel();

        m_southPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        m_southPanel.setLayout(new BoxLayout(m_southPanel, BoxLayout.Y_AXIS));

        m_combinationsContainer = new JPanel();

        m_combinationsContainer.setLayout(new WrapLayout());
//        m_combinationsContainer.setLayout(new GridLayout(0, 4, 4, 4));
        m_combinationsContainer.setPreferredSize(new Dimension(0, 70));

        m_status = new JLabel( "Welcome!" );
        m_status.setFont( new Font( "SansSerif", Font.PLAIN, 14 ) );

        m_southPanel.add(m_combinationsContainer);
        m_southPanel.add(m_status);

        m_listModel = new DefaultListModel<>();
        m_stateList = new JList<>( m_listModel );

        Box stateListPane = Box.createVerticalBox();
        stateListPane.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );

        m_listModel.addElement( "Autosave" );

        m_stateList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        m_stateList.setSelectedIndex( 0 );
        m_stateList.addListSelectionListener( this );
        m_stateList.addMouseListener( this );

        JLabel savedStates = new JLabel( "Saved States" );
        savedStates.setFont( dots.getFont().deriveFont( Font.BOLD, 18 ) );
        stateListPane.add( savedStates );
        stateListPane.add( new JButton( new ClearSavedStatesAction() ) );
        stateListPane.add( new JScrollPane( m_stateList ) );

        Box toolbars = Box.createVerticalBox();

        toolbars.add( toolBar );
        toolbars.add( positionToolBar );

        contentPane.add( toolbars, BorderLayout.NORTH );
        contentPane.add( dotBar, BorderLayout.WEST );
        contentPane.add( m_southPanel, BorderLayout.SOUTH );
        contentPane.add( stateListPane, BorderLayout.EAST );
        contentPane.add( m_board );

        m_frame.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        m_frame.setLocation( ( screenSize.width - m_frame.getSize().width ) / 2,
                             ( screenSize.height - m_frame.getSize().height ) / 2 );

        m_frame.setVisible( true );
    }

    public void clearCombinationContainer() {
        m_combinationsContainer.removeAll();
    }

    public JToggleButton addCombinationToDisplay(CellSolution combination, CellSum cellSum) {
        JToggleButton button = new JToggleButton(join(combination.getSolution(), " "), combination.isEnabled());

        button.setMaximumSize(new Dimension(0, 10));

        button.addActionListener(e -> {
            combination.setEnabled(button.isSelected() ? Enabled.ENABLED : Enabled.DISABLED_BY_USER);

            for (CellUi cell : cellSum.getCells()) {
                cell.repaint();
            }
        });

        m_combinationsContainer.add(button);

        m_southPanel.revalidate();

    	return button;
    }

    protected void addPositionButtons( JToolBar toolbar )
    {
        for ( WindowPosition i : WindowPosition.values() )
        {
            toolbar.add( new JButton( new WindowPositionAction( i ) ) );
        }
    }

    public void setFilename( File filename )
    {
        m_filename = filename;

        m_frame.setTitle( "SuDoku:  " + ( m_filename == null ? "No file" : m_filename.getAbsolutePath() ) );
    }

    public File getFilename()
    {
        return m_filename;
    }

    public void setStatus( String status )
    {
        if ( status == null || status.trim().length() == 0 )
        {
            m_status.setText( null );
        }
        else
        {
            m_status.setText( "Last status message:  " + status );
        }
    }

    public static void main( String[] args )
    {
        new SuDoku();
    }

    public void valueChanged( ListSelectionEvent event )
    {
        final int    selectedIndex = m_stateList.getSelectedIndex();
        final String selectedValue = m_stateList.getSelectedValue();

        if ( selectedIndex >= 0 && !selectedValue.equals( m_currentState ) )
        {
            // Compare the current state with the value in the HashMap and if they're different, overwrite Autosave
            // with that value.
            final String currentBoard = m_board.getAsString();
            final String savedBoard   = m_states.get( m_currentState );

            if ( !currentBoard.equals( savedBoard ) )
            {
                saveState( "Autosave" );
            }

            restoreState( selectedValue );
            m_currentState = selectedValue;
        }
    }

    public void saveState()
    {
        int subscript = m_states.size();

        while ( m_states.get( String.valueOf( subscript ) ) != null )
        {
            subscript++;
        }

        saveState( String.valueOf( subscript ) );
    }

    public void saveState( String stateName )
    {
        m_states.put( stateName, m_board.getAsString() );

        if ( !m_listModel.contains( stateName ) )
        {
            m_listModel.addElement( stateName );
        }
    }

    public void restoreState( String stateName )
    {
        if ( stateName == null )
        {
            return;
        }

        String state = m_states.get( stateName );

        if ( state == null )
        {
            setStatus( "No state named '" + stateName + "' found!" );
            return;
        }

        StringReader in = new StringReader( state );

        m_board.loadFromReader( in );

        in.close();
        clearCombinationContainer();
        m_board.refresh();

        setStatus( "Board restored from state." );
    }

    public void mouseClicked( MouseEvent event )
    {
        // SALMAN:  The deletion needs to be changed now that the right click is being used; the left click caused the
        // SALMAN:  selection to change.  The selection may no longer correspond with the item being deleted at this
        // SALMAN:  state, so that needs to be figured out.
        // Alt right click
        if ( event.isAltDown() && event.getButton() == MouseEvent.BUTTON3 )
        {
            // Need a better way to figure out what was deleted
            if ( m_stateList.getSelectedValue().equals( "Autosave" ) )
            {
                setStatus( "Cannot delete the Autosave." );
                return;
            }

            m_states.remove( m_stateList.getSelectedValue() );
            m_listModel.remove( m_stateList.getSelectedIndex() );
        }

        // Ctrl right click should let them rename a saved state (though not the Autosave, of course).
        if ( event.isControlDown() && event.getButton() == MouseEvent.BUTTON3 )
        {
            final String selectedValue = m_stateList.getSelectedValue();

            if ( selectedValue.equals( "Autosave" ) )
            {
                setStatus( "Cannot rename the automatically saved state." );
                return;
            }

            String newName = JOptionPane.showInputDialog( m_stateList,
                                                          "Enter new name for saved state "
                                                          + m_stateList.getSelectedValue(),
                                                          m_stateList.getSelectedValue() );

            if ( newName != null && newName.trim().length() > 0 )
            {
                m_states.put( newName, m_states.remove( selectedValue ) );
                m_listModel.set( m_stateList.getSelectedIndex(), newName );

                setStatus( "State name changed from " + selectedValue + " to " + newName + "." );
            }
        }
    }

    public void mousePressed( MouseEvent e )
    {
    }

    public void mouseReleased( MouseEvent e )
    {
    }

    public void mouseEntered( MouseEvent e )
    {
    }

    public void mouseExited( MouseEvent e )
    {
    }

    public class ClearSavedStatesAction
            extends AbstractAction
    {
        public ClearSavedStatesAction()
        {
            putValue( NAME, "Clear states" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_C );
        }

        public void actionPerformed( ActionEvent e )
        {
            int selection = JOptionPane.showConfirmDialog( m_stateList,
                                                           "Are you sure you want to remove all saved states?"
                                                           + "\nThis cannot be reversed!",
                                                           "Confirm removal of saved states",
                                                           JOptionPane.YES_NO_OPTION );

            if ( selection == JOptionPane.YES_OPTION )
            {
                String autoSave = m_states.get( "Autosave" );

                m_states.clear();

                if ( autoSave != null )
                {
                    m_states.put( "Autosave", autoSave );
                }

                if ( m_listModel.size() > 1 )
                {
                    m_listModel.removeRange( 1, m_listModel.size() - 1 );
                }

                setStatus( "All saved states have been cleared." );
            }
        }
    }

    public class WindowPositionAction
            extends AbstractAction
    {
        public WindowPositionAction( WindowPosition pos )
        {
            putValue( NAME, pos.name() );
            putValue( MNEMONIC_KEY, (int) pos.getKey() );
        }

        public void actionPerformed( ActionEvent event )
        {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

            switch ( Enum.valueOf( WindowPosition.class, event.getActionCommand() ) )
            {
                case Center:
                    m_frame.setLocation( ( screenSize.width - m_frame.getSize().width ) / 2,
                                         ( screenSize.height - m_frame.getSize().height ) / 2 );
                    break;
                case TopLeft:
                    m_frame.setLocation( 0, 0 );
                    break;
                case BottomLeft:
                    m_frame.setLocation( 0, screenSize.height - m_frame.getSize().height );
                    break;
                case TopRight:
                    m_frame.setLocation( screenSize.width - m_frame.getSize().width, 0 );
                    break;
                case BottomRight:
                    m_frame.setLocation( screenSize.width - m_frame.getSize().width,
                                         screenSize.height - m_frame.getSize().height );
                    break;
            }

            m_frame.requestFocusInWindow();
        }
    }

    public class ResizeBoardAction
            extends AbstractAction
    {
        public ResizeBoardAction( String label )
        {
            putValue( NAME, label );
            putValue( MNEMONIC_KEY, (int) label.charAt( 0 ) );
        }

        public void actionPerformed( ActionEvent e )
        {
            CELL_SIZE = Enum.valueOf( UiSize.class, e.getActionCommand() ).getSize();

            m_board.invalidate();
            m_frame.pack();
        }
    }

    public class ShowSelectiveDotsAction
            extends AbstractAction
    {
        public ShowSelectiveDotsAction( String label )
        {
            putValue( NAME, label );
            putValue( MNEMONIC_KEY, (int) label.charAt( 0 ) );
        }

        public void actionPerformed( ActionEvent e )
        {
            if ( e.getActionCommand().equals( "All" ) )
            {
                SHOW_SELECTIVE_DOT = CellUi.EMPTY_VALUE;
            }
            else
            {
                SHOW_SELECTIVE_DOT = Integer.parseInt( e.getActionCommand() );
            }

            // m_board.repaint();
            m_board.refresh();
        }
    }

    public class SaveAction
            extends AbstractAction
    {
        public SaveAction()
        {
            putValue( NAME, "Save" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_S );
        }

        public void actionPerformed( ActionEvent e )
        {
            try
            {
                if ( m_filename == null )
                {
                    JFileChooser chooser = new SuDokuFileChooser( m_filename );

                    chooser.showSaveDialog( m_board );

                    setFilename( chooser.getSelectedFile() );
                }

                if ( m_filename != null )
                {
                    BufferedWriter out = new BufferedWriter( new FileWriter( m_filename ) );

                    out.write( m_board.getAsString() );

                    out.close();

                    setStatus( "Saved " + getFilename() );
                }
            }
            catch ( IOException e1 )
            {
                setStatus( "Unable to save " + getFilename() );
            }

            m_board.repaint();
        }
    }

    public class SaveAsAction
            extends AbstractAction
    {
        public SaveAsAction()
        {
            putValue( NAME, "Save as..." );
            putValue( MNEMONIC_KEY, KeyEvent.VK_V );
        }

        public void actionPerformed( ActionEvent e )
        {
            try
            {
                JFileChooser chooser = new SuDokuFileChooser( m_filename );
                chooser.showSaveDialog( m_board );

                File filename = chooser.getSelectedFile();

                if ( filename != null )
                {
                    setFilename( filename );

                    BufferedWriter out = new BufferedWriter( new FileWriter( m_filename ) );

                    out.write( m_board.getAsString() );

                    out.close();

                    setStatus( "Saved and changed name to " + getFilename() );
                }
            }
            catch ( IOException e1 )
            {
                setStatus( "Unable to save " + getFilename() );
            }

            m_board.repaint();
        }
    }

    public class LoadThemeAction
            extends AbstractAction
    {
        public LoadThemeAction()
        {
            putValue( NAME, "Load theme" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_L );
        }

        public void actionPerformed( ActionEvent event )
        {
            JFileChooser chooser = new JFileChooser( THEME_NAME );
            chooser.showOpenDialog( m_board );

            if ( chooser.getSelectedFile() != null )
            {
                String filePath = chooser.getSelectedFile().getAbsolutePath();

                if ( !filePath.endsWith( ".properties" ) )
                {
                    filePath = filePath + ".properties";
                }

                THEME_NAME = filePath;

                m_themeMenu.add( new JMenuItem( new SelectThemeAction( filePath ) ) );

                m_board.refresh();
            }
        }
    }

    public class SelectThemeAction
            extends AbstractAction
    {
        public SelectThemeAction( String filename )
        {
            putValue( NAME, filename );
        }

        public void actionPerformed( ActionEvent event )
        {
            THEME_NAME = event.getActionCommand();

            m_board.refresh();
        }
    }

    public class DarkRandomThemeAction
            extends AbstractAction
    {
        public DarkRandomThemeAction()
        {
            putValue( NAME, "Dark random theme" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_D );
        }

        public void actionPerformed( ActionEvent event )
        {
            THEME_NAME = Theme.getRandomTheme( false );

            m_board.refresh();
        }
    }

    public class LightRandomThemeAction
            extends AbstractAction
    {
        public LightRandomThemeAction()
        {
            putValue( NAME, "Light Random theme" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_R );
        }

        public void actionPerformed( ActionEvent event )
        {
            THEME_NAME = Theme.getRandomTheme( true );

            m_board.refresh();
        }
    }

    public class LoadAction
            extends AbstractAction
    {
        public LoadAction()
        {
            putValue( NAME, "Open" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_O );
        }

        public void actionPerformed( ActionEvent e )
        {
            try
            {
                JFileChooser chooser = new SuDokuFileChooser( m_filename );
                chooser.showOpenDialog( m_board );

                if ( chooser.getSelectedFile() != null )
                {
                    setFilename( chooser.getSelectedFile() );

                    FileReader in = new FileReader( m_filename );

                    m_board.loadFromReader( in );

                    in.close();

                    setStatus( "Loaded " + getFilename() );
                }
            }
            catch ( IOException e1 )
            {
                setStatus( "Unable to load " + getFilename() );
            }

            m_board.refresh();
        }
    }

    public class ToggleShowAllowedDotsAction
            extends AbstractAction
    {
        public ToggleShowAllowedDotsAction()
        {
            putValue( NAME, "Allowed" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_W );
        }

        public void actionPerformed( ActionEvent e )
        {
            SHOW_ALLOWED_DOTS = !SHOW_ALLOWED_DOTS;

            m_board.repaint();
        }
    }

    public class ToggleShowProhibitedDotsAction
            extends AbstractAction
    {
        public ToggleShowProhibitedDotsAction()
        {
            putValue( NAME, "Prohibited" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_I );
        }

        public void actionPerformed( ActionEvent e )
        {
            SHOW_PROHIBITED_DOTS = !SHOW_PROHIBITED_DOTS;

            m_board.repaint();
        }
    }

    public class ToggleDotsAction
            extends AbstractAction
    {
        public ToggleDotsAction()
        {
            putValue( NAME, "Toggle Dots" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_D );
        }

        public void actionPerformed( ActionEvent e )
        {
            SHOW_DOTS = !SHOW_DOTS;

            m_board.repaint();
        }
    }

    public class ToggleDotsButtonAction
            extends ToggleDotsAction
    {
        public ToggleDotsButtonAction()
        {
            putValue( NAME, "Enabled" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_N );
        }
    }

    public class ToggleHoverDotsButtonAction
            extends AbstractAction
    {
        public ToggleHoverDotsButtonAction()
        {
            putValue( NAME, "Hover Dots" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_H );
        }

        public void actionPerformed( ActionEvent e )
        {
            SHOW_DOTS_ON_HOVER = !SHOW_DOTS_ON_HOVER;

            m_board.repaint();
        }
    }

    public class ChangeBoardSizeAction
            extends AbstractAction
    {
        public ChangeBoardSizeAction( boolean larger )
        {
            putValue( NAME, larger ? "+Size" : "-Size" );
            putValue( MNEMONIC_KEY, larger ? KeyEvent.VK_EQUALS : KeyEvent.VK_MINUS );
        }

        public void actionPerformed( ActionEvent event )
        {
            CELL_SIZE += ( event.getActionCommand().charAt( 0 ) == '+' ? 10 : -10 );

            m_board.invalidate();
            m_frame.pack();
        }
    }

    public class ToggleAlwaysOnTopAction
            extends AbstractAction
    {
        public ToggleAlwaysOnTopAction()
        {
            putValue( NAME, "Always On Top" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_O );
        }

        public void actionPerformed( ActionEvent e )
        {
            m_frame.setAlwaysOnTop( !m_frame.isAlwaysOnTop() );
        }
    }

    public class ToggleAllowancesAction
            extends AbstractAction
    {
        public ToggleAllowancesAction()
        {
            putValue( NAME, "Toggle Allowed values" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_L );
        }

        public void actionPerformed( ActionEvent e )
        {
            SHOW_ALLOWED_VALUES = !SHOW_ALLOWED_VALUES;

            m_board.repaint();
        }
    }

    public class ToggleAllowancesButtonAction
            extends ToggleAllowancesAction
    {
        public ToggleAllowancesButtonAction()
        {
            putValue( NAME, "Allowances" );
        }
    }

    public class MyAction
        extends AbstractAction
    {
        public MyAction( String name, int key )
        {
            putValue( NAME, name );
            putValue( MNEMONIC_KEY, key );
        }

        public void actionPerformed( ActionEvent event )
        {
            System.out.println( "-=-=-=-=-=-=" + new java.util.Date() + " MyAction event:  " + event );  // SALMAN:remove
        }
    }

    public class CopyPuzzleAction
            extends MyAction
    {
        public CopyPuzzleAction()
        {
            super( "Copy", KeyEvent.VK_C );
        }

        public void actionPerformed( ActionEvent event )
        {
            Clipboard       clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection( m_board.getSimpleString() );

            clipboard.setContents( selection, selection );

            setStatus( "Copied the grid contents to the clipboard." );
        }
    }

    public class ToggleUserProhibitionsButtonAction
            extends AbstractAction
    {
        public ToggleUserProhibitionsButtonAction()
        {
            putValue( NAME, "User Prohibitions" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_U );
        }

        public void actionPerformed( ActionEvent e )
        {
            SHOW_USER_PROHIBITIONS = !SHOW_USER_PROHIBITIONS;

            m_board.repaint();
        }
    }

    public class ToggleXWingButtonAction
            extends AbstractAction
    {
        public ToggleXWingButtonAction()
        {
            putValue( NAME, "X Wing" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_X );
        }

        public void actionPerformed( ActionEvent e )
        {
            SHOW_X_WING = !SHOW_X_WING;

            m_board.repaint();
        }
    }

    public class ToggleIsKillerButtonAction
            extends AbstractAction
    {
        public ToggleIsKillerButtonAction()
        {
            putValue( NAME, "Killer" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_K );
        }

        public void actionPerformed( ActionEvent e )
        {
            IS_KILLER = !IS_KILLER;

            m_board.repaint();
        }
    }

    public class ToggleGuessesAction
            extends AbstractAction
    {
        public ToggleGuessesAction()
        {
            putValue( NAME, "Toggle Guesses" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_G );
        }

        public void actionPerformed( ActionEvent e )
        {
            SHOW_GUESSES = !SHOW_GUESSES;

            m_board.repaint();
        }
    }

    public class ToggleGuessesButtonAction
            extends ToggleGuessesAction
    {
        public ToggleGuessesButtonAction()
        {
            putValue( NAME, "Guesses" );
        }
    }

    public class ResetPuzzleAction
            extends AbstractAction
    {
        public ResetPuzzleAction()
        {
            putValue( NAME, "Reset puzzle" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_R );
        }

        public void actionPerformed( ActionEvent e )
        {
            m_board.reset( true );

            m_board.refresh();
        }
    }

    public class QuickImportPuzzleAction
            extends AbstractAction
    {
        public QuickImportPuzzleAction()
        {
            putValue( NAME, "Quick import puzzle" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_Q );
        }

        public void actionPerformed( ActionEvent e )
        {
            m_board.quickImportPuzzle();

            m_board.refresh();

            setStatus( "Puzzle values imported -- click cells to place them or press <Escape> to exit quick import." );
        }
    }

    public class ImportPuzzleAction
            extends AbstractAction
    {
        public ImportPuzzleAction()
        {
            putValue( NAME, "Import puzzle" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_I );
        }

        public void actionPerformed( ActionEvent e )
        {
            setFilename( null );
            m_board.importPuzzle();

            m_board.refresh();

            setStatus( "Puzzle imported." );
        }
    }

    public class SetKillerPuzzleSumsAction
        extends AbstractAction {
        public SetKillerPuzzleSumsAction() {
            putValue(NAME, "Set killer puzzle sums");
            putValue(MNEMONIC_KEY, KeyEvent.VK_K);
        }

        public void actionPerformed(ActionEvent e) {
            m_board.setKillerPuzzleSums();

            m_board.refresh();

            setStatus("Setting killer puzzle sums.");
        }
    }

    public class ClearGuessesAction
            extends AbstractAction
    {
        public ClearGuessesAction()
        {
            putValue( NAME, "Clear guesses" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_C );
        }

        public void actionPerformed( ActionEvent e )
        {
            m_board.clearGuesses();

            m_board.refresh();

            setStatus( "Guesses cleared." );
        }
    }

    public class ResetUnlockedPuzzleAction
            extends AbstractAction
    {
        public ResetUnlockedPuzzleAction()
        {
            putValue( NAME, "Reset unlocked values" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_U );
        }

        public void actionPerformed( ActionEvent e )
        {
            m_board.reset( false );

            m_board.refresh();

            setStatus( "All unlocked values have been cleared." );
        }
    }

    public class ClearPuzzleAction
            extends AbstractAction
    {
        public ClearPuzzleAction()
        {
            putValue( NAME, "New puzzle" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_N );
        }

        public void actionPerformed( ActionEvent e )
        {
            m_board.clear();
            setFilename( null );

            m_board.refresh();
        }
    }

    public class LockBoardAction
            extends AbstractAction
    {
        public LockBoardAction()
        {
            putValue( NAME, "Lock all values" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_L );
        }

        public void actionPerformed( ActionEvent e )
        {
            m_board.lockValues();

            m_board.refresh();
        }
    }

    public class MarkBoardAsGivenAction
            extends AbstractAction
    {
        public MarkBoardAsGivenAction()
        {
            putValue( NAME, "Mark values as given" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_V );
        }

        public void actionPerformed( ActionEvent e )
        {
            m_board.markValuesAsGiven();

            m_board.refresh();
        }
    }

    public class AutoFillAction
            extends AbstractAction
    {
        public AutoFillAction()
        {
            putValue( NAME, "Autofill board" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_A );
        }

        public void actionPerformed( ActionEvent e )
        {
            m_board.autoFill();

            m_board.refresh();

            setStatus( "Autofill complete!" );
        }
    }

    public class SaveStateAction
            extends AbstractAction
    {
        public SaveStateAction()
        {
            putValue( NAME, "Save State" );
            putValue( MNEMONIC_KEY, KeyEvent.VK_S );
        }

        public void actionPerformed( ActionEvent e )
        {
            saveState();

            setStatus( "State saved!" );
        }
    }
}
