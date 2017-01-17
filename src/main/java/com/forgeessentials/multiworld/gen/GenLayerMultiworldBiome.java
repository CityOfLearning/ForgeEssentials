package com.forgeessentials.multiworld.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.forgeessentials.multiworld.WorldServerMultiworld;

import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

/**
 *
 * @author Olee
 */
public class GenLayerMultiworldBiome extends GenLayer {

	protected Random random = new Random();

	@SuppressWarnings("unchecked")
	protected List<BiomeEntry>[] biomes = new ArrayList[BiomeManager.BiomeType.values().length];

	public GenLayerMultiworldBiome(long seed, GenLayer parent, WorldServerMultiworld currentMultiworld) {
		super(seed);
		this.parent = parent;

		for (BiomeManager.BiomeType type : BiomeManager.BiomeType.values()) {
			biomes[type.ordinal()] = new ArrayList<>();
		}
		biomes[BiomeManager.BiomeType.DESERT.ordinal()].add(new BiomeEntry(BiomeGenBase.plains, 10));
		biomes[BiomeManager.BiomeType.WARM.ordinal()].add(new BiomeEntry(BiomeGenBase.plains, 10));
		biomes[BiomeManager.BiomeType.COOL.ordinal()].add(new BiomeEntry(BiomeGenBase.plains, 10));
		biomes[BiomeManager.BiomeType.ICY.ordinal()].add(new BiomeEntry(BiomeGenBase.plains, 10));
	}

	/**
	 * Returns a list of integer values generated by this layer. These may be
	 * interpreted as temperatures, rainfall amounts, or biomeList[] indices
	 * based on the particular GenLayer subclass.
	 */
	@Override
	public int[] getInts(int p_75904_1_, int p_75904_2_, int p_75904_3_, int p_75904_4_) {
		int[] aint = parent.getInts(p_75904_1_, p_75904_2_, p_75904_3_, p_75904_4_);
		int[] aint1 = IntCache.getIntCache(p_75904_3_ * p_75904_4_);

		for (int i1 = 0; i1 < p_75904_4_; ++i1) {
			for (int j1 = 0; j1 < p_75904_3_; ++j1) {
				initChunkSeed(j1 + p_75904_1_, i1 + p_75904_2_);
				int k1 = aint[j1 + (i1 * p_75904_3_)];
				int l1 = (k1 & 3840) >> 8;
				k1 &= -3841;

				if (isBiomeOceanic(k1)) {
					aint1[j1 + (i1 * p_75904_3_)] = k1;
				} else if (k1 == BiomeGenBase.mushroomIsland.biomeID) {
					aint1[j1 + (i1 * p_75904_3_)] = k1;
				} else if (k1 == 1) {
					if (l1 > 0) {
						if (nextInt(3) == 0) {
							aint1[j1 + (i1 * p_75904_3_)] = BiomeGenBase.mesaPlateau.biomeID;
						} else {
							aint1[j1 + (i1 * p_75904_3_)] = BiomeGenBase.mesaPlateau_F.biomeID;
						}
					} else {
						aint1[j1 + (i1 * p_75904_3_)] = getWeightedBiomeEntry(
								BiomeManager.BiomeType.DESERT).biome.biomeID;
					}
				} else if (k1 == 2) {
					if (l1 > 0) {
						aint1[j1 + (i1 * p_75904_3_)] = BiomeGenBase.jungle.biomeID;
					} else {
						aint1[j1 + (i1 * p_75904_3_)] = getWeightedBiomeEntry(
								BiomeManager.BiomeType.WARM).biome.biomeID;
					}
				} else if (k1 == 3) {
					if (l1 > 0) {
						aint1[j1 + (i1 * p_75904_3_)] = BiomeGenBase.megaTaiga.biomeID;
					} else {
						aint1[j1 + (i1 * p_75904_3_)] = getWeightedBiomeEntry(
								BiomeManager.BiomeType.COOL).biome.biomeID;
					}
				} else if (k1 == 4) {
					aint1[j1 + (i1 * p_75904_3_)] = getWeightedBiomeEntry(BiomeManager.BiomeType.ICY).biome.biomeID;
				} else {
					aint1[j1 + (i1 * p_75904_3_)] = BiomeGenBase.mushroomIsland.biomeID;
				}
			}
		}

		return aint1;
	}

	protected BiomeEntry getWeightedBiomeEntry(BiomeManager.BiomeType type) {
		List<BiomeEntry> biomeList = biomes[type.ordinal()];
		int totalWeight = WeightedRandom.getTotalWeight(biomeList);
		int rand = nextInt(totalWeight / 10) * 10;
		int weight = rand + (BiomeManager.isTypeListModded(type) ? nextInt(Math.min(10, totalWeight - rand)) : 0);
		return WeightedRandom.getRandomItem(random, biomeList, weight);
	}

}
