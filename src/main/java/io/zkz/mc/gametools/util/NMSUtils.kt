package io.zkz.mc.gametools.util

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld
import org.bukkit.craftbukkit.v1_20_R2.block.CraftBlock
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity

object NMSUtils {
    fun toNMS(entity: org.bukkit.entity.Entity): Entity {
        return (entity as CraftEntity).handle
    }

    fun toNMS(world: org.bukkit.World): ServerLevel {
        return (world as CraftWorld).handle
    }

    fun toNMS(block: org.bukkit.block.Block): BlockState {
        return (block as CraftBlock).nms
    }

    fun toNMSBlockPos(location: org.bukkit.Location): BlockPos {
        return BlockPos(location.blockX, location.blockY, location.blockZ)
    }

    fun getEntityBoundingBox(entity: org.bukkit.entity.Entity): AABB {
        return toNMS(entity).boundingBox
    }

    @Suppress("DEPRECATION")
    fun getBlockBoundingBox(block: org.bukkit.block.Block): AABB {
        val nmsBlockData: BlockState = toNMS(block)
        val nmsBlock: Block = nmsBlockData.block
        val nmsWorld = toNMS(block.world)
        val nmsBlockPos = toNMSBlockPos(block.location)
        val nmsVoxel: VoxelShape = nmsBlock.getShape(nmsBlockData, nmsWorld, nmsBlockPos, CollisionContext.empty())
        return nmsVoxel.bounds().move(nmsBlockPos)
    }
}