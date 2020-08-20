package io.github.technical27.mafia.commands

import io.github.technical27.mafia.commands.Command
import io.github.technical27.mafia.commands.CommandArgs
import io.github.technical27.mafia.CommandMap

class HelpCommand(val prefix: String, val commands: CommandMap) : Command {
  override fun getName() = "help"

  override fun getDescription() = "why do you need help with the help command?"

  private val HELP_START = "```Available Commands:\n"
  private val HELP_END = """
                         use ${prefix}help <command> to get more information about a command
                         all commands are NOT case sensitive```
                         """.trimIndent()

  override fun run(args: CommandArgs) {
    val channel = args.event.getChannel();
    if (args.args.size == 0) {
      channel
        .sendMessage(commands.keys.joinToString("\n", HELP_START, "\n\n$HELP_END", transform = { "- $it" }))
        .queue()
    } else {
      val cmdName = args.args[0].toLowerCase()
      val command = commands[cmdName]

      channel.sendMessage(
        if (command != null) "```$cmdName: ${command.getDescription()}```"
        else "```$cmdName isn't a command```"
      ).queue()
    }
  }
}
