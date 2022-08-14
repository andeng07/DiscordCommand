/*
 *  Copyright 2022 Centauri07
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package me.centauri07.dc.api

import me.centauri07.dc.api.command.Command

/**
 * @author Centauri07
 */
interface DiscordCommandManager {

    /**
     * This function is used to get the registered command via it's identifier from the command cache.
     *
     * @param name the name of the command
     * @return the registered command with the corresponding name.
     */
    fun getCommand(name: String): Command

}