package io.github.technical27.mafia

class Defaults {
  companion object {
    val DEFAULT_PREFIX = "!"

    fun prefix(p: String?) = if (p != null) p else DEFAULT_PREFIX
  }
}

