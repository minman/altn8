/*
 * Copyright 2012 The AltN8-Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package altn8.ui;

import altn8.AlternateConfiguration;

/**
 * To share data between UI and AlternateConfiguration
 */
interface DataInterface {
    /**
     * Pull data from {@link altn8.AlternateConfiguration}.
     *
     * @see #pushDataTo(altn8.AlternateConfiguration)
     */
    void pullDataFrom(AlternateConfiguration configuration);

    /**
     * Push data to {@link AlternateConfiguration}.
     *
     * @see #pullDataFrom(AlternateConfiguration)
     */
    public void pushDataTo(AlternateConfiguration configuration);

    /**
     * @return true if modified
     */
    public boolean isModified(AlternateConfiguration configuration);
}
