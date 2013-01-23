package org.carlspring.maven.web.xml;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author mtodorov
 */
public class WebXMLMergerMojoTest
        extends AbstractMojoTestCase
{


    public static final String DIR_TEST_RESOURCES = "target/test-classes/test-001-artifacts";


    WebXMLMergerMojo mojo;


    @Override
    public void setUp()
            throws Exception
    {
        super.setUp();

        mojo = (WebXMLMergerMojo) lookupMojo("merge", new File(DIR_TEST_RESOURCES, "poms/pom.xml"));
        mojo.setProject(initializeProject());
        mojo.setLocalRepository(initializeLocalRepository());
        mojo.setOutputFile(new File(DIR_TEST_RESOURCES, "web.xml").getCanonicalPath());
    }

    private MavenProject initializeProject()
            throws IOException, XmlPullParserException
    {
        MavenProject project = new MavenProject();
        project.setArtifacts(createArtifacts());

        return project;
    }

    private ArtifactRepository initializeLocalRepository()
    {
        File localRepoDir = new File(DIR_TEST_RESOURCES + "/local-repo");
        String localRepoURL = localRepoDir.toURI().toString();

        return new DefaultArtifactRepository("local", localRepoURL, new DefaultRepositoryLayout());
    }

    public void testExecute()
            throws Exception
    {
        mojo.execute();
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
