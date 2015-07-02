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

import java.awt.Color;
import java.awt.TexturePaint;

import utils.UIUtils;
import utils.Utils;


public abstract class Page {
	public static abstract class Builder {
		private Page page;
		
		abstract public Page build();

		protected void setPage(Page page) {
			this.page = page;
		}
		
		public Builder setClean(boolean isClean) {
			page.isClean = isClean;
			return this;
		}
		
		public Builder setGC(boolean isGC) {
			page.isGC = isGC;
			return this;
		}
		
		public Builder setValid(boolean isValid) {
			page.isValid = isValid;
			return this;
		}

		public Builder setLp(int lp) {
			Utils.validateNotNegative(lp, "logical page");
			page.lp = lp;
			return this;
		}
	}
	
	private int lp = -1;
	private boolean isClean = true;
	private boolean isValid = false;
	private boolean isGC = false;
	
	protected Page() {}
	
	protected Page(Page other) {
		isClean = other.isClean;
		isValid = other.isValid;
		isGC = other.isGC;
		lp = other.lp;
	}


	abstract public Color getBGColor();
	abstract public Builder getSelfBuilder();

	public boolean isClean() {
		return isClean;
	}

	public boolean isGC() {
		return isGC;
	}

	public int getLp() {
		return lp;
	}

	public boolean isValid() {
		return isValid;
	}

	public String getTitle() {
		if (isClean()) {
			return "";
		}
		return "" + lp;
	}
	
	public TexturePaint getPageTexture(Color color) {
		if (isGC()) {
			return UIUtils.getGCTexture(color);
		} 
		return null;
	}
}