# Domo Connector Plugin

## Introduction

The Domo Connector plugin helps you get started using Gradle by providing convenient tasks for creating new Domo Connector projects that work with the Gradle build system.
Eg. To create a new Domo Connector project you can run:

```gradle createDomoConnector```

Which will prompt you for the name of your new project and then create a new directory with it. It will also create a standard directory structure in your
project's directory that works with Gradle's default configurations.

The plugin also makes it easy to create your own templates which can be useful in creating new projects, or creating components within your projects. Eg.
It's easy to create a simple task to generate a new GSP that fits your company's standard layout. Or to create a more complex task to generate a new servlet
and add the entry into your webapp's web.xml file.

## Installation

The recommended way to install this plugin, is to use a gradle init script. To install, follow these steps:

Step 1. Create the gradle init script.
```groovy
touch ~/.gradle/init.d/domo-connector.gradle
```

Step 2. Copy the following contents into the gradle init script created in Step 1.
```groovy
initscript {
    dependencies {
        classpath 'gradle-templates:gradle-templates:1.5-SNAPSHOT'
    }
    repositories {
        maven {
            url 'http://svn.domo.com:8081/nexus/content/repositories/snapshots/'
        }
    }

}

allprojects {
    apply plugin: templates.TemplatesPlugin
}
```

Step 3. Verify installation by running the `gradle tasks` command and ensuring "*DomoConnector" tasks are present.
```
gradle tasks
```

## Usage

Run the `gradle tasks` command to see a list of "create", "convert", and "promote" tasks provided by the default plugin templates.

Running a create task will prompt the user for information and then generate the appropriate content.