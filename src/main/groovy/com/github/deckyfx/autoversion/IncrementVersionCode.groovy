package com.github.deckyfx.autoversion

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.regex.Matcher
import java.util.regex.Pattern

class IncrementVersionCode extends DefaultTask {
    String group        = AutoVersion.GROUP_NAME
    String description  = "Increase Version code number declared in project Manifest"

    @TaskAction
    def incrementVersionCode() {
        println(":incrementVersionCode - Incrementing Version Code...")
        File manifestFile                   = new File("${project.projectDir}/src/main/AndroidManifest.xml")
        manifestFile.createNewFile()
        File propertiesFile                 = new File("${project.projectDir}/gradle.properties")
        propertiesFile.createNewFile()
        Pattern patternVersionCode          = Pattern.compile("versionCode=\"?(\\d+)\"?")
        String manifestText                 = manifestFile.getText()
        String propertiesText               = propertiesFile.getText()
        Matcher manifestMatcherVersionCode  = patternVersionCode.matcher(manifestText)
        Matcher propertiesMatcherVersionCode = patternVersionCode.matcher(propertiesText)
        int mVersionCode                    = 0
        if (propertiesMatcherVersionCode.find()) {
            mVersionCode                    = Integer.parseInt(propertiesMatcherVersionCode.group(1))
        }
        def mNextVersionCode                = mVersionCode + 1
        String manifestContent              = ''
        String propertiesContent            = ''
        if (propertiesMatcherVersionCode.find()) {
            propertiesContent               = propertiesMatcherVersionCode.replaceAll("versionCode=" + mNextVersionCode)
            propertiesFile.write(propertiesContent)
        }
        println(":incrementVersionCode - current versionCode=" + mVersionCode);
        println(":incrementVersionCode - next versionCode=" + mNextVersionCode);
        manifestFile.write(manifestContent)
    }
}
