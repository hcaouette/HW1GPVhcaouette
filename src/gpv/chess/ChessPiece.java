/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2020 Gary F. Pollice
 *******************************************************************************/

package gpv.chess;

import gpv.Piece;
import gpv.util.*;

/**
 * The chess piece is a piece with some special properties that are used for
 * determining whether a piece can move. It implements the Piece interface
 * and adds properties and methods that are necessary for the chess-specific
 * behavior.
 * @version Feb 21, 2020
 */
public class ChessPiece implements Piece<ChessPieceDescriptor>
{
	private final ChessPieceDescriptor descriptor;
	private boolean hasMoved;	// true if this piece has moved
	
	/**
	 * The only constructor for a ChessPiece instance. Requires a descriptor.
	 * @param descriptor
	 */
	public ChessPiece(ChessPieceDescriptor descriptor)
	{
		this.descriptor = descriptor;
		hasMoved = false;
	}

	/*
	 * @see gpv.Piece#getDescriptor()
	 */
	@Override
	public ChessPieceDescriptor getDescriptor()
	{
		return descriptor;
	}
	
	/**
	 * @return the color
	 */
	public PlayerColor getColor()
	{
		return descriptor.getColor();
	}

	/**
	 * @return the name
	 */
	public PieceName getName()
	{
		return descriptor.getName();
	}

	/*
	 * @see gpv.Piece#canMove(gpv.util.Coordinate, gpv.util.Coordinate, gpv.util.Board)
	 */
	@Override
	public boolean canMove(Coordinate from, Coordinate to, Board b)
	{
		//checks to see if the piece will move out of the board's boundaries
		boolean inBounds = validateBoundaries(to);
		if(!inBounds) {return false;}
		
		//calculate distances from 'from' to 'to'
		int x1=from.getRow(),x2=to.getRow(),y1=from.getColumn(),y2=to.getColumn();
		int xDiff=x2-x1;
		int yDiff=y2-y1;
		
		Pattern pattern = identifyPattern(xDiff,yDiff);
		if(pattern == Pattern.UNKNOWN) {return false;}
		
		//validate whether the piece at 'from' can make the type of move to 'to' 
		boolean matchPattern = validatePattern(pattern,xDiff,yDiff,to,b);
		if (!matchPattern) {return false;}
		
		
		return false;
	}
	
	Pattern identifyPattern(int xDiff, int yDiff) {
		if(Math.abs(xDiff) == Math.abs(yDiff)) { return Pattern.DIAGONAL; }
		else if(xDiff==0 && yDiff!=0) { return Pattern.VERTICAL; }
		else if(xDiff!=0 && yDiff==0) { return Pattern.HORIZONTAL; }
		else if((Math.abs(xDiff) + Math.abs(yDiff))==3) { return Pattern.KNIGHT; }
		else { return Pattern.UNKNOWN; }
	}
	
	public boolean validatePattern(Pattern pattern, int xDiff, int yDiff, Coordinate to, Board b) {
		//pawn movements
		if(this.getName()==PieceName.PAWN)
		{
			//vertical movement options
			if(pattern==Pattern.VERTICAL)
			{
				//pawn vertical movement; checking how far it moves
				//either pawn has not moved and can move 1 or 2 tiles,
				//or it has, and this is standard pawn 1-movement
				if((!this.hasMoved() && Math.abs(yDiff)<3) || (this.hasMoved() && Math.abs(yDiff)==1))
				{
					//now checking direction by color.
					if(this.getColor()==PlayerColor.BLACK && yDiff<0)
					{
						//pawn is black, and therefore moves "down"
						return true;
					}
					else if(this.getColor()==PlayerColor.WHITE && yDiff>0)
					{
						//pawn is white, and therefore moves "up"
						return true;
					}
					//not moving in the right direction for its color
					else { return false; }
				}
				//pawn moves too far
				else { return false; }
			}
			//diagonal movement options, specifically for capturing.
			else if(pattern==Pattern.DIAGONAL)
			{
				//TODO
				//color & direction
				//check that there is a piece in the spot w/ to & b
			}
			//pawn can only move vertically or diagonally
			else { return false; }
		}
		//King and Queen movements
		if(this.getName()==PieceName.KING || this.getName()==PieceName.QUEEN)
		{
			//now check for pattern types; if it's not V/H/D then return false
			if(pattern==Pattern.VERTICAL || pattern==Pattern.HORIZONTAL|| pattern==Pattern.DIAGONAL)
			{
				
			}
			else { return false; }
		}
		return false;
	}
	
	public boolean validateBoundaries(Coordinate to) {
		int minCoord=0;
		int maxCoord=9;
		if((minCoord < to.getRow()) && (to.getRow() < maxCoord) && (minCoord < to.getColumn()) && (to.getColumn() < maxCoord)) {
			return true;
		}
		return false;
	}

	/**
	 * @return the hasMoved
	 */
	public boolean hasMoved()
	{
		return hasMoved;
	}

	/**
	 * Once it moves, you can't change it.
	 * @param hasMoved the hasMoved to set
	 */
	public void setHasMoved()
	{
		hasMoved = true;
	}
}
