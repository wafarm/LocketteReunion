package io.github.wafarm.lockettereunion.core

import org.bukkit.entity.Player

data class PlayerData(
    var name: String,
    var uuid: String = ""
) {
    override fun toString(): String {
        return "$name,$uuid"
    }

    companion object {
        fun fromString(string: String): PlayerData {
            val split = string.split(',')
            return PlayerData(split[0], split[1])
        }

        fun fromPlayer(player: Player): PlayerData {
            return PlayerData(player.name, player.uniqueId.toString())
        }
    }
}

// The first boolean is if the player exists
// The second boolean is if the list has updated
fun List<PlayerData>.hasPlayerAndUpdate(player: Player): Pair<Boolean, Boolean> {
    val playerUniqueId = player.uniqueId.toString()
    for (playerData in this) {
        if (playerData.uuid == "" && player.name == playerData.name) {
            // Update playerData.uuid to player's uuid
            playerData.uuid = playerUniqueId
            return Pair(true, true)
        }
        if (playerData.uuid == playerUniqueId) {
            if (player.name != playerData.name) {
                // Update playerData.name to player's name
                playerData.name = player.name
                return Pair(true, true)
            }
            return Pair(true, false)
        }
    }
    return Pair(false, false)
}


fun List<PlayerData>.hasPlayerName(playerName: String): Boolean {
    for (playerData in this) {
        if (playerData.name == playerName) return true
    }
    return false
}

operator fun List<PlayerData>.contains(playerId: String): Boolean {
    return hasPlayerName(playerId)
}
