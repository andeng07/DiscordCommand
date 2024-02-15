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
import net.dv8tion.jda.api.utils.messages.MessageCreateData

/**
 * This interface represents a Command Manager responsible for managing registered commands.
 *
 * @author Centauri07
 */
interface CommandManager {

    var onIncorrectUsage: ((Command) -> MessageCreateData)?

    var onIncorrectArgument: ((String?, String) -> MessageCreateData)?

    /**
     * Retrieves the registered command based on its identifier from the command cache.
     *
     * @param name the name of the command
     * @return the registered command with the corresponding name, or null if not found
     */
    fun getCommand(name: String): Command?

    /**
     * Registers a command to the command cache.
     *
     * @param command the command to be registered
     * @throws CommandAlreadyExistException if the command with the corresponding name is already registered in the command cache
     */
    fun registerCommand(command: Command)

    /**
     * Retrieves a list of registered commands.
     *
     * @return the list of registered commands
     */
    fun getCommands(): List<Command>

}