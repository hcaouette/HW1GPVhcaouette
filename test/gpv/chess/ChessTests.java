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

import static gpv.chess.ChessPieceDescriptor.*;
import static org.junit.Assert.*;
import static gpv.util.Coordinate.makeCoordinate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import gpv.util.Board;

/**
 * Tests to ensure that pieces are created correctly and that all pieces
 * get created.
 * @version Feb 23, 2020
 */
class ChessPieceTests
{
	private static ChessPieceFactory factory = null;
	private Board board;
	
	@BeforeAll
	public static void setupBeforeTests()
	{
		factory = new ChessPieceFactory();
	}
	
	@BeforeEach
	public void setupTest()
	{
		board = new Board(8, 8);
	}
	
	@Test
	void makePiece()
	{
		ChessPiece pawn = factory.makePiece(WHITEPAWN);
		assertNotNull(pawn);
	}
	
	/**
	 * This type of test loops through each value in the Enum and
	 * one by one feeds it as an argument to the test method.
	 * It's worth looking at the different types of parameterized
	 * tests in JUnit: 
	 * https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests
	 * @param d the Enum value
	 */
	@ParameterizedTest
	@EnumSource(ChessPieceDescriptor.class)
	void makeOneOfEach(ChessPieceDescriptor d)
	{
		ChessPiece p = factory.makePiece(d);
		assertNotNull(p);
		assertEquals(d.getColor(), p.getColor());
		assertEquals(d.getName(), p.getName());
	}

	@Test
	void placeOnePiece()
	{
		ChessPiece p = factory.makePiece(BLACKPAWN);
		board.putPieceAt(p, makeCoordinate(2, 2));
		assertEquals(p, board.getPieceAt(makeCoordinate(2, 2)));
	}

	@Test
	void placeTwoPieces()
	{
		ChessPiece bn = factory.makePiece(BLACKKNIGHT);
		ChessPiece wb = factory.makePiece(WHITEBISHOP);
		board.putPieceAt(bn, makeCoordinate(3, 5));
		board.putPieceAt(wb, makeCoordinate(2, 6));
		assertEquals(bn, board.getPieceAt(makeCoordinate(3, 5)));
		assertEquals(wb, board.getPieceAt(makeCoordinate(2, 6)));
	}
	
	@Test
	void checkForPieceHasMoved()
	{
		ChessPiece bq = factory.makePiece(BLACKQUEEN);
		assertFalse(bq.hasMoved());
		bq.setHasMoved();
		assertTrue(bq.hasMoved());
	}
	
	@Test
	void thisShouldFailOnDelivery()
	{
		ChessPiece wk = factory.makePiece(WHITEKING);
		board.putPieceAt(wk, makeCoordinate(1,5));
		assertTrue(wk.canMove(makeCoordinate(1,5), makeCoordinate(2, 5), board));
	}

	@Test
	void testIdentifyPatterns()
	{
		//
		ChessPiece bq = factory.makePiece(BLACKQUEEN);
		assertEquals(Pattern.VERTICAL,bq.identifyPattern(0, 3));
		assertEquals(Pattern.VERTICAL,bq.identifyPattern(0, -1));
		assertEquals(Pattern.HORIZONTAL,bq.identifyPattern(2,0));
		assertEquals(Pattern.HORIZONTAL,bq.identifyPattern(-3,0));
		assertEquals(Pattern.DIAGONAL,bq.identifyPattern(3, 3));
		assertEquals(Pattern.DIAGONAL,bq.identifyPattern(-2,-2));
		assertEquals(Pattern.DIAGONAL,bq.identifyPattern(-1,1));
		assertEquals(Pattern.KNIGHT,bq.identifyPattern(-1,2));
		assertEquals(Pattern.KNIGHT,bq.identifyPattern(-1,2));
		assertEquals(Pattern.KNIGHT,bq.identifyPattern(2,-1));
		assertEquals(Pattern.KNIGHT,bq.identifyPattern(-2,-1));
		assertEquals(Pattern.UNKNOWN,bq.identifyPattern(-1,4));
	}
	
