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
import net.dv8tion.jda.api.interactions.commands.OptionType

@DslMarker
@Target(AnnotationTarget.CLASS)
annotation class CommandDsl

/**
 * @author Centauri07
 */
@CommandDsl
class CommandBuilder(
    private val name: String,
    private val description: String,
    private val type: Class<*>
) {

    var executor: Executor? = null
    private var parent: Command? = null
    private var commandOptions: MutableList<CommandOption>? = null
    private var permissions: MutableList<Permission>? = null
    private var subCommands: MutableMap<String, CommandBuilder>? = null

    companion object {

        fun slash(name: String, description: String): CommandBuilder =
            CommandBuilder(name, description, SlashCommandInteractionEvent::class.java)

        fun message(name: String, description: String): CommandBuilder =
            CommandBuilder(name, description, MessageReceivedEvent::class.java)

    }

    fun option(type: OptionType, name: String, description: String, block: CommandOption.Builder.() -> Unit = { }) =
        CommandOption.Builder(type, name, description).apply(block).build()
            .also { commandOptions?.add(it) ?: run { commandOptions = mutableListOf(it) } }

    fun subCommand(name: String, description: String, block: CommandBuilder.() -> Unit) {
        val commandBuilder = CommandBuilder(name, description, type).apply(block)

        (subCommands?.set(name, commandBuilder) ?: run { subCommands = mutableMapOf(name to commandBuilder) }).also {
            println(subCommands?.size)
        }
    }

    fun permissions(block: PermissionBuilder.() -> Unit) {
        PermissionBuilder().apply(block)
    }

    @CommandDsl
    inner class PermissionBuilder {
        operator fun Permission.unaryPlus() {
            this@CommandBuilder.permissions?.add(this) ?: run { this@CommandBuilder.permissions = mutableListOf(this) }
        }
    }

    fun build(parent: Command? = null): Command = object : Command {
        override var parent: Command? = parent
        override val depth: Int = this@CommandBuilder.parent?.depth?.plus(1) ?: 0
        override val name: String = this@CommandBuilder.name
        override val description: String = this@CommandBuilder.description
        override val executor: Executor? = this@CommandBuilder.executor
        override val commandOptions: List<CommandOption> = this@CommandBuilder.commandOptions ?: emptyList()
        override val permissions: List<Permission> = this@CommandBuilder.permissions ?: emptyList()
        override val subCommands: Map<String, Command> =
            this@CommandBuilder.subCommands?.mapValues { (_, command) -> command.build(this) } ?: emptyMap()
        override val type: Class<*> = this@CommandBuilder.type
    }

}