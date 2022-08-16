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

package me.centauri07.dc.api.response

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.Modal

/**
 * @author Centauri07
 */
class Response private constructor(
    val type: Type,
    val stringResponse: String? = null,
    val messageResponse: Message? = null,
    val embedsResponse: List<MessageEmbed>? = null,
    val modalResponse: Modal? = null,
    var ephemeral: Boolean = false
) {

    enum class Type {
        STRING, MESSAGE, EMBEDS, MODAL, DEFFER
    }

    companion object {

        fun of(string: String): Response = Response(Type.STRING, stringResponse = string)
        fun of(message: Message): Response = Response(Type.MESSAGE, messageResponse = message)
        fun of (embeds: List<MessageEmbed>): Response = Response(Type.EMBEDS, embedsResponse = embeds)
        fun of(vararg embeds: MessageEmbed): Response = of(embeds.toList())
        fun of(modal: Modal): Response = Response(Type.MODAL, modalResponse = modal)
        fun of(): Response = Response(Type.DEFFER)

    }

    fun setEphemeral(): Response {
        ephemeral = true

        return this
    }

}