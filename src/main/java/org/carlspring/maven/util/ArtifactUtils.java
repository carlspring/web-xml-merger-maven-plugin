package org.carlspring.maven.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;

import java.io.File;
import java.io.IOException;

/**
 * @author mtodorov
 */
public class ArtifactUtils
{

    public static Artifact convertDependencyToArtifact(Dependency dependency)
    {
        return new DefaultArtifact(dependency.getGroupId(),
                                   dependency.getArtifactId(),
                                   VersionRange.createFromVersion(dependency.getVersion()),
                                   dependency.getScope(),
                                   dependency.getType(),
                                   dependency.getClassifier(),
                                   new DefaultArtifactHandler(dependency.getType()));
    }

    public static String getPathToDependency(Dependency dependency,
                                             ArtifactRepository localRepository)
            throws IOException
    {
        final Artifact artifact = convertDependencyToArtifact(dependency);
        File localArtifactDir = new File(localRepository.getBasedir(),
                                         localRepository.pathOf(artifact)).getParentFile();

        return getFileForArtifact(artifact, localArtifactDir).getAbsolutePath();
    }

    public static String getPathToArtifact(Artifact artifact,
                                           ArtifactRepository localRepository)
            throws IOException
    {
        File localArtifactDir = new File(localRepository.getBasedir(),
                                         localRepository.pathOf(artifact)).getParentFile();

        return getFileForArtifact(artifact, localArtifactDir).getAbsolutePath();
    }

    public static File getFileForDependency(Dependency dependency,
                                            File localArtifactDir)
            throws IOException
    {
        final Artifact artifact = convertDependencyToArtifact(dependency);

        return new File(new File(localArtifactDir, dependency.getGroupId().replaceAll("\\.", "/") + "/" +
                                                   dependency.getArtifactId() + "/" + dependency.getVersion()),
                        artifact.getArtifactId() + "-" +
                        artifact.getVersion() + (artifact.getClassifier() != null ?
                                                 "-" + artifact.getClassifier() : "") +
                        "." + artifact.getType()).getCanonicalFile();
    }

    public static File getFileForArtifact(Artifact artifact,
                                          File localArtifactDir)
            throws IOException
    {
        return new File(localArtifactDir,
                        artifact.getArtifactId() + "-" +
                        artifact.getVersion() + (artifact.getClassifier() != null ?
                                                 "-" + artifact.getClassifier() : "") +
                        "." + artifact.getType()).getCanonicalFile();
    }

}
