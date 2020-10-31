/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2013-2020 Andres Almiray.
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
package org.kordamp.gradle.plugin.livereload

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.kordamp.gradle.property.BooleanState
import org.kordamp.gradle.property.IntegerState
import org.kordamp.gradle.property.SimpleBooleanState
import org.kordamp.gradle.property.SimpleIntegerState
import org.kordamp.gradle.property.SimpleStringState
import org.kordamp.gradle.property.StringState

import static org.kordamp.gradle.util.StringUtils.isBlank

/**
 * @author Andres Almiray
 */
class LiveReloadTask extends DefaultTask {
    private static final String ASCIIDOCTOR = 'org.asciidoctor.jvm.convert'
    private static final String MARKDOWN = 'org.kordamp.gradle.markdown'
    private static final String MARKDOWN2HTML = 'markdownToHtml'

    private final BooleanState verbose
    private final IntegerState port
    private final StringState docRoot

    @Internal
    LiveReloadServer liveReloadServer

    LiveReloadTask() {
        verbose = SimpleBooleanState.of(this, 'livereload.verbose', false)
        port = SimpleIntegerState.of(this, 'livereload.port', 35729)
        docRoot = SimpleStringState.of(this, 'livereload.doc.root', '')
    }

    @Option(option = 'livereload-verbose', description = 'Print execution info')
    void setVerbose(boolean value) { verbose.property.set(value) }

    @Option(option = 'livereload-port', description = 'The livereload server port')
    void setPort(String value) { port.property.set(Integer.valueOf(value)) }

    @Option(option = 'livereload-doc-root', description = 'The directory to watch')
    void setDocRoot(String value) { docRoot.property.set(value) }

    @Internal
    Property<Boolean> getVerbose() { verbose.property }

    @Input
    Provider<Boolean> getResolvedVerbose() { verbose.provider }

    @Internal
    Property<Integer> getPort() { port.property }

    @Input
    Provider<Integer> getResolvedPort() { port.provider }

    @Internal
    Property<String> getDocRoot() { docRoot.property }

    @Input
    @Optional
    Provider<String> getResolvedDocRoot() { docRoot.provider }

    @TaskAction
    void runLiveReload() {
        int port = resolvedPort.get()
        String root = resolvedDocRoot.orNull

        if (isBlank(resolvedDocRoot.orNull)) {
            if (project.plugins.hasPlugin(ASCIIDOCTOR)) {
                Task asciidoctorTask = project.tasks.getByName('asciidoctor')
                root = asciidoctorTask?.outputDir?.canonicalPath
            } else if (project.plugins.hasPlugin(MARKDOWN)) {
                Task markdownToHtmlTask = project.tasks.getByName(MARKDOWN2HTML)
                root = markdownToHtmlTask?.outputDir?.canonicalPath
            }
            // TODO: check for jbake plugin
        }

        root = root ?: 'build/livereload'
        docRoot.property.set(root)
        project.file(root).mkdirs()

        project.logger.info("Enabling LiveReload at port $port for $root")
        if (resolvedVerbose.get()) {
            println("Enabling LiveReload at port $port for $root")
        }

        liveReloadServer = liveReloadServer ?: new LiveReloadServer(port, root)
        liveReloadServer.run()
    }
}