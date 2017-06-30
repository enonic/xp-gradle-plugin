package com.enonic.gradle.xp.repo

import com.enonic.gradle.xp.XpExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

class RepoHelper
{
    private final Project project

    private final XpExtension ext

    RepoHelper( final Project project )
    {
        this.project = project
        this.ext = XpExtension.get( this.project )
    }

    void addMavenDeployer( final RepositoryHandler handler )
    {
        handler.mavenDeployer {
            repository( url: this.ext.getRepoUrl() ) {
                authentication( userName: this.ext.getRepoUser(),
                                password: this.ext.getRepoPassword() )
            }
        }
    }
}
