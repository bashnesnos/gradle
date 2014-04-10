/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.application.ConfigureDistribution

/**
 * <p>A {@link Plugin} which runs a project as a Java Application.</p>
 */
class ApplicationPlugin implements Plugin<Project> {
    static final String APPLICATION_PLUGIN_NAME = "application"
    static final String APPLICATION_GROUP = APPLICATION_PLUGIN_NAME

    static final String TASK_RUN_NAME = "run"
    static final String TASK_START_SCRIPTS_NAME = "startScripts"
	static final String TASK_CONFIGURE_NAME = "configureDist"
    static final String TASK_INSTALL_NAME = "installApp"
    static final String TASK_DIST_ZIP_NAME = "distZip"
    static final String TASK_DIST_TAR_NAME = "distTar"

    private Project project
    private ApplicationPluginConvention pluginConvention

    void apply(final Project project) {
        this.project = project
        project.plugins.apply(JavaPlugin)

        addPluginConvention()
        addRunTask()

        addConfigureTask()
    }

    private void addPluginConvention() {
        pluginConvention = new ApplicationPluginConvention(project)
        pluginConvention.applicationName = project.name
		pluginConvention.applicationBinDir = "bin"
        pluginConvention.applicationLibDir = "lib"
		pluginConvention.overrideDefaults = false
        project.convention.plugins.application = pluginConvention
    }

    private void addRunTask() {
        def run = project.tasks.create(TASK_RUN_NAME, JavaExec)
        run.description = "Runs this project as a JVM application"
        run.group = APPLICATION_GROUP
        run.classpath = project.sourceSets.main.runtimeClasspath
        run.conventionMapping.main = { pluginConvention.mainClassName }
        run.conventionMapping.jvmArgs = { pluginConvention.applicationDefaultJvmArgs }
    }

	private void addConfigureTask() {
        def configureDist = project.tasks.create(TASK_CONFIGURE_NAME, ConfigureDistribution)
        configureDist.description = "Configures distribution contents"
        configureDist.group = APPLICATION_GROUP
        configureDist.conventionMapping.applicationDistribution = { pluginConvention.applicationDistribution }
		configureDist.conventionMapping.applicationBinDir = { pluginConvention.applicationBinDir }
        configureDist.conventionMapping.applicationLibDir = { pluginConvention.applicationLibDir }
		configureDist.conventionMapping.overrideDefaults = { pluginConvention.overrideDefaults }
        configureDist.pluginConvention pluginConvention
		//configureDist.defaultConfigure()
    }
	
}