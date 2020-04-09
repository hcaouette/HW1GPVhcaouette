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
import static gpv.util.Coordinate.*;

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
	public ChessPieceDescriptor getDescriptor() { return descriptor; }
	
	/**
	 * @return the color
	 */
	public PlayerColor getColor() { return descriptor.getColor(); }

	/**
	 * @return the name
	 */
	public PieceName getName() { return descriptor.getName(); }

	/*
	 * @see gpv.Piece#canMove(gpv.util.Coordinate, gpv.util.Coordinate, gpv.util.Board)
	 */
	@Override
	public boolean canMove(Coordinate from, Coordinate to, Board b)
	{
		//checks 'to' coordinates against board bounds
		if(!b.validateBoundaries(to)) {return false;}
		
		int yDiff=to.getRow()-from.getRow();
		int xDiff=to.getColumn()-from.getColumn();
		Pattern pattern = identifyPattern(xDiff,yDiff);

		//checks that pattern is not unknown
		if(pattern == Pattern.UNKNOWN) {return false;}
		
		//validates that a piece, this, can move in a given Pattern
		Movement movement=new Movement(pattern,this);
		if(!movement.getPPMatch()) {return false;}
		
		//checks for obstructions, allows for castling
		if(!obstructionFree(pattern,from,to,b)) {return false;}
		
		//pawn diagonal check
		if(this.getName()==PieceName.PAWN && pattern==Pattern.DIAGONAL)
		{
			ChessPiece pieceOnTarget=(ChessPiece) b.getPieceAt(to);
			if(pieceOnTarget!=null) {
				return canCapture(to,b);
			}else {return false;}
		}
		
		if(this.getName()==PieceName.PAWN && pattern==Pattern.VERTICAL)
		{
			if(Math.abs(yDiff)>1)
			{
				if(this.hasMoved()==true) {return false;}
			}
			else if(yDiff<1 && this.getColor()==PlayerColor.WHITE) {return false;} //unidirectional
			else if(yDiff>0 && this.getColor()==PlayerColor.BLACK) {return false;} //unidirectional
		}
		
		if(this.getName()==PieceName.KING && ((Math.abs(yDiff)>1) || (Math.abs(xDiff)>1)))
		{
			System.out.println(from.toString());
			System.out.println(to.toString());
			System.out.println(xDiff);
			System.out.println(yDiff);
			return false;}
		
		return true;
	}
	
	/**
	 * Determines whether or not there are obstructions which will invalidate the move
	 * @param pattern, the Pattern determined in canMove
	 * @param from, the Coordinate from canMove
	 * @param to, the Coordinate from canMove
	 * @param b, the Board from canMove
	 * @return true for no obstructions, false for existing obstructions
	 */
	public boolean obstructionFree(Pattern pattern,Coordinate from,Coordinate to,Board b)
	{
		int fRow=from.getRow(),tRow=to.getRow();
		int fCol=from.getColumn(),tCol=to.getColumn();
		
		ChessPiece pieceOnTarget=(ChessPiece) b.getPieceAt(to);
		boolean tExists;
		if(pieceOnTarget!=null) {tExists=true;} else {tExists=false;}
		
		//check whether or not This can capture a piece existing on 'to'
		boolean canCap;
		if(tExists) {
			canCap=canCapture(to,b);
		}
		else { canCap=true;}
		
		//if pieceOnTarget does not exist or it is a different color from This
		//then the target, 'to', is valid
		boolean validTarget;
		if(!tExists || canCap) {validTarget=true;} else {validTarget=false;}
		
		//if the target is not valid then you return false early
		//there is no point in checking the path when the target is blocked
		if(!validTarget) {return false;}
		
		//if the target is valid a knight can always move
		if(pattern==Pattern.KNIGHT) {return true;}
		
		//checks for vertical obstructions in between 'from' and 'to'
		if(pattern==Pattern.VERTICAL)
		{
			int range=tRow-fRow;
			if(range>0) //moving upwards
			{
				for(int i=1;i<=Math.abs(range);i++)
				{
					int newRow=fRow+i;
					ChessPiece test=(ChessPiece) b.getPieceAt(makeCoordinate(newRow,fCol));
					if(canCap && (newRow==tRow)) {return true;}
					else if(test!=null) {return false;}
					
				}
			}else if(range<0) //moving downwards
			{
				for(int i=1;i<=Math.abs(range);i++)
				{
					int newRow=fRow-i;
					ChessPiece test=(ChessPiece) b.getPieceAt(makeCoordinate(newRow,fCol));
					if(canCap && (newRow==tRow)) {return true;}
					else if(test!=null) {return false;}
				}
			}
		}
		
		//checks for horizontal obstructions in between 'from' and 'to'
		//also checks Rook and King to see if they are able to castle.
		//returns true for rook or king if they can be castled and the other is not an obstruction.
		if(pattern==Pattern.HORIZONTAL)
		{
			int range=tCol-fCol;
			if(range>0) //moving rightwards
			{
				for(int i=1;i<=Math.abs(range);i++)
				{
					int newCol=fCol+i;
					ChessPiece test=(ChessPiece) b.getPieceAt(makeCoordinate(fRow,newCol));
					if(canCap && (newCol==tCol)) {return true;}
					else if(canCastle(to,b)) {return true;}
					else if(test!=null) {return false;}
					
				}
			}else if(range<0) //moving leftwards
			{
				for(int i=1;i<=Math.abs(range);i++)
				{
					int newCol=fCol-i;
					ChessPiece test=(ChessPiece) b.getPieceAt(makeCoordinate(fRow,newCol));
					if(canCap && (newCol==tCol)) {return true;}
					else if(canCastle(to,b)) {return true;}
					else if(test!=null) {return false;}
				}
			}
		}
		
		//checks for diagonal obstructions in between 'from' and 'to'
		//also checks Pawns to see if there IS an enemy piece to capture.
		//returns false for pawn if there is no capture.
		if(pattern==Pattern.DIAGONAL)
		{
			int cRange=tCol-fCol;
			int rRange=tRow-fRow;
			if(cRange>0 && rRange>0) //moving up-right
			{
				for(int i=1;i<=Math.abs(rRange);i++)
				{
					int newCol=fCol+i;
					int newRow=fRow+i;
					ChessPiece test=(ChessPiece) b.getPieceAt(makeCoordinate(newRow,newCol));
					if(canCap && (newCol==tCol)) {return true;}
					else if(test!=null) {return false;}
				}
			}else if(cRange<0 && rRange<0) //moving down-left
			{
				for(int i=1;i<=Math.abs(rRange);i++)
				{
					int newCol=fCol-i;
					int newRow=fRow-i;
					ChessPiece test=(ChessPiece) b.getPieceAt(makeCoordinate(newRow,newCol));
					if(canCap && (newCol==tCol)) {return true;}
					else if(test!=null) {return false;}
				}
			}else if(cRange<0 && rRange>0) //moving up-left
			{
				for(int i=1;i<=Math.abs(rRange);i++)
				{
					int newCol=fCol-i;
					int newRow=fRow+i;
					ChessPiece test=(ChessPiece) b.getPieceAt(makeCoordinate(newRow,newCol));
					if(canCap && (newCol==tCol)) {return true;}
					else if(test!=null) {return false;}
				}
			}else if(cRange>0 && rRange<0) //moving down-right
			{
				for(int i=1;i<=Math.abs(rRange);i++)
				{
					int newCol=fCol+i;
					int newRow=fRow-i;
					ChessPiece test=(ChessPiece) b.getPieceAt(makeCoordinate(newRow,newCol));
					if(canCap && (newCol==tCol)) {return true;}
					else if(test!=null) {return false;}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * determine whether or not a rook and a king can castle
	 * @param to, the coordinate being castled to
	 * @param b, the board being used
	 * @return boolean, whether or not castling can occur
	 */
	public boolean canCastle(Coordinate to, Board b)
	{
		ChessPiece target =(ChessPiece) b.getPieceAt(to);
		if(this.hasMoved() || target.hasMoved()) {return false;}
		
		boolean tIsKing = (target.getName() == PieceName.KING);
		boolean tIsRook = (target.getName() == PieceName.ROOK);
		
		if(tIsKing && this.getName()==PieceName.ROOK) {return true;}
		else if(tIsRook && this.getName()==PieceName.KING) {return true;}
		else {return false;}
	}
	
	/**
	 * determines whether or not 'this' can capture the piece at 'to'
	 * @param to, the coordinate being castled to
	 * @param b, the board being used
	 * @return boolean, whether or not this can capture the piece at 'to'
	 */
	public boolean canCapture(Coordinate to, Board b)
	{
		ChessPiece pieceOnTarget=(ChessPiece) b.getPieceAt(to);
		if(this.getColor()!=pieceOnTarget.getColor())
		{
			return true;
		}
		else {return false;}
	}
	
	
	/**
	 * Based on the xDiff and yDiff between from and to, identifies a movement pattern
	 * @param xDiff the difference in x-coords of from and to
	 * @param yDiff the difference in y-coords of from and to
	 * @return enum Pattern for comparison later in canMove
	 */
	Pattern identifyPattern(int xDiff, int yDiff) {
		if(Math.abs(xDiff) == Math.abs(yDiff)) { return Pattern.DIAGONAL; }
		else if(xDiff==0 && yDiff!=0) { return Pattern.VERTICAL; }
		else if(xDiff!=0 && yDiff==0) { return Pattern.HORIZONTAL; }
		else if((Math.abs(xDiff) + Math.abs(yDiff))==3) { return Pattern.KNIGHT; }
		else { return Pattern.UNKNOWN; }
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
