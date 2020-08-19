package io.github.technical27.mafia.commands

import io.github.technical27.mafia.commands.Command
import io.github.technical27.mafia.commands.CommandArgs
import io.github.technical27.mafia.CommandMap

class HelpCommand(val prefix: String, val commands: CommandMap) : Command {
  override fun run(args: CommandArgs) {
    val channel = args.event.getChannel();
    if (args.args.size == 0) {
      channel
        .sendMessage(
          commands.keys
            .joinToString(
              "\n", "```Available Commands:\n",
              "\n\nuse ${prefix}help <command> to get more information about a command\nall commands are NOT case sensitive```",
              transform = { "- $it" }
            )
        ).queue()
    } else {
      val cmdName = args.args[0].toLowerCase()
      val command = commands[cmdName]

      if (command != null) {
        channel.sendMessage("```$cmdName: ${command.getDescription()}```").queue()
      } else {
        channel.sendMessage("```$cmdName isn't a command```").queue()
      }
    }
  }

  override fun getName() = "help"

  override fun getDescription() = "why do you need help with the help command?"
}
