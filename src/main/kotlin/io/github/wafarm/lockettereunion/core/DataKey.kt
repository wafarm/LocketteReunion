package io.github.wafarm.lockettereunion.core

import io.github.wafarm.lockettereunion.LocketteReunion
import org.bukkit.NamespacedKey

object DataKey {
    val IS_LOCKED = NamespacedKey(LocketteReunion.plugin, "lock")
    val LOCK_OWNER = NamespacedKey(LocketteReunion.plugin, "owner")
    val LOCK_PLAYERS = NamespacedKey(LocketteReunion.plugin, "players")
    val SIGN_OWNER = NamespacedKey(LocketteReunion.plugin, "owner")
    val IS_SIGN_LOCK = NamespacedKey(LocketteReunion.plugin, "is-lock")
}
