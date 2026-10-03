package com.snowmonkeys.worldgen;

import com.mojang.serialization.Codec;
import com.snowmonkeys.registry.ModBlocks;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A natural hot spring: an uneven pool of steaming water, lined with onsen stone, with a geothermal vent
 * in the middle of the floor.
 * <p>
 * Every height comes from the generation heightmaps; nothing uses a fixed Y level. That makes it work
 * with terrain overhauls like Tectonic, which raise mountains far above vanilla heights and make slopes
 * steeper. If the first spot is too steep, the feature tries nearby spots and smaller pools before giving up.
 */
public class HotSpringFeature extends Feature<NoneFeatureConfiguration> {
    private static final int ATTEMPTS = 6;
    private static final int SEARCH_OFFSET = 7;
    private static final int MAX_SLOPE = 3;
    private static final int CLEARANCE = MAX_SLOPE + 2;

    public HotSpringFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
            BlockPos center = attempt == 0 ? origin : origin.offset(
                    random.nextInt(SEARCH_OFFSET * 2 + 1) - SEARCH_OFFSET, 0,
                    random.nextInt(SEARCH_OFFSET * 2 + 1) - SEARCH_OFFSET);
            // Later attempts use smaller pools; those fit on narrow ledges.
            int maxRadius = attempt < ATTEMPTS / 2 ? 4 : 2;
            int radiusX = 2 + random.nextInt(maxRadius - 1);
            int radiusZ = 2 + random.nextInt(maxRadius - 1);
            Set<Long> pool = shape(center, radiusX, radiusZ, random);
            Integer waterY = findWaterLevel(level, pool);
            if (waterY != null) {
                build(level, random, center, pool, waterY);
                return true;
            }
        }
        return false;
    }

    private static Set<Long> shape(BlockPos center, int radiusX, int radiusZ, RandomSource random) {
        Set<Long> cells = new HashSet<>();
        double rx = radiusX + 0.5;
        double rz = radiusZ + 0.5;
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                double nx = dx / rx;
                double nz = dz / rz;
                if (nx * nx + nz * nz <= 1.0 - random.nextDouble() * 0.3) {
                    cells.add(ChunkPos.asLong(center.getX() + dx, center.getZ() + dz));
                }
            }
        }
        // The center always belongs to the pool; the vent goes there.
        cells.add(ChunkPos.asLong(center.getX(), center.getZ()));
        return cells;
    }

    /** Returns the Y level for the water surface, or null if this spot is unsuitable. */
    private static Integer findWaterLevel(WorldGenLevel level, Set<Long> pool) {
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (long cell : pool) {
            int x = ChunkPos.getX(cell);
            int z = ChunkPos.getZ(cell);
            int top = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
            BlockPos surface = new BlockPos(x, top - 1, z);
            // Not under water, on ice, or on a frozen lake or ocean.
            if (!level.getFluidState(surface.above()).isEmpty() || !level.getFluidState(surface).isEmpty()) {
                return null;
            }
            BlockState ground = level.getBlockState(surface);
            if (ground.is(BlockTags.ICE) || ground.is(BlockTags.LEAVES) || ground.is(BlockTags.LOGS)) {
                return null;
            }
            minY = Math.min(minY, top);
            maxY = Math.max(maxY, top);
            if (maxY - minY > MAX_SLOPE) {
                return null;
            }
        }
        int waterY = minY - 1;
        return waterY - 2 > level.getMinBuildHeight() ? waterY : null;
    }

    private void build(WorldGenLevel level, RandomSource random, BlockPos center, Set<Long> pool, int waterY) {
        BlockState water = ModBlocks.HOT_SPRING_WATER.get().defaultBlockState();
        BlockState onsenStone = ModBlocks.ONSEN_STONE.get().defaultBlockState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // Basin: open air above, water (deeper toward the middle), onsen stone floor.
        for (long cell : pool) {
            int x = ChunkPos.getX(cell);
            int z = ChunkPos.getZ(cell);
            boolean inner = isInner(pool, x, z);
            int bottom = inner && random.nextInt(3) != 0 ? waterY - 1 : waterY;

            for (int y = waterY + 1; y <= waterY + CLEARANCE; y++) {
                pos.set(x, y, z);
                if (!level.getBlockState(pos).isAir()) {
                    this.setBlock(level, pos, Blocks.AIR.defaultBlockState());
                }
            }
            for (int y = bottom; y <= waterY; y++) {
                this.setBlock(level, pos.set(x, y, z), water);
            }
            this.setBlock(level, pos.set(x, bottom - 1, z), onsenStone);
        }

        // Walls: anything next to the water that wouldn't hold it in becomes onsen stone, with support
        // underneath, so pools on cliff edges don't spill.
        for (long cell : pool) {
            int x = ChunkPos.getX(cell);
            int z = ChunkPos.getZ(cell);
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                int nx = x + direction.getStepX();
                int nz = z + direction.getStepZ();
                if (pool.contains(ChunkPos.asLong(nx, nz))) {
                    continue;
                }
                for (int y = waterY; y >= waterY - 4; y--) {
                    pos.set(nx, y, nz);
                    BlockState state = level.getBlockState(pos);
                    boolean holdsWater = state.blocksMotion() && state.getFluidState().isEmpty();
                    if (holdsWater && y < waterY) {
                        break;
                    }
                    if (!holdsWater || random.nextInt(3) != 0) {
                        this.setBlock(level, pos, onsenStone);
                    }
                }
            }
        }

        // Mineral crust: patches of onsen stone on the ground around the pool.
        int reach = 2;
        for (int dx = -7 - reach; dx <= 7 + reach; dx++) {
            for (int dz = -7 - reach; dz <= 7 + reach; dz++) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;
                if (pool.contains(ChunkPos.asLong(x, z)) || distanceToPool(pool, x, z, reach) > reach) {
                    continue;
                }
                if (random.nextFloat() > 0.55F) {
                    continue;
                }
                int top = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
                if (Math.abs(top - 1 - waterY) > 2) {
                    continue;
                }
                pos.set(x, top - 1, z);
                BlockState ground = level.getBlockState(pos);
                if (ground.is(BlockTags.DIRT) || ground.is(BlockTags.BASE_STONE_OVERWORLD)
                        || ground.is(Blocks.GRAVEL) || ground.is(Blocks.SNOW_BLOCK)) {
                    this.setBlock(level, pos, onsenStone);
                }
            }
        }

        // The vent keeps the spring hot and sends up a plume of steam.
        int ventY = waterY - 1;
        pos.set(center.getX(), ventY, center.getZ());
        if (level.getBlockState(pos).is(ModBlocks.HOT_SPRING_WATER.get())) {
            ventY--;
        }
        this.setBlock(level, pos.set(center.getX(), ventY, center.getZ()), ModBlocks.HOT_SPRING_VENT.get().defaultBlockState());
    }

    private static boolean isInner(Set<Long> pool, int x, int z) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!pool.contains(ChunkPos.asLong(x + direction.getStepX(), z + direction.getStepZ()))) {
                return false;
            }
        }
        return true;
    }

    private static int distanceToPool(Set<Long> pool, int x, int z, int maxDistance) {
        for (int d = 1; d <= maxDistance; d++) {
            for (int dx = -d; dx <= d; dx++) {
                for (int dz = -d; dz <= d; dz++) {
                    if (pool.contains(ChunkPos.asLong(x + dx, z + dz))) {
                        return d;
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}
