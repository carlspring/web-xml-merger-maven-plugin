package org.carlspring.maven.web.xml;

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

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author mtodorov
 */
public class XMLMerger
{

    public static final String XPATH_WEB_APP = "/web-app";

    private String outputFileName;

    private String resourceName;

    private String destinationResource;

    private int maxLoadedStreams = 0;

    private boolean verbose = true;


    public void execute(File[] files)
            throws Exception
    {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        XPathExpression compiledExpression = xpath.compile(XPATH_WEB_APP);

        List<String> excludedTags = new ArrayList<String>();
        excludedTags.add("display-name");

        Set<String> fileSet = new LinkedHashSet<String>();
        for (File file : files)
        {
            fileSet.add(file.getCanonicalPath());
        }

        final Document mergedDoc = merge(compiledExpression, excludedTags, fileSet);

        if (verbose)
        {
            store(mergedDoc);
        }
    }

    public void execute(Set<String> warPaths)
            throws Exception
    {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        XPathExpression compiledExpression = xpath.compile(XPATH_WEB_APP);

        List<String> excludedTags = new ArrayList<String>();
        excludedTags.add("display-name");

        final Document mergedDoc = merge(compiledExpression, excludedTags, warPaths);

        if (verbose)
        {
            store(mergedDoc);
        }
    }

    public Document merge(XPathExpression expression,
                          List<String> excludedTags,
                          Set<String> files)
            throws ParserConfigurationException,
                   IOException,
                   SAXException,
                   XPathExpressionException
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document outputDocument = docBuilder.parse(new FileInputStream(getOutputFileName()));

        Node results = (Node) expression.evaluate(outputDocument, XPathConstants.NODE);
        if (results == null)
        {
            throw new IOException(outputFileName + ": the expression does not evaluate to a node!");
        }


        if (!files.isEmpty())
        {
            System.out.println("Merging web.xml files...");
        }

        for (String file : files)
        {
            InputStream is = null;
            JarFile jarFile = null;

            try
            {
                if (file.toLowerCase().endsWith(".war"))
                {
                    jarFile = new JarFile(file);
                    ZipEntry entry = jarFile.getEntry("WEB-INF/web.xml");

                    is = jarFile.getInputStream(entry);
                    System.out.println(" -> Applying " + file + "/WEB-INF/web.xml");
                }
                else
                {
                    is = new FileInputStream(file);
                    System.out.println(" -> Applying " + file);
                }

                Document merge = docBuilder.parse(is);
                Node nextResults = (Node) expression.evaluate(merge, XPathConstants.NODE);

                while (nextResults.hasChildNodes())
                {
                    Node child = nextResults.getFirstChild();
                    nextResults.removeChild(child);

                    boolean include = true;
                    if (child.getNodeName() != null && excludedTags.contains(child.getNodeName()))
                    {
                        include = false;
                    }

                    if (include)
                    {
                        child = outputDocument.importNode(child, true);

                        results.appendChild(child);
                    }
                }
            }
            finally
            {
                if (jarFile != null)
                {
                    jarFile.close();
                }

                if (is != null)
                {
                    is.close();
                }
            }
        }

        return outputDocument;
    }

    private void store(Document document)
            throws Exception
    {
        File outputFile = new File(outputFileName).getCanonicalFile();

        System.out.println("Storing '" +outputFile.getPath() + "'...");

        OutputFormat format = new OutputFormat(document);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(4);

        Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile));
        XMLSerializer serializer = new XMLSerializer(writer, format);
        serializer.serialize(document);

        writer.flush();
        writer.close();

        writer = new OutputStreamWriter(System.out);
        serializer = new XMLSerializer(writer, format);
        serializer.serialize(document);

        writer.flush();
        writer.close();
    }

    public String getResourceName()
    {
        return resourceName;
    }

    public void setResourceName(String resourceName)
    {
        this.resourceName = resourceName;
    }

    public String getDestinationResource()
    {
        return destinationResource;
    }

    public void setDestinationResource(String destinationResource)
    {
        this.destinationResource = destinationResource;
    }

    public String getOutputFileName()
    {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName)
    {
        this.outputFileName = outputFileName;
    }

    public int getMaxLoadedStreams()
    {
        return maxLoadedStreams;
    }

    public void setMaxLoadedStreams(int maxLoadedStreams)
    {
        this.maxLoadedStreams = maxLoadedStreams;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

}
