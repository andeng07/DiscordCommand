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

package me.centauri07.dc.api.command.option

import me.centauri07.dc.api.argument.Argument
import me.centauri07.dc.api.command.builder.CommandDsl
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType

/**
 * @author Centauri07
 *
 * This class represents an option of a command.
 */
data class CommandOption(
    val type: OptionType,
    val name: String,
    val description: String,
    val required: Boolean,
    val choices: List<Command.Choice>,
    val validators: List<Validator>
) {

    @CommandDsl
    class Builder(val type: OptionType, val name: String, val description: String) {
        var required: Boolean = true
        private val choices: MutableList<Command.Choice> = mutableListOf()
        private val validators: MutableList<Validator> = mutableListOf()

        fun choices(block: ChoiceBuilder.() -> Unit) = ChoiceBuilder().apply(block)

        fun validate(message: String, predicate: Argument.() -> Boolean) = validators.add(Validator(predicate, message))

        fun build(): CommandOption = CommandOption(type, name, description, required, choices, validators)

        @CommandDsl
        inner class ChoiceBuilder {

            operator fun Command.Choice.unaryPlus() = this@Builder.choices.add(this)

        }

    }

    data class Validator(val predicate: Argument.() -> Boolean, val message: String)

}