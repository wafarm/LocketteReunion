package io.github.wafarm.lockettereunion

import io.github.wafarm.lockettereunion.core.LocketteCore
import io.github.wafarm.lockettereunion.listener.EnvironmentListener
import io.github.wafarm.lockettereunion.listener.PlayerListener
import io.github.wafarm.lockettereunion.util.SignUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.logging.Logger

class LocketteReunion : JavaPlugin() {


    override fun onEnable() {
        plugin = this
        LocketteReunion.logger = logger
        server.pluginManager.registerEvents(PlayerListener(), this)
        server.pluginManager.registerEvents(EnvironmentListener(), this)
        getCommand("lock")?.setTabCompleter { _, _, _, args ->
            if (args.isEmpty()) {
                return@setTabCompleter listOf("help", "add", "remove")
            } else if (args.size == 1) {
                return@setTabCompleter listOf("help", "add", "remove").filter {
                    it.startsWith(args[0])
                }
            }
            return@setTabCompleter null
        }
        getCommand("lock")?.setExecutor { sender, _, _, args ->
            if (sender !is Player) {
                return@setExecutor false
            }
            if (args.isEmpty() || args[0] == "help") {
                return@setExecutor false
            } else if (args.size == 2) {
                if (args[0] != "add" && args[0] != "remove") return@setExecutor false

                val data = sender.getMetadata("selected-sign")
                var blockPosition: Location? = null
                data.forEach {
                    if (it.owningPlugin?.name == plugin.name) {
                        blockPosition = it.value() as Location
                    }
                }
                if (blockPosition == null) {
                    sender.sendMessageAlternate("&6[LocketteReunion] &cYou must select a lock first.")
                    return@setExecutor true
                }

                val sign = blockPosition!!.block
                val playerName = args[1]
                val player = Bukkit.getPlayer(playerName)
                if (player == null || player.name != playerName) {
                    sender.sendMessageAlternate("&6[LocketteReunion] &cYou must enter a valid online player name.")
                    return@setExecutor true
                }
                val playerId = player.uniqueId.toString()
                val block = SignUtil.getBlockBehindSign(sign)
                val playerIds = LocketteCore.getBlockPlayers(block)


                val updatePlayers = { newPlayerIds: List<String> ->
                    LocketteCore.setBlockPlayers(block, newPlayerIds)
                    val players = mutableListOf<String>()
                    newPlayerIds.forEach {
                        val p = Bukkit.getPlayer(UUID.fromString(it))!!
                        players.add(p.name)
                    }
                    SignUtil.updateSign(sign, players)
                }

                if (args[0] == "add") {
                    if (playerId in playerIds) {
                        sender.sendMessageAlternate("&6[LocketteReunion] &cPlayer is already in the lock whitelist.")
                    } else if (playerIds.size == 3) {
                        sender.sendMessageAlternate("&6[LocketteReunion] &cWhitelist is full.")
                    } else {
                        val newPlayerIds = playerIds + playerId
                        updatePlayers(newPlayerIds)
                        sender.sendMessageAlternate("&6[LocketteReunion] &aPlayer is added to the whitelist.")
                    }
                } else {
                    if (playerId !in playerIds) {
                        sender.sendMessageAlternate("&6[LocketteReunion] &cPlayer is not in the lock whitelist.")
                    } else {
                        val newPlayerIds = playerIds - playerId
                        updatePlayers(newPlayerIds)
                        sender.sendMessageAlternate("&6[LocketteReunion] &aPlayer is removed from the whitelist.")
                    }
                }

                return@setExecutor true
            }
            return@setExecutor false
        }
    }

    override fun onDisable() {

    }

    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var logger: Logger
    }
}

fun Player.sendMessageAlternate(message: String) {
    sendMessage(ChatColor.translateAlternateColorCodes('&', message))
}
