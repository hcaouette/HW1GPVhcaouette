package gpv.chess;

import gpv.util.Board;
import gpv.util.Coordinate;


/**
 * A move is a combination of a Chess Piece and a Pattern,
 * without the coordinates 'from' and 'to', with p_p_match 
 * marking whether or not a Piece & it's Pattern are compatible.
 * 
 * @version April 8, 2020
 */
public class Move {

	private Pattern pattern;
	private ChessPiece piece;
	private boolean p_p_Match;
	
	public Move(Pattern pattern, ChessPiece piece) {
		this.pattern=pattern;
		this.piece=piece;
		this.p_p_Match=patternPieceMatch();
	}
	
	public boolean patternPieceMatch() {
		if(this.pattern== Pattern.KNIGHT && this.piece.getName()==PieceName.KNIGHT) { return true;}
		
		if(this.pattern== Pattern.DIAGONAL)
		{
			if((this.piece.getName()==PieceName.PAWN) || (this.piece.getName()==PieceName.BISHOP) ||
					(this.piece.getName()==PieceName.QUEEN) || (this.piece.getName()==PieceName.KING))
			{
				return true;
			}
		}
		
		if(this.pattern== Pattern.HORIZONTAL)
		{
			if((this.piece.getName()==PieceName.PAWN) || (this.piece.getName()==PieceName.QUEEN) ||
					(this.piece.getName()==PieceName.KING))
			{
				return true;
			}
		}
		
		if(this.pattern== Pattern.VERTICAL)
		{
			if((this.piece.getName()==PieceName.PAWN) || (this.piece.getName()==PieceName.ROOK) ||
					(this.piece.getName()==PieceName.QUEEN) || (this.piece.getName()==PieceName.KING))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean validatePattern(Pattern pattern, int xDiff, int yDiff, Coordinate to, Board b) {
		//pawn movements
		if(piece.getName()==PieceName.PAWN)
		{
			//vertical movement options
			if(pattern==Pattern.VERTICAL)
			{
				//pawn vertical movement; checking how far it moves
				//either pawn has not moved and can move 1 or 2 tiles,
				//or it has, and this is standard pawn 1-movement
				if((!piece.hasMoved() && Math.abs(yDiff)<3) || (piece.hasMoved() && Math.abs(yDiff)==1))
				{
					//now checking direction by color.
					if(piece.getColor()==PlayerColor.BLACK && yDiff<0)
					{
						//pawn is black, and therefore moves "down"
						return true;
					}
					else if(piece.getColor()==PlayerColor.WHITE && yDiff>0)
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
		if(piece.getName()==PieceName.KING || piece.getName()==PieceName.QUEEN)
		{
			//now check for pattern types; if it's not V/H/D then return false
			if(pattern==Pattern.VERTICAL || pattern==Pattern.HORIZONTAL|| pattern==Pattern.DIAGONAL)
			{
				
			}
			else { return false; }
		}
		return false;
	}
	
	//getter helper methods
	//there are no setters
	public Pattern getPattern() { return this.pattern; }
	public ChessPiece getPiece() { return this.piece; }
	public boolean getPPMatch() { return this.p_p_Match; }
}
