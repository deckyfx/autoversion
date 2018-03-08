package com.github.deckyfx.autoversion

import com.github.deckyfx.autoversion.tasks.*
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import java.util.logging.Logger

class AutoVersion implements Plugin<Project> {
    static final String GROUP_NAME                              = "autoversion"
    private static final String UTF_8_ENCODING                  = 'UTF-8'
    private static final String PRINT_VERSION_TASK_NAME         = 'printCurrentVersion'
    private static final String BUMP_MAJOR_TASK_NAME            = 'bumpMajor'
    private static final String BUMP_MINOR_TASK_NAME            = 'bumpMinor'
    private static final String BUMP_PATCH_TASK_NAME            = 'bumpPatch'
    private static final String CHANGE_VERSION_TASK_NAME        = 'changeVersion'
    private static final String MODIFIES_VERSION_INDICATOR_PROPERTY = 'modifiesVersion'

    static String getPrintVersionTaskName() {
        return PRINT_VERSION_TASK_NAME
    }

    static String getBumpMajorTaskName() {
        return BUMP_MAJOR_TASK_NAME
    }

    static String getBumpMinorTaskName() {
        return BUMP_MINOR_TASK_NAME
    }

    static String getBumpPatchTaskName() {
        return BUMP_PATCH_TASK_NAME
    }

    static String getChangeVersionTaskName() {
        return CHANGE_VERSION_TASK_NAME
    }

    static String getModifiesVersionIndicatorProperty() {
        return MODIFIES_VERSION_INDICATOR_PROPERTY
    }

    private Logger logger

    @Override
    void apply(Project project) {
        assert project : 'Null project is illegal'
        assert project.android : 'Must apply plugin after applied android pluggin and declared `android { }`'

        addTasks(project)

        Set<Task> patchBumpTask       = project.getTasksByName(getBumpPatchTaskName(), false)
        patchBumpTask.each {
            task->
                BumpPatchTask t = (BumpPatchTask) task;
                t.execute()
        }

        /*
        * Ensure that at most one task modifies the project version.
        */
        project.gradle.taskGraph.whenReady { taskGraph ->
            def numVersionModifierTasks = taskGraph.allTasks.findAll { task ->
                task.hasProperty(getModifiesVersionIndicatorProperty()) \
                    && task."${getModifiesVersionIndicatorProperty()}"
            }.size()

            if (numVersionModifierTasks > 1) {
                throw new GradleException('Only one task may modify the project version in a single build.')
            }
        }
    }

    private void addTasks(Project project) {
        assert project : 'Null project is illegal'
        assert project.android : 'Must apply plugin after applied android pluggin and declared `android { }`'

        def printVersionTask    = project.task(getPrintVersionTaskName(),   type:PrintVersionTask)
        def changeVersionTask   = project.task(getChangeVersionTaskName(),  type:ChangeVersionTask)
        def majorBumpTask       = project.task(getBumpMajorTaskName(),      type:BumpMajorTask)
        def minorBumpTask       = project.task(getBumpMinorTaskName(),      type:BumpMinorTask)
        def patchBumpTask       = project.task(getBumpPatchTaskName(),      type:BumpPatchTask)
        [
                majorBumpTask,
                minorBumpTask,
                patchBumpTask
        ].each { task ->
            task.ext."$MODIFIES_VERSION_INDICATOR_PROPERTY" = true

            printVersionTask.shouldRunAfter task
            task.dependsOn changeVersionTask
        }
    }
}