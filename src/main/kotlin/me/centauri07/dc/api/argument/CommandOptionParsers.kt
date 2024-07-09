package me.centauri07.dc.api.argument

import me.centauri07.dc.api.command.option.CommandOption
import me.centauri07.dc.api.exception.CommandArgumentException
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel

object StringArgumentParser : ArgumentParser<String> {

    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Result<String> {
        if (!arguments[0].startsWith("\""))
            return Result.success(arguments.removeFirst())

        if (arguments[0].length > 1 && arguments[0].endsWith("\""))
            return Result.success(arguments.removeFirst().removeSurrounding("\""))

        val stringBuilder = StringBuilder(arguments.removeFirst())

        while (!arguments[0].endsWith("\"")) {
            arguments.removeFirstOrNull()?.let {
                stringBuilder.append(" $it")
            } ?: return Result.failure(
                CommandArgumentException(
                    commandOption,
                    "Provided argument does not have an ending quotation mark.",
                    stringBuilder.toString()
                )
            )
        }

        stringBuilder.append(" ${arguments.removeFirst()}")

        return Result.success(stringBuilder.removeSurrounding("\"").toString())
    }

}

object IntegerArgumentParser : ArgumentParser<Long> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Result<Long> {
        val argument = arguments.removeFirst()

        return argument.toLongOrNull()?.let { Result.success(it) } ?: Result.failure(
            CommandArgumentException(commandOption, "Provided argument is not a valid integer.", argument)
        )
    }

}

object NumberArgumentParser : ArgumentParser<Double> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Result<Double> {
        val argument = arguments.removeFirst()

        return argument.toDoubleOrNull()?.let { Result.success(it) } ?: Result.failure(
            CommandArgumentException(commandOption, "Provided argument is not a valid number.", argument)
        )
    }
}

object BooleanArgumentParser : ArgumentParser<Boolean> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Result<Boolean> {

        val argument = arguments.removeFirst()

        return arguments.removeFirst().lowercase().toBooleanStrictOrNull()?.let { Result.success(it) }
            ?: Result.failure(
                CommandArgumentException(
                    commandOption,
                    "Provided argument is not a valid boolean. Expected 'true' or 'false'.",
                    argument
                )
            )
    }

}

object DiscordUserArgumentParser : ArgumentParser<User> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Result<User> {
        val toParse = arguments.removeFirst()

        val userId: Long = try {
            if (toParse.startsWith("<@") && toParse.endsWith(">")) {
                toParse.substring(2, toParse.length - 1).toLong()
            } else toParse.toLong()
        } catch (e: NumberFormatException) {
            return Result.failure(
                CommandArgumentException(
                    commandOption,
                    "Provided argument is not a valid user ID. Expected either a user " +
                            "mention in the format '<@123456789>' or a numeric user ID.",
                    toParse
                )
            )
        }

        return guild.getMemberById(userId)?.user?.let { Result.success(it) } ?: Result.failure(
            CommandArgumentException(
                commandOption,
                "No member found with the provided user ID",
                toParse
            )
        )
    }

}

object DiscordRoleArgumentParser : ArgumentParser<Role> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Result<Role> {
        val toParse = arguments.removeFirst()

        val roleId: Long = try {
            if (toParse.startsWith("<@&") && toParse.endsWith(">")) {
                toParse.substring(3, toParse.length - 1).toLong()
            } else toParse.toLong()
        } catch (e: NumberFormatException) {
            return Result.failure(
                CommandArgumentException(
                    commandOption,
                    "Provided argument is not a valid role ID. Expected either a role " +
                            "mention in the format '<@&123456789>' or a numeric role ID.",
                    toParse
                )
            )
        }

        return guild.getRoleById(roleId)?.let { Result.success(it) } ?: Result.failure(
            CommandArgumentException(
                commandOption,
                "No role found with the provided role ID",
                toParse
            )
        )
    }

}

object DiscordChannelArgumentParser : ArgumentParser<Channel> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Result<Channel> {
        val toParse = arguments.removeFirst()

        val roleId: Long = try {
            if (toParse.startsWith("<#") && toParse.endsWith(">")) {
                toParse.substring(2, toParse.length - 1).toLong()
            } else toParse.toLong()
        } catch (e: NumberFormatException) {
            return Result.failure(
                CommandArgumentException(
                    commandOption,
                    "Provided argument is not a valid channel ID. Expected either a channel " +
                            "mention in the format '<#123456789>' or a numeric channel ID.",
                    toParse
                )
            )
        }

        return guild.getGuildChannelById(roleId)?.let { Result.success(it) } ?: Result.failure(
            CommandArgumentException(
                commandOption,
                "No channel found with the provided channel ID",
                toParse
            )
        )
    }

}