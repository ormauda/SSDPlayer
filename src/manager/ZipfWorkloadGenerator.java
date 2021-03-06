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
package manager;

import java.text.DecimalFormat;

import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;

import entities.Block;
import entities.Chip;
import entities.Device;
import entities.Page;
import entities.Plane;

public class ZipfWorkloadGenerator <P extends Page, B extends Block<P>, T extends Plane<P,B>, C extends Chip<P,B,T>, D extends Device<P,B,T,C>, S extends SSDManager<P, B, T, C, D>> 
	extends WorkloadGenerator<P,B,T,C,D,S> {
	private ZipfDistribution zipf;
	private int seed;
	private double exponent;

	public ZipfWorkloadGenerator(S manager, int traceLength, int seed, double exponent, int maxWriteSize, boolean isWriteSizeUniform) {
		super("Zipf", manager, traceLength, maxWriteSize, isWriteSizeUniform);
		this.seed = seed;
		this.exponent = exponent;
		JDKRandomGenerator jdkRandomGenerator = new JDKRandomGenerator();
		jdkRandomGenerator.setSeed(seed);
		zipf = new ZipfDistribution(jdkRandomGenerator, lpRange, exponent);
	}

	@Override
	protected int getLP() {
		return zipf.sample()-1;
	}
	
	public String getName() {
		String string = super.getName() + "_seed(" + seed + ")" + "_exp(" + new DecimalFormat("0.##").format(exponent) + ")";
		return string;
	}
}
