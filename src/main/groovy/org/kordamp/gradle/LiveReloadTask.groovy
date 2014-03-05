/*
 * Copyright 2013-2014 the original author or authors.
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
package org.kordamp.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * @author Andres Almiray
 */
class LiveReloadTask extends DefaultTask {
    private static final String ASCIIDOCTOR = 'asciidoctor'
    private static final String MARKDOWN = 'markdown'
    private static final String MARKDOWN2HTML = 'markdownToHtml'

    @Optional @Input String docRoot
    @Input Integer port
    LiveReloadServer liveReloadServer

    LiveReloadTask() {
        port = 35729
    }

    @TaskAction
    void runLiveReload() {
        if (docRoot == null) {
            if (project.plugins.hasPlugin(ASCIIDOCTOR)) {
                Task asciidoctorTask = project.tasks.getByName(ASCIIDOCTOR)
                docRoot = asciidoctorTask?.outputDir?.canonicalPath
            } else if (project.plugins.hasPlugin(MARKDOWN)) {
                Task markdownToHtmlTask = project.tasks.getByName(MARKDOWN2HTML)
                docRoot = markdownToHtmlTask?.outputDir?.canonicalPath
            }
            // TODO: check for jbake plugin
        }

        docRoot = docRoot ?: 'build/livereload'

        println("Enabling LiveReload at port $port for $docRoot")

        liveReloadServer = liveReloadServer ?: new LiveReloadServer(port, docRoot)
        liveReloadServer.run()
    }
}