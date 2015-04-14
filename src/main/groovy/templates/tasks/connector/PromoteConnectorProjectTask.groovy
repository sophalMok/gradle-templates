package templates.tasks.connector

import org.gradle.api.Task
import org.gradle.api.tasks.TaskAction
import templates.ConnectorTemplatesPlugin

/**
 * Task that promotes a connector to the specified environment.
 */
class PromoteConnectorProjectTask extends AbstractConnectorProjectTask {

    PromoteConnectorProjectTask() {
        super(
                ConnectorTemplatesPlugin.PROMOTE_CONNECTOR_TASK_NAME,
                'Promotes a Connector to a target environment.'
        )
    }

    @TaskAction
    void taskAction() {

        String mainClass = 'com.domo.connector.client.cli.ConnectorPromote'

        int i = promoteEnvironment()
        Env env = Env.valueOfInt(i);
        String defaultBundlePath = getBundlePath()
        String bundleName = promptBundlePath(defaultBundlePath)

        logger.debug("Classpath: " + project.configurations.getByName('cliclient').resolve().each { it.getName() })

        project.javaexec {
            classpath = project.configurations.getByName('cliclient')
            main = mainClass
            args = ["--uri=$env.uri", "--apiDataUri=$env.apiDataUri()", "--package=$bundleName"]
        }

    }

    enum Env {

        DEV("http://dev3.maestro.domosoftware.net:8700/", "http://dev3.maestro.domosoftware.net:9700/", 1),
        FRDEV("http://frdev.maestro.domosoftware.net:8700/", "http://frdev.maestro.domosoftware.net:9700/", 2),
        FRPROD1("http://frprod1.maestro.domosoftware.net:8700/", "http://frprod1.maestro.domosoftware.net:9700/", 3),
        DOMOVM("http://dev.local.domo.com:8700/", "http://dev.local.domo.com:9700/", 4)

        private final String uri, apiDataUri
        private final int option

        private Env(String uri, String apiDataUri, int option) {
            this.uri = uri
            this.apiDataUri = apiDataUri
            this.option = option
        }

        String getUri() {
            uri
        }

        String getApiDataUri() {
            apiDataUri
        }

        static Env valueOfInt(int value) {
            for (Env env : values()) {
                if (env.option == value) {
                    return env
                }
            }
            throw new IllegalArgumentException("Unknown Env for value: " + String.valueOf(value))
        }

    }

    private String getBundlePath() {
        Set<Task> bundleTasks = project.getTasksByName('bundle', false);
        Task bundleTask = bundleTasks.iterator().next(); // expecting only 1 bundle task
        bundleTask.property("archivePath")
    }


}
