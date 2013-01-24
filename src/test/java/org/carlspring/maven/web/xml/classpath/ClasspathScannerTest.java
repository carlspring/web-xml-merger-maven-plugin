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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.assertTrue;


/**
 * @author mtodorov
 */
public class ClasspathScannerTest
{

    public static final String DIR_TEST_RESOURCES = "target/test-classes/test-002-classpath";

    static String CLASSPATH;

    static String CLASSPATH_ORIG;


    @Before
    public void setUp()
            throws Exception
    {
        setupClasspath();
    }

    private void setupClasspath()
            throws IOException
    {
        CLASSPATH = System.getProperty("java.class.path");

        if (CLASSPATH_ORIG == null)
        {
            CLASSPATH_ORIG = CLASSPATH;
        }

        File resourcesBaseDir = new File(DIR_TEST_RESOURCES).getCanonicalFile();

        CLASSPATH += ":" + new File(resourcesBaseDir, "dirs/dir1");
        CLASSPATH += ":" + new File(resourcesBaseDir, "dirs/dir2");
        CLASSPATH += ":" + new File(resourcesBaseDir, "wars/webapp1.war");
        CLASSPATH += ":" + new File(resourcesBaseDir, "wars/webapp2.war");

        System.setProperty("java.class.path", CLASSPATH);
    }

    @After
    public void tearDown()
            throws Exception
    {
        System.setProperty("java.class.path", CLASSPATH_ORIG);
    }

    @Test
    public void testSearch()
    {
        WebXMLClasspathScanner scanner = new WebXMLClasspathScanner();

        Map<String, InputStream> foundFiles = scanner.findWebXMLResourcesInClasspath();

        assertTrue("Failed to locate any matches!", !foundFiles.isEmpty());
    }

}
