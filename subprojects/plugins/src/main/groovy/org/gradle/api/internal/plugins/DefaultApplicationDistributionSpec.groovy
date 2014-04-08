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
package org.gradle.api.internal.plugins;

import org.gradle.api.file.*;
import org.gradle.api.internal.file.copy.*;
import org.gradle.api.specs.Spec;
import org.gradle.api.Action;
import java.util.regex.Pattern;


public class DefaultApplicationDistributionSpec implements ApplicationDistributionSpec, CopySourceSpec {

	private final CopySpecInternal rootSpec;
    private final CopySpecInternal mainSpec;
    private final CopySpecInternal launchScriptSpec;
    private final CopySpecInternal appClasspathSpec;

    public DefaultApplicationDistributionSpec(CopySpecInternal rootSpec) {
        this.rootSpec = rootSpec;
        this.mainSpec = rootSpec.addChild();
        this.launchScriptSpec = rootSpec.addChild();
        this.appClasspathSpec = rootSpec.addChild();
    }

    // -----------------------------------------------
    // ---- Delegate CopySpec methods to rootSpec ----
    // -----------------------------------------------

    public CopySpecInternal getRootSpec() {
        return rootSpec;
    }

    protected CopySpec getMainSpec() {
        return mainSpec;
    }

    public CopySpec getLaunchScripts() {
        return launchScriptSpec;
    }

    public CopySpec getClasspathSpec() {
        return appClasspathSpec;
    }

    public boolean hasSource() {
        getRootSpec().hasSource()
    }

    public Iterable<CopySpecInternal> getChildren() {
        getRootSpec().getChildren()
    }

    public CopySpecInternal addChild() {
        getRootSpec().addChild()
        return this;
    }

    public CopySpecInternal addChildBeforeSpec(CopySpecInternal childSpec) {
        getRootSpec().addChild(childSpec)   
        return this;
    }

    public CopySpecInternal addFirst() {
        getRootSpec().addFirst()
        return this;
    }

    public void walk(Action<? super CopySpecResolver> action) {
        getRootSpec().walk(action)
    }

    public CopySpecResolver buildRootResolver() {
        getRootSpec().buildRootResolver()
    }

    public CopySpecResolver buildResolverRelativeToParent(CopySpecResolver parent) {
        getRootSpec().buildResolverRelativeToParent(parent)   
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCaseSensitive() {
        return getMainSpec().isCaseSensitive();
    }

    /**
     * {@inheritDoc}
     */
    public void setCaseSensitive(boolean caseSensitive) {
        getMainSpec().setCaseSensitive(caseSensitive);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getIncludeEmptyDirs() {
        return getMainSpec().getIncludeEmptyDirs();
    }

    /**
     * {@inheritDoc}
     */
    public void setIncludeEmptyDirs(boolean includeEmptyDirs) {
        getMainSpec().setIncludeEmptyDirs(includeEmptyDirs);
    }

    /**
     * {@inheritDoc}
     */
    public void setDuplicatesStrategy(DuplicatesStrategy strategy) {
        getRootSpec().setDuplicatesStrategy(strategy);
    }

    /**
     * {@inheritDoc}
     */
    public DuplicatesStrategy getDuplicatesStrategy() {
        return getRootSpec().getDuplicatesStrategy();
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec from(Object... sourcePaths) {
        getMainSpec().from(sourcePaths);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec filesMatching(String pattern, Action<? super FileCopyDetails> action) {
        getMainSpec().filesMatching(pattern, action);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec filesNotMatching(String pattern, Action<? super FileCopyDetails> action) {
        getMainSpec().filesNotMatching(pattern, action);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec from(Object sourcePath, Closure c) {
        getMainSpec().from(sourcePath, c);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec with(CopySpec... sourceSpecs) {
        getMainSpec().with(sourceSpecs);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec into(Object destDir) {
        getRootSpec().into(destDir);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec into(Object destPath, Closure configureClosure) {
        getMainSpec().into(destPath, configureClosure);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec include(String... includes) {
        getMainSpec().include(includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec include(Iterable<String> includes) {
        getMainSpec().include(includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec include(Spec<FileTreeElement> includeSpec) {
        getMainSpec().include(includeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec include(Closure includeSpec) {
        getMainSpec().include(includeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec exclude(String... excludes) {
        getMainSpec().exclude(excludes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec exclude(Iterable<String> excludes) {
        getMainSpec().exclude(excludes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec exclude(Spec<FileTreeElement> excludeSpec) {
        getMainSpec().exclude(excludeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec exclude(Closure excludeSpec) {
        getMainSpec().exclude(excludeSpec);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec setIncludes(Iterable<String> includes) {
        getMainSpec().setIncludes(includes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getIncludes() {
        return getMainSpec().getIncludes();
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec setExcludes(Iterable<String> excludes) {
        getMainSpec().setExcludes(excludes);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getExcludes() {
        return getMainSpec().getExcludes();
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec rename(Closure closure) {
        getMainSpec().rename(closure);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec rename(String sourceRegEx, String replaceWith) {
        getMainSpec().rename(sourceRegEx, replaceWith);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec rename(Pattern sourceRegEx, String replaceWith) {
        getMainSpec().rename(sourceRegEx, replaceWith);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec filter(Map<String, ?> properties, Class<? extends FilterReader> filterType) {
        getMainSpec().filter(properties, filterType);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec filter(Class<? extends FilterReader> filterType) {
        getMainSpec().filter(filterType);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec filter(Closure closure) {
        getMainSpec().filter(closure);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec expand(Map<String, ?> properties) {
        getMainSpec().expand(properties);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getDirMode() {
        return getMainSpec().getDirMode();
    }

    /**
     * {@inheritDoc}
     */
    public Integer getFileMode() {
        return getMainSpec().getFileMode();
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec setDirMode(Integer mode) {
        getMainSpec().setDirMode(mode);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec setFileMode(Integer mode) {
        getMainSpec().setFileMode(mode);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec eachFile(Action<? super FileCopyDetails> action) {
        getMainSpec().eachFile(action);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DefaultApplicationDistributionSpec eachFile(Closure closure) {
        getMainSpec().eachFile(closure);
        return this;
    }

}