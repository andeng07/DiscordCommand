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

package me.centauri07.dc.api.command

import me.centauri07.dc.api.command.option.CommandOption
import me.centauri07.dc.api.executor.Executor
import net.dv8tion.jda.api.Permission

/**
 * Represents a registered command to be executed.
 *
 * @author Centauri07
 */
interface Command {

    /**
     * Gets the parent command.
     * Returns null if there is no parent.
     */
    val parent: Command?

    /**
     * Gets the depth of the command from the parent.
     * Returns zero if there is no parent.
     */
    val depth: Int

    /**
     * Gets the name of the command.
     */
    val name: String

    /**
     * Provides a brief explanation of the command's behavior.
     */
    val description: String

    /**
     * Gets the executor for the command's behavior.
     * This is the function that will be called to execute the command's behavior.
     */
    val executor: Executor?

    /**
     * Gets the list of command options.
     */
    val commandOptions: List<CommandOption>

    /**
     * Gets the list of permissions required to execute the command.
     */
    val permissions: List<Permission>

    /**
     * Gets the map of subcommands for this command.
     * The key (String) represents the subcommand name,
     * and the value (Command) is the corresponding command object.
     */
    val subCommands: Map<String, Command>

    /**
     * Gets the event class of the command.
     * This determines how the command will be executed.
     */
    val type: Class<*>
}