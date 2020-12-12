package info.solidsoft.gradle.pitest.functional

import groovy.transform.CompileDynamic
import info.solidsoft.gradle.pitest.PitestPlugin
import nebula.test.functional.ExecutionResult
import org.gradle.internal.jvm.Jvm

@SuppressWarnings("GrMethodMayBeStatic")
@CompileDynamic
class PitestPluginPitVersionFunctionalSpec extends AbstractPitestFunctionalSpec {

    private static final String MINIMAL_SUPPORTED_PIT_VERSION = "1.4.0"  //minimal PIT version that required Java 8 - May 2018
    private static final String MINIMAL_JAVA11_COMPATIBLE_PIT_VERSION = "1.4.2" //in fact 1.4.1, but 1.4.2 is also Java 12 compatible
    private static final String MINIMAL_JAVA13_COMPATIBLE_PIT_VERSION = "1.4.6" //not officially, but at least simple case works
    private static final String MINIMAL_JAVA14_COMPATIBLE_PIT_VERSION = "1.4.11" //not officially, but with ASM 7.3.1

    void "setup and run pitest task with PIT #pitVersion"() {
        given:
            buildFile << getBasicGradlePitestConfig()
        and:
            buildFile << """
                pitest {
                    pitestVersion = '$pitVersion'
                }
            """.stripIndent()
        and:
            writeHelloPitClass()
            writeHelloPitTest()
        when:
            ExecutionResult result = runTasks('pitest')
        then:
            !result.standardError.contains("Build failed with an exception")
            !result.failure
            result.wasExecuted(':pitest')
        and:
            result.standardOutput.contains("Using PIT: ${pitVersion}")
            result.standardOutput.contains("pitest-${pitVersion}.jar")
        and:
            result.standardOutput.contains('Generated 2 mutations Killed 1 (50%)')
            result.standardOutput.contains('Ran 2 tests (1 tests per mutation)')
        where:
            pitVersion << getPitVersionsCompatibleWithCurrentJavaVersion().unique() //be aware that unique() is available since Groovy 2.4.0
    }

    private List<String> getPitVersionsCompatibleWithCurrentJavaVersion() {
        return [getMinimalPitVersionCompatibleWithCurrentJavaVersion(), PitestPlugin.DEFAULT_PITEST_VERSION]
    }

    @SuppressWarnings("IfStatementCouldBeTernary")
    private String getMinimalPitVersionCompatibleWithCurrentJavaVersion() {
        if (isJava14Compatible()) {
            return MINIMAL_JAVA14_COMPATIBLE_PIT_VERSION
        }
        if (isJava13Compatible()) {
            return MINIMAL_JAVA13_COMPATIBLE_PIT_VERSION
        }
        if (Jvm.current().javaVersion.isJava11Compatible()) {
            return MINIMAL_JAVA11_COMPATIBLE_PIT_VERSION
        }
        return MINIMAL_SUPPORTED_PIT_VERSION
    }

}
