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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.versioning.VersionRange;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;


/**
 * @author mtodorov
 */
public class ArtifactsScannerTest
{

    public static final String DIR_TEST_RESOURCES = "target/test-classes/test-001-artifacts";


    @Before
    public void setUp()
            throws Exception
    {
        setupProject();
    }

    private void setupProject()
            throws IOException
    {
    }

    @Test
    public void testSearch()
            throws IOException
    {
        DependenciesScanner scanner = new DependenciesScanner();

        Set<Artifact> artifacts = createArtifacts();

        ArtifactRepository localRepository = new DefaultArtifactRepository("local",
                                                                           "file://" + new File(DIR_TEST_RESOURCES).getCanonicalPath() + "/local-repo",
                                                                           new DefaultRepositoryLayout());

        Set<String> foundFiles = scanner.findResourcesInArtifacts(artifacts, localRepository);

        assertTrue("Failed to locate any matches!", !foundFiles.isEmpty());

        for (String key : foundFiles)
        {
            System.out.println("Path: " + key);
        }
    }

    private Set<Artifact> createArtifacts()
    {
        Set<Artifact> artifacts = new LinkedHashSet<Artifact>();

        Artifact artifact1 = new DefaultArtifact("com.foo",
                                                 "cool-webapp",
                                                 VersionRange.createFromVersion("1.0-SNAPSHOT"),
                                                 "compile",
                                                 "war",
                                                 null,
                                                 new DefaultArtifactHandler("war"));

        Artifact artifact2 = new DefaultArtifact("com.foo",
                                                 "my-webapp",
                                                 VersionRange.createFromVersion("1.1-SNAPSHOT"),
                                                 "compile",
                                                 "war",
                                                 null,
                                                 new DefaultArtifactHandler("war"));

        Artifact artifact3 = new DefaultArtifact("com.foo",
                                                 "assembly-webapp",
                                                 VersionRange.createFromVersion("1.0-SNAPSHOT"),
                                                 "compile",
                                                 "war",
                                                 null,
                                                 new DefaultArtifactHandler("war"));

        Artifact artifact4 = new DefaultArtifact("com.foo",
                                                 "foo-app",
                                                 VersionRange.createFromVersion("1.1-SNAPSHOT"),
                                                 "compile",
                                                 "jar",
                                                 null,
                                                 new DefaultArtifactHandler("jar"));

        Artifact artifact5 = new DefaultArtifact("com.foo",
                                                 "bar-app",
                                                 VersionRange.createFromVersion("1.1-SNAPSHOT"),
                                                 "compile",
                                                 "jar",
                                                 null,
                                                 new DefaultArtifactHandler("jar"));


        artifacts.add(artifact1);
        artifacts.add(artifact2);
        artifacts.add(artifact3);
        artifacts.add(artifact4);
        artifacts.add(artifact5);

        return artifacts;
    }

}
