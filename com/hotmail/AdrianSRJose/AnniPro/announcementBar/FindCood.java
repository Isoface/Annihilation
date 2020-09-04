package com.hotmail.AdrianSRJose.AnniPro.announcementBar;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FindCood {
	public static Location getFindCood3D(Player player, int Distance) {
		return getFindCood3D((Entity) player, (double) Distance);
	}

	public static Location getFindCood3D(Player player, double Distance) {
		return getFindCood3D((Entity) player, Distance);
	}

	public static Location getFindCood3D(Entity entity, int Distance) {
		return getFindCood3D(entity, (double) Distance);
	}

	public static Location getFindCood3D(Entity entity, double Distance) {
		return findCood(entity.getLocation(), Distance);
	}

	public static Location getFindCood2D(Player player, int Distance) {
		return getFindCood2D((Entity) player, (double) Distance);
	}

	public static Location getFindCood2D(Player player, double Distance) {
		return getFindCood2D((Entity) player, Distance);
	}

	public static Location getFindCood2D(Entity entity, int Distance) {
		return getFindCood2D(entity, (double) Distance);
	}

	public static Location getFindCood2D(Entity entity, double Distance) {
		Location loc = entity.getLocation().clone();
		loc.setPitch(0F);
		return findCood(loc, Distance);
	}

	public static Location findCood(Location loc, double Distance) {
		double Yaw = loc.getYaw();
		if (Yaw > 0)
			Yaw = Yaw - 360;
		double Y = Math.toRadians(Math.abs(Yaw));
		double P = Math.toRadians(loc.getPitch());
		double d = Math.abs(Math.cos(P) * Distance);
		double z = Math.cos(Y) * d;
		double x = Math.sin(Y) * d;
		Location nl = loc.clone().add(x, 0.0D, z);
		return nl;
	}

	public static Location findCoodY(Location loc, double Distance) {
		double P = Math.toRadians(loc.getYaw());
		double y = (Math.sin(P) * (Distance / 6.0));
		//
		if (Math.abs(loc.getYaw()) >= 0 && Math.abs(loc.getYaw()) <= 50.0
				|| Math.abs(loc.getYaw()) <= 360.0 && Math.abs(loc.getYaw()) >= 310.0) {
			y = (Math.sin(P) * Distance);

			if (Math.abs(loc.getYaw()) >= 0 && Math.abs(loc.getYaw()) <= 4.0)
				y = (Math.sin(P) * (Distance * 6));
			else if (Math.abs(loc.getYaw()) <= 360.0 && Math.abs(loc.getYaw()) >= 345.0)
				y = (Math.sin(P) * (Distance * 6));
		}
		//
		else if (Math.abs(loc.getYaw()) >= 120.0 && Math.abs(loc.getYaw()) <= 230.0)
			y = (Math.sin(P) * Distance);
		//
		if (!(Math.abs(loc.getYaw()) >= 180.0 && Math.abs(loc.getYaw()) <= 360.0))
			y = y * -1;
		//
		return loc.clone().add(0, y, 0);
	}
}
