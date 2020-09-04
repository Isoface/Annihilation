package com.hotmail.AdrianSRJose.AnniPro.utils;

public class Perm implements Comparable<Perm> {
	public final String perm;
	public final double multiplier;

	public Perm(String perm, int mult) {
		this.perm = perm;
		multiplier = mult;
	}

	@Override
	public int compareTo(Perm arg0) {
		return Double.compare(multiplier, arg0.multiplier);
	}
}
