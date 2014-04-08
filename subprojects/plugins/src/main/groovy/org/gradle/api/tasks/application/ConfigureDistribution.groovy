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
package org.gradle.api.tasks.application

import org.gradle.api.internal.ConventionTask
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.*
import org.gradle.api.tasks.*
import org.gradle.api.Task
import org.gradle.util.ConfigureUtil
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Tar
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.GradleException
import org.gradle.api.internal.plugins.*

/**
 * <p>A {@link org.gradle.api.Task} for configuring application plugin's distribution spec.</p>
 */
public class ConfigureDistribution extends ConventionTask {
    /**
     * The distribution CopySpec
     */
	CopySpec applicationDistribution
	String applicationBinDir
	String applicationLibDir
	Boolean overrideDefaults
	ApplicationPluginConvention pluginConvention

    CopySpec defaultConfigure() {
		def theAppSpec = getApplicationDistribution()
		String theBinDir =  getApplicationBinDir()
		String theLibDir =  getApplicationLibDir()
		Boolean theOverrideDefaults = getOverrideDefaults()

		pluginConvention.applicationBinDir = theBinDir
        pluginConvention.applicationLibDir = theLibDir
		pluginConvention.overrideDefaults = theOverrideDefaults

		addCreateScriptsTask()

		if (!theOverrideDefaults){
			def jar = project.tasks[JavaPlugin.JAR_TASK_NAME]
			def startScripts = project.tasks[ApplicationPlugin.TASK_START_SCRIPTS_NAME]
			
			theAppSpec.with {
				from(project.file("src/dist"))

				if (theLibDir == null || theLibDir.equals(".")) {
					from(jar)
					from(project.configurations.runtime)
				}
				else{
					into(theLibDir) {
						from(jar)
						from(project.configurations.runtime)
					}
				}

				if (theBinDir == null || theBinDir.equals(".")) {
					from(startScripts) {
						fileMode = 0755
					}
				}
				else {
					into(theBinDir) {
						from(startScripts)
						fileMode = 0755
					}
				}
			}
		}

		pluginConvention.applicationDistribution = theAppSpec
		configureDistDependentTasks()
		theAppSpec
    }

    @Override
    public Task configure(Closure closure) {
    	//super.configure(closure) //recurses to death
    	setApplicationDistribution(new DefaultApplicationDistributionSpec(project.copySpec {})) //replacing default copySpec
    	def result = ConfigureUtil.configure(closure, this, false);
    	defaultConfigure()
    	result
    }

    private void addInstallTask() {
        def installTask = recreateTask(ApplicationPlugin.TASK_INSTALL_NAME, Sync)
		String theBinDir =  getApplicationBinDir()
		String theLibDir =  getApplicationLibDir()

        installTask.description = "Installs the project as a JVM application along with libs and OS specific scripts."
		installTask.dependsOn project.tasks[ApplicationPlugin.TASK_CONFIGURE_NAME]
        installTask.group = ApplicationPlugin.APPLICATION_GROUP
        installTask.with pluginConvention.applicationDistribution
        installTask.into { project.file("${project.buildDir}/install/${pluginConvention.applicationName}") }
        installTask.doFirst {
            if (destinationDir.directory) {
                if (!new File(destinationDir, theLibDir).directory || !new File(destinationDir, theBinDir).directory) {
                    throw new GradleException("The specified installation directory '${destinationDir}' is neither empty nor does it contain an installation for '${pluginConvention.applicationName}'.\n" +
                            "If you really want to install to this directory, delete it and run the install task again.\n" +
                            "Alternatively, choose a different installation directory."
                    )
                }
            }
        }
        installTask.doLast {
            project.ant.chmod(file: "${destinationDir.absolutePath}/${theBinDir != null ? theBinDir : '.'}/${pluginConvention.applicationName}", perm: 'ugo+x')
        }
    }

    private void addDistZipTask() {
        addArchiveTask(ApplicationPlugin.TASK_DIST_ZIP_NAME, Zip)
    }

	private void addDistTarTask() {
        addArchiveTask(ApplicationPlugin.TASK_DIST_TAR_NAME, Tar)
	}

    private <T extends AbstractArchiveTask> void addArchiveTask(String name, Class<T> type) {
        def archiveTask = recreateTask(name, type)
		archiveTask.dependsOn project.tasks[ApplicationPlugin.TASK_CONFIGURE_NAME]
        archiveTask.description = "Bundles the project as a JVM application with libs and OS specific scripts."
        archiveTask.group = ApplicationPlugin.APPLICATION_GROUP
        archiveTask.conventionMapping.baseName = { pluginConvention.applicationName }
        def baseDir = { archiveTask.archiveName - ".${archiveTask.extension}" }
        archiveTask.into(baseDir) {
            with(pluginConvention.applicationDistribution)
        }
    }

    // @Todo: refactor this task configuration to extend a copy task and use replace tokens
    private void addCreateScriptsTask() {
        def startScripts = recreateTask(ApplicationPlugin.TASK_START_SCRIPTS_NAME, CreateStartScripts)
        startScripts.description = "Creates OS specific scripts to run the project as a JVM application."
        startScripts.classpath = project.tasks[JavaPlugin.JAR_TASK_NAME].outputs.files + project.configurations.runtime
        startScripts.conventionMapping.mainClassName = { pluginConvention.mainClassName }
        startScripts.conventionMapping.applicationName = { pluginConvention.applicationName }
        startScripts.conventionMapping.outputDir = { new File(project.buildDir, 'scripts') }
        startScripts.conventionMapping.defaultJvmOpts = { pluginConvention.applicationDefaultJvmArgs }
		startScripts.conventionMapping.applicationBinDir = { pluginConvention.applicationBinDir }
        startScripts.conventionMapping.applicationLibDir = { pluginConvention.applicationLibDir }
    }

    private Task recreateTask(String name, Class<? extends Task> type) {
        def taskInstance = project.tasks.findByPath(name)
        if (taskInstance != null) {
            taskInstance = project.tasks.replace(name, type)
        }
        else {
            taskInstance = project.tasks.create(name, type)   
        }
        taskInstance
    }

    private configureDistDependentTasks() {
        addInstallTask()
        addDistZipTask()
        addDistTarTask()
    }

}
