/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
 * Copyright (c) 2015, Technion � Israel Institute of Technology
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *******************************************************************************/
package entities;

import java.util.ArrayList;
import java.util.List;

import manager.SSDManager;

import org.javatuples.Pair;

import utils.Utils;

public abstract class Plane<P extends Page, B extends Block<P>> {
	public abstract static class Builder<P extends Page, B extends Block<P>> {
		private Plane<P,B> plane;

		abstract public Plane<P,B> build();
		
		public Builder<P,B> setManager(SSDManager<P,B,?,?,?> manager) {
			plane.manager = manager;
			return this;
		}
		
		public Builder<P,B> setBlocks(List<B> blocksList) {
			plane.blocksList = new ArrayList<B>(blocksList);
			return this;
		}
		
		protected void setPlane(Plane<P,B> plane) {
			this.plane = plane;
		}

		protected void validate() {
			Utils.validateNotNull(plane.manager, "manager");
		}
	}

	private List<B> blocksList;
	private SSDManager<P,B,?,?,?> manager;

	private int validPagesCounter = 0;
	private int numOfClean = 0;
	private int activeBlockIndex = -1;
	private int lowestEraseCleanBlockIndex = -1;
	private int lowestValidBlockIndex = -1;

	protected Plane() {}

	protected Plane(Plane<P,B> other) {
		this.blocksList = new ArrayList<B>(other.blocksList);
		this.manager = other.manager;
		initValues();
	}
	
	abstract public Builder<P,B> getSelfBuilder();
	abstract public Plane<P,B> writeLP(int lp, int arg);
	abstract protected Pair<? extends Plane<P, B>, Integer> cleanPlane();

	public Iterable<B> getBlocks() {
		return blocksList;
	}
	
	public B getBlock(int i) {
		return blocksList.get(i);
	}
	
	public int getBlocksNum() {
		return blocksList.size();
	}
	
	public int getValidPagesCounter() {
		return validPagesCounter;
	}
	
	public int getPagesInBlock() {
		if(blocksList.isEmpty()) {
			return 0;
		}
		return blocksList.get(0).getPagesNum();
	}

	public List<B> getNewBlocksList() {
		return new ArrayList<B>(blocksList);
	}
	
	@SuppressWarnings("unchecked")
	public Plane<P,B> invalidate(int lp) {
		List<B> updatedBlocks = new ArrayList<B>();
		for (B block : getBlocks()) {
			updatedBlocks.add((B) block.invalidate(lp));
		}
		Builder<P,B> builder = getSelfBuilder();
		builder.setBlocks(updatedBlocks);
		return builder.build();
	}
	
	
	public Pair<? extends Plane<P,B>, Integer> clean() {
		if(!invokeCleaning()) {
			return new Pair<Plane<P, B>, Integer>(this, 0);
		}
		return cleanPlane();
	}

	protected int getWritableBlocksNum() {
		return getNumOfClean();
	}

	protected int getNumOfClean() {
		return numOfClean;
	}
	
	protected int getActiveBlockIndex() {
		return activeBlockIndex;
	}

	protected int getLowestValidBlockIndex() {
		return lowestValidBlockIndex;
	}
	
	protected int getLowestEraseCleanBlockIndex() {
		return lowestEraseCleanBlockIndex;
	}
	
	/**
	 * Choose a victim block for garbage collection
	 * @param plane
	 * @return block index of block with smallest sum of valid counters
	 */
	protected Pair<Integer, B> pickBlockToClean() {
		return new Pair<Integer, B>(getLowestValidBlockIndex(), getBlock(getLowestValidBlockIndex()));
	}
	
	protected boolean invokeCleaning() {
		return getWritableBlocksNum() < manager.getGCT();
	}

	protected boolean isUsed(B block) {
		return block.getStatus() == BlockStatusGeneral.USED;
	}

	private void initValues() {
		int minValid = Integer.MAX_VALUE;
		int minEraseClean = Integer.MAX_VALUE;

		int i = 0;
		for (B block : getBlocks()) {
			validPagesCounter += block.getValidCounter();
			if (block.getStatus() == BlockStatusGeneral.ACTIVE) {
				activeBlockIndex = i;
			}else if (block.getStatus() == BlockStatusGeneral.CLEAN) {
				if (block.getEraseCounter() < minEraseClean) {
					minEraseClean = block.getEraseCounter();
					lowestEraseCleanBlockIndex = i;
				}
				++numOfClean;
			} else if(isUsed(block)) {
				if (block.getValidCounter() < minValid) {
					minValid = block.getValidCounter();
					lowestValidBlockIndex = i;
				}
			}
			++i;
		}
	}
}