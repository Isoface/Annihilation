package com.hotmail.AdrianSRJose.base;

import org.bukkit.util.Vector;

public enum Direction {
	North, South, East, West, NorthWest, NorthEast, SouthWest, SouthEast;

	public Vector getVector() {
		switch (this) {
		case North:
		default:
			return new Vector(0, 0, -1);
		case South:
			return new Vector(0, 0, 1);
		case West:
			return new Vector(-1, 0, 0);
		case East:
			return new Vector(1, 0, 0);
		case NorthWest:
			return new Vector(-1, 0, -1);
		case NorthEast:
			return new Vector(1, 0, -1);
		case SouthWest:
			return new Vector(-1, 0, 1);
		case SouthEast:
			return new Vector(1, 0, 1);
		}
	}

	public static Direction getDirection(Vector vec) {
		Vector k = vec.normalize();
		int x = k.getBlockX();
		int z = k.getBlockZ();
		if (z < 1) {
			if (x < 0)
				return Direction.NorthWest;
			else if (x > 0)
				return Direction.NorthEast;
			else
				return Direction.North;
		} else if (z > 1) {
			if (x < 0)
				return Direction.SouthWest;
			else if (x > 0)
				return Direction.SouthEast;
			else
				return Direction.South;
		} else if (x < 0)
			return Direction.West;
		else
			return Direction.East;
	}

	public static Direction getOpposite(Direction direc) {
		switch (direc) {
		default:
			return null;
		case North:
			return Direction.South;
		case South:
			return Direction.North;
		case East:
			return Direction.West;
		case West:
			return Direction.East;
		case NorthWest:
			return Direction.SouthEast;
		case NorthEast:
			return Direction.SouthWest;
		case SouthWest:
			return Direction.NorthEast;
		case SouthEast:
			return Direction.NorthWest;
		}
	}

	public static Direction getOpposite(Vector vec) {
		return Direction.getOpposite(Direction.getDirection(vec));
	}
}
