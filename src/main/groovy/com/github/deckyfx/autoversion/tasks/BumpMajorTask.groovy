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

import com.github.deckyfx.autoversion.version.Version

/**
 * Performs a major bump on the version represented by a
 * project's version data file.
 * <p>
 * This method modifies the version file data, so the changes
 * made are persistent.
 *
 * @author davidfallah
 * @since v0.5.0
 */
class BumpMajorTask extends AbstractBumpTask {

    BumpMajorTask() {
        super(Version.Category.MAJOR)
        this.description = 'Performs a major bump of the project version.'
    }
}
