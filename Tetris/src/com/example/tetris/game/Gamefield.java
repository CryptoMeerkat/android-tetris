package com.example.tetris.game;

import java.util.Arrays;

public class Gamefield {

	public static final byte light 			= Tetris.light;
	public static final int BRICK_I 		= 0;
	public static final int BRICK_J 		= 1;
	public static final int BRICK_L 		= 2;
	public static final int BRICK_O 		= 3;
	public static final int BRICK_S 		= 4;
	public static final int BRICK_T 		= 5;
	public static final int BRICK_Z 		= 6;
	public static final int matrixDimension = 24;
	public static final int gamefieldWidth 	= 12;
	public static final int startRow 		= 1;	// the start row for the first brick (THIS MUST BE >= 1!!!!)
	public static final int startCol 		= 5;	// the start col for the first brick
	public static final int scoreCap		= 999;	// the score of the user is capped 
	
	public static final BrickOrientation startOrientation = BrickOrientation.UP; // default brick startup orientation is UP
	
	private byte[][] gamefield; // the gamefield as such
	private int[] nextBlocks; // stores the next three upcoming blocks
	
	private Gameblock currentBlock;
	
	// stores if a block was just created - the game can fail if the next move is isntantly not possible
	private boolean newBlockMove;
	
	private boolean gameover;	// stores if the gamefield is full
	private int score;	// stores the game score
	
	public Gamefield() {
		
		gamefield = new byte[matrixDimension][matrixDimension];
		for (byte[] row : gamefield) Arrays.fill(row, (byte) 0);
		
		nextBlocks = new int[3];
		Arrays.fill(nextBlocks, -1); // invalidate fields
		
		fillNextBlocks();
		
		// once create a random new block, which is the active game block
		currentBlock = new Gameblock((int) (Math.random()*7),
									 startOrientation,
									 startRow,
									 startCol);
		newBlockMove = true;
		gameover = false;
		score = 0;
		
	}
	
	/**
	 * This is the primary method to modify the current active brick.
	 * This method is called from the Game engine. The amount of calling
	 * this method is also the amount of movements the player can do
	 * in one turn.
	 * 
	 * @param colOffset the offset of the active brick, which the user wants
	 * @param turns the turns the brick should do for the user
	 */
	public void moveActiveBrick(int colOffset, int turns) {
		
		// turn is only possible, when away from the wall!
		if (currentBlock.getCol() > 1 && currentBlock.getCol() < 10) {
			
			// try to turn the active brick around
			BrickOrientation brickOrientation = BrickOrientation.values()[turns%4];
			if (!currentBlock.isLocked(gamefield, brickOrientation)) {
				currentBlock.setBrickOri(brickOrientation);
			}
			
		}
		
		int newCol = currentBlock.getPossibleOffset(colOffset);
		
		// check if column offset move is possible
		if (!currentBlock.isLocked(gamefield, currentBlock.getBrickOri(), currentBlock.getRow(), newCol)) {
			currentBlock.setCol(newCol);
		}
		
		// check whether the brick can get into the next row
		if (!currentBlock.isLockedNextRow(gamefield)) {
			
			newBlockMove = false;
			currentBlock.setNextRow();
			
		} else {
			
			if (newBlockMove) {
				
				// Twice in a row a new brick was created, this means a brick cannot move any more
				// the game is over!
				gameover = true;
				
			} else {
			
				lockBrick();
				
				checkForCompletedRows();
				
				currentBlock = new Gameblock(getNextBlock(),
											 startOrientation,
											 startRow,
											 startCol);
				newBlockMove = true;
				
			}
		}
		
	}

	/**
	 * Checks for rows, which are filled from left to right
	 * with blocks. Then the row is cleared and all items
	 * above are moved one step further down the matrix.
	 * <br />
	 * <br />
	 * Just think of the classic Tetrix row clear step here!
	 */
	private void checkForCompletedRows() {
		
		int countRowCompleted = 0;
		
		// check from bottom up, for easier row deletion
		for (int row = 23; row > 0; row--) {
			
			boolean rowCompleted = true;
			
			for (int tmpCol = 0; tmpCol < gamefieldWidth; tmpCol++) {
				
				if (gamefield[row][tmpCol] != light) {
					rowCompleted = false;
				}
				
			}
			
			// maybe row is completed. then everything row above needs to be moved one slot down
			if (rowCompleted) {
				
				countRowCompleted++;
				
				for (int tmpRow = row; tmpRow > 0; tmpRow--) {

					for (int tmpCol = 0; tmpCol < gamefieldWidth; tmpCol++) {
						gamefield[tmpRow][tmpCol] = gamefield[tmpRow-1][tmpCol];
					}
					
				}
				
				row++; // need to check the same row again for completion (e.g. two rows completed)
				
			}
			
		}
		
		// per row 10 points
		// rowcount^2 as multiplier
		// but only count if at least one row has been eliminated
		score += (int) (10 * Math.pow(countRowCompleted, 2)) * Math.max(countRowCompleted, 1);
		
	}
	
