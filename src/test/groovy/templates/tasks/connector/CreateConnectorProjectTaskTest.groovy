package templates.tasks.connector

import org.junit.Test
import templates.AbstractTaskTester

class CreateConnectorProjectTaskTest extends AbstractTaskTester {

    CreateConnectorProjectTaskTest() {
        super(CreateConnectorProjectTask)
    }

    @Test
    void create() {

        def projectName = 'Urban Airship'
        def sanitizedProjectName = projectName.replaceAll('[^a-zA-Z0-9]+', "").trim();
        def dataWriterProjectName = 'DataWriter' + sanitizedProjectName
        def projectGroup = 'com.domo.connector'
        def projectVersion = '0.0'

        def connectorId = "${projectGroup}.${sanitizedProjectName.toLowerCase()}"

        project.ext[CreateConnectorProjectTask.PROJECT_PARENT_DIR] = folder.getRoot() as String
        project.ext[CreateConnectorProjectTask.NEW_PROJECT_NAME] = projectName
        project.ext[CreateConnectorProjectTask.PROJECT_GROUP] = projectGroup
        project.ext[CreateConnectorProjectTask.PROJECT_VERSION] = projectVersion

        task.create()

        assertFileExists folder.root, "${dataWriterProjectName}/src/main/java"
        assertFileExists folder.root, "${dataWriterProjectName}/src/main/resources"
        assertFileExists folder.root, "${dataWriterProjectName}/src/main/resources/conf"
        assertFileExists folder.root, "${dataWriterProjectName}/src/main/resources/resources"
        assertFileExists folder.root, "${dataWriterProjectName}/src/test/resources/log4j.properties"
        assertFileExists folder.root, "${dataWriterProjectName}/src/test/java"
        assertFileExists folder.root, "${dataWriterProjectName}/src/test/resources"
        assertFileExists folder.root, "${dataWriterProjectName}/LICENSE.txt"
        assertFileExists folder.root, "${dataWriterProjectName}/.gitignore"

        def packageName = connectorId.replaceAll('\\.', '/')
        assertFileExists folder.root, "${dataWriterProjectName}/src/main/java/${packageName}/Constants.java"
        assertFileExists folder.root, "${dataWriterProjectName}/src/main/java/${packageName}/ProcessRecords.java"
        assertFileExists folder.root, "${dataWriterProjectName}/src/main/java/${packageName}/api/AccountClient.java"
        assertFileExists folder.root, "${dataWriterProjectName}/src/main/java/${packageName}/api/AuthenticateClient.java"
        assertFileExists folder.root, "${dataWriterProjectName}/src/main/java/${packageName}/api/Client.java"
        assertFileExists folder.root, "${dataWriterProjectName}/src/main/java/${packageName}/api/ContactClient.java"

        assertFileContains folder.root, "${dataWriterProjectName}/build.gradle", "group='${projectGroup}'"

        assertFileContains folder.root, "${dataWriterProjectName}/settings.gradle", "rootProject.name='${dataWriterProjectName}'"

        assertFileContains folder.root, "${dataWriterProjectName}/src/main/resources/domo-connector.properties", 'factory=com.domo.connector.general.GeneralDataStreamFactory'
        assertFileContains folder.root, "${dataWriterProjectName}/src/main/resources/domo-connector.properties", 'recordprocessor=com.domo.connector.skeleton.ProcessRecords'

        assertFileContains folder.root, "${dataWriterProjectName}/src/main/resources/conf/connector.json", "\"id\": \"${projectGroup}.${sanitizedProjectName.toLowerCase()}\""
        assertFileContains folder.root, "${dataWriterProjectName}/src/main/resources/conf/connector.json", "\"major\": ${projectVersion.split("\\.")[0]}"
        assertFileContains folder.root, "${dataWriterProjectName}/src/main/resources/conf/connector.json", "\"minor\": ${projectVersion.split("\\.")[1]}"
        assertFileContains folder.root, "${dataWriterProjectName}/src/main/resources/conf/connector.json", "\"label\": \"${projectName}\""
        assertFileContains folder.root, "${dataWriterProjectName}/src/main/resources/conf/connector.json", "\"description\": \"${projectName} Domo Connector\""

    }
}
