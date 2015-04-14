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
                'Creates a new Connector project in a new directory named after your project.'
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
            'domo-connector.properties' template: '/templates/connector/domo-connector.properties.tmpl', dataStreamFactory: 'com.domo.connector.general.GeneralDataStreamFactory', recordProcessor: 'com.domo.connector.skeleton.ProcessRecords', validator: '@validator@', validatorJar: '@validatorJar@'
        }

        ProjectTemplate.fromRoot(confPath(projectPath)) {
            'connector.json' template: '/templates/connector/connector.json.tmpl', id: connectorId, major: connectorMajor, minor: connectorMinor, label: connectorLabel, description: connectorDescription
        }

        ProjectTemplate.fromRoot(gradlePath(projectPath)) {
            'version.gradle' template: '/templates/connector/version.gradle.tmpl'
        }

        ProjectTemplate.fromRoot(resourcesPath(projectPath)) {
            'log4j.properties' template: '/templates/connector/log4j.properties.tmpl'
        }

    }
}
