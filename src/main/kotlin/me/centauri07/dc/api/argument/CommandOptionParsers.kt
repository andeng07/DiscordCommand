package me.centauri07.dc.api.argument

import me.centauri07.dc.api.command.option.CommandOption
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel

object StringArgumentParser : ArgumentParser<String> {

    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): String? {
        if (!arguments[0].startsWith("\"")) {
            return arguments.removeFirst()
        }

        val stringBuilder = StringBuilder(arguments.removeFirst())

        while (!arguments[0].endsWith("\"")) {
            arguments.removeFirstOrNull()?.let {
                stringBuilder.append(" $it")
            } ?: return null
        }

        stringBuilder.append(" ${arguments.removeFirst()}")

        return stringBuilder.removeSurrounding("\"").toString()
    }

}

object IntegerArgumentParser : ArgumentParser<Long> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Long? = arguments.removeFirst().toLongOrNull()

}

object NumberArgumentParser : ArgumentParser<Double> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Double? = arguments.removeFirst().toDoubleOrNull()
}

object BooleanArgumentParser : ArgumentParser<Boolean> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Boolean? = arguments.removeFirst().lowercase().toBooleanStrictOrNull()

}

object DiscordUserArgumentParser : ArgumentParser<User> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): User? {
        val toParse = arguments.removeFirst()

        val userId: Long = try {
            if (toParse.startsWith("<@") && toParse.endsWith(">")) {
                toParse.substring(2, toParse.length - 1).toLong()
            } else toParse.toLong()
        } catch (e: NumberFormatException) {
            return null
        }

        return guild.getMemberById(userId)?.user
    }

}

object DiscordRoleArgumentParser : ArgumentParser<Role> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Role? {
        val toParse = arguments.removeFirst()

        val roleId: Long = try {
            if (toParse.startsWith("<@&") && toParse.endsWith(">")) {
                toParse.substring(2, toParse.length - 1).toLong()
            } else toParse.toLong()
        } catch (e: NumberFormatException) {
            return null
        }

        return guild.getRoleById(roleId)
    }

}

object DiscordChannelArgumentParser : ArgumentParser<Channel> {
    override fun parse(
        guild: Guild,
        commandOption: CommandOption,
        arguments: ArrayDeque<String>
    ): Channel? {
        val toParse = arguments.removeFirst()

        val roleId: Long = try {
            if (toParse.startsWith("<#") && toParse.endsWith(">")) {
                toParse.substring(2, toParse.length - 1).toLong()
            } else toParse.toLong()
        } catch (e: NumberFormatException) {
            return null
        }

        return guild.getGuildChannelById(roleId)
    }

}