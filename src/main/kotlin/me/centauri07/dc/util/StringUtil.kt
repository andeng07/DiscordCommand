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

package me.centauri07.dc.util

import me.centauri07.dc.api.command.Command
import java.lang.StringBuilder

/**
 * @author Centauri07
 */
fun getUsage(command: Command, stringBuilder: StringBuilder, indent: Int): String {
    stringBuilder.append("\n" + "  ".repeat(indent) + command.name + " ")

    if (command.commandOptions.isNotEmpty()) {
        stringBuilder.append(command.commandOptions.joinToString(" ") { "${it.name}:${it.type}" })

        return stringBuilder.toString()
    }

    command.subCommands.values.forEach {
        getUsage(it, stringBuilder, indent + 1)
    }

    return stringBuilder.toString()
}