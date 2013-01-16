package org.carlspring.maven.web.xml.classpath;

import org.carlspring.maven.web.xml.filters.WARFileFilter;

import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Map;

/**
 * This class finds all the web.xml resources on the classpath.
 *
 * @author mtodorov
 */
public class WebXMLClasspathScanner extends ClasspathScanner
{

    public Map<String, InputStream> findWebXMLResourcesInClasspath()
    {
        return findResourcesInClasspath(".*web\\.xml", new FilenameFilter[]{new WARFileFilter()});
    }

}
