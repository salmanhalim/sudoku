package sudoku.ui;


import java.io.File;

import javax.swing.JFileChooser;


/**
 * File choose dialog for .sud files.
 * <BR><BR>
 *
 * @author Salman Halim
 * @version $Revision:$
 */
public class SuDokuFileChooser
    extends JFileChooser
{
    public SuDokuFileChooser( File startingPath )
    {
        super( startingPath );
        setFileFilter( new SuDokuFileFilter() );
    }

    public File getSelectedFile()
    {
        File result = super.getSelectedFile();

        if ( result != null && !result.getName().endsWith( SuDokuFileFilter.FILE_EXTENSION ) )
        {
            result = new File( result.getAbsolutePath() + SuDokuFileFilter.FILE_EXTENSION );
        }

        return result;
    }
}
