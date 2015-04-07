package templates.tasks.connector

import templates.ProjectTemplate
import templates.TemplatesPlugin
import templates.tasks.AbstractProjectTask

/**
 * Base class for Connector project tasks.
 */
abstract class AbstractConnectorProjectTask extends AbstractProjectTask {

    static final String CONNECTOR_LABEL = 'connectorLabel'
    static final String CONNECTOR_DESCRIPTION = 'connectorDescription'
    static final String CONNECTOR_PROMOTE_TARGET = 'connectorPromoteTarget'
    static final String CONNECTOR_BUNDLE_PATH = 'connectorBundlePath'

    AbstractConnectorProjectTask(final String name, final String description) {
        super(name, description)
    }

    protected String connectorLabel(final String defaultLabel) {
        project.properties[CONNECTOR_LABEL] ?: TemplatesPlugin.prompt('Connector Label:', defaultLabel)
    }

    protected String connectorDescription(final String defaultDescription) {
        project.properties[CONNECTOR_DESCRIPTION] ?: TemplatesPlugin.prompt('Connector Description:', defaultDescription)
    }

    protected String promptBundlePath(final String defaultBundlePath) {
        project.properties[CONNECTOR_BUNDLE_PATH] ?: TemplatesPlugin.prompt('Bundle Path:', defaultBundlePath)
    }

    protected int promoteEnvironment() {
        def options = [PromoteConnectorProjectTask.Env.DEV.name(),
                       PromoteConnectorProjectTask.Env.FRDEV.name(),
                       PromoteConnectorProjectTask.Env.FRPROD1.name(),
                       PromoteConnectorProjectTask.Env.DOMOVM.name()]
        project.properties[CONNECTOR_PROMOTE_TARGET] ?: TemplatesPlugin.promptOptions('Environment:', 1, options) + 1
    }

    /**
     * Creates the basic Connector project directory structure.
     *
     * @param path the root of the project. Optional,defaults to user.dir.
     */
    protected void createBase(String path = defaultDir()) {
        ProjectTemplate.fromRoot(path) {
            'src' {
                'main' {
                    'java' {}
                    'resources' {
                        'conf' {}
                        'resources' {}
                    }
                }
                'test' {
                    'java' {}
                    'resources' {}
                }
            }
            'LICENSE.txt' '// Your License Goes here'
        }
    }

    protected String srcPath(String projectPath) {
        return "$projectPath/src"
    }

    protected String confPath(String projectPath) {
        return resourcesPath(projectPath) + '/conf'
    }

    protected String gradlePath(String projectPath) {
        return "$projectPath/gradle"
    }

    protected String connectorJsonPath(String projectPath) {
        return confPath(projectPath) + '/connector.json'
    }

    protected String iconsPath(String projectPath) {
        return resourcesPath(projectPath) + '/icons'
    }

    protected String mainPath(String projectPath) {
        return srcPath(projectPath) + '/main'
    }

    protected String javaPath(String projectPath) {
        return mainPath(projectPath) + '/java'
    }

    protected String resourcesPath(String projectPath) {
        return mainPath(projectPath) + '/resources'
    }

    protected String resourcesBundlesPath(String projectPath) {
        return resourcesPath(projectPath) + '/resources'
    }

    protected String testPath(String projectPath) {
        return srcPath(projectPath) + '/test'
    }

    protected String testJavaPath(String projectPath) {
        return testPath(projectPath) + '/java'
    }

    protected String testResourcesPath(String projectPath) {
        return testPath(projectPath) + '/resources'
    }
}
