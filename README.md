# Experimental Gradle plugin for PIT Mutation Testing in Android projects
This is a fork of [gradle-pitest-plugin](https://github.com/szpak/gradle-pitest-plugin)
which supports Android gradle projects.

# Applying plugin in `build.gradle`
## With [Gradle plugin portal](https://plugins.gradle.org/plugin/pl.droidsonroids.pitest)

```groovy
plugins {
  id 'pl.droidsonroids.pitest' version '0.2.5'
}
```

```kotlin
plugins {
  id("pl.droidsonroids.pitest") version "0.2.5"
}
```

## With Maven central repository
```kotlin
buildscript {
  repositories {
    mavenCentral()
    google()
  }
  dependencies {
    classpath "pl.droidsonroids.gradle:gradle-pitest-plugin:0.2.5"
  }
}

plugins {
  id("com.android.application")
  //or id("com.android.library")
  //or id("com.android.test")
  id("pl.droidsonroids.pitest")
}
```

# Usage
`pitest<variant>` tasks will be created for each build variant
(eg. `pitestProDebug` for `pro` product flavor and `debug` build type).
Additionally `pitest` task will run tasks for all variants.

After the measurements a report created by PIT will be placed in `${PROJECT_DIR}/build/reports/pitest/<variant>` directory.

For more information see [README of source project](https://github.com/szpak/gradle-pitest-plugin/blob/master/README.md)

## Robolectric and UnMock
This plugin by default adds mockable Android JAR (generated by Android Gradle Plugin) to classpath
used under pitest tests.

If you are using alternative Android framework in tests, like [Robolectric](http://robolectric.org/) or
[UnMock Gradle Plugin](https://github.com/bjoernQ/unmock-plugin), you may want to add `excludeMockableAndroidJar`
to pitest configuration eg:
```groovy
pitest {
    targetClasses = ['com.myapp.*']
    excludeMockableAndroidJar = true
}
```
In such case default mockable Android JAR won't be added and alternative one will be used under tests.

# <a name="troubleshooting"></a> Troubleshooting
## Tests fail when run under pitest but pass without it
Issue occurs when using [Android API](https://developer.android.com/reference/packages.html)
without mocking it.
Pitest verbose logs may list exceptions like `ExceptionInitializerError`.

The fastest solution is to set `android.testOptions.unitTests.returnDefaultValues = true`.
See [Local unit testing documentation](https://developer.android.com/training/testing/unit-testing/local-unit-tests.html#error-not-mocked)
to see other consequences of this change.
