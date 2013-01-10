package org.carlspring.maven.web.xml.filters;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author mtodorov
 */
public class WARFileFilter implements FilenameFilter
{

    @Override
    public boolean accept(File dir,
                          String name)
    {
        return name.toLowerCase().endsWith(".war");
    }

}
