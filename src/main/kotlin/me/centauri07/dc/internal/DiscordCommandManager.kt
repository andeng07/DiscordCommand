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

import me.centauri07.dc.api.CommandManager
import me.centauri07.dc.api.argument.Argument
import me.centauri07.dc.api.command.Command
import me.centauri07.dc.api.exception.CommandAlreadyExistException
import me.centauri07.dc.api.exception.CommandArgumentException
import me.centauri07.dc.api.response.Response.Type.*
import me.centauri07.dc.util.getUsage
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.*
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.awt.Color

/**
 * @author Centauri07
 */
class DiscordCommandManager(private val jda: JDA, private val prefix: String): CommandManager {

    init {
        jda.addEventListener(this)
    }

    private val commandMap: MutableMap<String, Command> = mutableMapOf()

    override fun getCommand(name: String): Command? = commandMap[name]

    override fun getCommands(): List<Command> = commandMap.values.toList()

    override fun registerCommand(command: Command) {

        if (commandMap.containsKey(command.name)) throw CommandAlreadyExistException("command with name ${command.name} has already been registered.")
        
        when (command.type) {

            MessageReceivedEvent::class.java -> {
                commandMap[command.name] = command
            }

            SlashCommandInteractionEvent::class.java -> {

                jda.guilds.forEach {
                    it.upsertCommand(getCommandData(command)).queue()
                }

                commandMap[command.name] = command
            }

            else -> {
                throw IllegalArgumentException("Command's type ${command.type} is not supported!")
            }
        }

    }

    @SubscribeEvent
    fun onMessageReceived(event: MessageReceivedEvent) {

        // validate if the message is coming from a real member
        if (event.author.isBot) return
        // validate if the message came from the guild or not by checking if the member object exists
        val member = event.member ?: return

        val message = event.message.contentRaw
        // check if the message is starting with the corresponding prefix { @link #prefix }
        if (!message.startsWith(prefix)) return

        val messageIndices = message.split(" ")
        // get the command with the corresponding name
        val command = getCommand(messageIndices[0].drop(1)) ?: return

        // check if the command's type is corresponding to the event
        if (command.type != event::class.java) return

        // the command to be executed
        var currentCommand: Command = command

        var messageIndex = 0

        // getting the command to be executed
        while(currentCommand.subCommands.isNotEmpty()) {

            messageIndex += 1

            try {
                currentCommand = currentCommand.subCommands[messageIndices[messageIndex]] ?: run {
                    event.message.replyEmbeds(
                        EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription("Wrong command usage")
                            .addField("Correct Usage", "```${getUsage(currentCommand, StringBuilder(), 0)}```", false)
                            .build()
                    ).mentionRepliedUser(false).queue()

                    return
                }
            } catch (e: IndexOutOfBoundsException) {
                break
            }

        }

        // send command usage if the command is null or the command's executor is null
        if (currentCommand.executor == null) {

            event.message.replyEmbeds(
                EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("Wrong command usage")
                    .addField("Correct Usage", "```${getUsage(currentCommand, StringBuilder(), 0)}```", false)
                    .build()
            ).queue()

            return

        }

        if (currentCommand.permissions.isNotEmpty()) {
            // check if the member has at least one of the required permissions to execute the command
            if (!member.permissions.groupingBy { currentCommand.permissions }.eachCount().any { it.value > 1 }) return
        }

        // parse the argument and reply with error if encountered an exception
        val arguments = try {
            Argument.from(currentCommand.commandOptions, messageIndices.drop(messageIndex + 1), event.guild)
        } catch (e: CommandArgumentException) {
            event.message.replyEmbeds(
                EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("There has been an error while parsing arguments")
                    .addField("Argument", e.source ?: "none", false)
                    .addField("Error", e.message!!, false)
                    .build()
            ).mentionRepliedUser(false).queue()

            return
        }

        // execute command's executor and get the response
        val response = currentCommand.executor!!.onCommand(member, arguments, event)

        if (response.ephemeral) throw UnsupportedOperationException("Cannot send an ephemeral message")

        // reply using the response
        when (response.type) {
            STRING -> event.message.reply(response.stringResponse!!).mentionRepliedUser(false).queue()
            MESSAGE -> event.message.reply(MessageCreateData.fromMessage(response.messageResponse!!)).mentionRepliedUser(false).queue()
            EMBEDS -> event.message.replyEmbeds(response.embedsResponse!!).mentionRepliedUser(false).queue()
            MODAL, DEFFER -> throw UnsupportedOperationException("Response type is unsupported")
        }

    }

