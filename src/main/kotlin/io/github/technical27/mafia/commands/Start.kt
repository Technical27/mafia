package io.github.technical27.mafia.commands

import io.github.technical27.mafia.commands.Command

class StartCommand : Command {
  override fun getName() = "start"
  override fun getDescription() = "starts the game"
}
