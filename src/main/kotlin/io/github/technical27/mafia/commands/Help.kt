package io.github.technical27.mafia.commands

import kotlinx.coroutines.reactive.*

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

  override suspend fun run(args: CommandArgs) {
    val channel = args.event.message.channel.awaitSingle()
    if (args.args.size == 0) {
      channel
        .createMessage(commands.keys.joinToString("\n", HELP_START, "\n\n$HELP_END", transform = { "- $it" }))
        .awaitSingle()
    } else {
      val cmdName = args.args[0].toLowerCase()
      val command = commands[cmdName]

      channel.createMessage(
        if (command != null) "```$cmdName: ${command.getDescription()}```"
        else "```$cmdName isn't a command```"
      ).awaitSingle()
    }
  }
}
