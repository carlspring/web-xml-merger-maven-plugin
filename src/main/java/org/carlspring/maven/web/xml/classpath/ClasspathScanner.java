package org.carlspring.maven.web.xml.classpath;

/**
 * Copyright 2013 Martin Todorov,
 * Carlspring Consulting & Development Ltd.
 *
 *      http://www.carlspring.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.carlspring.maven.web.xml.filters.JARFileFilter;

import java.io.*;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author mtodorov
 */
public class ClasspathScanner
{


    public Map<String, InputStream> findResourcesInClasspath(String fileNamePattern,
                                                             FilenameFilter[] filters)
    {
        Map<String, InputStream> result = new TreeMap<String, InputStream>();
        String classPath = System.getProperty("java.class.path");
        String[] pathElements = classPath.split(System.getProperty("path.separator"));

        if (filters == null)
        {
            filters = new FilenameFilter[]{new JARFileFilter()};
        }

        for (String element : pathElements)
        {
            try
            {
                File newFile = new File(element);
                if (newFile.isDirectory())
                {
                    result.putAll(getDirectoryMatchesForResource(newFile, fileNamePattern, filters));
                }
                else
                {
                    result.putAll(getArchiveFileMatchesForResource(newFile, fileNamePattern, filters));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Map<String, InputStream> getArchiveFileMatchesForResource(File resourceFile,
                                                                      String fileNamePattern)
            throws IOException
    {
        return getDirectoryMatchesForResource(resourceFile, fileNamePattern, null);
    }

    private Map<String, InputStream> getArchiveFileMatchesForResource(File resourceFile,
                                                                      String fileNamePattern,
                                                                      FilenameFilter[] filters)
            throws IOException
    {
        if (!isAcceptedByFilters(resourceFile, filters))
        {
            return new LinkedHashMap<String, InputStream>();
        }

        Map<String, InputStream> result = new TreeMap<String, InputStream>();
        if (resourceFile.canRead())
        {
            JarFile jarFile = new JarFile(resourceFile);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements())
            {
                JarEntry singleEntry = entries.nextElement();

                if (singleEntry.getName().matches(fileNamePattern))
                {
                    System.out.println("Match found in archive " + resourceFile.getCanonicalPath() + ":/" +
                                       singleEntry.getName());

                    result.put(jarFile.getName() + "/" + singleEntry.getName(), jarFile.getInputStream(singleEntry));
                }
            }
        }
        return result;
    }

    private boolean isAcceptedByFilters(File resourceFile,
                                        FilenameFilter[] filters)
            throws IOException
    {
        for (FilenameFilter filter : filters)
        {
            if (resourceFile.isFile())
            {
                if (filter.accept(resourceFile.getParentFile().getCanonicalFile(), resourceFile.getName()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private Map<String, InputStream> getDirectoryMatchesForResource(File directory,
                                                                    String fileNamePattern,
                                                                    FilenameFilter[] filters)
            throws IOException
    {
        Map<String, InputStream> result = new TreeMap<String, InputStream>();
        File[] files = directory.listFiles();
        if (files != null)
        {
            for (File currentFile : files)
            {
                if (currentFile.isFile() && currentFile.getAbsolutePath().matches(fileNamePattern))
                {
                    if (isAcceptedByFilters(currentFile, filters))
                    {
                        System.out.println("Match found in directory: " + currentFile.getAbsolutePath());
                        result.put(currentFile.getAbsolutePath(), new FileInputStream(currentFile));
                    }
                }
                else if (currentFile.isDirectory())
                {
                    result.putAll(getDirectoryMatchesForResource(currentFile, fileNamePattern, filters));
                }
            }
        }

        return result;
    }

}
