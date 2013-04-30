package com.mojang.minecraft.item;

import ch.spacebase.openclassic.api.block.VanillaBlock;
import ch.spacebase.openclassic.api.math.MathHelper;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.item.TakeEntityAnim;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.particle.SmokeParticle;
import com.mojang.minecraft.particle.TerrainParticle;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.render.TextureManager;
import java.util.Random;
import org.lwjgl.opengl.GL11;

public class PrimedTnt extends Entity {

	public static final long serialVersionUID = 0L;
	private float xd;
	private float yd;
	private float zd;
	public int life = 0;
	private boolean defused;

	public PrimedTnt(Level var1, float var2, float var3, float var4) {
		super(var1);
		this.setSize(0.98F, 0.98F);
		this.heightOffset = this.bbHeight / 2.0F;
		this.setPos(var2, var3, var4);
		float var5 = (float) (Math.random() * 3.1415927410125732D * 2.0D);
		this.xd = -MathHelper.sin(var5 * 3.1415927F / 180.0F) * 0.02F;
		this.yd = 0.2F;
		this.zd = -MathHelper.cos(var5 * (float) Math.PI / 180.0F) * 0.02F;
		this.makeStepSound = false;
		this.life = 40;
		this.xo = var2;
		this.yo = var3;
		this.zo = var4;
	}

	public void hurt(Entity var1, int var2) {
		if (!this.removed) {
			super.hurt(var1, var2);
			if (var1 instanceof Player) {
				this.remove();
				this.level.addEntity(new Item(this.level, this.x, this.y, this.z, VanillaBlock.TNT.getId()));
			}

		}
	}

	public boolean isPickable() {
		return !this.removed;
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.yd -= 0.04F;
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.98F;
		this.yd *= 0.98F;
		this.zd *= 0.98F;
		if (this.onGround) {
			this.xd *= 0.7F;
			this.zd *= 0.7F;
			this.yd *= -0.5F;
		}

		if (!this.defused) {
			if (this.life-- > 0) {
				this.level.particleEngine.spawnParticle(new SmokeParticle(this.level, this.x, this.y + 0.6F, this.z));
			} else {
				this.remove();
				Random rand = new Random();
				this.level.explode(null, this.x, this.y, this.z, 4.0F);

				for (int var3 = 0; var3 < 100; ++var3) {
					float var4 = (float) rand.nextGaussian();
					float var5 = (float) rand.nextGaussian();
					float var6 = (float) rand.nextGaussian();
					float var7 = (float) Math.sqrt(var4 * var4 + var5 * var5 + var6 * var6);
					float var8 = var4 / var7 / var7;
					float var9 = var5 / var7 / var7;
					var7 = var6 / var7 / var7;
					this.level.particleEngine.spawnParticle(new TerrainParticle(this.level, this.x + var4, this.y + var5, this.z + var6, var8, var9, var7, VanillaBlock.TNT));
				}

			}
		}
	}

	public void playerTouch(Entity entity) {
		if (this.defused) {
			if (((Player) entity).addResource(VanillaBlock.TNT.getId())) {
				this.level.addEntity(new TakeEntityAnim(this.level, this, entity));
				this.remove();
			}
		}
	}

	public void render(TextureManager textures, float var2) {
		float brightness = this.level.getBrightness((int) this.x, (int) this.y, (int) this.z);
		GL11.glPushMatrix();
		VanillaBlock.TNT.getModel().renderAll(this.xo + (this.x - this.xo) * var2 - 0.5F, this.yo + (this.y - this.yo) * var2 - 0.5F, this.zo + (this.z - this.zo) * var2 - 0.5F, brightness);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, ((this.life / 4 + 1) % 2) * 0.4F);
		if (this.life <= 16) {
			GL11.glColor4f(1, 1, 1, ((this.life + 1) % 2) * 0.6F);
		}

		if (this.life <= 2) {
			GL11.glColor4f(1, 1, 1, 0.9F);
		}

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		VanillaBlock.TNT.getModel().renderAll(this.xo + (this.x - this.xo) * var2 - 0.5F, this.yo + (this.y - this.yo) * var2 - 0.5F, this.zo + (this.z - this.zo) * var2 - 0.5F, -1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
