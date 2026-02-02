/// MIT License
///
/// Copyright (c) 2026 Lumian Studio
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
/// SOFTWARE.
package xyz.lumian.constructeer.multimining.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.multimining.item.multimining.MultiMiningBox;

import java.util.List;



//**********************************************************************************************************************
public class FallingObjectEntity
    extends Display
{
    //******************************************************************************************************************
    public final static class RenderState
    {
        //**************************************************************************************************************
        public MultiMiningBox box          = new MultiMiningBox();
        public Vector3f       motion       = new Vector3f();
        public Direction      direction    = Direction.NORTH;
        public float          rotation     = 0f;
        public float          nextRotation = 0f;
        
        //**************************************************************************************************************
        public float calculateAngle(final float deltaTime)
        {
            return Mth.lerp(deltaTime, this.rotation, this.nextRotation);
        }
        
        public Quaternionf calculateQuaternion(final float deltaTime)
        {
            final Vec3i normal = this.direction.getUnitVec3i();
            final float angle  = this.calculateAngle(deltaTime);
            return new Quaternionf(new AxisAngle4f(angle, normal.getZ(), 0, (normal.getX() * -1)));
        }
        
        //==============================================================================================================
        public void updateRotation(final int tick)
        {
            if (this.rotation == 0f && tick > 0)
            {
                this.rotation = FallingObjectEntity.calculateTilt(tick);
            }
            else
            {
                this.rotation = this.nextRotation;
            }
            
            this.nextRotation = FallingObjectEntity.calculateTilt(tick + 1);
        }
    }
    
    //******************************************************************************************************************
    private static final EntityDataAccessor<MultiMiningBox> DATA_RENDER_BOX = SynchedEntityData
        .defineId(FallingObjectEntity.class, ModEntityDataSerialisers.FALLING_OBJECT_RENDER_BOX);
    private static final EntityDataAccessor<Integer>        DATA_TILT       = SynchedEntityData
        .defineId(FallingObjectEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Direction>      DATA_FALL_DIR   = SynchedEntityData
        .defineId(FallingObjectEntity.class, EntityDataSerializers.DIRECTION);
    
    private static final int   FALL_DESTRUCTION_TICKS = (20 * 3);
    private static final int   FALL_BURNING_TICKS     = (20 * 5);
    private static final int   TILT_TICKS             = (int) (20 * 4.5);
    private static final float MAX_ROTATION           = (float) (Math.PI * 0.55f);
    private static final float INTERPOLATION_EPS      = (1.0f / TILT_TICKS);
    
    //******************************************************************************************************************
    private static float calculateTilt(final int tiltTime)
    {
        final float interpolation = (FallingObjectEntity.INTERPOLATION_EPS * tiltTime);
        final float skewed_curve  = (interpolation * interpolation * interpolation * interpolation * interpolation);
        return (FallingObjectEntity.MAX_ROTATION * skewed_curve);
    }
    
    //******************************************************************************************************************
    private final RenderState renderState = new RenderState();
    
    private           int        fallTime  = 0;
    private           Vector3f   motionVec = new Vector3f();
    private @Nullable SoundEvent effect    = null;
    
    //******************************************************************************************************************
    public FallingObjectEntity(final EntityType<?> entityType, final Level level)
    {
        super(entityType, level);
        this.noPhysics = false;
    }
    
    //==================================================================================================================
    public @Nullable RenderState getRenderState() { return this.renderState; }
    
    public List<MultiMiningBox.Part> getParts()
    {
        return this.entityData.get(FallingObjectEntity.DATA_RENDER_BOX).parts();
    }
    
    public MultiMiningBox getBox() { return this.entityData.get(FallingObjectEntity.DATA_RENDER_BOX); }
    
    @Override public SoundSource getSoundSource() { return SoundSource.BLOCKS; }
    
    public @Nullable SoundEvent getEffect() { return this.effect; }
    
    @Override protected double getDefaultGravity() { return 0.04f; }
    
    //==================================================================================================================
    public void setBoxAndFallDirection(final MultiMiningBox box, final Direction direction)
    {
        this.entityData.set(FallingObjectEntity.DATA_RENDER_BOX, box);
        this.entityData.set(FallingObjectEntity.DATA_FALL_DIR,   direction);
    }
    
    @Override
    public void setPos(double x, double y, double z)
    {
        super.setPos(x, y, z);
    }
    
    public void setEffect(final @Nullable SoundEvent soundEvent) { this.effect = soundEvent; }
    
    //==================================================================================================================
    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(FallingObjectEntity.DATA_RENDER_BOX, new MultiMiningBox());
        builder.define(FallingObjectEntity.DATA_FALL_DIR,   Direction.NORTH);
        builder.define(FallingObjectEntity.DATA_TILT,       0);
    }
    
    @Override
    protected void readAdditionalSaveData(final ValueInput input)
    {
        super.readAdditionalSaveData(input);
        
        final MultiMiningBox box = input.read("box", MultiMiningBox.CODEC).orElseGet(MultiMiningBox::new);
        final Direction      dir = input.read("dir", Direction     .CODEC).orElse   (Direction.NORTH);
        this.setBoxAndFallDirection(box, dir);
        
        this.entityData.set(FallingObjectEntity.DATA_TILT, input.getIntOr("tilt_progress", 0));
        this.fallTime = input.getIntOr("fall_time", 0);
        
        this.effect = input.read("effect", BuiltInRegistries.SOUND_EVENT.byNameCodec()).orElse(null);
    }
    
    @Override
    protected void addAdditionalSaveData(final ValueOutput output)
    {
        super.addAdditionalSaveData(output);
        output.store("box",           MultiMiningBox.CODEC, this.getBox());
        output.store("fall_time",     Codec         .INT,   this.fallTime);
        output.store("dir",           Direction     .CODEC, this.entityData.get(FallingObjectEntity.DATA_FALL_DIR));
        output.store("tilt_progress", Codec         .INT,   this.entityData.get(FallingObjectEntity.DATA_TILT));
        
        if (this.effect != null)
        {
            output.store("effect", Identifier.CODEC, this.effect.location());
        }
    }
    
    //==================================================================================================================
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> dataAccessor)
    {
        super.onSyncedDataUpdated(dataAccessor);
        
        if (dataAccessor.equals(FallingObjectEntity.DATA_TILT))
        {
            this.updateRenderState = true;
        }
        
        if (
            dataAccessor.equals(FallingObjectEntity.DATA_RENDER_BOX)
            || dataAccessor.equals(FallingObjectEntity.DATA_FALL_DIR)
        )
        {
            final Direction      dir    = this.entityData.get(FallingObjectEntity.DATA_FALL_DIR);
            final MultiMiningBox box    = this.entityData.get(FallingObjectEntity.DATA_RENDER_BOX);
            this.motionVec         = new Vector3f(
                (box.baseWidth() * 0.5f * dir.getUnitVec3i().getX()),
                0,
                (box.baseDepth() * 0.5f * dir.getUnitVec3i().getZ()));
            this.updateRenderState = true;
        }
    }
    
    @SuppressWarnings("resource")
    @Override
    public void tick()
    {
        if (!(this.level() instanceof ServerLevel level))
        {
            super.tick();
            return;
        }
        
        final boolean on_fire;
        
        if (this.isOnFire())
        {
            on_fire = true;
            // TODO burns parts one by one
        } else on_fire = false;
        
        if (!this.onGround())
        {
            ++this.fallTime;
            
            // start burning
            if (this.fallTime > FallingObjectEntity.FALL_BURNING_TICKS && !on_fire)
            {
                this.igniteForTicks(1000);
            }
            
            this.applyGravity();
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            
            if (this.isAlive())
            {
                if (!this.onGround())
                {
                    final BlockPos pos = this.blockPosition();
                    
                    if (
                        (this.fallTime > 100 && (pos.getY() <= level.getMinY() || pos.getY() > level.getMaxY()))
                        || this.fallTime > 20
                    )
                    {
                        this.discard();
                    }
                    else if (this.isInWaterOrRain())
                    {
                        this.fallTime = Math.min(100, this.fallTime);
                    }
                }
                else
                {
                    this.setDeltaMovement(Vec3.ZERO);
                    
                    if (this.fallTime >= FallingObjectEntity.FALL_DESTRUCTION_TICKS)
                    {
                        this.kill(level);
                    }
                }
            }
            
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        }
        else if (this.isAlive())
        {
            int tilt = this.entityData.get(FallingObjectEntity.DATA_TILT);
            
            if (tilt < 1 && this.effect != null)
            {
                this.playSound(this.effect, 0.3f, 1.0f);
            }
            
            this.entityData.set(FallingObjectEntity.DATA_TILT, ++tilt);
            
            if (tilt >= FallingObjectEntity.TILT_TICKS)
            {
                this.kill(level);
            }
        }
        
        super.tick();
    }
    
    //------------------------------------------------------------------------------------------------------------------
    @Override
    protected void updateRenderSubState(final boolean interpolate, final float partialTick)
    {
        this.renderState.box       = this.getBox();
        this.renderState.motion    = this.motionVec;
        this.renderState.direction = this.entityData.get(FallingObjectEntity.DATA_FALL_DIR);
        
        this.renderState.updateRotation(this.entityData.get(FallingObjectEntity.DATA_TILT));
    }
    
    //==================================================================================================================
    @SuppressWarnings("resource")
    @Override
    public void onRemoval(final RemovalReason reason)
    {
        if ((this.level() instanceof ServerLevel level) && reason.shouldDestroy())
        {
            this.getBox().parts().forEach(part -> part.drops().forEach(stack -> this.spawnAtLocation(level, stack)));
        }
    }
}
