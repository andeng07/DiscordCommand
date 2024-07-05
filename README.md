# Discord Command API

This repository contains a DSL-based API for creating and managing Discord commands using the JDA library. The API supports both message commands and slash commands, with options, permissions, and subcommands.

## Getting Started

To use the Discord Command API, you need to initialize it with a JDA instance and a command prefix. Then, you can define your commands using the provided DSL.

### Initialization

First, initialize the `DiscordCommandManager` with your JDA instance and a command prefix:

```kotlin
/**
 * Entry point for creating and managing Discord commands using JDA.
 *
 * @param jda The JDA instance.
 * @param prefix The prefix for message commands.
 */
val discordCommandApi = DiscordCommandManager(jda, "!")
```

You can also define a callback for incorrect command usage:

```kotlin
/**
 * Callback to handle incorrect command usage. This provides feedback to the user
 * when they do not use a command correctly.
 */
discordCommandApi.onIncorrectUsage = {
    MessageCreateData.fromContent(getUsage(it, StringBuilder(), 0))
}
```

### Creating Commands

#### Message Command

To create a message command, use the `createMessageCommand` function. You can define options and set an executor for the command:

```kotlin
/**
 * Creates a message command named "hello" with a description.
 * This command has two options and an executor to handle the command logic.
 */
discordCommandApi.createMessageCommand("hello", "hello command") {
    option(OptionType.USER, "who", "say hello to who") // Adds a USER type option
    option(OptionType.STRING, "message", "additional message") // Adds a STRING type option

    executor = object : Executor {
        /**
         * Handles the execution of the "hello" command.
         *
         * @param executor The member who executed the command.
         * @param arguments The list of arguments provided by the user.
         * @param event The event that triggered the command.
         * @return A Response object with the result of the command.
         */
        override fun onCommand(executor: Member, arguments: List<Argument>, event: Event): Response {
            val user = arguments[0].value as User
            val message = arguments[1].value as String

            return Response.of("Greetings from ${user.asMention}: $message")
        }
    }
}
```

#### Slash Command

To create a slash command, use the `createSlashCommand` function. You can define permissions, options, and subcommands:

```kotlin
/**
 * Creates a slash command named "greet" with a description.
 * This command has permissions and a subcommand with options and an executor.
 */
discordCommandApi.createSlashCommand("greet", "Send a greeting message!") {

    permissions {
        +Permission.ADMINISTRATOR // Requires ADMINISTRATOR permission to use this command
    }

    subCommand("hello", "Send a hello greeting!") {
        option(OptionType.USER, "user", "target") // Adds a USER type option for the subcommand

        option(OptionType.STRING, "message", "The greeting message.") {
            choices {
                +Command.Choice("test", "test")
                +Command.Choice("test2", "test2")
                +Command.Choice("test3", "test3")
                +Command.Choice("test4", "test4")
            }
        }

        executor = object : Executor {
            /**
             * Handles the execution of the "hello" subcommand.
             *
             * @param executor The member who executed the command.
             * @param arguments The list of arguments provided by the user.
             * @param event The event that triggered the command.
             * @return A Response object with the result of the command.
             */
            override fun onCommand(executor: Member, arguments: List<Argument>, event: Event): Response {
                val user = arguments[0].value as User
                val message = arguments[1].value as String

                return Response.of(
                    EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setTitle("Greetings ${user.name}")
                        .addField("From ${executor.user.name}", message, false)
                        .build()
                )
            }
        }
    }
}
```

### Example

Here's an example of how to set up the Discord Command API:

```kotlin
val discordCommandApi = DiscordCommandManager(
    jda, "!"
) // initialize api

discordCommandApi.onIncorrectUsage = {
    MessageCreateData.fromContent(getUsage(it, StringBuilder(), 0))
} // called when the user inputs the wrong command usage

discordCommandApi.createMessageCommand("hello", "hello command") {
    option(OptionType.USER, "who", "say hello to who")
    option(OptionType.STRING, "message", "additional message")

    executor = object : Executor {
        override fun onCommand(executor: Member, arguments: List<Argument>, event: Event): Response {
            val user = arguments[0].value as User
            val message = arguments[1].value as String

            return Response.of("Greetings from ${user.asMention}: $message")
        }
    }
}

discordCommandApi.createSlashCommand("greet", "Send a greeting message!") {

    permissions {
        +Permission.ADMINISTRATOR
    }

    subCommand("hello", "Send a hello greeting!") {
        option(OptionType.USER, "user", "target")

        option(OptionType.STRING, "message", "The greeting message.") {
            choices {
                +Command.Choice("test", "test")
                +Command.Choice("test2", "test2")
                +Command.Choice("test3", "test3")
                +Command.Choice("test4", "test4")
            }
        }

        executor = object : Executor {
            override fun onCommand(executor: Member, arguments: List<Argument>, event: Event): Response {
                val user = arguments[0].value as User
                val message = arguments[1].value as String

                return Response.of(
                    EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setTitle("Greetings ${user.name}")
                        .addField("From ${executor.user.name}", message, false)
                        .build()
                )
            }
        }
    }
}
```

## Contributions

Contributions are welcome! Please fork this repository and submit a pull request.

## Cloning

You can clone this repository using the following command:

```
git clone https://github.com/andeng07/DiscordCommand.git
```

## Dependency

To use this library in your project, you can add it as a dependency via JitPack:

**Step 1.** Add the JitPack repository to your build file:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

**Step 2.** Add the dependency:

```gradle
dependencies {
    implementation 'com.github.andeng07:DiscordCommand:{version}'
}
```
