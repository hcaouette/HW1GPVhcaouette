package gpv.chess;


/**
 * A move is a combination of a Chess Piece and a Pattern,
 * without the coordinates 'from' and 'to', with p_p_match 
 * marking whether or not a Piece & it's Pattern are compatible.
 * 
 * @version April 8, 2020
 */
public class Movement {

	private Pattern pattern;
	private ChessPiece piece;
	private boolean p_p_Match;
	
	public Movement(Pattern pattern, ChessPiece piece) {
		this.pattern=pattern;
		this.piece=piece;
		this.p_p_Match=patternPieceMatch();
	}
	
	//getter helper methods
	//there are no setters
	public Pattern getPattern() { return this.pattern; }
	public ChessPiece getPiece() { return this.piece; }
	public boolean getPPMatch() { return this.p_p_Match; }
	
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
			if((this.piece.getName()==PieceName.ROOK) || (this.piece.getName()==PieceName.QUEEN) ||
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
	
	
}
