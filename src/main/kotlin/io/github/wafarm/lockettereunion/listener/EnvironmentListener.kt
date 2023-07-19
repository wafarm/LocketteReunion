package io.github.wafarm.lockettereunion.listener

import io.github.wafarm.lockettereunion.core.LocketteCore
import org.bukkit.block.Hopper
import org.bukkit.block.TileState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.PlayerInventory

class EnvironmentListener : Listener {
    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        val it = event.blockList().iterator()
        while (it.hasNext()) {
            val block = it.next()
            if (LocketteCore.isProtectedBlock(block)) it.remove()
        }
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        val it = event.blockList().iterator()
        while (it.hasNext()) {
            val block = it.next()
            if (LocketteCore.isProtectedBlock(block)) it.remove()
        }
    }

    @EventHandler
    fun onPistonExtend(event: BlockPistonExtendEvent) {
        event.blocks.forEach {
            if (LocketteCore.isProtectedBlock(it)) {
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onPistonRetract(event: BlockPistonRetractEvent) {
        event.blocks.forEach {
            if (LocketteCore.isProtectedBlock(it)) {
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onHopperMoveItem(event: InventoryMoveItemEvent) {
        if (event.destination is PlayerInventory || event.source is PlayerInventory) return

        val check = if (event.source.holder is Hopper) {
            event.destination.holder
        } else if (event.destination.holder is Hopper) {
            event.source.holder
        } else null

        if (check == null) return

        if (check is TileState && LocketteCore.isProtectedBlock(check.block)) {
            event.isCancelled = true
        }
    }
}
