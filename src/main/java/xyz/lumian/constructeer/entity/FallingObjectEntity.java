package xyz.lumian.constructeer.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import xyz.lumian.constructeer.item.multimining.MultiMiningBox;

import java.util.List;



//**********************************************************************************************************************
public class FallingObjectEntity
    extends Display
{
    //******************************************************************************************************************
    public final static class RenderState
    {
        //**************************************************************************************************************
        public List<MultiMiningBox.Part> parts = List.of();
    }
    
    //******************************************************************************************************************
    private static final EntityDataAccessor<List<MultiMiningBox.Part>> DATA_PARTS = SynchedEntityData
        .defineId(FallingObjectEntity.class, ModEntityDataSerialisers.MULTI_MINING_PARTS);
    
    private static final int   ANIMATION_DURATION_SECONDS = 5;
    private static final int   ANIMATION_TICKS            = (ANIMATION_DURATION_SECONDS * 20);
    private static final float ANIMATION_DELTA            = (1.0f / ANIMATION_TICKS);
    
    private static final int FALL_DESTRUCTION_TICKS = (20 * 3);
    private static final int FALL_BURNING_TICKS     = (20 * 5);
    private static final int TILT_TICKS             = (20 * 3);
    
    //******************************************************************************************************************
    private final RenderState renderState = new RenderState();
    
    private MultiMiningBox box      = new MultiMiningBox();
    private Direction      fallDir  = Direction.NORTH;
    private int            fallTime = 0;
    private int            tiltTime = 0;
    
    //******************************************************************************************************************
    public FallingObjectEntity(final EntityType<?> entityType, final Level level) { super(entityType, level); }
    
    //==================================================================================================================
    public @Nullable RenderState getRenderState() { return this.renderState; }
    
    public List<MultiMiningBox.Part> getParts() { return this.entityData.get(FallingObjectEntity.DATA_PARTS); }
    
    public MultiMiningBox getBox() { return this.box; }
    
    @Override protected double getDefaultGravity() { return 0.04f; }
    
    //==================================================================================================================
    public void setBox(final MultiMiningBox box)
    {
        if (!box.equals(this.box))
        {
            this.box = box;
            this.entityData.set(FallingObjectEntity.DATA_PARTS, this.box.parts());
        }
    }
    
    public void setFallingDirection(final Direction direction) { this.fallDir = direction; }
    
    //==================================================================================================================
    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(FallingObjectEntity.DATA_PARTS, List.of());
    }
    
    @Override
    protected void readAdditionalSaveData(final ValueInput input)
    {
        super.readAdditionalSaveData(input);
        this.setBox             (input.read("object_box", MultiMiningBox.CODEC).orElseGet(MultiMiningBox::new));
        this.setFallingDirection(input.read("direction",  Direction     .CODEC).orElse   (Direction.NORTH));
        
        this.fallTime = input.getIntOr("fall_time", 0);
        this.tiltTime = input.getIntOr("tilt_time", 0);
    }
    
    @Override
    protected void addAdditionalSaveData(final ValueOutput output)
    {
        super.addAdditionalSaveData(output);
        output.store("object_box", MultiMiningBox.CODEC, this.box);
        output.store("direction",  Direction     .CODEC, this.fallDir);
        output.store("fall_time",  Codec         .INT,   this.fallTime);
        output.store("tilt_time",  Codec         .INT,   this.tiltTime);
    }
    
    //==================================================================================================================
    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> dataAccessor)
    {
        super.onSyncedDataUpdated(dataAccessor);
        
        if (dataAccessor.equals(FallingObjectEntity.DATA_PARTS))
        {
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
            if (this.fallTime > FallingObjectEntity.FALL_BURNING_TICKS && !on_fire && !this.isInWaterOrRain())
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
                        || this.fallTime > 600
                    )
                    {
                        this.discard();
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
        }
        else if (this.isAlive())
        {
            ++this.tiltTime;
            this.applyGravity();
            
            if (this.getXRot() < 90)
            {
                final float rotation = Math.min(90f, (this.getXRot() + (float) this.getDeltaMovement().y));
                this.setXRot(rotation);
            }
            
            if (this.tiltTime >= FallingObjectEntity.TILT_TICKS)
            {
                this.kill(level);
            }
        }
        
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        super.tick();
    }
    
    //------------------------------------------------------------------------------------------------------------------
    @Override
    protected void updateRenderSubState(final boolean interpolate, final float partialTick)
    {
        this.renderState.parts = this.getParts();
    }
    
    //------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("resource")
    @Override
    public void onRemoval(final RemovalReason reason)
    {
        if ((this.level() instanceof ServerLevel level) && reason.shouldDestroy())
        {
            this.box.parts().forEach(part -> part.drops().forEach(stack -> this.spawnAtLocation(level, stack)));
        }
    }
}
