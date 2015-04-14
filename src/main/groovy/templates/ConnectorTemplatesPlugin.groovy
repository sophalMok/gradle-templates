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

	public static final String CREATE_CONNECTOR_TASK_NAME = 'createDomoConnector'
	public static final String CONVERT_CONNECTOR_TASK_NAME = 'convertDomoConnector'
	public static final String PROMOTE_CONNECTOR_TASK_NAME = 'promoteDomoConnector'
	public static final String CREATE_CONNECTOR_TASK_DESCRIPTION = 'Creates a new Domo Connector project in a new directory named after your project.'
	public static final String CONVERT_CONNECTOR_TASK_DESCRIPTION = 'Converts a Domo Connector ANT project to a Domo Connector GRADLE Project.'
	public static final String PROMOTE_CONNECTOR_TASK_DESCRIPTION = 'Promotes a Domo Connector to a target environment.'

	void apply(Project project) {
		project.task CREATE_CONNECTOR_TASK_NAME, type:CreateConnectorProjectTask
        project.task CONVERT_CONNECTOR_TASK_NAME, type:ConvertConnectorProjectTask
        project.task PROMOTE_CONNECTOR_TASK_NAME, type:PromoteConnectorProjectTask
	}

}
