package org.ivis.util;

import java.awt.geom.Line2D;
import java.awt.Point;

/**
 * This class maintains a list of static geometry related utility methods.
 *
 * @author Ugur Dogrusoz
 * @author Esat Belviranli
 * @author Shatlyk Ashyralyev
 *
 * Copyright: i-Vis Research Group, Bilkent University, 2007 - present
 */
abstract public class IGeometry
{

	public static final double HALF_PI = 0.5 * Math.PI;
	public static final double ONE_AND_HALF_PI = 1.5 * Math.PI;
	public static final double TWO_PI = 2.0 * Math.PI;
	public static final double THREE_PI = 3.0 * Math.PI;


	/**
	 * This method calculates *half* the amount in x and y directions of the two
	 * input rectangles needed to separate them keeping their respective
	 * positioning, and returns the result in the input array. An input
	 * separation buffer added to the amount in both directions. We assume that
	 * the two rectangles do intersect.
	 * @param rectA rectangle A
	 * @param rectB rectangle B
	 * @param overlapAmount overlap
	 * @param separationBuffer separation buffer
	 */
	public static void calcSeparationAmount(RectangleD rectA, RectangleD rectB,
		double[] overlapAmount, double separationBuffer)
	{
		assert rectA.intersects(rectB);

		double[] directions = new double[2];

		IGeometry.decideDirectionsForOverlappingNodes(rectA, rectB, directions);
		
		overlapAmount[0] = Math.min(rectA.getRight(), rectB.getRight()) -
			Math.max(rectA.x, rectB.x);
		overlapAmount[1] = Math.min(rectA.getBottom(), rectB.getBottom()) -
			Math.max(rectA.y, rectB.y);
		
		// update the overlapping amounts for the following cases:
		
		if ( (rectA.getX() <= rectB.getX()) && (rectA.getRight() >= rectB.getRight()) )
		/* Case x.1:
		 *
		 * rectA
		 * 	|                       |
		 * 	|        _________      |
		 * 	|        |       |      |
		 * 	|________|_______|______|
		 * 			 |       |
		 *           |       |
		 *        rectB
		 */
		{
			overlapAmount[0] += Math.min((rectB.getX() - rectA.getX()),
				(rectA.getRight() - rectB.getRight()));
		}
		else if( (rectB.getX() <= rectA.getX()) && (rectB.getRight() >= rectA.getRight()))
		/* Case x.2:
		 *
		 * rectB
		 * 	|                       |
		 * 	|        _________      |
		 * 	|        |       |      |
		 * 	|________|_______|______|
		 * 			 |       |
		 *           |       |
		 *        rectA
		 */
		{
			overlapAmount[0] += Math.min((rectA.getX() - rectB.getX()),
				(rectB.getRight() - rectA.getRight()));
		}
		
		if ( (rectA.getY() <= rectB.getY()) && (rectA.getBottom() >= rectB.getBottom()) )
		/* Case y.1:
		 *          ________ rectA
		 *         |
		 *         |
		 *   ______|____  rectB
		 *         |    |
		 *         |    |
		 *   ______|____|
		 *         |
		 *         |
		 *         |________
		 *
		 */
		{
			overlapAmount[1] += Math.min((rectB.getY() - rectA.getY()),
				(rectA.getBottom() - rectB.getBottom()));
		}
		else if ((rectB.getY() <= rectA.getY()) && (rectB.getBottom() >= rectA.getBottom()) )
		/* Case y.2:
		 *          ________ rectB
		 *         |
		 *         |
		 *   ______|____  rectA
		 *         |    |
		 *         |    |
		 *   ______|____|
		 *         |
		 *         |
		 *         |________
		 *
		 */
		{
			overlapAmount[1] += Math.min((rectA.getY() - rectB.getY()),
				(rectB.getBottom() - rectA.getBottom()));
		}
		
		// find slope of the line passes two centers
        double slope =
			Math.abs((double)(rectB.getCenterY() - rectA.getCenterY()) /
        		(rectB.getCenterX() - rectA.getCenterX()));
        
        // if centers are overlapped
        if ((rectB.getCenterY() == rectA.getCenterY()) &&
			(rectB.getCenterX() == rectA.getCenterX()) )
        {
        	// assume the slope is 1 (45 degree)
        	slope = 1.0;
        }
        
		// change y
        double moveByY = slope * overlapAmount[0];
        // change x
        double moveByX =  overlapAmount[1] / slope;
        
        // now we have two pairs:
        // 1) overlapAmount[0], moveByY
        // 2) moveByX, overlapAmount[1]
     
        // use pair no:1
        if (overlapAmount[0] < moveByX)
        {
        	moveByX = overlapAmount[0];
        }
        // use pair no:2
        else
        {
        	moveByY = overlapAmount[1];
        }

		// return half the amount so that if each rectangle is moved by these
		// amounts in opposite directions, overlap will be resolved
		
        overlapAmount[0] = -1 * directions[0] * ((moveByX / 2) + separationBuffer);
        overlapAmount[1] = -1 * directions[1] * ((moveByY / 2) + separationBuffer);
	}

