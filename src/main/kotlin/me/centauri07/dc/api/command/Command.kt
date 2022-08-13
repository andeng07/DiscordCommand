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

/**
 * @author Centauri07
 *
 * This class represents the registered commands to get or weill be executed.
 */
interface Command {

    /**
     * Used to get the registered command object that has been registered.
     * Name must be unique since this is the identifier to get the registered command object.
     * Format should be in lowercase.
     */
    val name: String

    /**
     * A brief explanation of the command's behaviour.
     */
    val description: String

    /**
     * Used to execute the command's behaviour by a pre-defined function when getting called.
     */
    val executor: Executor

    /**
     * The list of command option of the command.
     */
    val commandOptions: MutableList<CommandOption>

}