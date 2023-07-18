package io.github.wafarm.lockettereunion.core

import io.github.wafarm.lockettereunion.LocketteReunion
import io.github.wafarm.lockettereunion.util.SignUtil
import org.bukkit.block.*
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
            if (state is Chest) {
                val holder = state.inventory.holder
                if (holder is DoubleChest) {
                    val leftChest = holder.leftSide!! as Chest
                    val rightChest = holder.rightSide!! as Chest
                    return leftChest.persistentDataContainer.getOrDefault(
                        DataKey.IS_LOCKED,
                        PersistentDataType.BOOLEAN,
                        false
                    ) || rightChest.persistentDataContainer.getOrDefault(
                        DataKey.IS_LOCKED,
                        PersistentDataType.BOOLEAN,
                        false
                    )
                }
            }
            return state.persistentDataContainer.getOrDefault(DataKey.IS_LOCKED, PersistentDataType.BOOLEAN, false)
        }
        return false
    }

    fun getBlockPlayers(block: Block): List<String> {
        val state = block.state as TileState
        if (state is Chest) {
            val holder = state.inventory.holder
            if (holder is DoubleChest) {
                val leftChest = holder.leftSide!! as Chest
                val rightChest = holder.rightSide!! as Chest
                return if (leftChest.persistentDataContainer.has(DataKey.LOCK_PLAYERS, PersistentDataType.STRING))
                    leftChest.persistentDataContainer.get(DataKey.LOCK_PLAYERS, PersistentDataType.STRING)!!.split(';')
                else
                    rightChest.persistentDataContainer.get(DataKey.LOCK_PLAYERS, PersistentDataType.STRING)!!.split(';')
            }
        }
        return state.persistentDataContainer.get(DataKey.LOCK_PLAYERS, PersistentDataType.STRING)!!.split(';')
    }

    fun setBlockPlayers(block: Block, players: List<String>) {
        val state = block.state as TileState
        val playersString = players.joinToString(";")
        LocketteReunion.logger.info(playersString)
        state.persistentDataContainer.set(DataKey.LOCK_PLAYERS, PersistentDataType.STRING, playersString)
        state.update()
    }

    fun hasBlockPermission(block: Block, player: Player): Boolean {
        if (!canLockBlock(block) || !isBlockLocked(block)) return true
        val playerIds = getBlockPlayers(block)
        return player.uniqueId.toString() in playerIds
    }

    fun removeLock(block: Block) {
        val state = block.state
        if (state is TileState) {
            with(state.persistentDataContainer) {
                remove(DataKey.IS_LOCKED)
                remove(DataKey.LOCK_PLAYERS)
                remove(DataKey.LOCK_OWNER)
            }
            state.update()
        }
    }

    fun isSignALock(block: Block): Boolean {
        val sign = block.state as Sign
        return sign.persistentDataContainer.getOrDefault(DataKey.IS_SIGN_LOCK, PersistentDataType.BOOLEAN, false)
    }

    fun hasSignPermission(block: Block, player: Player): Boolean {
        val sign = block.state as Sign
        val owner = sign.persistentDataContainer.get(DataKey.SIGN_OWNER, PersistentDataType.STRING)!!
        return player.uniqueId.toString() == owner
    }

}
