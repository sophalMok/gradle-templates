package templates

import org.gradle.api.Plugin
import org.gradle.api.Project
import templates.tasks.connector.ConvertConnectorProjectTask
import templates.tasks.connector.CreateConnectorProjectTask
import templates.tasks.connector.PromoteConnectorProjectTask

/**
 * Adds basic tasks for bootstrapping Domo connector projects. Adds createConnector,
 * convertConnector, and promoteConnector tasks.
 */
class ConnectorTemplatesPlugin implements Plugin<Project> {

	void apply(Project project) {
		project.extensions.create("connector", ConnectorPluginExtension)
		project.task 'createConnector', type:CreateConnectorProjectTask
        project.task 'convertConnector', type:ConvertConnectorProjectTask
        project.task 'promoteConnector', type:PromoteConnectorProjectTask
	}

}

class ConnectorPluginExtension {
	long timestamp = System.currentTimeMillis()

}