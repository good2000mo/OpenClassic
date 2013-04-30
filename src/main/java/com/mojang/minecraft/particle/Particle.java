package com.mojang.minecraft.particle;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.ShapeRenderer;

public class Particle extends Entity {

	private static final long serialVersionUID = 1L;
	protected int tex;
	protected float uo;
	protected float vo;
	protected int age = 0;
	protected int lifetime = 0;
	protected float size;
	protected float gravity;
	protected float rCol;
	protected float gCol;
	protected float bCol;

	public Particle(Level level, float x, float y, float z, float var5, float var6, float var7) {
		super(level);
		this.setSize(0.2F, 0.2F);
		this.heightOffset = this.bbHeight / 2.0F;
		this.setPos(x, y, z);
		this.rCol = this.gCol = this.bCol = 1.0F;
		this.xd = var5 + (float) (Math.random() * 2.0D - 1.0D) * 0.4F;
		this.yd = var6 + (float) (Math.random() * 2.0D - 1.0D) * 0.4F;
		this.zd = var7 + (float) (Math.random() * 2.0D - 1.0D) * 0.4F;
		float var8 = (float) (Math.random() + Math.random() + 1.0D) * 0.15F;
		float var2 = (float) Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
		this.xd = this.xd / var2 * var8 * 0.4F;
		this.yd = this.yd / var2 * var8 * 0.4F + 0.1F;
		this.zd = this.zd / var2 * var8 * 0.4F;
		this.uo = (float) Math.random() * 3.0F;
		this.vo = (float) Math.random() * 3.0F;
		this.size = (float) (Math.random() * 0.5D + 0.5D);
		this.lifetime = (int) (4.0D / (Math.random() * 0.9D + 0.1D));
		this.age = 0;
		this.makeStepSound = false;
	}

	public Particle setPower(float power) {
		this.xd *= power;
		this.yd = (this.yd - 0.1F) * power + 0.1F;
		this.zd *= power;
		return this;
	}

	public Particle scale(float scale) {
		this.setSize(0.2F * scale, 0.2F * scale);
		this.size *= scale;
		return this;
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		this.yd = (float) (this.yd - 0.04D * this.gravity);
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.98F;
		this.yd *= 0.98F;
		this.zd *= 0.98F;
		if (this.onGround) {
			this.xd *= 0.7F;
			this.zd *= 0.7F;
		}
	}

	public void render(ShapeRenderer renderer, float var2, float var3, float var4, float var5, float var6, float var7) {
		float var8;
		float var9 = (var8 = (this.tex % 16) / 16.0F) + 0.0624375F;
		float var10;
		float var11 = (var10 = (this.tex / 16f) / 16.0F) + 0.0624375F;
		float var12 = 0.1F * this.size;
		float var13 = this.xo + (this.x - this.xo) * var2;
		float var14 = this.yo + (this.y - this.yo) * var2;
		float var15 = this.zo + (this.z - this.zo) * var2;
		var2 = this.getBrightness(var2);
		renderer.color(this.rCol * var2, this.gCol * var2, this.bCol * var2);
		renderer.vertexUV(var13 - var3 * var12 - var6 * var12, var14 - var4 * var12, var15 - var5 * var12 - var7 * var12, var8, var11);
		renderer.vertexUV(var13 - var3 * var12 + var6 * var12, var14 + var4 * var12, var15 - var5 * var12 + var7 * var12, var8, var10);
		renderer.vertexUV(var13 + var3 * var12 + var6 * var12, var14 + var4 * var12, var15 + var5 * var12 + var7 * var12, var9, var10);
		renderer.vertexUV(var13 + var3 * var12 - var6 * var12, var14 - var4 * var12, var15 + var5 * var12 - var7 * var12, var9, var11);
	}

	public int getParticleId() {
		return 0;
	}
}