    @SubscribeEvent
    fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {

        // get the command with the corresponding name
        val command = getCommand(event.name) ?: return

        // check if  the command's type is corresponding to the event
        if (command.type != event::class.java) return

        val member = event.member ?: return

        var currentCommand: Command? = command

        // replace the currentCommand with the sub command provided with the user
        if (event.subcommandName != null) {

            if (event.subcommandGroup != null) currentCommand = currentCommand?.subCommands?.get(event.subcommandGroup)

            currentCommand = currentCommand?.subCommands?.get(event.subcommandName)

        }

        // check if the command or the command's executor is not null
        if (currentCommand?.executor == null) return

        if (currentCommand.permissions.isNotEmpty()) {
            // check if the member has at least one of the required permissions to execute the command
            if (!member.permissions.groupingBy { command.permissions }.eachCount().any { it.value > 1 }) return
        }

        // execute the command's executor and get the response
        val response = currentCommand.executor!!.onCommand(member, Argument.from(event.options), event)

        // reply using the response
        when (response.type) {
            STRING -> event.reply(response.stringResponse!!).setEphemeral(response.ephemeral).queue()
            MESSAGE -> event.reply(MessageCreateData.fromMessage(response.messageResponse!!)).setEphemeral(response.ephemeral).queue()
            EMBEDS -> event.replyEmbeds(response.embedsResponse!!).setEphemeral(response.ephemeral).queue()
            MODAL -> event.replyModal(response.modalResponse!!).queue()
            DEFFER -> event.deferReply().queue()
        }

    }


    private fun getCommandData(command: Command): SlashCommandData {

        val slashCommandData = Commands.slash(command.name, command.description)

        if (command.permissions.isNotEmpty()) slashCommandData.defaultPermissions = DefaultMemberPermissions.enabledFor(command.permissions)

        if (command.subCommands.isEmpty()) {
            command.commandOptions.forEach {
                val option = OptionData(
                    it.type, it.name, it.description, it.required
                )

                if (it.choices != null && it.choices.isNotEmpty()) option.addChoices(it.choices)

                slashCommandData.addOptions(option)
            }

            return slashCommandData
        }
        
        for (subCommand in command.subCommands.values) {

            if (subCommand.subCommands.isNotEmpty()) {
                val subCommandGroupData = SubcommandGroupData(subCommand.name, subCommand.description)

                subCommand.subCommands.forEach { (_, command) -> run {

                    val subCommandData = SubcommandData(command.name, command.description)

                    command.commandOptions.forEach {
                        val option = OptionData(
                            it.type, it.name, it.description, it.required
                        )

                        if (it.choices != null && it.choices.isNotEmpty()) option.addChoices(it.choices)

                        subCommandData.addOptions(option)
                    }

                    subCommandGroupData.addSubcommands(subCommandData)

                } }

                slashCommandData.addSubcommandGroups(subCommandGroupData)
            } else {

                val subCommandData = SubcommandData(subCommand.name, subCommand.description)

                subCommand.commandOptions.forEach {
                    val option = OptionData(
                        it.type, it.name, it.description, it.required
                    )

                    if (it.choices != null && it.choices.isNotEmpty()) option.addChoices(it.choices)

                    subCommandData.addOptions(option)
                }

                slashCommandData.addSubcommands(subCommandData)

            }

        }

        return slashCommandData
        
    }

}