package io.github.technical27.mafia

import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.entities.User

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

class GameListener : ListenerAdapter() {
  val games: HashMap<User, Game> = HashMap()
  val invitedPlayers: HashMap<User, Game> = HashMap()

  override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
    val author = event.getAuthor()
    val guild = event.getGuild()

    if (author.isBot()) return

    val game = games[author]
    if (game == null || game.state != GameState.INVITE) return

    val content = event.getMessage().getContentRaw()
    val mentions = MENTION_REGEX.findAll(content)

    if (mentions.count() > 0) {
      val cache = guild.getMemberCache()
      for (mention in mentions) {
        val member = cache.getElementById(mention.groupValues[1])
        if (member != null) invitePlayer(member.getUser(), game)
      }
    }
  }

  override fun onGenericMessageReaction(event: GenericMessageReactionEvent) {
    val author = event.getUser()
    val game = invitedPlayers[author]

    if (game == null || author == null) return

    when (event.getReactionEmote().getEmoji()) {
      CHECKMARK -> game.addPlayer(author)
      CROSSMARK -> invitedPlayers.remove(author)
    }
  }

  fun invitePlayer(user: User, game: Game) {
    invitedPlayers[user] = game
    user.openPrivateChannel()
      .flatMap({
        it.sendMessage("${user.getName()} invite you to a game of mafia, react to accept or decline")
      })
      .queue({ msg ->
        msg.addReaction(CHECKMARK).flatMap({ msg.addReaction(CROSSMARK) }).queue()
      })
  }

  fun newGame(user: User) {
    games[user] = Game(user)
  }

  companion object {
    val CHECKMARK = "\u2705"
    val CROSSMARK = "\u274C"
    val MENTION_REGEX = Regex("<@!?(\\d+)>")
  }
}
