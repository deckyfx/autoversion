package com.github.deckyfx.autoversion

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.regex.Matcher
import java.util.regex.Pattern

class IncrementVersionName extends DefaultTask {
    String group                    = AutoVersion.GROUP_NAME
    String description              = "Increase Version name number declared in project Manifest"

    @TaskAction
    def incrementVersionName() {
        println(":incrementVersionName - Incrementing Version Name...")
        File manifestFile                   = new File("${project.projectDir}/src/main/AndroidManifest.xml")
        manifestFile.createNewFile()
        File propertiesFile                 = new File("${project.projectDir}/gradle.properties")
        propertiesFile.createNewFile()
        Pattern patternVersionNumber        = Pattern.compile("versionName=\"?(\\d+)\\.(\\d+)\\.(\\d+)\"?")
        String manifestText                 = manifestFile.getText()
        String propertiesText               = propertiesFile.getText()
        Matcher manifestMatcherVersionNumber = patternVersionNumber.matcher(manifestText)
        Matcher propertiesMatcherVersionNumber = patternVersionNumber.matcher(propertiesText)
        int majorVersion = 0
        int minorVersion = 0
        int pointVersion = 0
        if (propertiesMatcherVersionNumber.find()) {
            majorVersion = Integer.parseInt(propertiesMatcherVersionNumber.group(1))
            minorVersion = Integer.parseInt(propertiesMatcherVersionNumber.group(2))
            pointVersion = Integer.parseInt(propertiesMatcherVersionNumber.group(3))
        }
        String mVersionName                 = majorVersion + "." + minorVersion + "." + pointVersion
        String mNextVersionName             = majorVersion + "." + minorVersion + "." + (pointVersion + 1)
        String manifestContent              = manifestMatcherVersionNumber.replaceAll("versionName=\"" + mNextVersionName + "\"")
        String propertiesContent            = ''
        if (propertiesMatcherVersionNumber.find()) {
            propertiesContent = propertiesMatcherVersionNumber.replaceAll("versionName=" + mNextVersionName)
            propertiesFile.write(propertiesContent)
        }
        println(":incrementVersionName - current versionName=" + mVersionName);
        println(":incrementVersionName - new versionName=" + mNextVersionName);
        manifestFile.write(manifestContent)
    }
}