	/**
     * This method decides the separation direction of overlapping nodes
     * 
     * if directions[0] = -1, then rectA goes left
     * if directions[0] = 1,  then rectA goes right
     * if directions[1] = -1, then rectA goes up
     * if directions[1] = 1,  then rectA goes down
     */
    private static void decideDirectionsForOverlappingNodes(RectangleD rectA,
		RectangleD rectB,
		double[] directions)
    {
    	if (rectA.getCenterX() < rectB.getCenterX())
    	{
    		directions[0] = -1;
    	}
    	else
    	{
    		directions[0] = 1;
    	}
    	
    	if (rectA.getCenterY() < rectB.getCenterY())
    	{
    		directions[1] = -1;
    	}
    	else
    	{
    		directions[1] = 1;
    	}
    }
    
	/**
	 * This method calculates the intersection (clipping) points of the two
	 * input rectangles with line segment defined by the centers of these two
	 * rectangles. The clipping points are saved in the input double array and
	 * whether or not the two rectangles overlap is returned.
	 * @param rectA rectangle A
	 * @param rectB rectangle B
	 * @param result intersection points array
	 *
	 * @return true when A and B overlap; false otherwise
	 */
	public static boolean getIntersection(RectangleD rectA,
		RectangleD rectB,
		double[] result)
	{
		//result[0-1] will contain clipPoint of rectA, result[2-3] will contain clipPoint of rectB

		double p1x = rectA.getCenterX();
		double p1y = rectA.getCenterY();		
		double p2x = rectB.getCenterX();
		double p2y = rectB.getCenterY();
		
		//if two rectangles intersect, then clipping points are centers
		if (rectA.intersects(rectB))
		{
			result[0] = p1x;
			result[1] = p1y;
			result[2] = p2x;
			result[3] = p2y;
			return true;
		}
		
		//variables for rectA
		double topLeftAx = rectA.getX();
		double topLeftAy = rectA.getY();
		double topRightAx = rectA.getRight();
		double bottomLeftAx = rectA.getX();
		double bottomLeftAy = rectA.getBottom();
		double bottomRightAx = rectA.getRight();
		double halfWidthA = rectA.getWidthHalf();
		double halfHeightA = rectA.getHeightHalf();
		
		//variables for rectB
		double topLeftBx = rectB.getX();
		double topLeftBy = rectB.getY();
		double topRightBx = rectB.getRight();
		double bottomLeftBx = rectB.getX();
		double bottomLeftBy = rectB.getBottom();
		double bottomRightBx = rectB.getRight();
		double halfWidthB = rectB.getWidthHalf();
		double halfHeightB = rectB.getHeightHalf();

		//flag whether clipping points are found
		boolean clipPointAFound = false;
		boolean clipPointBFound = false;
		

		// line is vertical
		if (p1x == p2x)
		{
			if(p1y > p2y)
			{
				result[0] = p1x;
				result[1] = topLeftAy;
				result[2] = p2x;
				result[3] = bottomLeftBy;
				return false;
			}
			else if(p1y < p2y)
			{
				result[0] = p1x;
				result[1] = bottomLeftAy;
				result[2] = p2x;
				result[3] = topLeftBy;
				return false;
			}
			else
			{
				//not line, return null;
			}
		}
		// line is horizontal
		else if (p1y == p2y)
		{
			if(p1x > p2x)
			{
				result[0] = topLeftAx;
				result[1] = p1y;
				result[2] = topRightBx;
				result[3] = p2y;
				return false;
			}
			else if(p1x < p2x)
			{
				result[0] = topRightAx;
				result[1] = p1y;
				result[2] = topLeftBx;
				result[3] = p2y;
				return false;
			}
			else
			{
				//not valid line, return null;
			}
		}
		else
		{
			//slopes of rectA's and rectB's diagonals
			double slopeA = rectA.height / rectA.width;
			double slopeB = rectB.height / rectB.width;
			
			//slope of line between center of rectA and center of rectB
			double slopePrime = (p2y - p1y) / (p2x - p1x);
			int cardinalDirectionA;
			int cardinalDirectionB;
			double tempPointAx;
			double tempPointAy;
			double tempPointBx;
			double tempPointBy;
			
			//determine whether clipping point is the corner of nodeA
			if((-slopeA) == slopePrime)
			{
				if(p1x > p2x)
				{
					result[0] = bottomLeftAx;
					result[1] = bottomLeftAy;
					clipPointAFound = true;
				}
				else
				{
					result[0] = topRightAx;
					result[1] = topLeftAy;
					clipPointAFound = true;
				}
			}
			else if(slopeA == slopePrime)
			{
				if(p1x > p2x)
				{
					result[0] = topLeftAx;
					result[1] = topLeftAy;
					clipPointAFound = true;
				}
				else
				{
					result[0] = bottomRightAx;
					result[1] = bottomLeftAy;
					clipPointAFound = true;
				}
			}
			
			//determine whether clipping point is the corner of nodeB
			if((-slopeB) == slopePrime)
			{
				if(p2x > p1x)
				{
					result[2] = bottomLeftBx;
					result[3] = bottomLeftBy;
					clipPointBFound = true;
				}
				else
				{
					result[2] = topRightBx;
					result[3] = topLeftBy;
					clipPointBFound = true;
				}
			}
			else if(slopeB == slopePrime)
			{
				if(p2x > p1x)
				{
					result[2] = topLeftBx;
					result[3] = topLeftBy;
					clipPointBFound = true;
				}
				else
				{
					result[2] = bottomRightBx;
					result[3] = bottomLeftBy;
					clipPointBFound = true;
				}
			}
			
			//if both clipping points are corners
			if(clipPointAFound && clipPointBFound)
			{
				return false;
			}
			
			//determine Cardinal Direction of rectangles
			if(p1x > p2x)
			{
				if(p1y > p2y)
				{
					cardinalDirectionA = getCardinalDirection(slopeA, slopePrime, 4);
					cardinalDirectionB = getCardinalDirection(slopeB, slopePrime, 2);
				}
				else
				{
					cardinalDirectionA = getCardinalDirection(-slopeA, slopePrime, 3);
					cardinalDirectionB = getCardinalDirection(-slopeB, slopePrime, 1);
				}
			}
			else
			{
				if(p1y > p2y)
				{
					cardinalDirectionA = getCardinalDirection(-slopeA, slopePrime, 1);
					cardinalDirectionB = getCardinalDirection(-slopeB, slopePrime, 3);
				}
				else
				{
					cardinalDirectionA = getCardinalDirection(slopeA, slopePrime, 2);
					cardinalDirectionB = getCardinalDirection(slopeB, slopePrime, 4);
				}
			}
			//calculate clipping Point if it is not found before
			if(!clipPointAFound)
			{
				switch(cardinalDirectionA)
				{
					case 1:
						tempPointAy = topLeftAy;
						tempPointAx = p1x + ( -halfHeightA ) / slopePrime;
						result[0] = tempPointAx;
						result[1] = tempPointAy;
						break;
					case 2:
						tempPointAx = bottomRightAx;
						tempPointAy = p1y + halfWidthA * slopePrime;
						result[0] = tempPointAx;
						result[1] = tempPointAy;
						break;
					case 3:
						tempPointAy = bottomLeftAy;
						tempPointAx = p1x + halfHeightA / slopePrime;
						result[0] = tempPointAx;
						result[1] = tempPointAy;
						break;
					case 4:
						tempPointAx = bottomLeftAx;
						tempPointAy = p1y + ( -halfWidthA ) * slopePrime;
						result[0] = tempPointAx;
						result[1] = tempPointAy;
						break;
				}
			}
			if(!clipPointBFound)
			{
				switch(cardinalDirectionB)
				{
					case 1:
						tempPointBy = topLeftBy;
						tempPointBx = p2x + ( -halfHeightB ) / slopePrime;
						result[2] = tempPointBx;
						result[3] = tempPointBy;
						break;
					case 2:
						tempPointBx = bottomRightBx;
						tempPointBy = p2y + halfWidthB * slopePrime;
						result[2] = tempPointBx;
						result[3] = tempPointBy;
						break;
					case 3:
						tempPointBy = bottomLeftBy;
						tempPointBx = p2x + halfHeightB / slopePrime;
						result[2] = tempPointBx;
						result[3] = tempPointBy;
						break;
					case 4:
						tempPointBx = bottomLeftBx;
						tempPointBy = p2y + ( -halfWidthB ) * slopePrime;
						result[2] = tempPointBx;
						result[3] = tempPointBy;
						break;
				}
			}
			
		}
		
		return false;
	}
	
