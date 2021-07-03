/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.panda_lang.reposilite.maven.application

import net.dzikoysk.dynamiclogger.Journalist
import org.panda_lang.reposilite.config.Configuration.RepositoryConfiguration
import org.panda_lang.reposilite.failure.FailureFacade
import org.panda_lang.reposilite.maven.DeploymentService
import org.panda_lang.reposilite.maven.LookupService
import org.panda_lang.reposilite.maven.MavenFacade
import org.panda_lang.reposilite.maven.MetadataService
import org.panda_lang.reposilite.maven.RepositorySecurityProvider
import org.panda_lang.reposilite.maven.RepositoryServiceFactory
import org.panda_lang.reposilite.maven.infrastructure.DeploymentEndpoint
import org.panda_lang.reposilite.maven.infrastructure.IndexEndpoint
import org.panda_lang.reposilite.maven.infrastructure.LookupEndpoint
import org.panda_lang.reposilite.web.ReposiliteContextFactory
import org.panda_lang.reposilite.web.api.RouteHandler

internal object MavenWebConfiguration {

    fun createFacade(journalist: Journalist, failureFacade: FailureFacade, repositoriesConfiguration: Map<String, RepositoryConfiguration>): MavenFacade {
        val repositoryService = RepositoryServiceFactory.createRepositoryService(journalist, repositoriesConfiguration)
        val metadataService = MetadataService(failureFacade)
        val lookupService = LookupService(repositoryService, RepositorySecurityProvider())
        val deployService = DeploymentService(journalist, repositoryService, metadataService)

        return MavenFacade(journalist, metadataService, lookupService, deployService)
    }

    fun routing(contextFactory: ReposiliteContextFactory, mavenFacade: MavenFacade): List<RouteHandler> =
        listOf(
            IndexEndpoint(contextFactory, mavenFacade),
            LookupEndpoint(contextFactory, mavenFacade),
            DeploymentEndpoint(contextFactory, mavenFacade)
        )

}