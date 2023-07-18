package io.github.wafarm.lockettereunion

import io.github.wafarm.lockettereunion.listener.PlayerListener
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class LocketteReunion : JavaPlugin() {


    override fun onEnable() {
        plugin = this
        LocketteReunion.logger = logger
        server.pluginManager.registerEvents(PlayerListener(), this)
    }

    override fun onDisable() {

    }

    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var logger: Logger
    }
}
