package io.github.technical27.mafia

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import io.github.cdimascio.dotenv.dotenv
import io.github.technical27.mafia.commands.*
import io.github.technical27.mafia.Defaults
import io.github.technical27.mafia.GameListener

typealias CommandMap = HashMap<String, Command>

class BotListener(val prefix: String, val commands: CommandMap) : ListenerAdapter() {
  override fun onReady(event: ReadyEvent) {
    println("logged in")
  }

  override fun onMessageReceived(event: MessageReceivedEvent) {
    val msg = event.getMessage()
    val content = msg.getContentRaw()

    if (event.getAuthor().isBot() || !content.startsWith(prefix)) return

    val args = ArrayList(content.drop(1).split(Regex("\\s+")))
    val cmdName = args.removeAt(0).toLowerCase()

    val command = commands[cmdName]

    if (command != null) command.run(CommandArgs(event, args))
  }
}

infix fun CommandMap.register(cmd: Command) {
  val name = cmd.getName()
  println("registering command: $name")
  this[name] = cmd
}

fun main() {
  println("starting up")
  val dotenv = dotenv()
  val commands = CommandMap()
  val prefix = Defaults.prefix(dotenv["DISCORD_PREFIX"])
  val gameListener = GameListener()

  commands register StartCommand(gameListener)
  commands register HelpCommand(prefix, commands)

  val jda = JDABuilder
    .createDefault(dotenv["DISCORD_TOKEN"])
    .addEventListeners(BotListener(prefix, commands))
    .addEventListeners(gameListener)
    .addEventListeners()
    .build()

  Runtime.getRuntime().addShutdownHook(Thread({ jda.shutdown() }))
}
