package betweenlands.entities.mobs;

import java.util.Random;

import betweenlands.entities.particles.EntityDruidCastingFX;
import betweenlands.lib.Ids;
import betweenlands.lib.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDarkDruid extends EntityMob
{
    public int attackTimer = 140;
    private int attackCounter;
    public int ID = 0;
    
    public int forgetTimer = 40;
    private int forgetCounter = 0;
    
    public boolean isCasting;
    public float wanderMoveSpeed;
    
    EntityLivingBase lastAttackTarget = null;
    EntityLivingBase currentAttackTarget = null;
    
    public final EntityAIBase meleeAI;
    public final EntityAIBase wanderAI;
    public static final int darkDruidHealth = 10;
    public static float moveSpeed = 0.23F; // Not sure of this but... Meh.
    
    public EntityDarkDruid(World par1World)
    
    {   super(par1World);
        /* super(par1World);
        attackCounter = 0;
        //this.texture = TheBetweenlands.Mobs + "DarkDruid.png";
        this.moveSpeed = 0.23F;
    */
        this.wanderMoveSpeed = 0.23F; 
        meleeAI = new EntityAIAttackOnCollide(this, EntityPlayer.class, this.moveSpeed, false); 
        wanderAI = new EntityAIWander(this, this.wanderMoveSpeed);
        
        this.getNavigator().setBreakDoors(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this));
        this.tasks.addTask(2, meleeAI);
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, this.moveSpeed));
        this.tasks.addTask(6, wanderAI);
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 24.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
       // this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 24.0F, 0, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 24, true));
    	
        //this.setSize(0.9F, 1.3F);
        //this.setSize(0.9F, 1.7F);
        this.setSize(1.1F, 1.7F);
        this.getNavigator().setSpeed(0.23F);
    }
    
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        EntityPlayer tar = this.worldObj.getClosestVulnerablePlayerToEntity(this, 64.0D);
        if (tar == null)
        {
            currentAttackTarget = null;
        }
        
        if (tar != currentAttackTarget)
        {
            if (this.getDistanceToEntity(tar) <= 8.0F)
            {
                currentAttackTarget = tar;
            }
       
        }
        
        
        
        if (currentAttackTarget != null && currentAttackTarget != lastAttackTarget && this.getEntitySenses().canSee(currentAttackTarget))
        {
            if (this.attackCounter == 0)
            {
                this.attackCounter++;
                this.tasks.removeTask(meleeAI);
            }
            else if (this.attackCounter < this.attackTimer)
            {
                this.attackCounter++;
                this.isCasting = true;
                this.chargeSpell(currentAttackTarget);
                this.spawnParticles();
            }
            else
            {
                this.attackCounter = 0;
                this.isCasting = false;
                this.castSpell(currentAttackTarget);
                lastAttackTarget = currentAttackTarget;
                currentAttackTarget = null;
                this.tasks.addTask(2, meleeAI);
                this.forgetCounter = this.forgetTimer;
            }
        }
        else
        {
            this.attackCounter = 0;
            this.isCasting = false;
            this.currentAttackTarget = null;
            if (this.forgetCounter <= 0)
                lastAttackTarget = null;
            else
                this.forgetCounter--;
        }
    }
    
    public void spawnParticles()
    {
        
        double pX = Math.random() * 0.25 - 0.125;
        double pY = Math.random() * 0.25 - 0.125;
        double pZ = Math.random() * 0.25 - 0.125;
        
        double pPosX = this.posX + Math.sin(-this.rotationYaw * Math.PI / 180);
        double pPosZ = this.posZ + Math.cos(-this.rotationYaw * Math.PI / 180);
        
        EntityDruidCastingFX particle = new EntityDruidCastingFX(this.worldObj, 
                pPosX + Math.sin(-this.rotationYaw * Math.PI / 180 - Math.PI / 2) / 2.5 + pX, 
                this.posY + 1.4 + pY, 
                pPosZ + Math.cos(-this.rotationYaw * Math.PI / 180 - Math.PI / 2) / 2.5 + pZ,
                -Math.sin(-this.rotationYaw * Math.PI / 180) + pX,
                pY,
                -Math.cos(-this.rotationYaw * Math.PI / 180) + pZ);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        
        System.out.println();
        System.out.println(-Math.sin(-this.rotationYaw * Math.PI / 180) + pX);
        System.out.println(-Math.cos(-this.rotationYaw * Math.PI / 180) + pZ);
        
        pX = Math.random() * 0.25 - 0.125;
        pY = Math.random() * 0.25 - 0.125;
        pZ = Math.random() * 0.25 - 0.125;
        
        EntityDruidCastingFX particle2 = new EntityDruidCastingFX(this.worldObj, 
                pPosX + Math.sin(-this.rotationYaw * Math.PI / 180 + Math.PI / 2) / 2.5 + pX, 
                this.posY + 1.4 + pY, 
                pPosZ + Math.cos(-this.rotationYaw * Math.PI / 180 + Math.PI / 2) / 2.5 + pZ,
                -Math.sin(-this.rotationYaw * Math.PI / 180) + pX,
                pY,
                -Math.cos(-this.rotationYaw * Math.PI / 180) + pZ);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle2);
    }
    
    public void chargeSpell(EntityLivingBase currentAttackTarget2)
    {
        currentAttackTarget2.motionX *= 0.0D;
        currentAttackTarget2.motionZ *= 0.0D;
        currentAttackTarget2.motionY = 0.05D;
    }
    
    public void castSpell(EntityLivingBase currentAttackTarget2)
    {
        currentAttackTarget2.motionX = 2.0D * Math.signum(currentAttackTarget2.posX - this.posX);
        currentAttackTarget2.motionZ = 2.0D * Math.signum(currentAttackTarget2.posZ - this.posZ);
        currentAttackTarget2.motionY = 1.5D;
    }
    
    public int getMaxHealth()
    {
        return darkDruidHealth;
    } 

    /**
     * Returns the item ID for the item the mob drops on death.
     */
    protected int getDropItemId()
    {
        return ID;
    }
    

    /**
     * Drop 0-2 items of this living's type. @param par1 - Whether this entity has recently been hit by a player. @param
     * par2 - Level of Looting used to kill this mob.
     */
    public void dropFewItems(boolean par1, int par2)
    {
       /* if (this.rand.nextInt(5) == 0)
        {
            this.dropItem(this.getDropItemId(), 1);
        } */
    		int RM;
    		RM = this.rand.nextInt(4);
    		if (RM == 0) {
    			ID = Ids.swampTalismanPiece1_actual+Ids.idShift;
    		}
    		else if (RM == 1) {
    			ID = Ids.swampTalismanPiece2_actual+Ids.idShift;
    		}
    		else if (RM == 2) {
    			ID = Ids.swampTalismanPiece3_actual+Ids.idShift;
    		}
    		else if (RM == 3) {
    			ID = Ids.swampTalismanPiece4_actual+Ids.idShift;
    		}
    		
    		this.dropItem(ID, 1);
    	
    }
    
    /**
     * Returns true if the newer Entity AI code should be run
     */
    @Override
    protected boolean isAIEnabled()
    {
        return true;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    @Override
    public boolean getCanSpawnHere()
    {
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox) ;
    }
    
    protected String getLivingSound() {
        return "thebl:DarkDruidLiving"; }
    
    protected String getHurtSound() {
    	return "thebl:DarkDruidHit";
    
    }
    
    protected String getDeathSound()
    { return "thebl:DarkDruidDie"; }
    
    
    protected float getSoundVolume()
    {
       // return 0.4F;
    	return 1.0f;
    }
    
    
    
        
}