	/**
	 * This method returns in which cardinal direction does input point stays
	 * 1: North
	 * 2: East
	 * 3: South
	 * 4: West
	 */
	private static int getCardinalDirection(double slope,
		double slopePrime,
		int line)
	{
		if (slope > slopePrime)
		{
			return line;
		}
		else
		{
			return 1 + line % 4;
		}
	}
	
	/**
	 * This method calculates the intersection of the two lines defined by
	 * point pairs (s1,s2) and (f1,f2).
	 *
	 * @param s1 the first line point 1
	 * @param s2 the first line point 2
	 * @param f1 the second line point 1
	 * @param f2 the second line point 2
	 *
	 * @return the intersection point
	 */
	public static Point getIntersection(Point s1, Point s2, Point f1, Point f2)
	{
		int x1 = s1.x;
		int y1 = s1.y;
		int x2 = s2.x;
		int y2 = s2.y;
		int x3 = f1.x;
		int y3 = f1.y;
		int x4 = f2.x;
		int y4 = f2.y;

		int x, y; // intersection point

		int a1, a2, b1, b2, c1, c2; // coefficients of line eqns.

		int denom;

		a1 = y2 - y1;
		b1 = x1 - x2;
		c1 = x2 * y1 - x1 * y2;  // { a1*x + b1*y + c1 = 0 is line 1 }

		a2 = y4 - y3;
		b2 = x3 - x4;
		c2 = x4 * y3 - x3 * y4;  // { a2*x + b2*y + c2 = 0 is line 2 }

		denom = a1 * b2 - a2 * b1;

		if (denom == 0)
		{
			return null;
		}

		x = (b1 * c2 - b2 * c1) / denom;
		y = (a2 * c1 - a1 * c2) / denom;

		return new Point(x, y);
	}

