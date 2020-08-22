package io.github.technical27.mafia

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.*

import discord4j.core.DiscordClient
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.ReactionAddEvent

import io.github.cdimascio.dotenv.dotenv

import io.github.technical27.mafia.commands.*
import io.github.technical27.mafia.Defaults
import io.github.technical27.mafia.GameListener

typealias CommandMap = HashMap<String, Command>

class CommandParser(val prefix: String, val commands: CommandMap) {
  suspend fun parse(event: MessageCreateEvent) {
    val message = event.message

    if (!message.author.isPresent() || !message.content.startsWith(prefix)) return

    val author = message.author.get()
    if (author.isBot()) return

    val content = message.content
    val args = ArrayList(content.drop(1).split(Regex("\\s+")))
    val cmdName = args.removeAt(0).toLowerCase()

    commands[cmdName]?.run(CommandArgs(event, args))
  }
}

infix fun CommandMap.register(cmd: Command) {
  val name = cmd.getName()
  println("registering command: $name")
  this[name] = cmd
}

fun main() = runBlocking<Unit> {
  println("starting up")
  val dotenv = dotenv()
  val commands = CommandMap()
  val prefix = Defaults.prefix(dotenv["DISCORD_PREFIX"])
  val commandParser = CommandParser(prefix, commands)
  val gameListener = GameListener()

  commands register StartCommand(gameListener)
  commands register HelpCommand(prefix, commands)

  val client = DiscordClient.create(dotenv["DISCORD_TOKEN"]!!)
  val gateway = client.login().awaitSingle()

  Runtime.getRuntime().addShutdownHook(Thread({ gateway.logout().block() }))

  gateway.on(MessageCreateEvent::class.java)
    .asFlow()
    .onEach {
      commandParser.parse(it)
      gameListener.message(it)
    }
    .launchIn(this)

  gateway.on(ReactionAddEvent::class.java)
    .asFlow()
    .onEach { gameListener.reaction(it) }
    .launchIn(this)
}
