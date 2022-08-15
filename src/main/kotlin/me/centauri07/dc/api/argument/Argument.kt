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

package me.centauri07.dc.api.argument

import me.centauri07.dc.api.command.option.CommandOption
import me.centauri07.dc.api.exception.CommandArgumentException
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.OptionType.*
import okhttp3.internal.trimSubstring
import kotlin.IllegalArgumentException

/**
 * @author Centauri07
 */
data class Argument (val type: OptionType, val name: String, val value: Any) {

    fun attachment(): Message.Attachment = value as Message.Attachment
    fun string(): String = value.toString()
    fun boolean(): Boolean = value as Boolean
    fun long(): Long = value as Long
    fun int(): Int = value as Int
    fun double(): Double = value as Double
    fun mentionable(): IMentionable = value as IMentionable
    fun member(): Member = value as Member
    fun user(): User = value as User
    fun role(): Role = value as Role
    fun channel(): GuildChannelUnion = value as GuildChannelUnion

    companion object {
        fun from(options: List<OptionMapping>): List<Argument> {

            return options.map { Argument(it.type, it.name, when (it.type) {
                UNKNOWN -> IllegalArgumentException("Unknown type")
                SUB_COMMAND -> IllegalArgumentException("Argument cannot be a sub command")
                SUB_COMMAND_GROUP -> IllegalArgumentException("Argument cannot be a sub command group")
                STRING -> it.asString
                INTEGER -> it.asLong
                BOOLEAN -> it.asBoolean
                USER -> it.asUser
                CHANNEL -> it.asChannel
                ROLE -> it.asRole
                MENTIONABLE -> it.asMentionable
                NUMBER -> it.asDouble
                ATTACHMENT -> it.asAttachment
            }) }

        }

        fun from(options: List<CommandOption>, arguments: List<String>, guild: Guild): List<Argument> {

            val convertedArguments: MutableList<Argument> = mutableListOf()

            var current = listOf(*arguments.toTypedArray())

            for (i in options.indices) {
                if (current.isEmpty()) throw CommandArgumentException("There are no enough argument(s) for ${options.map(CommandOption::name).joinToString(", ")}")

                val commandOption = options[i]

                var toDrop = 1

                val value: Any = when (commandOption.type) {
                    UNKNOWN -> throw IllegalArgumentException("Unknown type")
                    SUB_COMMAND -> throw IllegalArgumentException("Argument cannot be a sub command")
                    SUB_COMMAND_GROUP -> throw IllegalArgumentException("Argument cannot be a sub command group")
                    STRING -> {
                        if (!current[0].startsWith("\"")) throw CommandArgumentException("Cannot find start of string argument")

                        var currentIndex = 0

                        try {
                            while (!current[currentIndex].endsWith("\"")) {
                                currentIndex += 1

                                if (current.size > currentIndex) throw CommandArgumentException("Cannot find end of string argument")
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            throw CommandArgumentException("Cannot find end of string argument")
                        }

                        toDrop = currentIndex + 1

                        val result = current.subList(0, toDrop).joinToString(" ")

                        result.trimSubstring(1, result.length - 1)
                    }
                    INTEGER -> current[0].toLong()
                    BOOLEAN -> current[0].toBoolean()
                    USER -> TODO()
                    CHANNEL -> TODO()
                    ROLE -> TODO()
                    MENTIONABLE -> TODO()
                    NUMBER -> current[0].toDouble()
                    ATTACHMENT -> throw IllegalArgumentException("Unsupported type")
                }

                current = current.drop(toDrop)

                convertedArguments.add(Argument(commandOption.type, commandOption.name, value))

            }

            return convertedArguments

        }
    }
}