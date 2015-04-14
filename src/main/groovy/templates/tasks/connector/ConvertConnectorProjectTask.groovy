package templates.tasks.connector

import org.gradle.api.tasks.TaskAction
import templates.ConnectorTemplatesPlugin

/**
 * Task that creates a new connector project in a specified directory.
 */
class ConvertConnectorProjectTask extends AbstractConnectorProjectTask {

    ConvertConnectorProjectTask() {
        super(
                ConnectorTemplatesPlugin.CONVERT_CONNECTOR_TASK_NAME,
                ConnectorTemplatesPlugin.CONVERT_CONNECTOR_TASK_DESCRIPTION
        )
    }

    @TaskAction
    void taskAction() {

        String projectPath = project.projectDir.path;

        // Move ./src/domo-connector.properties -> ./src/main/resources
        if (new File(srcPath(projectPath), 'domo-connector.properties').exists()) {
            move(srcPath(projectPath) + '/domo-connector.properties', resourcesPath(projectPath))
        }
        // Move ./domo-connector.properties -> ./src/main/resources
        if (new File(projectPath, 'domo-connector.properties').exists()) {
            move(projectPath + '/domo-connector.properties', resourcesPath(projectPath))
        }

        // Move ./conf/connector.json -> ./src/main/resources/connector.json
        if (new File(oldConfPath(projectPath), 'connector.json').exists()) {
            mkDir(confPath(projectPath))
            move(oldConfPath(projectPath), confPath(projectPath))
        }

        // Move ./src/main/resources/UiBundle*.properties -> ./src/main/resources/resources/UiBundle*.properties
        def regex = 'UiBundle.*\\.properties' // .properties
        if (fileTypesExist(resourcesPath(projectPath), regex)) {
            mkDir(resourcesBundlesPath(projectPath))
            moveFileTypesAndDelete(resourcesPath(projectPath), regex, resourcesBundlesPath(projectPath))
        }

        // Move ./icons -> ./src/main/resources
        if (new File(oldIconsPath(projectPath)).exists()) {
            move(oldIconsPath(projectPath), iconsPath(projectPath))
        }

        // Move docs/Notes.txt -> ./README.MD
        if (new File(oldDocsPath(projectPath), 'Notes.txt').exists()) {
            moveAndDeleteParent(oldDocsPath(projectPath) + '/Notes.txt', "$projectPath/README.MD")
        }

        // Delete ./lib
        delete(new File(oldLibPath(projectPath)))

        // Move testingFiles/ -> src/test/resources"
        if (new File(oldTestingFilesPath(projectPath)).exists()) {
            move(oldTestingFilesPath(projectPath), testingFilesPath(projectPath))
        }

        // Delete ./zipfile
        delete(new File(oldZipFilePath(projectPath)))

        // Delete eclipse IDE files
        delete(new File(projectPath, '.classpath'))
        delete(new File(projectPath, '.project'))

        // Delete Ant build files
        delete(new File(projectPath, 'ivy.xml'))
        delete(new File(projectPath, 'ivysettings.xml'))
        delete(new File(projectPath, 'updateProject.xml'))
        delete(new File(projectPath, 'build.xml'))
        delete(new File(projectPath, 'ant.properties'))

        // Delete misc output files (need to write these to the ./build dir)
        delete(new File(projectPath, 'output.log'))
        delete(new File(projectPath, 'promote-results.txt'))
        delete(new File(projectPath, 'update-results.txt'))

        // Move test container files to ./src/test/resources
        move("$projectPath/Test.properties", testResourcesPath(projectPath))
        move("$projectPath/launch-connector.xml", testResourcesPath(projectPath))
        move("$projectPath/launch-container.xml", testResourcesPath(projectPath))
        move("$projectPath/shutdown-container.xml", testResourcesPath(projectPath))
        move("$projectPath/run-in-harness.sh", testResourcesPath(projectPath))

        // Delete ./csvOutputFiles (in future, should write these to ./build dir)
        if (new File(oldCsvOutputFilesPath(projectPath)).exists()) {
            delete(new File(oldCsvOutputFilesPath(projectPath)))
        }

        // Read *Ivy.xml to pull dependencies and write to dependencies.gradle or temp file.
        def ivyRegex = ".*Ivy\\.xml"
        if (fileTypesExist(projectPath, ivyRegex)) {
            File ivyXmlFile = getFirstFileMatch(projectPath, ivyRegex)
            def ivyModule = new XmlSlurper().parse(ivyXmlFile)
            def file = new File(projectPath, "dependencies.gradle")
            def writer = new PrintWriter(file)
            writer.println("// Copy these dependencies to your project's build.gradle dependencies section and delete this file.")
            ivyModule.dependencies.dependency.each {
                def configuration = "${it.@org}".contains('junit') ? 'testCompile' : 'compile'
                writer.println("${configuration} \'${it.@org}:${it.@name}:${it.@rev}\'")
            }
            writer.close()
            delete(ivyXmlFile);
        }

        // Delete ./*.pom file
        def pomRegex = ".*\\.pom"
        if (fileTypesExist(projectPath, pomRegex)) {
            File file = getFirstFileMatch(projectPath, pomRegex)
            delete(file)
        }

    }

