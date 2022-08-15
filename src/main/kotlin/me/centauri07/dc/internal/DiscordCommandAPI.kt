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

package me.centauri07.dc.internal

import me.centauri07.dc.api.CommandAPI
import me.centauri07.dc.api.CommandManager
import me.centauri07.dc.api.command.Command
import me.centauri07.dc.api.exception.APIAlreadyInitializedException
import net.dv8tion.jda.api.JDA

/**
 * @author Centauri07
 */
class DiscordCommandAPI: CommandAPI {
    private var commandManager: CommandManager? = null

    override fun initialize(jda: JDA, prefix: String) {
        if (commandManager != null) throw APIAlreadyInitializedException("API has already been initialized")

        commandManager = DiscordCommandManager(jda, prefix)
    }

    override fun registerCommand(command: Command) {
        commandManager!!.registerCommand(command)
    }
}