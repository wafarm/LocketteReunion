package io.github.wafarm.lockettereunion.core

import io.github.wafarm.lockettereunion.util.SignUtil
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.TileState
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

object LocketteCore {

    private val lockables = listOf("CHEST", "TRAPPED_CHEST", "FURNACE", "BURNING_FURNACE", "HOPPER")

    fun lockBlock(block: Block, blockFace: BlockFace, owner: Player) {
        val state = block.state
        if (state is TileState) {
            with(state.persistentDataContainer) {
                set(DataKey.IS_LOCKED, PersistentDataType.BOOLEAN, true)
                set(DataKey.LOCK_OWNER, PersistentDataType.STRING, owner.uniqueId.toString())
                set(DataKey.LOCK_PLAYERS, PersistentDataType.STRING, owner.uniqueId.toString())
            }
            state.update()
            SignUtil.setSign(block, blockFace, owner)
        }
    }

    fun canLockBlock(block: Block): Boolean {
        val state = block.state
        val material = block.type
        return state is TileState && material.name in lockables
    }

    fun isBlockLocked(block: Block): Boolean {
        val state = block.state
        if (state is TileState) {
            return state.persistentDataContainer.getOrDefault(DataKey.IS_LOCKED, PersistentDataType.BOOLEAN, false)
        }
        return false
    }

    fun hasBlockPermission(block: Block, player: Player): Boolean {
        if (!isBlockLocked(block) || !canLockBlock(block)) return true
        val state = block.state as TileState
        val data = state.persistentDataContainer.get(DataKey.LOCK_PLAYERS, PersistentDataType.STRING)!!
        val playerIds = data.split(';')
        return player.uniqueId.toString() in playerIds
    }

}
