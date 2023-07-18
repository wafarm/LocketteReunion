package io.github.wafarm.lockettereunion.listener

import io.github.wafarm.lockettereunion.LocketteReunion
import io.github.wafarm.lockettereunion.core.LocketteCore
import io.github.wafarm.lockettereunion.sendMessageAlternate
import io.github.wafarm.lockettereunion.util.SignUtil
import org.bukkit.GameMode
import org.bukkit.Tag
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.metadata.FixedMetadataValue

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
                            player.sendMessageAlternate("&6[LocketteReunion] &cBlock is already locked.")
                            return
                        }
                        LocketteCore.lockBlock(block, blockFace, player)
                        SignUtil.decreaseSignAmount(player)
                        player.sendMessageAlternate("&6[LocketteReunion] &aAdded lock.")
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
            player.sendMessageAlternate("&6[LocketteReunion] &cYou are not allowed to open this.")
        }
    }

    @EventHandler
    fun onPlayerInteractSign(event: PlayerInteractEvent) {
        val player = event.player
        if (event.hand != EquipmentSlot.HAND || event.action != Action.RIGHT_CLICK_BLOCK) return

        val block = event.clickedBlock!!
        if (Tag.SIGNS.isTagged(block.type)) {
            if (LocketteCore.isSignALock(block)) {
                event.isCancelled = true
                if (LocketteCore.hasSignPermission(block, player)) {
                    player.setMetadata("selected-sign", FixedMetadataValue(LocketteReunion.plugin, block.location))
                    player.sendMessageAlternate("&6[LocketteReunion] &aSign selected.")
                } else {
                    player.sendMessageAlternate("&6[LocketteReunion] &cYou are not allowed to select this.")
                }
            }
        }
    }

    @EventHandler
    fun onAttemptBreakSign(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val player = event.player
        val block = event.block
        if (!Tag.SIGNS.isTagged(block.type)) return
        if (LocketteCore.isSignALock(block)) {
            if (LocketteCore.hasSignPermission(block, player)) {
                LocketteCore.removeLock(SignUtil.getBlockBehindSign(block))
                player.sendMessageAlternate("&6[LocketteReunion] &aLock successfully removed.")
            } else {
                event.isCancelled = true
                player.sendMessageAlternate("&6[LocketteReunion] &cYou are not allowed to break this.")
            }
        }
    }

    @EventHandler
    fun onAttemptBreakLockedBlock(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val player = event.player
        val block = event.block
        if (LocketteCore.isBlockLocked(block)) {
            event.isCancelled = true
            player.sendMessageAlternate("&6[LocketteReunion] &cBlock is locked.")
        }
    }
}
