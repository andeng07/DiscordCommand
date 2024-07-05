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

        fun parseAll(options: List<OptionMapping>): kotlin.Result<List<Argument>> = kotlin.Result.success(options.map {
            Argument(
                it.type, it.name, when (it.type) {
                    UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> return kotlin.Result.failure(
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
        })

        fun parseAll(
            guild: Guild,
            options: List<CommandOption>,
            arguments: List<String>
        ): kotlin.Result<List<Argument>> {
            val toParse = ArrayDeque(arguments)

            val argumentsParsed = options.map {
                val parser = parsers[it.type]
                    ?: return kotlin.Result.failure(NullPointerException("Argument parser for ${it.type.name} not found."))

                val result = parser.executeParse(guild, it, toParse)

                if (result.result.isFailure) return kotlin.Result.failure(result.result.exceptionOrNull()!!)

                Argument(it.type, it.name, result.result.getOrNull()!!)
            }

            if (toParse.isNotEmpty()) return kotlin.Result.failure(IllegalStateException("Arguments does not match the command options."))

            return kotlin.Result.success(argumentsParsed)
        }
    }

    fun executeParse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Result<T> {

        if (arguments.isEmpty()) Result.failure<T>(
            CommandArgumentException(
                commandOption,
                "There are no more arguments to parse."
            )
        )

        val parseResult = parse(guild, commandOption, arguments) ?: return Result.failure(
            CommandArgumentException(
                commandOption,
                "Fail to parse the argument.",
                arguments.joinToString(" ")
            )
        )

        return Result.success(parseResult, arguments)

    }

    fun parse(guild: Guild, commandOption: CommandOption, arguments: ArrayDeque<String>): T?

    data class Result<T>(
        val result: kotlin.Result<T>,
        val remainingArguments: List<String>
    ) {

        companion object {
            fun <T> success(value: T, remainingArguments: List<String>): Result<T> =
                Result(kotlin.Result.success(value), remainingArguments)

            fun <T> failure(exception: Throwable): Result<T> = Result(kotlin.Result.failure(exception), emptyList())
        }

    }

}