package io.github.technical27.mafia.commands

import discord4j.core.event.domain.message.MessageCreateEvent

data class CommandArgs(val event: MessageCreateEvent, val args: ArrayList<String>)

interface Command {
  suspend fun run(args: CommandArgs) {
    println("command: ${getName()} empty placeholder")
  }

  fun getName() = "base"

  fun getDescription() = "no description"
}
