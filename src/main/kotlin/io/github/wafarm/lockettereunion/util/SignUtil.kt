package io.github.wafarm.lockettereunion.util

import io.github.wafarm.lockettereunion.core.DataKey
import io.github.wafarm.lockettereunion.core.PlayerData
import org.bukkit.DyeColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.block.data.Directional
import org.bukkit.block.sign.Side
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

object SignUtil {
    fun setSign(block: Block, blockFace: BlockFace, owner: Player): Block {
        val item = owner.inventory.itemInMainHand
        val newSign = block.getRelative(blockFace)
        val material = Material.getMaterial(item.type.name.replace("_SIGN", "_WALL_SIGN"))
        if (material != null && Tag.WALL_SIGNS.isTagged(material)) {
            newSign.type = material
        } else {
            newSign.type = Material.OAK_WALL_SIGN
        }

        val data = newSign.blockData
        if (data is Directional) {
            data.facing = blockFace
            newSign.setBlockData(data, true)
            newSign.state.update()
        }

        val sign = newSign.state as Sign
        if (newSign.type == Material.DARK_OAK_WALL_SIGN || newSign.type == Material.CRIMSON_WALL_SIGN) {
            sign.getSide(Side.FRONT).color = DyeColor.WHITE
        }

        with(sign.persistentDataContainer) {
            set(DataKey.IS_SIGN_LOCK, PersistentDataType.BOOLEAN, true)
            set(DataKey.SIGN_OWNER, PersistentDataType.STRING, owner.uniqueId.toString())
        }

        with(sign.getSide(Side.FRONT)) {
            setLine(0, "[PRIVATE]")
            setLine(1, owner.name)
        }
        sign.update()

        return newSign
    }

    fun updateSign(block: Block, players: List<PlayerData>) {
        if (players.size > 3) throw IllegalArgumentException("Invalid argument")
        val sign = block.state as Sign
        with(sign.getSide(Side.FRONT)) {
            var i = 1
            players.forEach {
                setLine(i, it.name)
                i += 1
            }
            while (i <= 3) setLine(i++, "")
        }
        sign.update()
    }

    fun getBlockBehindSign(block: Block): Block {
        val state = block.state as Sign
        val data = state.blockData as Directional
        return block.getRelative(data.facing.oppositeFace)
    }

    fun decreaseSignAmount(player: Player) {
        if (player.gameMode == GameMode.CREATIVE) return
        player.inventory.itemInMainHand.amount = player.inventory.itemInMainHand.amount.dec()
    }
}