    def File getFirstFileMatch(String path, String regex) {
        List<File> ivyXmlFiles = new File(path).listFiles(new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                return name.matches(regex)
            }
        })
        ivyXmlFiles.iterator().next();
    }

    def moveFileTypesAndDelete(String from, String regex, String to) {
        List<File> filesToMove = new File(from).listFiles(new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                return name.matches(regex)
            }
        })
        for (File file: filesToMove) {
            move(file.getPath(), to)
        }
    }

    boolean fileTypesExist(String path, String regex) {
        File file = new File(path)
        if (file.isDirectory()) {
            for (File c : file.listFiles(new FilenameFilter() {
                @Override
                boolean accept(File dir, String name) {
                    return name.matches(regex)
                }
            })) {
                return true;
            }
        }
        false;
    }

    protected String oldConfPath(String projectPath) {
        return "$projectPath/conf"
    }

    protected String testingFilesPath(String projectPath) {
        return testResourcesPath(projectPath) + '/testingFiles'
    }

    protected String oldCsvOutputFilesPath(String projectPath) {
        return "$projectPath/csvOutputFiles"
    }

    protected String oldDocsPath(String projectPath) {
        return "$projectPath/docs"
    }

    protected String oldLibPath(String projectPath) {
        return "$projectPath/lib"
    }

    protected String oldIconsPath(String projectPath) {
        return "$projectPath/icons"
    }

    protected String oldTestingFilesPath(String projectPath) {
        return "$projectPath/testingFiles"
    }

    protected String oldZipFilePath(String projectPath) {
        return "$projectPath/zipfile"
    }

    protected void mkDir(String directory) {
        logger.info("Creating directory $directory")
        new File(directory).mkdir()
    }

    protected void moveAndDeleteParent(String from, String to) {
        move(from, to)
        delete(new File(from).getParentFile())
    }

    protected void move(String from, String to) {
        if (new File(from).isFile() && new File(to).isDirectory()) {
            logger.debug("Moving from is a file: $from and to is a directory: $to")
            to = new File(to).getPath() + '/' + new File(from).getName()
            logger.debug("Moving setting to: $to")
        }
        logger.info("Moving $from -> $to")
        new File(from).renameTo(to)
        deleteQuietly(new File(from))
    }

    protected void deleteQuietly(File file) {
        delete(file, true)
    }

    protected void delete(File file) {
        delete(file, false)
    }

    protected void delete(File file, boolean quietly) {
        if (!file.isDirectory() || file.listFiles().length == 0) {
            if (!quietly) logger.info("Deleting $file")
            file.delete()
        } else {
            file.listFiles().each {
                delete(it)
            }
            if (!quietly) logger.info("Deleting $file")
            file.delete()
        }
    }
}