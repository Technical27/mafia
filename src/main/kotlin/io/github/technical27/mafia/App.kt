package io.github.technical27.mafia

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import io.github.cdimascio.dotenv.dotenv
import io.github.technical27.mafia.commands.*
import io.github.technical27.mafia.Defaults

typealias CommandMap = HashMap<String, Command>

class BotListener(val prefix: String, val commands: CommandMap) : ListenerAdapter() {
  override fun onReady(event: ReadyEvent) {
    println("logged in")
  }

  override fun onMessageReceived(event: MessageReceivedEvent) {
    val msg = event.getMessage()
    val content = msg.getContentDisplay()

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
  val dotenv = dotenv()
  val commands = CommandMap()
  val prefix = Defaults.prefix(dotenv["DISCORD_PREFIX"])

  commands register StartCommand()
  commands register HelpCommand(prefix, commands)

  val jda = JDABuilder
    .createDefault(dotenv["DISCORD_TOKEN"])
    .addEventListeners(BotListener(prefix, commands))
    .build()

  Runtime.getRuntime().addShutdownHook(Thread({ jda.shutdown() }))
}