	/**
	 * Fix the current active brick in LED matrix. Which means, that this
	 * block cannot move any further and is now fixed.
	 */
	private void lockBrick() {
		insertBrickInMatrix(gamefield,
						    currentBlock.getBlockID(),
						    currentBlock.getBrickOri(),
						    currentBlock.getRow(),
						    currentBlock.getCol());
		
	}
	
	/**
	 * User after a block is placed to get the next randomly created block.
	 */
	private int getNextBlock() {
		int currentBlock = nextBlocks[0];
		
		for (int i = 0; i < nextBlocks.length - 1; i++)
			nextBlocks[i] = nextBlocks[i+1];
		
		nextBlocks[nextBlocks.length-1] = -1;
		
		fillNextBlocks();
		
		return currentBlock;
	}
	
	/**
	 * Fills the array of the next blocks. To check if a block is not set yet, 
	 * the entry in the nextBlocks[] must be < 0.
	 */
	private void fillNextBlocks() {
		
		for (int i = 0; i < nextBlocks.length; i++) {
			
			if (nextBlocks[i] < 0 ) {
				int rand = (int) (Math.random()*7);
				nextBlocks[i] = rand;
			}
			
		}
		
	}

	/**
	 * Returns a byte[][] representing the gamefield. Which essentially means,
	 * that if a value in this array is not equals 0, then there is a block above
	 * this slot.
	 * The gamefield dimension is given by the class field {@code gamefieldDimension}.
	 * 
	 * @return the gamefield
	 */
	public byte[][] getGamefield() {
		
		byte[][] currentGamefield = new byte[matrixDimension][matrixDimension];
		for (byte[] row : currentGamefield) Arrays.fill(row, (byte) 0);
		
		// copy left half of gamefield
		for (int row = 0; row < gamefield.length; row++) {
			for (int col = 0; col < 13; col++) {
				currentGamefield[row][col] = gamefield[row][col];
			}
		}
		
		// now print the next three elements to the lower right matrix
		for (int i = 0; i < 3; i++) {
			
			// bricks are printed 14|14   17|17 and   20|20
			insertBrickInMatrix(currentGamefield,
							    nextBlocks[i],
							    BrickOrientation.UP,
							    14 + 3*i,
							    14 + 3*i);
			
		}
		
		// print the active brick into the gamefield
		insertBrickInMatrix(currentGamefield,
							currentBlock.getBlockID(),
							currentBlock.getBrickOri(),
							currentBlock.getRow(),
							currentBlock.getCol());
		
		// print the current score into the gamefield
		insertScoreInMatrix(currentGamefield, score);
		
		return currentGamefield;
	}
	
	/**
	 * Returns whether the game is over.
	 * @return true, if the game is over
	 */
	public boolean isGameover() {
		return gameover;
	}
	
	public BrickOrientation getOrientation() {
		return currentBlock.getBrickOri();
	}
	
	/**
	 * Returns the current game score.
	 * @return the current game score
	 */
	public int getScore() {
		return score;
	}
	
	//########################### HELPER METHODS ##############################
	
	/**
	 * Inserts a brick with the given number into the given matrix. The given orientation
	 * and row and colum are respected.
	 * 
	 * @param matrix the matrix where the brick is put into
	 * @param brickNo the brick number (to identify the brick type)
	 * @param brickOri the brick orientation, default is 0
	 * @param row
	 * @param col
	 */
	private void insertBrickInMatrix(byte[][] matrix, int brickNo, BrickOrientation brickOri, int row, int col) {
		
		switch(brickNo) {
			case BRICK_I: printBrickI(matrix, brickOri, row, col);
						  break;
			case BRICK_J: printBrickJ(matrix, brickOri, row, col);
						  break;
			case BRICK_L: printBrickL(matrix, brickOri, row, col);
						  break;
			case BRICK_O: printBrickO(matrix, brickOri, row, col);
						  break;
			case BRICK_S: printBrickS(matrix, brickOri, row, col);
						  break;
			case BRICK_T: printBrickT(matrix, brickOri, row, col);
						  break;
			default: 	  printBrickZ(matrix, brickOri, row, col);
						  break;
		
		}
		
	}

	/**
	 * Insert the currents score into the given gamefield.
	 * <b>Attention:</b> The score is capped by 999! This is need to
	 * fit into the LED matrix.
	 * 
	 * @param matrix 	the matrix where the brick is put into
	 * @param score 	the current game score
	 */
	private void insertScoreInMatrix(byte[][] matrix, int score) {
		
		int cappedScore = Math.min(score, scoreCap);
		
		// get the digit out of the capped score
		int lowestDigit = Math.round(cappedScore) % 10;
		int midDigit 	= Math.round(cappedScore / 10) % 10;
		int highDigit 	= Math.round(cappedScore / 100) % 10;
		
		insertNumberInMatrix(matrix, lowestDigit, 4, 21);
		insertNumberInMatrix(matrix, midDigit, 4, 17);
		insertNumberInMatrix(matrix, highDigit, 4, 13);
		
	}
	
