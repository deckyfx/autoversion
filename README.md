# autoversion

Will raise android patch version everytime you run gradle sync

```
buildscript {
    ext.pluginversion = '1.0.3'

    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "gradle.plugin.com.github.deckyfx:autoversion:$pluginversion"
    }

}

apply plugin: 'com.android.application'
```