package com.snowmonkeys.entity;

import com.snowmonkeys.entity.goal.BatheInHotSpringGoal;
import com.snowmonkeys.registry.ModEntities;
import com.snowmonkeys.registry.ModFluids;
import com.snowmonkeys.registry.ModSounds;
import com.snowmonkeys.registry.ModTags;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * Japanese macaque (Macaca fuscata). They live in troops in cold forests and mountains, soak in hot springs
 * to stay warm, roll snowballs, grab dropped fruit, and gang up with snowballs on anyone who picks a fight.
 */
public class SnowMonkey extends Animal implements RangedAttackMob {
    private static final EntityDataAccessor<Boolean> DATA_BATHING =
            SynchedEntityData.defineId(SnowMonkey.class, EntityDataSerializers.BOOLEAN);

    private static final int FORGET_ATTACKER_TICKS = 300;

    private int bathCooldown;
    private int snowballRollTime;
    private int eatCooldown;

    public SnowMonkey(EntityType<? extends SnowMonkey> type, Level level) {
        super(type, level);
        this.bathCooldown = this.random.nextInt(600);
        this.snowballRollTime = nextSnowballTime(this.random);
        this.setCanPickUpLoot(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    public static boolean checkSnowMonkeySpawnRules(EntityType<SnowMonkey> type, LevelAccessor level,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).is(ModTags.SNOW_MONKEYS_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
    }

    private static int nextSnowballTime(RandomSource random) {
        return 4800 + random.nextInt(4800);
    }

    @Override
    protected void registerGoals() {
        // Don't float while sitting in a shallow pool; they'd bob up and down instead of soaking.
        this.goalSelector.addGoal(0, new FloatGoal(this) {
            @Override
            public boolean canUse() {
                return !SnowMonkey.this.isBathing() && super.canUse();
            }
        });
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4) {
            @Override
            public boolean canUse() {
                return SnowMonkey.this.isBaby() && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.2, 25, 10.0F));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, stack -> stack.is(ModTags.SNOW_MONKEY_FOOD), false));
        this.goalSelector.addGoal(5, new BatheInHotSpringGoal(this, 1.0, 20));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BATHING, false);
    }

    public boolean isBathing() {
        return this.entityData.get(DATA_BATHING);
    }

    public void setBathing(boolean bathing) {
        this.entityData.set(DATA_BATHING, bathing);
    }

    public boolean isInHotSpring() {
        return this.isInFluidType(ModFluids.HOT_SPRING_TYPE.get());
    }

    public boolean wantsToBathe() {
        return this.bathCooldown <= 0 && this.getTarget() == null && !this.isInLove() && !this.isLeashed();
    }

    public void resetBathCooldown() {
        // Snowy weather makes the troop head back to the water sooner.
        int base = this.level().isRaining() ? 600 : 1200;
        this.bathCooldown = base + this.random.nextInt(2400);
    }

    private boolean isOnSnow() {
        BlockPos pos = this.blockPosition();
        return this.level().getBlockState(pos).is(BlockTags.SNOW) || this.level().getBlockState(pos.below()).is(BlockTags.SNOW);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.isBathing() && this.random.nextInt(80) == 0) {
                this.level().addParticle(ParticleTypes.HEART,
                        this.getRandomX(0.6), this.getY() + this.getBbHeight() + 0.1, this.getRandomZ(0.6), 0.0, 0.02, 0.0);
            }
            return;
        }

        if (this.bathCooldown > 0) {
            this.bathCooldown--;
        }
        if (this.eatCooldown > 0) {
            this.eatCooldown--;
        }
        if (this.isBathing() && this.tickCount % 100 == 0) {
            this.heal(1.0F);
        }

        // Real Japanese macaques have been seen rolling snowballs, apparently for fun.
        if (this.isAlive() && !this.isBaby() && !this.isBathing() && --this.snowballRollTime <= 0) {
            if (this.isOnSnow()) {
                this.playSound(SoundEvents.SNOW_PLACE, 1.0F, 1.2F);
                this.spawnAtLocation(Items.SNOWBALL);
            }
            this.snowballRollTime = nextSnowballTime(this.random);
        }

        // Troops hold a grudge only briefly before going back to their business.
        if (this.getTarget() != null && this.tickCount - this.getLastHurtByMobTimestamp() > FORGET_ATTACKER_TICKS) {
            this.setTarget(null);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        Snowball snowball = new Snowball(this.level(), this);
        double dy = target.getEyeY() - 1.1F;
        double dx = target.getX() - this.getX();
        double dyFromBall = dy - snowball.getY();
        double dz = target.getZ() - this.getZ();
        double arc = Math.sqrt(dx * dx + dz * dz) * 0.2F;
        snowball.shoot(dx, dyFromBall + arc, dz, 1.5F, 10.0F);
        this.playSound(SoundEvents.SNOWBALL_THROW, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(snowball);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.SNOW_MONKEY_FOOD);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.SNOW_MONKEY.get().create(level);
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return this.eatCooldown <= 0 && !this.isBathing() && this.isFood(stack);
    }

    /** Snow monkeys eat food left on the ground instead of holding it. */
    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (!this.wantsToPickUp(stack)) {
            return;
        }
        this.onItemPickup(itemEntity);
        this.take(itemEntity, 1);
        ItemStack remaining = stack.copy();
        remaining.shrink(1);
        if (remaining.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(remaining);
        }
        this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.3F);
        this.heal(2.0F);
        if (this.isBaby()) {
            this.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-this.getAge()), true);
        }
        this.eatCooldown = 200;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        // Monkeys sit with their heads above the water.
        return type != ModFluids.HOT_SPRING_TYPE.get() && super.canDrownInFluidType(type);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BathCooldown", this.bathCooldown);
        tag.putInt("SnowballRollTime", this.snowballRollTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.bathCooldown = tag.getInt("BathCooldown");
        if (tag.contains("SnowballRollTime")) {
            this.snowballRollTime = tag.getInt("SnowballRollTime");
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isBathing() ? null : ModSounds.SNOW_MONKEY_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.SNOW_MONKEY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SNOW_MONKEY_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.FOX_STEP, 0.15F, 1.1F);
    }
}
