/*
 * Copyright 2014-2015 David Fallah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.deckyfx.autoversion.tasks

import com.github.deckyfx.autoversion.AutoVersion
import com.github.deckyfx.autoversion.version.BaseVersion
import com.github.deckyfx.autoversion.version.Version
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Abstract class for tasks that perform bumps on the version data
 * represented by a project's version data file.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class AbstractBumpTask extends DefaultTask {

    private static final TASK_GROUP         = AutoVersion.GROUP_NAME
    private static final TASK_DESCRIPTION   = 'Performs a bump of the project version.'

    private final versionBumpCategory

    /**
     * Constructs an {@code AbstractBumpTask} without specifying any version category to bump the
     * current version by when executed.
     * <p>
     */
    protected AbstractBumpTask() {
        this.group                  = TASK_GROUP
        this.description            = TASK_DESCRIPTION
        this.versionBumpCategory    = null
    }

    /**
     * Constructs an {@code AbstractBumpTask} that will bump the current version by {@code bumpCategory}
     * when executed.
     *
     * @param bumpCategory the category to bump the current project version by
     */
    protected AbstractBumpTask(Version.Category bumpCategory) {
        this.group                      = TASK_GROUP
        this.description                = TASK_DESCRIPTION
        this.versionBumpCategory        = bumpCategory
    }

    @TaskAction
    void start() {
        assert project : 'Null project is illegal'
        assert project.android : 'Must apply plugin after applied android pluggin and declared `android { }`'

        if (project.hasProperty("android")) {
            def versionParser   = BaseVersion.Parser.instance
            def currVersion     = versionParser.parse(getManifestVersion())
            switch (this.versionBumpCategory) {
                case Version.Category.MAJOR:
                    writeVersion(currVersion.incrementMajor())
                break
                case Version.Category.MINOR:
                    writeVersion(currVersion.incrementMinor())
                break
                case Version.Category.PATCH:
                    writeVersion(currVersion.incrementPatch())
                break
            }
        }
    }

    String getManifestVersion() {
        File manifestfile           = project.android.sourceSets.main.manifest.srcFile
        GPathResult manifestxml     = new XmlSlurper().parse(manifestfile)
        String version              = manifestxml."@android:versionName"
        if (!version) {
            version = "0.0.0"
        }
        return version
    }

    void writeVersion(BaseVersion newversion) {
        File manifestFile                   = project.android.sourceSets.main.manifest.srcFile
        Pattern patternVersionName          = Pattern.compile("versionName=\"?(\\d+)\\.(\\d+)\\.(\\d+)\"?")
        Pattern patternVersionCode          = Pattern.compile("versionCode=\"?(\\d+)\"?")
        String manifestText                 = manifestFile.getText()
        Matcher manifestMatcherVersionName  = patternVersionName.matcher(manifestText)
        boolean versionNameFound            = manifestMatcherVersionName.find()
        if (versionNameFound) {
            manifestText                    = manifestMatcherVersionName.replaceFirst("versionName=\"" + newversion.toString() + "\"")
        } else {
            manifestText                    = manifestText.replace("<manifest", "<manifest\nandroid:versionName=\"" + newversion.toString() + "\"")
        }
        Matcher manifestMatcherVersionCode  = patternVersionCode.matcher(manifestText)
        boolean versionCodeFound            = manifestMatcherVersionCode.find()
        if (versionCodeFound) {
            manifestText                    = manifestMatcherVersionCode.replaceFirst("versionCode=\"" + String.valueOf(newversion.getMajor()) + "\"")
        } else {
            manifestText                    = manifestText.replace("<manifest", "<manifest\nandroid:versionCode=\"" + String.valueOf(newversion.getMajor()) + "\"")
        }
        if (!versionNameFound && !versionCodeFound) {
            manifestText                    = manifestText.replace("<manifest", "<manifest\nxmlns:android=\"http://schemas.android.com/apk/res/android\"")
        }
        manifestFile.write(manifestText)

        File propertiesFile                 = new File(project.getProjectDir().toString() + '/gradle.properties')
        propertiesFile.createNewFile()
        Properties propertydata             = new Properties()
        propertydata.load(new FileReader(propertiesFile))
        propertydata.setProperty("versionName", newversion.toString())
        propertydata.setProperty("versionCode", String.valueOf(newversion.getMajor()))
        FileWriter writer                   = new FileWriter(new File(project.getProjectDir().toString() + '/gradle.properties'))
        try {
            propertydata.store(writer, 'Some comment')
            writer.flush()
        } finally {
            writer.close()
        }
    }
}
