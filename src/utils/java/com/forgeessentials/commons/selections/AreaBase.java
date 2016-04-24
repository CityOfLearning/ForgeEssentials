package com.forgeessentials.commons.selections;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AreaBase {

	private static final Pattern pattern = Pattern.compile("\\s*\\{\\s*(\\[.*\\])\\s*,\\s*(\\[.*\\])\\s*\\}\\s*");

	public static AreaBase fromString(String value) {
		Matcher match = pattern.matcher(value);
		if (!match.matches()) {
			return null;
		}
		Point p1 = Point.fromString(match.group(1));
		Point p2 = Point.fromString(match.group(2));
		if ((p1 == null) || (p2 == null)) {
			return null;
		}
		return new AreaBase(p1, p2);
	}

	/**
	 * Get the highest XYZ coordinate in OOBB [p1,p2]
	 */
	public static Point getMaxPoint(Point p1, Point p2) {
		return new Point(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()),
				Math.max(p1.getZ(), p2.getZ()));
	}

	/**
	 * Get the lowest XYZ coordinate in OOBB [p1,p2]
	 */
	public static Point getMinPoint(Point p1, Point p2) {
		return new Point(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()),
				Math.min(p1.getZ(), p2.getZ()));
	}

	protected Point high;

	protected Point low;

	/**
	 * Points are inclusive.
	 *
	 * @param p1
	 * @param p2
	 */
	public AreaBase(Point p1, Point p2) {
		low = getMinPoint(p1, p2);
		high = getMaxPoint(p1, p2);
	}

	/**
	 * checks if this area contains with another
	 *
	 * @param area
	 *            to check against this area
	 * @return True, AreaBAse area is completely within this area
	 */
	public boolean contains(AreaBase area) {
		return this.contains(area.high) && this.contains(area.low);
	}

	/**
	 * Determines if a given point is within the bounds of an area.
	 *
	 * @param p
	 *            Point to check against the Area
	 * @return True, if the Point p is inside the area.
	 */
	public boolean contains(Point p) {
		return high.isGreaterEqualThan(p) && low.isLessEqualThan(p);
	}

	public AreaBase copy() {
		return new AreaBase(low, high);
	}

	public Point getCenter() {
		return new Point((high.x + low.x) / 2, (high.y + low.y) / 2, (high.z + low.z) / 2);
	}

	public Point getHighPoint() {
		return high;
	}

	/**
	 * @param area
	 *            The area to be checked.
	 * @return NULL if the areas to do not intersect. Argument if this area
	 *         completely contains the argument.
	 */
	public AreaBase getIntersection(AreaBase area) {
		if (area == null) {
			return null;
		}

		boolean hasIntersection = false;
		Point p = new Point(0, 0, 0);
		Point minp = new Point(0, 0, 0);
		Point maxp = new Point(0, 0, 0);
		int[] xs = { low.x, high.x, area.low.x, area.high.x };
		int[] ys = { low.y, high.y, area.low.y, area.high.y };
		int[] zs = { low.z, high.z, area.low.z, area.high.z };

		for (int x : xs) {
			p.setX(x);
			for (int y : ys) {
				p.setY(y);
				for (int z : zs) {
					p.setZ(z);
					if (this.contains(p) && area.contains(p)) {
						if (!hasIntersection) {
							hasIntersection = true;
							minp = p;
							maxp = p;
						} else {
							minp = AreaBase.getMinPoint(minp, p);
							maxp = AreaBase.getMaxPoint(maxp, p);
						}
					}
				}
			}
		}

		if (!hasIntersection) {
			return null;
		} else {
			return new AreaBase(minp, maxp);
		}
	}

	public Point getLowPoint() {
		return low;
	}

	public Point getSize() {
		return new Point(high.x - low.x, high.y - low.y, high.z - low.z);
	}

	/**
	 * @param area
	 *            The area to be checked.
	 * @return NULL if the areas to do not make a cuboid together.
	 */
	public AreaBase getUnity(AreaBase area) {
		if (!makesCuboidWith(area)) {
			return null;
		} else {
			return new AreaBase(getMinPoint(low, area.low), getMaxPoint(high, area.high));
		}
	}

	public int getXLength() {
		return (high.getX() - low.getX()) + 1;
	}

	public int getYLength() {
		return (high.getY() - low.getY()) + 1;
	}

	public int getZLength() {
		return (high.getZ() - low.getZ()) + 1;
	}

	/**
	 * checks if this area is overlapping with another
	 *
	 * @param area
	 *            to check against this area
	 * @return True, if the given area overlaps with this one.
	 */
	public boolean intersectsWith(AreaBase area) {
		return getIntersection(area) != null;
	}

	public boolean makesCuboidWith(AreaBase area) {
		boolean alignX = (low.getX() == area.low.getX()) && (high.getX() == area.high.getX());
		boolean alignY = (low.getY() == area.low.getY()) && (high.getY() == area.high.getY());
		boolean alignZ = (low.getZ() == area.low.getZ()) && (high.getZ() == area.high.getZ());

		return alignX || alignY || alignZ;
	}

	public void redefine(Point p1, Point p2) {
		low = getMinPoint(p1, p2);
		high = getMaxPoint(p1, p2);
	}

	@Override
	public String toString() {
		return "{" + high.toString() + " , " + low.toString() + " }";
	}
}