	/**
	 * This method finds and returns the angle of the vector from the + x-axis
	 * in clockwise direction (compatible w/ Java coordinate system!).
	 *
	 * @param Cx vector first point's x coordinate
	 * @param Cy vector first point's y
	 * @param Nx vector second point's x
	 * @param Ny vector second point's y
	 *
	 * @return the angle
	 */
	public static double angleOfVector(double Cx, double Cy,
		double Nx, double Ny)
	{
		double C_angle;

		if (Cx != Nx)
		{
			C_angle = Math.atan((Ny - Cy) / (Nx - Cx));

			if (Nx < Cx)
			{
				C_angle += Math.PI;
			}
			else if (Ny < Cy)
			{
				C_angle += TWO_PI;
			}
		}
		else if (Ny < Cy)
		{
			C_angle = ONE_AND_HALF_PI; // 270 degrees
		}
		else
		{
			C_angle = HALF_PI; // 90 degrees
		}

//		assert 0.0 <= C_angle && C_angle < TWO_PI;

		return C_angle;
	}

	/**
	 * This method converts the given angle in radians to degrees.
	 *
	 * @param rad angle value in Radian
	 * @return angle in degree
	 */
	public static double radian2degree(double rad)
	{
		return 180.0 * rad / Math.PI;
	}

	/**
	 * This method checks whether the given two line segments (one with point
	 * p1 and p2, the other with point p3 and p4) intersect at a point other
	 * than these points.
	 *
	 * @param p1 segment 1 point 1
	 * @param p2 segment 1 point 2
	 * @param p3 segment 2 point 1
	 * @param p4 segment 2 point 2
	 *
	 * @return true when the two segments intersect at a different point
	 */
	public static boolean doIntersect(PointD p1, PointD p2,
		PointD p3, PointD p4)
	{
		boolean result = Line2D.linesIntersect(p1.x, p1.y,
			p2.x, p2.y, p3.x, p3.y,
			p4.x, p4.y);

		return result;
	}


	// ****** Following method is used in SBGN-PD Layout *******

	/**
	 * Calculates the angle between 3 points in given order. Returns its
	 * absolute value.
	 *
	 * @param targetPnt the first point
	 * @param centerPnt the centre point
	 * @param node the third point
	 *
	 * @return angle defined by the three points, in that order
	 */
	public static double calculateAngle(PointD targetPnt, PointD centerPnt,
			PointD node)
	{

		PointD point1 = new PointD(targetPnt.x - centerPnt.x, targetPnt.y
				- centerPnt.y);
		PointD point2 = new PointD(node.x - centerPnt.x, node.y - centerPnt.y);

		if (Math.abs(point1.x) < 0)
			point1.x = 0.0001;
		if (Math.abs(point1.y) < 0)
			point1.y = 0.0001;

		double angleValue = (point1.x * point2.x + point1.y * point2.y)
				/ (Math.sqrt(point1.x * point1.x + point1.y * point1.y) * Math
						.sqrt(point2.x * point2.x + point2.y * point2.y));

		return Math.abs(Math.toDegrees(Math.acos(angleValue)));
	}

}