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
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.carlspring.maven.util.ArtifactUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author mtodorov
 */
public class DependenciesScanner
{


//    public Map<String, InputStream> findResourcesInDependencies(List<Dependency> dependencies,
//                                                                File localRepository
//                                                                /*, ArtifactRepository localRepository*/)
//            throws IOException
//    {
//        Map<String, InputStream> results = new TreeMap<String, InputStream>();
//
//        Set<Artifact> wars = getWARArtifacts(dependencies);
//
//        for (Dependency war : wars)
//        {
//            //! File dependencyFile = ArtifactUtils.getFileForDependency(war, new File(localRepository.getBasedir()));
//            File dependencyFile = ArtifactUtils.getFileForDependency(war, localRepository);
//            results.put(dependencyFile.getAbsolutePath(), new FileInputStream(dependencyFile));
//        }
//
//        return results;
//    }

    public Map<String, InputStream> findResourcesInArtifacts(Set<Artifact> artifacts,
                                                             ArtifactRepository localRepository)
            throws IOException
    {
        Map<String, InputStream> results = new TreeMap<String, InputStream>();

        Set<Artifact> wars = getWARArtifacts(artifacts);

        for (Artifact war : wars)
        {
            //! File dependencyFile = ArtifactUtils.getFileForDependency(war, new File(localRepository.getBasedir()));
            File dependencyFile = new File(localRepository.getBasedir(), localRepository.pathOf(war));
            results.put(dependencyFile.getAbsolutePath(), new FileInputStream(dependencyFile));
        }

        return results;
    }

    private Set<Artifact> getWARArtifacts(Set<Artifact> artifacts)
    {
        Set<Artifact> wars = new LinkedHashSet<Artifact>();
        for (Artifact artifact : artifacts)
        {
            if (artifact.getType().equals("war"))
            {
                wars.add(artifact);
            }
        }

        return wars;
    }

    /*
    private List<Artifact> getWARDependencies(List<Artifact> artifacts)
    {
        List<Artifact> wars = new ArrayList<Artifact>();
        for (Artifact artifact : artifacts)
        {
            if (artifact.getType().equals("war"))
            {
                wars.add(artifact);
            }
        }

        return wars;
    }
    */

}
