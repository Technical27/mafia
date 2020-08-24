package io.github.technical27.mafia.commands

import kotlinx.coroutines.reactive.*

import io.github.technical27.mafia.commands.Command
import io.github.technical27.mafia.commands.CommandArgs
import io.github.technical27.mafia.GameListener

class StartCommand(val gameListener: GameListener) : Command {
  override fun getName() = "start"

  override fun getDescription() = "starts the game"

  override suspend fun run(args: CommandArgs) {
    val user = args.event.message.getAuthor()
    if (!user.isPresent()) return

    val author = user.get()
    val channel = args.event.message.getChannel().awaitSingle();

    if (args.args.size == 0) {
      val message = channel.createMessage("```Welcome to mafia!, mention 5-9 other people to invite them!```").awaitSingle()
      gameListener.newGame(author, message)
    }
  }
}
