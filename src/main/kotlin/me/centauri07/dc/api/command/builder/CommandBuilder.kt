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

package me.centauri07.dc.api.command.builder

import me.centauri07.dc.api.command.Command
import me.centauri07.dc.api.command.option.CommandOption
import me.centauri07.dc.api.executor.Executor
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * @author Centauri07
 */
class CommandBuilder(
    private val name: String,
    private val description: String,
    private val type: Class<*>
) {

    private var parent: Command? = null
    private var executor: Executor? = null
    private var commandOptions: MutableList<CommandOption>? = null
    private var permissions: MutableList<Permission>? = null
    private var subCommands: MutableMap<String, Command>? = null

    companion object {

        fun slash(name: String, description: String): CommandBuilder = CommandBuilder(name, description, SlashCommandInteractionEvent::class.java)
        fun message(name: String, description: String): CommandBuilder = CommandBuilder(name, description, MessageReceivedEvent::class.java)

    }

    fun setExecutor(executor: Executor): CommandBuilder {
        this.executor = executor

        return this
    }

    fun addCommandOption(vararg commandOptions: CommandOption): CommandBuilder {
        if (commandOptions.isEmpty()) throw IllegalArgumentException("Cannot add an empty array.")

        if (subCommands?.isNotEmpty() == true) throw IllegalStateException("Command cannot have options and sub command at the same time.")

        if (this.commandOptions == null) this.commandOptions = mutableListOf()

        this.commandOptions!!.addAll(commandOptions)

        return this
    }


    fun addPermissions(vararg permissions: Permission): CommandBuilder {
        if (permissions.isEmpty()) throw IllegalArgumentException("Cannot add an empty array.")

        if (this.permissions == null) commandOptions = mutableListOf()

        this.permissions!!.addAll(permissions)

        return this
    }

    fun addSubCommands(vararg subCommands: Command): CommandBuilder {
        if (subCommands.isEmpty()) throw IllegalArgumentException("Cannot add an empty array.")

        if (commandOptions?.isNotEmpty() == true) throw IllegalStateException("Command cannot have options and sub command at the same time.")

        if (this.subCommands == null) this.subCommands = mutableMapOf()

        subCommands.forEach {
            if (it.type != type) throw IllegalArgumentException("Subcommand's type must be the same type with its parent command.")

            this.subCommands!![it.name] = it
        }

        return this
    }

    fun build(parent: Command? = null): Command = object: Command {
        override var parent: Command? = this@CommandBuilder.parent
        override val depth: Int = this@CommandBuilder.parent?.depth?.plus(1) ?: 0
        override val name: String = this@CommandBuilder.name
        override val description: String = this@CommandBuilder.description
        override val executor: Executor? = this@CommandBuilder.executor
        override val commandOptions: List<CommandOption> = this@CommandBuilder.commandOptions ?: emptyList()
        override val permissions: List<Permission> = this@CommandBuilder.permissions ?: emptyList()
        override val subCommands: Map<String, Command> = this@CommandBuilder.subCommands ?: emptyMap()
        override val type: Class<*> = this@CommandBuilder.type
    }

}