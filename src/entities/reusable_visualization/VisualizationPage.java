/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Or Mauda, Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
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
package entities.reusable_visualization;

import java.awt.Color;
import java.awt.TexturePaint;

import manager.ReusableVisualizationSSDManager;
import utils.UIUtils;
import utils.Utils;
import entities.Page;

public class VisualizationPage extends Page {
	public static class Builder extends Page.Builder {
		private VisualizationPage page;
		
		public Builder() {
			setPage(new VisualizationPage());
		}
		
		Builder(VisualizationPage page) {
			setPage(new VisualizationPage(page));
		}

		protected void setPage(VisualizationPage page) {
			super.setPage(page);
			this.page = page;
		}

		public Builder setWriteLevel(int writeLevel) {
			if(writeLevel <= 0 || writeLevel >2) {
				throw new IllegalArgumentException("Write Level should be 1 or 2, given: "+writeLevel);
			}
			page.writeLevel = writeLevel;
			return this;
		}

		public Builder setGcWriteLevel(int gcWriteLevel) {
//			Utils.validateNotNegative(gcWriteLevel, "gc write level");
			page.gcWriteLevel = gcWriteLevel;
			return this;
		}
		
		public Builder setIsPartial(boolean isPartial) {
			page.isPartial = isPartial;
			return this;
		}
		
		public Builder setManager(ReusableVisualizationSSDManager manager) {
			page.manager = manager;
			return this;
		}
		
		public VisualizationPage build() {
			validate();
			return new VisualizationPage(page);
		}
		
		protected void validate() {
			Utils.validateNotNull(page.manager, "manager");
		}
	}
	
	private int writeLevel = -1;
	private int gcWriteLevel = -1;
	private boolean isPartial = false;
	private ReusableVisualizationSSDManager manager = null;
	
	VisualizationPage() {}
	
	VisualizationPage(VisualizationPage other) {
		super(other);
		writeLevel = other.writeLevel;
		gcWriteLevel = other.gcWriteLevel;
		isPartial = other.isPartial;
		manager = other.manager;
	}

	public int getWriteLevel() {
		return writeLevel;
	}
	
	public Builder getSelfBuilder() {
		return new Builder(this);
	}

	public int getGcWriteLevel() {
		return gcWriteLevel;
	}
	
	public boolean isPartial() {
		return isPartial;
	}

	@Override
	public Color getBGColor() {
		if (isClean()) {
			return manager.getCleanColor();
		} else if (isGC()){
			return manager.getWriteLevelColor(gcWriteLevel);
		} else {
			return manager.getWriteLevelColor(writeLevel);
		}
	}

	@Override
	public TexturePaint getPageTexture(Color color) {
		if (isPartial) {
			return UIUtils.getGCTexture(color);
		}
		return super.getPageTexture(color);
	}
}
