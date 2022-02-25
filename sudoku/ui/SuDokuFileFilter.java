package sudoku.ui;


import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * Filter for .sud files.
 * <BR><BR>
 *
 * @author Salman Halim
 * @version $Revision:$
 */
public class SuDokuFileFilter
        extends FileFilter
{
    public static final String FILE_EXTENSION = ".sud";

    public boolean accept( File pathname )
    {
        return pathname.isDirectory() || pathname.getName().endsWith( ( FILE_EXTENSION ) );
    }

    public String getDescription()
    {
        return "SuDoku files";
    }
}