	/**
	 * Inserts the given number at the given poition into the LED matrix.
	 * 
	 * @param matrix 	the matrix where the brick is put into
	 * @param number 	the number to print
	 * @param row 		the row where to start printing the number (the upper left corner)
	 * @param col 		the column where to start printing the number (the upper left corner)
	 */
	private void insertNumberInMatrix(byte[][] matrix, int number, int row, int col) {
		
		byte[][] numberField;
		int numberwidth  = 3;
		int numberheight = 5;
		
		// selects the pattern to use depending on the given number
		switch (number) {
			case 0: numberField = StaticFields.NUMBER_ZERO;
					break;
			case 1: numberField = StaticFields.NUMBER_ONE;
					break;
			case 2: numberField = StaticFields.NUMBER_TWO;
					break;
			case 3: numberField = StaticFields.NUMBER_THREE;
					break;
			case 4: numberField = StaticFields.NUMBER_FOUR;
					break;
			case 5: numberField = StaticFields.NUMBER_FIVE;
					break;
			case 6: numberField = StaticFields.NUMBER_SIX;
					break;
			case 7: numberField = StaticFields.NUMBER_SEVEN;
					break;
			case 8: numberField = StaticFields.NUMBER_EIGHT;
					break;
			case 9: numberField = StaticFields.NUMBER_NINE;
					break;
			default:numberField = StaticFields.NUMBER_INVALID;
					break;
		}
		
		// really prints the number into the matrix
		for (int r = 0, tmpRow = row + r; r < numberheight; r++, tmpRow++) {
			
			for (int c = 0, tmpCol = col; c < numberwidth; c++, tmpCol++) {
				
				matrix[tmpRow][tmpCol] = numberField[r][c];
				
			}
			
		}
		
	}
	
	//########################### BLOCK PRINTING METHODS ##############################
	
	private void printBrickI(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {

			matrix[row-1][col] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			matrix[row+2][col] = light;
			
		} else {

			matrix[row][col-1] = light;
			matrix[row][col] = light;
			matrix[row][col+1] = light;
			matrix[row][col+2] = light;
			
		}
		
	}
	
	private void printBrickJ(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP) {

			matrix[row-1][col] = light;
			matrix[row-1][col+1] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			
		} else if (brickOri == BrickOrientation.RIGHT) {

			matrix[row][col-1] = light;
			matrix[row][col] = light;
			matrix[row][col+1] = light;
			matrix[row+1][col+1] = light;
			
		} else if (brickOri == BrickOrientation.DOWN) {

			matrix[row-1][col] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			matrix[row+1][col-1] = light;
			
		} else {

			matrix[row-1][col-1] = light;
			matrix[row][col-1] = light;
			matrix[row][col] = light;
			matrix[row][col+1] = light;
			
		}
		
	}
	
	private void printBrickL(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP) {

			matrix[row-1][col] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			matrix[row+1][col+1] = light;
			
		} else if (brickOri == BrickOrientation.RIGHT) {

			matrix[row][col-1] = light;
			matrix[row+1][col-1] = light;
			matrix[row][col] = light;
			matrix[row][col+1] = light;
			
		} else if (brickOri == BrickOrientation.DOWN) {

			matrix[row-1][col-1] = light;
			matrix[row-1][col] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			
		} else {

			matrix[row][col-1] = light;
			matrix[row][col] = light;
			matrix[row][col+1] = light;
			matrix[row-1][col+1] = light;
			
		}
		
	}

	private void printBrickO(byte[][] matrix, BrickOrientation brickOri, int row, int col) {

		matrix[row][col] = light;
		matrix[row][col+1] = light;
		matrix[row+1][col] = light;
		matrix[row+1][col+1] = light;
		
	}

	private void printBrickS(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {

			matrix[row+1][col-1] = light;
			matrix[row+1][col] = light;
			matrix[row][col] = light;
			matrix[row][col+1] = light;
			
		} else {

			matrix[row-1][col-1] = light;
			matrix[row][col-1] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			
		}
		
	}
	
	private void printBrickT(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP) {

			matrix[row][col-1] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			matrix[row][col+1] = light;
			
		} else if (brickOri == BrickOrientation.RIGHT) {

			matrix[row][col-1] = light;
			matrix[row-1][col] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			
		} else if (brickOri == BrickOrientation.DOWN) {

			matrix[row][col-1] = light;
			matrix[row][col] = light;
			matrix[row-1][col] = light;
			matrix[row][col+1] = light;
			
		} else {

			matrix[row][col+1] = light;
			matrix[row-1][col] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			
		}
		
	}

	private void printBrickZ(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {

			matrix[row][col-1] = light;
			matrix[row][col] = light;
			matrix[row+1][col] = light;
			matrix[row+1][col+1] = light;
			
		} else {

			matrix[row-1][col] = light;
			matrix[row][col] = light;
			matrix[row][col-1] = light;
			matrix[row+1][col-1] = light;
			
		}
		
	}

}
