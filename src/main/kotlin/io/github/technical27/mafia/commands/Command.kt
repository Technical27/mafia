package io.github.technical27.mafia.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

data class CommandArgs(val event: MessageReceivedEvent, val args: ArrayList<String>)

interface Command {
  fun run(args: CommandArgs) {
    println("command: ${getName()} empty placeholder")
  }

  fun getName() = "base"

  fun getDescription() = "no description"
}
