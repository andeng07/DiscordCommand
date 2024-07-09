package me.centauri07.dc.api.argument

import me.centauri07.dc.api.command.option.CommandOption
import me.centauri07.dc.api.exception.CommandArgumentException
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.OptionType.*

interface ArgumentParser<T> {

    companion object {
        val parsers: MutableMap<OptionType, ArgumentParser<*>> = mutableMapOf(
            STRING to StringArgumentParser,
            INTEGER to IntegerArgumentParser,
            NUMBER to NumberArgumentParser,
            BOOLEAN to BooleanArgumentParser,
            USER to DiscordUserArgumentParser,
            ROLE to DiscordRoleArgumentParser,
            CHANNEL to DiscordChannelArgumentParser
        )

        // TODO implement validators for OptionMapping

        fun parseAll(options: List<OptionMapping>): Result<List<Argument>> = Result.success(options.map {
            val argument = Argument(
                it.type, it.name, when (it.type) {
                    UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> return Result.failure(
                        IllegalArgumentException(
                            "Type not supported"
                        )
                    )

                    STRING -> it.asString
                    INTEGER -> it.asLong
                    BOOLEAN -> it.asBoolean
                    USER -> it.asUser
                    CHANNEL -> it.asChannel
                    ROLE -> it.asRole
                    MENTIONABLE -> it.asMentionable
                    NUMBER -> it.asDouble
                    ATTACHMENT -> it.asAttachment
                }
            )



            argument
        })

        fun parseAll(
            guild: Guild,
            options: List<CommandOption>,
            arguments: List<String>
        ): Result<List<Argument>> {
            val toParse = ArrayDeque(arguments)

            val argumentsParsed = options.map {
                val parser = parsers[it.type]
                    ?: return Result.failure(
                        CommandArgumentException(
                            it,
                            "Argument parser for ${it.type.name} not found. Please contact the developer."
                        )
                    )

                if (toParse.isEmpty()) return Result.failure(
                    CommandArgumentException(it, "Insufficient arguments provided.")
                )

                val result = parser.executeParse(guild, it, toParse)

                if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)

                result.getOrNull()!!
            }

            if (toParse.isNotEmpty()) return Result.failure(
                CommandArgumentException(
                    options.last(),
                    "Unable to parse the remaining arguments.",
                    toParse.joinToString(" ")
                )
            )

            return Result.success(argumentsParsed)
        }
    }

    fun executeParse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Result<Argument> {

        if (arguments.isEmpty()) Result.failure<T>(
            CommandArgumentException(
                commandOption,
                "There are no more arguments to parse."
            )
        )

        val copy = arguments.toList()

        val parseResult = parse(guild, commandOption, arguments)

        parseResult.exceptionOrNull()?.let { return Result.failure(it) }

        val argument = Argument(commandOption.type, commandOption.name, parseResult.getOrNull()!!)

        commandOption.validators.firstOrNull { it.predicate(argument) }?.let {
            return Result.failure(
                CommandArgumentException(
                    commandOption, it.message, copy.dropLast(arguments.size).joinToString(" ")
                )
            )
        }

        return Result.success(argument)
    }

    fun parse(guild: Guild, commandOption: CommandOption, arguments: ArrayDeque<String>): Result<T>

}