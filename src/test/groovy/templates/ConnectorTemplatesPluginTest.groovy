package templates

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ConnectorTemplatesPluginTest {

    @Rule public TemporaryFolder rootFolder = new TemporaryFolder()

    @Test void 'apply'(){
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'templates'

        assert project.tasks.createConnector
        assert project.tasks.convertConnector
        assert project.tasks.promoteConnector
    }
}
