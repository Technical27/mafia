package io.github.technical27.mafia.commands

import io.github.technical27.mafia.commands.Command
import io.github.technical27.mafia.commands.CommandArgs
import io.github.technical27.mafia.GameListener

class StartCommand(val gameListener: GameListener) : Command {
  override fun getName() = "start"

  override fun getDescription() = "starts the game"

  override fun run(args: CommandArgs) {
    val channel = args.event.getChannel();

    if (args.args.size == 0) {
      channel.sendMessage("```Welcome to mafia!, mention 5-9 other people to invite them!```").queue()
      val event = args.event
      gameListener.newGame(event.getAuthor())
    }
  }
}
