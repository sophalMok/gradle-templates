package templates.tasks.connector

import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction
import templates.ConnectorTemplatesPlugin
import templates.ProjectTemplate
import templates.TemplatesPlugin

/**
 * Task that creates a new connector project in a specified directory.
 */
class CreateConnectorProjectTask extends AbstractConnectorProjectTask {

    public static final String DEFAULT_PROJECT_GROUP = 'com.domo.connector'
    public static final String PROJECT_NAME_PREFIX = 'DataWriter'

    CreateConnectorProjectTask() {
        super(
                ConnectorTemplatesPlugin.CREATE_CONNECTOR_TASK_NAME,
                ConnectorTemplatesPlugin.CREATE_CONNECTOR_TASK_DESCRIPTION
        )
    }

    @TaskAction
    void create() {

        // prompt for project name
        final String projectName = projectName()
        TemplatesPlugin.requireNonNull(projectName, "You must enter a project name.")
        // prompt for project path
        final String sanitizedProjectName = projectName.replaceAll('[^a-zA-Z0-9]+', "").trim();
        final String dataWriterProjectName = PROJECT_NAME_PREFIX + sanitizedProjectName
        final String projectPath = projectPath(dataWriterProjectName)
        // prompt for project group
        final String projectGroup = projectGroup(DEFAULT_PROJECT_GROUP)
        // prompt for project version
        final String projectVersion = projectVersion()
        // prompt for connect label
        final String connectorLabel = connectorLabel(projectName)
        // prompt for connect description
        final String connectorDescription = connectorDescription("${projectName} Domo Connector")

        final String connectorId = "${projectGroup}.${sanitizedProjectName.toLowerCase()}"
        final String connectorMajor = projectVersion.split("\\.")[0]
        final String connectorMinor = projectVersion.split("\\.")[1]

        createBase projectPath

        ProjectTemplate.fromRoot(projectPath) {
            'build.gradle' template: '/templates/connector/build.gradle.tmpl', group: projectGroup
            'settings.gradle' template: '/templates/connector/settings.gradle.tmpl', projectName: dataWriterProjectName
            '.gitignore' template: '/templates/connector/.gitignore.tmpl'
        }

        ProjectTemplate.fromRoot(confPath(projectPath)) {
            'connector.json' template: '/templates/connector/connector.json.tmpl', id: connectorId, major: connectorMajor, minor: connectorMinor, label: connectorLabel, description: connectorDescription
        }

        ProjectTemplate.fromRoot(resourcesPath(projectPath)) {
            'domo-connector.properties' template: '/templates/connector/domo-connector.properties.tmpl', dataStreamFactory: 'com.domo.connector.general.GeneralDataStreamFactory', recordProcessor: 'com.domo.connector.skeleton.ProcessRecords'
        }

        ProjectTemplate.fromRoot(resourcesBundlesPath(projectPath)) {
            'UiBundle.properties' template: '/templates/connector/resources/UiBundle.properties.tmpl'
            'UiBundle_sw.properties' template: '/templates/connector/resources/UiBundle_sw.properties.tmpl'
        }

        ProjectTemplate.fromRoot(testResourcesPath(projectPath)) {
            'log4j.properties' template: '/templates/connector/log4j.properties.tmpl'
        }

        ProjectTemplate.fromRoot(packagePath(projectPath, connectorId)) {
            'Constants.java' template: '/templates/connector/src/main/java/Constants.java.tmpl', packageName: connectorId
            'ProcessRecords.java' template: '/templates/connector/src/main/java/ProcessRecords.java.tmpl', packageName: connectorId
        }

        ProjectTemplate.fromRoot(packagePath(projectPath, connectorId, "api")) {
            'AccountClient.java' template: '/templates/connector/src/main/java/api/AccountClient.java.tmpl', packageName: connectorId
            'AuthenticateClient.java' template: '/templates/connector/src/main/java/api/AuthenticateClient.java.tmpl', packageName: connectorId
            'Client.java' template: '/templates/connector/src/main/java/api/Client.java.tmpl', packageName: connectorId
            'ContactClient.java' template: '/templates/connector/src/main/java/api/ContactClient.java.tmpl', packageName: connectorId
        }

        ProjectTemplate.fromRoot(iconsPath(projectPath)) {
            'large.png' content: '/templates/connector/icons/large.png'
            'small.png' content: '/templates/connector/icons/small.png'
            'wide-large.png' content: '/templates/connector/icons/wide-large.png'
        }

        ProjectTemplate.fromRoot(integrationTestResourcesPath(projectPath)) {
            'integrationTest.properties' template: '/templates/connector/src/integrationTest/resources/integrationTest.properties.tmpl', connectorName: sanitizedProjectName.toLowerCase()
        }
    }
}
