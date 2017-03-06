# Experimental Gradle plugin for PIT Mutation Testing in Android projects
This is a fork of [gradle-pitest-plugin](https://github.com/szpak/gradle-pitest-plugin)
which supports Android gradle projects. 

# Applying plugin in `build.gradle`
## With [Gradle plugin portal](https://plugins.gradle.org/plugin/pl.droidsonroids.pitest)

```groovy
plugins {
  id "pl.droidsonroids.pitest" version "0.0.5"
}
```

## With Maven central repository
```groovy
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'pl.droidsonroids.gradle:gradle-pitest-plugin:0.0.5'
  }
}

apply plugin: 'com.android.application'
//or apply plugin: 'com.android.library'
//or apply plugin: 'com.android.test'

apply plugin: 'pl.droidsonroids.pitest'
```

#Usage
`pitest<variant>` tasks will be created for each build variant 
(eg. `pitestProDebug` for `pro` product flavor and `debug` build type).
Additionally `pitest` task will run tasks for all variants.

After the measurements a report created by PIT will be placed in `${PROJECT_DIR}/build/reports/pitest/<variant>` directory.

`pitest` configuration extension contains additionally `androidRuntimeDependency` property 
which defaults to `org.robolectric:android-all:7.1.0_r7-robolectric-0` but may be overridden.   

For more information see [README of source project](https://github.com/szpak/gradle-pitest-plugin/blob/master/README.md)
