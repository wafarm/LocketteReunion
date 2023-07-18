package io.github.wafarm.lockettereunion.listener

import io.github.wafarm.lockettereunion.core.LocketteCore
import io.github.wafarm.lockettereunion.util.SignUtil
import org.bukkit.GameMode
import org.bukkit.Tag
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class PlayerListener : Listener {

    @EventHandler
    fun onPlayerLockChest(event: PlayerInteractEvent) {
        val player = event.player

        if (event.hand != EquipmentSlot.HAND || event.action != Action.RIGHT_CLICK_BLOCK) return
        if (Tag.SIGNS.isTagged(player.inventory.itemInMainHand.type)) {
            if (player.gameMode == GameMode.SPECTATOR) return
            if (player.isSneaking) return

            val blockFace = event.blockFace
            val block = event.clickedBlock!!
            when (blockFace) {
                BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH -> {
                    if (LocketteCore.canLockBlock(block)) {
                        // All check passed, prevent the default behavior of the click
                        event.isCancelled = true
                        if (LocketteCore.isBlockLocked(block)) {
                            player.sendMessage("&6[LocketteReunion] &cBlock is already locked.")
                            return
                        }
                        LocketteCore.lockBlock(block, blockFace, player)
                        SignUtil.decreaseSignAmount(player)
                    }
                }

                else -> Unit
            }
        }
    }

    @EventHandler
    fun onPlayerOpenContainer(event: PlayerInteractEvent) {
        val player = event.player

        if (event.hand != EquipmentSlot.HAND || event.action != Action.RIGHT_CLICK_BLOCK) return
        if (Tag.SIGNS.isTagged(player.inventory.itemInMainHand.type)) {
            // Player is holding a sign, return
            return
        }

        val block = event.clickedBlock!!
        if (!LocketteCore.hasBlockPermission(block, player)) {
            // Prevent player from interacting with it
            event.isCancelled = true
            player.sendMessage("&6[LocketteReunion] &cYou are not allowed to open this.")
        }
    }

    @EventHandler
    fun onPlayerOpenSign(event: PlayerInteractEvent) {
        
    }
}
