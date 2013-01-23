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
import org.apache.maven.plugin.MojoExecutionException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

        InputStream[] streams = loadStreams(files);

        final Document mergedDoc = merge(compiledExpression, excludedTags, streams);

        if (verbose)
        {
            store(mergedDoc);
        }
    }

    // This method is probably not a good idea
    private InputStream[] loadStreams(File[] files)
            throws FileNotFoundException, MojoExecutionException
    {
        InputStream[] streams = new FileInputStream[files.length];
        int i = 0;
        for (File file : files)
        {
            streams[i] = new FileInputStream(file);
            if (maxLoadedStreams == 0 || ((i + 1) < maxLoadedStreams))
            {
                i++;
            }
            else
            {
                throw new MojoExecutionException("Exceeded the number of permitted simultaneously open streams!");
            }
        }
        return streams;
    }

    public void execute(InputStream[] streams)
            throws Exception
    {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        XPathExpression compiledExpression = xpath.compile(XPATH_WEB_APP);

        List<String> excludedTags = new ArrayList<String>();
        excludedTags.add("display-name");

        final Document mergedDoc = merge(compiledExpression, excludedTags, streams);

        if (verbose)
        {
            store(mergedDoc);
        }
    }

    public Document merge(XPathExpression expression,
                          List<String> excludedTags,
                          InputStream[] streams)
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

        for (int i = 0; i < streams.length; i++)
        {
            Document merge = docBuilder.parse(streams[i]);
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

        return outputDocument;
    }

    private void store(Document document)
            throws Exception
    {
        OutputFormat format = new OutputFormat(document);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(4);

        Writer writer = new OutputStreamWriter(new FileOutputStream(outputFileName));
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
