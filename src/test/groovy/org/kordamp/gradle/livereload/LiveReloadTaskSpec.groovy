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
package org.kordamp.gradle.livereload

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * @author Andres Almiray
 */
class LiveReloadTaskSpec extends Specification {
    private static final String LIVERELOAD = 'liveReload'
    private static final String BUILD_DOCUMENTS = 'build/documents'

    Project project
    LiveReloadServer mockLiveReloadServer
    File testRootDir

    def setup() {
        project = ProjectBuilder.builder().withName('test').build()
        mockLiveReloadServer = Mock(LiveReloadServer)
        testRootDir = new File('.')
    }

    @SuppressWarnings('MethodName')
    def "Adds liveReload task with default docRoot"() {
        expect:
            project.tasks.findByName(LIVERELOAD) == null

        when:
            Task task = project.tasks.create(name: LIVERELOAD, type: LiveReloadTask) {
                liveReloadServer = mockLiveReloadServer
            }

            task.runLiveReload()

        then:
            task.docRoot == 'build/livereload'
    }

    @SuppressWarnings('MethodName')
    def "Adds liveReload task with user defined docRoot"() {
        expect:
            project.tasks.findByName(LIVERELOAD) == null

        when:
            Task task = project.tasks.create(name: LIVERELOAD, type: LiveReloadTask) {
                liveReloadServer = mockLiveReloadServer
                docRoot = BUILD_DOCUMENTS
            }

            task.runLiveReload()

        then:
            task.docRoot == BUILD_DOCUMENTS
    }
}