	//tests Move's ability to correctly assign p_p_match for the KNIGHT Pattern
	@Test
	void testPatternPieceMatchKNIGHT() {
		Movement tMove = new Movement(Pattern.KNIGHT,factory.makePiece(BLACKKNIGHT));
		assertTrue(tMove.getPPMatch());
		tMove = new Movement(Pattern.KNIGHT,factory.makePiece(BLACKPAWN));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.KNIGHT,factory.makePiece(BLACKROOK));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.KNIGHT,factory.makePiece(BLACKBISHOP));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.KNIGHT,factory.makePiece(BLACKKING));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.KNIGHT,factory.makePiece(BLACKQUEEN));
		assertFalse(tMove.getPPMatch());
	}
	
	//tests Move's ability to correctly assign p_p_match for the VERTICAL Pattern
	@Test
	void testPatternPieceMatchVERTICAL() {
		Movement tMove = new Movement(Pattern.VERTICAL,factory.makePiece(BLACKKNIGHT));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.VERTICAL,factory.makePiece(BLACKPAWN));
		assertTrue(tMove.getPPMatch());
		tMove = new Movement(Pattern.VERTICAL,factory.makePiece(BLACKROOK));
		assertTrue(tMove.getPPMatch());
		tMove = new Movement(Pattern.VERTICAL,factory.makePiece(BLACKBISHOP));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.VERTICAL,factory.makePiece(BLACKKING));
		assertTrue(tMove.getPPMatch());
		tMove = new Movement(Pattern.VERTICAL,factory.makePiece(BLACKQUEEN));
		assertTrue(tMove.getPPMatch());
	}
	
	//tests Move's ability to correctly assign p_p_match for the HORIZONTAL Pattern
	@Test
	void testPatternPieceMatchHORIZONTAL() {
		Movement tMove = new Movement(Pattern.HORIZONTAL,factory.makePiece(BLACKKNIGHT));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.HORIZONTAL,factory.makePiece(BLACKPAWN));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.HORIZONTAL,factory.makePiece(BLACKROOK));
		assertTrue(tMove.getPPMatch());
		tMove = new Movement(Pattern.HORIZONTAL,factory.makePiece(BLACKBISHOP));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.HORIZONTAL,factory.makePiece(BLACKKING));
		assertTrue(tMove.getPPMatch());
		tMove = new Movement(Pattern.HORIZONTAL,factory.makePiece(BLACKQUEEN));
		assertTrue(tMove.getPPMatch());
	}
	
	//tests Move's ability to correctly assign p_p_match for the DIAGONAL Pattern
	@Test
	void testPatternPieceMatchDIAGONAL() {
		Movement tMove = new Movement(Pattern.DIAGONAL,factory.makePiece(BLACKKNIGHT));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.DIAGONAL,factory.makePiece(BLACKPAWN));
		assertTrue(tMove.getPPMatch());
		tMove = new Movement(Pattern.DIAGONAL,factory.makePiece(BLACKROOK));
		assertFalse(tMove.getPPMatch());
		tMove = new Movement(Pattern.DIAGONAL,factory.makePiece(BLACKBISHOP));
		assertTrue(tMove.getPPMatch());
		tMove = new Movement(Pattern.DIAGONAL,factory.makePiece(BLACKKING));
		assertTrue(tMove.getPPMatch());
		tMove = new Movement(Pattern.DIAGONAL,factory.makePiece(BLACKQUEEN));
		assertTrue(tMove.getPPMatch());
	}
	
	@Test
	void testValidatePatterns()
	{
		
	}
	
	@Test
	void checkObstructions() {
		ChessPiece wq = factory.makePiece(WHITEQUEEN);
		ChessPiece bq = factory.makePiece(BLACKQUEEN);
		ChessPiece bk = factory.makePiece(BLACKKNIGHT);
		
		board.putPieceAt(wq, makeCoordinate(2, 2));
		board.putPieceAt(bq, makeCoordinate(6, 2));
		
		//KNIGHT cases
		//safely capture wq
		assertTrue(bk.obstructionFree(Pattern.KNIGHT, makeCoordinate(3,4), makeCoordinate(2,2), board));
		//cannot capture bq
		assertFalse(bk.obstructionFree(Pattern.KNIGHT, makeCoordinate(1,4), makeCoordinate(6,2), board));
		
		//VERTICAL cases
		assertTrue(wq.obstructionFree(Pattern.VERTICAL, makeCoordinate(2,2), makeCoordinate(6,2), board));
		assertTrue(bq.obstructionFree(Pattern.VERTICAL, makeCoordinate(6,2), makeCoordinate(2,2), board));
		
		//move the knight inbetween the queens and run again.
		board.putPieceAt(bk, makeCoordinate(4,2));
		assertFalse(wq.obstructionFree(Pattern.VERTICAL, makeCoordinate(2,2), makeCoordinate(6,2), board));
		assertFalse(bq.obstructionFree(Pattern.VERTICAL, makeCoordinate(6,2), makeCoordinate(2,2), board));

		
		//HORIZONTAL cases
		board.putPieceAt(wq, makeCoordinate(2, 2));
		board.putPieceAt(bq, makeCoordinate(2, 6));
		assertTrue(wq.obstructionFree(Pattern.HORIZONTAL, makeCoordinate(2,2), makeCoordinate(2,6), board));
		assertTrue(bq.obstructionFree(Pattern.HORIZONTAL, makeCoordinate(2,6), makeCoordinate(2,2), board));
		
		//move the knight inbetween the queens and run again
		board.putPieceAt(bk, makeCoordinate(2,4));
		assertFalse(wq.obstructionFree(Pattern.HORIZONTAL, makeCoordinate(2,2), makeCoordinate(2,6), board));
		assertFalse(bq.obstructionFree(Pattern.HORIZONTAL, makeCoordinate(2,6), makeCoordinate(2,2), board));

		
		//DIAGONAL cases
		board.putPieceAt(wq, makeCoordinate(2, 2)); //bottom left
		board.putPieceAt(bq, makeCoordinate(6, 6)); //top right
		assertTrue(wq.obstructionFree(Pattern.DIAGONAL, makeCoordinate(2,2), makeCoordinate(6,6), board));
		assertTrue(bq.obstructionFree(Pattern.DIAGONAL, makeCoordinate(6,6), makeCoordinate(2,2), board));

		board.putPieceAt(wq, makeCoordinate(6, 2)); //top left
		board.putPieceAt(bq, makeCoordinate(2, 6)); //bottom right
		assertTrue(wq.obstructionFree(Pattern.DIAGONAL, makeCoordinate(6,2), makeCoordinate(2,6), board));
		assertTrue(bq.obstructionFree(Pattern.DIAGONAL, makeCoordinate(2,6), makeCoordinate(6,2), board));
		
		board.putPieceAt(bk, makeCoordinate(4,4)); // knight in-between
		assertFalse(wq.obstructionFree(Pattern.DIAGONAL, makeCoordinate(6,2), makeCoordinate(2,6), board));
		assertFalse(bq.obstructionFree(Pattern.DIAGONAL, makeCoordinate(2,6), makeCoordinate(6,2), board));
		assertFalse(wq.obstructionFree(Pattern.DIAGONAL, makeCoordinate(2,2), makeCoordinate(6,6), board));
		assertFalse(bq.obstructionFree(Pattern.DIAGONAL, makeCoordinate(6,6), makeCoordinate(2,2), board));
		
		//CASTLING
	}
	
	

}

