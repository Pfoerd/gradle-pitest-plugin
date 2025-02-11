/* Copyright (c) 2012 Marcin Zajączkowski
 * All rights reserved.
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
package pl.droidsonroids.gradle.pitest

import groovy.transform.CompileDynamic
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

@CompileDynamic
class PitestPluginTest extends Specification {

    static void assertThatTasksAreInGroup(Project project, List<String> taskNames, String group) {
        taskNames.each { String taskName ->
            Task task = project.tasks[taskName]
            assert task != null
            assert task.group == group
        }
    }

    void "add pitest tasks to android library project in proper group"() {
        when:
            Project project = AndroidUtils.createSampleLibraryProject()
            project.evaluate()
        then:
            project.plugins.hasPlugin(PitestPlugin)
            List<String> tasks = [AndroidUtils.PITEST_RELEASE_TASK_NAME, "${PitestPlugin.PITEST_TASK_NAME}Debug"]
            assertThatTasksAreInGroup(project, tasks, PitestPlugin.PITEST_TASK_GROUP)
    }

    void "apply pitest plugin without android plugin applied"() {
        given:
            Project project = ProjectBuilder.builder().build()
        expect:
            !project.plugins.hasPlugin("com.android.application") &&
                !project.plugins.hasPlugin("com.android.library") &&
                !project.plugins.hasPlugin("com.android.test")
        when:
            project.apply(plugin: "pl.droidsonroids.pitest")
            project.evaluate()
        then:
            thrown(GradleException)
    }

    @SuppressWarnings("ImplicitClosureParameter")
    void "depend on the Android task that copies resources to the build directory (for robolectric, etc)"() {
        when:
            Project project = AndroidUtils.createSampleLibraryProject()
            project.evaluate()
        then:
            assert project.tasks[AndroidUtils.PITEST_RELEASE_TASK_NAME].getDependsOn().find { it == 'compileReleaseUnitTestSources' }
            assert project.tasks["${PitestPlugin.PITEST_TASK_NAME}Debug"].getDependsOn().find { it == 'compileDebugUnitTestSources' }
    }

    @SuppressWarnings("ImplicitClosureParameter")
    void "depend on the Android application task that copies resources to the build directory (for robolectric, etc)"() {
        when:
            Project project = AndroidUtils.createSampleApplicationProject()
            project.evaluate()
        then:
            assert project.tasks["${PitestPlugin.PITEST_TASK_NAME}FreeBlueRelease"].getDependsOn().find { it == 'compileFreeBlueReleaseUnitTestSources' }
    }

    @SuppressWarnings("ImplicitClosureParameter")
    void "combined task classpath contains correct variant paths for flavored application projects"() {
        when:
            Project project = AndroidUtils.createSampleApplicationProject()
            project.evaluate()
        then:
            Object classpath = project.tasks["${PitestPlugin.PITEST_TASK_NAME}FreeBlueRelease"].additionalClasspath.files
            assert classpath.find { it.toString().endsWith('sourceFolderJavaResources/freeBlue/release') }
            assert classpath.find { it.toString().endsWith('sourceFolderJavaResources/test/freeBlue/release') }
    }

    void "strange sdk versions get properly sanitized"() {
        when:
            String version = PitestPlugin.sanitizeSdkVersion('strange version (0)')
        then:
            assert version == 'strange-version--0-'
    }

}
