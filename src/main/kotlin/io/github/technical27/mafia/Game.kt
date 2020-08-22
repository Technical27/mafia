package io.github.technical27.mafia

import kotlinx.coroutines.reactive.*
import kotlinx.coroutines.*

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.event.domain.message.ReactionAddEvent
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.reaction.ReactionEmoji
import discord4j.common.util.Snowflake

enum class GameState {
  INVITE,
  STARTED
}

class Game(val host: User) {
  val players: HashSet<User> = hashSetOf(host)
  var state = GameState.INVITE

  fun addPlayer(player: User) {
    players.add(player)
  }
}

class GameListener {
  val games: HashMap<User, Game> = HashMap()
  val invitedPlayers: HashMap<User, Game> = HashMap()

  suspend fun message(event: MessageCreateEvent) {
    val message = event.message

    if (!message.author.isPresent()) return
    val author = message.author.get()
    if (author.isBot()) return

    val game = games[author]
    if (game == null || game.state != GameState.INVITE) return

    val mentions = MENTION_REGEX.findAll(message.content)

    if (mentions.count() > 0) {
      val gateway = message.getClient()
      for (mention in mentions) {
        val user = gateway.getUserById(Snowflake.of(mention.groupValues[1])).awaitSingle()
        invitePlayer(user, game)
      }
    }
  }

  suspend fun reaction(event: ReactionAddEvent) {
    val author = event.getUser().awaitSingle()
    val game = invitedPlayers[author]

    if (game == null || author == null) return

    val emoji = event.getEmoji().asUnicodeEmoji()
    if (!emoji.isPresent()) return

    when (emoji.get()) {
      CHECKMARK -> addPlayer(author, game)
      CROSSMARK -> invitedPlayers.remove(author)
    }
  }

  suspend fun invitePlayer(user: User, game: Game) {
    invitedPlayers[user] = game
    val channel = user.getPrivateChannel().awaitSingle()
    val message = channel.createMessage(
      "${game.host.getUsername()} invited you to a game of mafia, react to accept or decline"
    ).awaitSingle()
    message.addReaction(CHECKMARK).and(message.addReaction(CROSSMARK)).awaitFirstOrNull()

    GlobalScope.launch {
      // Actual delay
      // delay(1000L * 60L * 5L)
      delay(10_000L)
      message.delete().awaitFirstOrNull()
      invitedPlayers.remove(user)
    }
  }

  fun newGame(user: User) {
    games[user] = Game(user)
  }

  fun addPlayer(user: User, game: Game) {
    invitedPlayers.remove(user)
    game.addPlayer(user)
  }

  companion object {
    val CHECKMARK = ReactionEmoji.unicode("\u2705")
    val CROSSMARK = ReactionEmoji.unicode("\u274C")
    val MENTION_REGEX = Regex("<@!?(\\d+)>")
  }
}
