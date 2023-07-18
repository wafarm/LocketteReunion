package io.github.wafarm.lockettereunion

import org.bukkit.plugin.java.JavaPlugin

class LocketteReunion : JavaPlugin() {
    private val logger = getLogger()

    override fun onEnable() {
        logger.info("LocketteReunion is loading")
    }

    override fun onDisable() {

    }
}
