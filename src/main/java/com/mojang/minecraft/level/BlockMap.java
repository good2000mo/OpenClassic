package com.mojang.minecraft.level;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.model.Vector;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.minecraft.render.ClippingHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockMap implements Serializable {

	public static final long serialVersionUID = 0L;
	private int width;
	private int depth;
	private int height;
	private Slot slot = new Slot();
	private Slot slot2 = new Slot();
	public List<Entity>[] entityGrid;
	public List<Entity> all = new ArrayList<Entity>();
	private List<Entity> tmp = new ArrayList<Entity>();

	@SuppressWarnings("unchecked")
	public BlockMap(int width, int height, int depth) {
		this.width = width / 16;
		this.depth = height / 16;
		this.height = depth / 16;
		if (this.width == 0) {
			this.width = 1;
		}

		if (this.depth == 0) {
			this.depth = 1;
		}

		if (this.height == 0) {
			this.height = 1;
		}

		this.entityGrid = new ArrayList[this.width * this.depth * this.height];

		for (width = 0; width < this.width; ++width) {
			for (height = 0; height < this.depth; ++height) {
				for (depth = 0; depth < this.height; ++depth) {
					this.entityGrid[(depth * this.depth + height) * this.width + width] = new ArrayList<Entity>();
				}
			}
		}

	}

	public void insert(Entity entity) {
		this.all.add(entity);
		this.slot.init(this, entity.x, entity.y, entity.z).add(entity);
		entity.xOld = entity.x;
		entity.yOld = entity.y;
		entity.zOld = entity.z;
		entity.blockMap = this;
	}

	public void remove(Entity entity) {
		this.slot.init(this, entity.xOld, entity.yOld, entity.zOld).remove(entity);
		this.all.remove(entity);
	}

	public void moved(Entity entity) {
		Slot var2 = this.slot.init(this, entity.xOld, entity.yOld, entity.zOld);
		Slot var3 = this.slot2.init(this, entity.x, entity.y, entity.z);
		if (!var2.equals(var3)) {
			var2.remove(entity);
			var3.add(entity);
			entity.xOld = entity.x;
			entity.yOld = entity.y;
			entity.zOld = entity.z;
		}
	}

	public List<Entity> getEntities(Entity exclude, float x, float y, float z, float x2, float y2, float z2) {
		this.tmp.clear();
		return this.getEntities(exclude, x, y, z, x2, y2, z2, this.tmp);
	}

	public List<Entity> getEntities(Entity exclude, float x, float y, float z, float x2, float y2, float z2, List<Entity> result) {
		Slot slot = this.slot.init(this, x, y, z);
		Slot slot2 = this.slot2.init(this, x2, y2, z2);

		for (int var11 = slot.xSlot - 1; var11 <= slot2.xSlot + 1; ++var11) {
			for (int var12 = slot.ySlot - 1; var12 <= slot2.ySlot + 1; ++var12) {
				for (int var13 = slot.zSlot - 1; var13 <= slot2.zSlot + 1; ++var13) {
					if (var11 >= 0 && var12 >= 0 && var13 >= 0 && var11 < this.width && var12 < this.depth && var13 < this.height) {
						List<Entity> entities = this.entityGrid[(var13 * this.depth + var12) * this.width + var11];

						for (Entity entity : entities) {
							if (entity != exclude && entity.intersects(x, y, z, x2, y2, z2)) {
								result.add(entity);
							}
						}
					}
				}
			}
		}

		return result;
	}

	public void removeAllNonCreativeModeEntities() {
		List<Entity> cache = new ArrayList<Entity>();
		
		for (int x = 0; x < this.width; ++x) {
			for (int z = 0; z < this.depth; ++z) {
				for (int y = 0; y < this.height; ++y) {
					List<Entity> entities = this.entityGrid[(y * this.depth + z) * this.width + x];
					cache.addAll(entities);
					
					for (Entity entity : cache) {
						if (!entity.isCreativeModeAllowed()) {
							entities.remove(entity);
						}
					}
					
					cache.clear();
				}
			}
		}

		cache.addAll(this.all);
		for(Entity entity : cache) {
			if(!entity.isCreativeModeAllowed()) {
				this.all.remove(entity);
			}
		}
	}

	public void clear() {
		for (int x = 0; x < this.width; ++x) {
			for (int var2 = 0; var2 < this.depth; ++var2) {
				for (int var3 = 0; var3 < this.height; ++var3) {
					this.entityGrid[(var3 * this.depth + var2) * this.width + x].clear();
				}
			}
		}
	}

	public List<Entity> getEntities(Entity var1, AABB var2) {
		this.tmp.clear();
		return this.getEntities(var1, var2.x0, var2.y0, var2.z0, var2.x1, var2.y1, var2.z1, this.tmp);
	}

	public List<Entity> getEntities(Entity var1, AABB var2, List<Entity> var3) {
		return this.getEntities(var1, var2.x0, var2.y0, var2.z0, var2.x1, var2.y1, var2.z1, var3);
	}

	public void tickAll() {
		for (int var1 = 0; var1 < this.all.size(); ++var1) {
			Entity var2;
			(var2 = this.all.get(var1)).tick();
			if (var2.removed) {
				this.all.remove(var1--);
				this.slot.init(this, var2.xOld, var2.yOld, var2.zOld).remove(var2);
			} else {
				int var3 = (int) (var2.xOld / 16.0F);
				int var4 = (int) (var2.yOld / 16.0F);
				int var5 = (int) (var2.zOld / 16.0F);
				int var6 = (int) (var2.x / 16.0F);
				int var7 = (int) (var2.y / 16.0F);
				int var8 = (int) (var2.z / 16.0F);
				if (var3 != var6 || var4 != var7 || var5 != var8) {
					this.moved(var2);
				}
			}
		}

	}

	public void render(Vector model, ClippingHelper var2, TextureManager textureManager, float var4) {
		for (int var5 = 0; var5 < this.width; ++var5) {
			float var6 = ((var5 << 4) - 2);
			float var7 = ((var5 + 1 << 4) + 2);

			for (int var8 = 0; var8 < this.depth; ++var8) {
				float var9 = ((var8 << 4) - 2);
				float var10 = ((var8 + 1 << 4) + 2);

				for (int var11 = 0; var11 < this.height; ++var11) {
					List<?> var12;
					if ((var12 = this.entityGrid[(var11 * this.depth + var8) * this.width + var5]).size() != 0) {
						float var13 = ((var11 << 4) - 2);
						float var14 = ((var11 + 1 << 4) + 2);
						if (var2.isBoxInFrustrum(var6, var9, var13, var7, var10, var14)) {
							float var16 = var14;
							float var17 = var10;
							float var15 = var7;
							var14 = var13;
							var13 = var9;
							float var18 = var6;
							ClippingHelper var19 = var2;
							int var20 = 0;

							boolean var10000;
							while (true) {
								if (var20 >= 6) {
									var10000 = true;
									break;
								}

								if (var19.frustrum[var20][0] * var18 + var19.frustrum[var20][1] * var13 + var19.frustrum[var20][2] * var14 + var19.frustrum[var20][3] <= 0.0F) {
									var10000 = false;
									break;
								}

								if (var19.frustrum[var20][0] * var15 + var19.frustrum[var20][1] * var13 + var19.frustrum[var20][2] * var14 + var19.frustrum[var20][3] <= 0.0F) {
									var10000 = false;
									break;
								}

								if (var19.frustrum[var20][0] * var18 + var19.frustrum[var20][1] * var17 + var19.frustrum[var20][2] * var14 + var19.frustrum[var20][3] <= 0.0F) {
									var10000 = false;
									break;
								}

								if (var19.frustrum[var20][0] * var15 + var19.frustrum[var20][1] * var17 + var19.frustrum[var20][2] * var14 + var19.frustrum[var20][3] <= 0.0F) {
									var10000 = false;
									break;
								}

								if (var19.frustrum[var20][0] * var18 + var19.frustrum[var20][1] * var13 + var19.frustrum[var20][2] * var16 + var19.frustrum[var20][3] <= 0.0F) {
									var10000 = false;
									break;
								}

								if (var19.frustrum[var20][0] * var15 + var19.frustrum[var20][1] * var13 + var19.frustrum[var20][2] * var16 + var19.frustrum[var20][3] <= 0.0F) {
									var10000 = false;
									break;
								}

								if (var19.frustrum[var20][0] * var18 + var19.frustrum[var20][1] * var17 + var19.frustrum[var20][2] * var16 + var19.frustrum[var20][3] <= 0.0F) {
									var10000 = false;
									break;
								}

								if (var19.frustrum[var20][0] * var15 + var19.frustrum[var20][1] * var17 + var19.frustrum[var20][2] * var16 + var19.frustrum[var20][3] <= 0.0F) {
									var10000 = false;
									break;
								}

								++var20;
							}

							boolean var21 = var10000;

							for (int var23 = 0; var23 < var12.size(); ++var23) {
								Entity var22;
								if ((var22 = (Entity) var12.get(var23)).shouldRender(model)) {
									if (!var21) {
										AABB var24 = var22.bb;
										if (!var2.isBoxInFrustrum(var24.x0, var24.y0, var24.z0, var24.x1, var24.y1, var24.z1)) {
											continue;
										}
									}

									var22.render(textureManager, var4);
								}
							}
						}
					}
				}
			}
		}

	}
	
	public static class Slot implements Serializable {
		public static final long serialVersionUID = 0L;
		
		private BlockMap parent;
		private int xSlot;
		private int ySlot;
		private int zSlot;

		public Slot init(BlockMap parent, float x, float y, float z) {
			this.parent = parent;
			this.xSlot = (int) (x / 16);
			this.ySlot = (int) (y / 16);
			this.zSlot = (int) (z / 16);
			if (this.xSlot < 0) {
				this.xSlot = 0;
			}

			if (this.ySlot < 0) {
				this.ySlot = 0;
			}

			if (this.zSlot < 0) {
				this.zSlot = 0;
			}

			if (this.xSlot >= parent.width) {
				this.xSlot = parent.width - 1;
			}

			if (this.ySlot >= parent.depth) {
				this.ySlot = parent.depth - 1;
			}

			if (this.zSlot >= parent.height) {
				this.zSlot = parent.height - 1;
			}

			return this;
		}

		public void add(Entity entity) {
			if (this.xSlot >= 0 && this.ySlot >= 0 && this.zSlot >= 0) {
				parent.entityGrid[(this.zSlot * parent.depth + this.ySlot) * parent.width + this.xSlot].add(entity);
			}

		}

		public void remove(Entity entity) {
			if (this.xSlot >= 0 && this.ySlot >= 0 && this.zSlot >= 0) {
				parent.entityGrid[(this.zSlot * parent.depth + this.ySlot) * parent.width + this.xSlot].remove(entity);
			}
		}
	}
}
