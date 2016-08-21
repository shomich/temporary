package ru.timmy;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.eclipse.aether.impl.ArtifactResolver;

import java.io.File;
import java.util.List;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
public class MyMojo
        extends AbstractMojo {
    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Location of the file.
     *
     * @parameter
     * @required
     */
    private String artifact;

    /**
     * The project currently being built.
     *
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    private MavenProject project;

    /**
     * @parameter expression="${localRepository}"
     */
    protected ArtifactRepository m_localRepository;

    /**
     * @parameter default-value="${localRepository}"
     */
    private org.apache.maven.artifact.repository.ArtifactRepository
            localRepository;

    /**
     * @parameter default-value="${project.remoteArtifactRepositories}"
     */
    private List remoteRepositories;

    /**
     * @component
     */
    private org.apache.maven.repository.RepositorySystem repository;

    /**
     * The Maven Session
     *
     * @required
     * @readonly
     * @parameter
     * expression="${session}"
     */
    private MavenSession session;

    public void execute()
            throws MojoExecutionException {
        final List<Dependency> dependencies = project.getDependencies();

        for (Dependency d : dependencies) {
            File artifactFile = resolve(artifact);
            System.out.println(artifactFile.getAbsolutePath());
        }
    }

    private File resolve(String artifactDescriptor) {
        String[] s = artifactDescriptor.split(":");

        String type = (s.length >= 4 ? s[3] : "jar");
        Artifact artifact = repository.createArtifact(s[0], s[1], s[2], type);

        ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setArtifact(artifact);

        request.setResolveRoot(true).setResolveTransitively(false);
        request.setServers( session.getRequest().getServers() );
        request.setMirrors( session.getRequest().getMirrors() );
        request.setProxies( session.getRequest().getProxies() );
        request.setLocalRepository(session.getLocalRepository());
        request.setRemoteRepositories(session.getRequest().getRemoteRepositories());
        repository.resolve(request);
        return artifact.getFile();
    }
}
