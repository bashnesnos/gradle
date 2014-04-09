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

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.plugins.StartScriptGenerator
import org.gradle.api.tasks.*
import org.gradle.util.GUtil
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Action

/**
 * <p>A {@link org.gradle.api.Task} for creating OS dependent start scripts.</p>
 */
public class CreateStartScripts extends Copy {

    /**
     * The application's main class.
     */
    @Input
    String mainClassName

    /**
     * The application's default JVM options.
     */
    @Input
    @Optional
    Iterable<String> defaultJvmOpts = []

    /**
     * The application's name.
     */
    @Input
    String applicationName

    String optsEnvironmentVar

    String exitEnvironmentVar

    /**
     * The class path for the application.
     */
    FileCollection classpath

    String applicationBinDir
    String applicationLibDir

    /**
     * Returns the name of the application's OPTS environment variable.
     */
    @Input
    String getOptsEnvironmentVar() {
        if (optsEnvironmentVar) {
            return optsEnvironmentVar
        }
        if (!getApplicationName()) {
            return null
        }
        return "${GUtil.toConstant(getApplicationName())}_OPTS"
    }

    @Input
    String getExitEnvironmentVar() {
        if (exitEnvironmentVar) {
            return exitEnvironmentVar
        }
        if (!getApplicationName()) {
            return null
        }
        return "${GUtil.toConstant(getApplicationName())}_EXIT_CONSOLE"
    }

    File getUnixScript() {
        return new File(getOutputDir(), getApplicationName())
    }

    File getWindowsScript() {
        return new File(getOutputDir(), "${getApplicationName()}.bat")
    }

    String getRelPath(String aDir) {
        return aDir != null && !aDir.equals(".") ? "$aDir/" : ""
    }

//    @TaskAction
//    void generate() {
//        String theBinDir =  getApplicationBinDir()
//        String theLibDir =  getApplicationLibDir()
//
//        def generator = new StartScriptGenerator()
//        generator.applicationName = getApplicationName()
//        generator.mainClassName = getMainClassName()
//        generator.defaultJvmOpts = getDefaultJvmOpts()
//        generator.optsEnvironmentVar = getOptsEnvironmentVar()
//        generator.exitEnvironmentVar = getExitEnvironmentVar()
//        generator.classpath = getClasspath().collect { it.name =~ /\.jar/ ? "${getRelPath(theLibDir)}${it.name}" : "${it.name}" }
//        generator.scriptRelPath = "${getRelPath(theBinDir)}${getUnixScript().name}"
//        generator.generateUnixScript(getUnixScript())
//        generator.generateWindowsScript(getWindowsScript())
//    }

    Map<String,?> generateWindowsScriptParameters() {
        generator.classpath = getClasspath().collect { it.name =~ /\.jar/ ? "${getRelPath(theLibDir)}${it.name}" : "${it.name}" }
        generator.scriptRelPath = "${getRelPath(theBinDir)}${getUnixScript().name}"

        def windowsClassPath = classpath.collect { "%APP_HOME%\\${it.replace('/', '\\')}" }.join(";")
        def appHome = appHomeRelativePath.replace('/', '\\')
        //argument quoting:
        // - " must be encoded as \"
        // - % must be encoded as %%
        // - pathological case: \" must be encoded as \\\", but other than that, \ MUST NOT be quoted
        // - other characters (including ') will not be quoted
        // - use a state machine rather than regexps
        def quotedDefaultJvmOpts = defaultJvmOpts.collect {
            def wasOnBackslash = false
            it = it.collect { ch ->
                def repl = ch
                if (ch == '%') {
                    repl = '%%'
                } else if (ch == '"') {
                    repl = (wasOnBackslash ? '\\' : '') + '\\"'
                }
                wasOnBackslash = (ch == '\\')
                repl
            }
            (/"${it.join()}"/)
        }
        def defaultJvmOptsString = quotedDefaultJvmOpts.join(' ')
        return [applicationName: applicationName,
                optsEnvironmentVar: optsEnvironmentVar,
                exitEnvironmentVar: exitEnvironmentVar,
                mainClassName: mainClassName,
                defaultJvmOpts: defaultJvmOptsString,
                appNameSystemProperty: appNameSystemProperty,
                appHomeRelativePath: appHome,
                classpath: windowsClassPath]
    }

    Action getWindowsScriptAction() {
        return { fileCopyDetails ->
            fileCopyDetails.filter(ReplaceTokens, tokens: generateWindowsScriptParameters())
        } as Action
    }

}
