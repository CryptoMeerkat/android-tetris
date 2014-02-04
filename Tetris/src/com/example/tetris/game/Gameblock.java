package com.example.tetris.game;

public class Gameblock {
	
	private int blockID;
	private BrickOrientation brickOri;
	private int row;
	private int col;
	private int maxRightCol = 11; // the maximum col positon right
	private int maxLeftCol 	= 0; // the maximum col positon left
	
	public Gameblock(int blockID, BrickOrientation brickOri, int row, int col) {
		this.blockID = blockID;
		this.brickOri = brickOri;
		this.row = row;
		this.col = col;
	}
	
	/**
	 * Checks if the block cannot move any further.
	 * 
	 * @param gamefield the gamefield to check with
	 * @return true, if block cannot move any further
	 */
	public boolean isLocked(byte[][] gamefield) {
		return isLocked(gamefield, brickOri);
	}
	
	public boolean isLocked(byte[][] gamefield, BrickOrientation brickOri) {
		return isLocked(gamefield, brickOri, row, col);
	}
	
	public boolean isLocked(byte[][] gamefield, BrickOrientation brickOri, int row, int col) {
		
		switch(blockID) {
			case Gamefield.BRICK_I: return checkBrickI(gamefield, brickOri, row, col);
			case Gamefield.BRICK_J: return checkBrickJ(gamefield, brickOri, row, col);
			case Gamefield.BRICK_L: return checkBrickL(gamefield, brickOri, row, col);
			case Gamefield.BRICK_O: return checkBrickO(gamefield, brickOri, row, col);
			case Gamefield.BRICK_S: return checkBrickS(gamefield, brickOri, row, col);
			case Gamefield.BRICK_T: return checkBrickT(gamefield, brickOri, row, col);
			default: 	  			return checkBrickZ(gamefield, brickOri, row, col);
		}
		
	}
	
	public boolean isLockedNextRow(byte[][] gamefield) {
		return isLocked(gamefield, brickOri, row+1, col);
	}
	
	/**
	 * Calculates the possible column offset for this brick.
	 * Returns the max offset, if the given colOffset is greater than the max offset
	 * or the given colOffset, if it's between the max offset to the left and right.
	 * 
	 * @param colOffset the wanted column offset
	 * @return the offset the brick can do
	 */
	public int getPossibleOffset(int colOffset) {
		
		switch(blockID) {
			case Gamefield.BRICK_I: return checkMaxOffsetBrickI(brickOri, col + colOffset);
			case Gamefield.BRICK_J: return checkMaxOffsetBrickJ(brickOri, col + colOffset);
			case Gamefield.BRICK_L: return checkMaxOffsetBrickL(brickOri, col + colOffset);
			case Gamefield.BRICK_O: return checkMaxOffsetBrickO(brickOri, col + colOffset);
			case Gamefield.BRICK_S: return checkMaxOffsetBrickS(brickOri, col + colOffset);
			case Gamefield.BRICK_T: return checkMaxOffsetBrickT(brickOri, col + colOffset);
			default: 	  			return checkMaxOffsetBrickZ(brickOri, col + colOffset);
		}
		
	}
	
	
	private int checkMaxOffsetBrickI(BrickOrientation brickOri, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {
			
			if (col < maxLeftCol) return maxLeftCol;
			if (col > maxRightCol) return maxRightCol;
			
		} else {

			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol - 2) return maxRightCol - 2;
			
		}
		
		return col;
		
	}
	
	private int checkMaxOffsetBrickJ(BrickOrientation brickOri, int col) {
		
		if (brickOri == BrickOrientation.UP) {
			
			if (col < maxLeftCol) return maxLeftCol;
			if (col > maxRightCol - 1) return maxRightCol - 1;
			
		} else if (brickOri == BrickOrientation.DOWN) {

			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol) return maxRightCol;
			
		} else {
			
			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol - 1) return maxRightCol - 1;
			
		}
		
		return col;
		
	}
	
	private int checkMaxOffsetBrickL(BrickOrientation brickOri, int col) {
		
		if (brickOri == BrickOrientation.UP) {
			
			if (col < maxLeftCol) return maxLeftCol;
			if (col > maxRightCol - 1) return maxRightCol - 1;
			
		} else if (brickOri == BrickOrientation.DOWN) {

			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol) return maxRightCol;
			
		} else {
			
			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol - 1) return maxRightCol - 1;
			
		}
		
		return col;
		
	}
	
	private int checkMaxOffsetBrickO(BrickOrientation brickOri, int col) {
		
		if (col < maxLeftCol) return maxLeftCol;
		if (col > maxRightCol - 1) return maxRightCol - 1;
		
		return col;
		
	}
	
	private int checkMaxOffsetBrickS(BrickOrientation brickOri, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {
			
			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol - 1) return maxRightCol - 1;
			
		} else {
			
			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol) return maxRightCol;
			
		}
		
		return col;
		
	}
	
	private int checkMaxOffsetBrickT(BrickOrientation brickOri, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {
			
			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol - 1) return maxRightCol - 1;
			
		} else if (brickOri == BrickOrientation.RIGHT) {
			
			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol) return maxRightCol;
			
		} else {
			
			if (col < maxLeftCol) return maxLeftCol;
			if (col > maxRightCol - 1) return maxRightCol - 1;
			
		}
		
		return col;
		
	}
	
	private int checkMaxOffsetBrickZ(BrickOrientation brickOri, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {
			
			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol - 1) return maxRightCol - 1;
			
		} else {
			
			if (col < maxLeftCol + 1) return maxLeftCol + 1;
			if (col > maxRightCol) return maxRightCol;
			
		}
		
		return col;
		
	}
	
	private boolean checkBrickI(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {
			
			if (row > 21) return true;

			return matrix[row-1][col] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light
					|| matrix[row+2][col] == Gamefield.light;
			
		} else {

			if (row > 23) return true;

			return matrix[row][col-1] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row][col+1] == Gamefield.light
					|| matrix[row][col+2] == Gamefield.light;
			
		}
		
	}
	
	private boolean checkBrickJ(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP) {

			if (row > 22) return true;
			
			return matrix[row-1][col] == Gamefield.light
					|| matrix[row-1][col+1] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light;
			
		} else if (brickOri == BrickOrientation.RIGHT) {
			
			if (row > 22) return true;
			
			return matrix[row][col-1] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row][col+1] == Gamefield.light
					|| matrix[row+1][col+1] == Gamefield.light;
			
		} else if (brickOri == BrickOrientation.DOWN) {

			if (row > 22) return true;
			
			return matrix[row-1][col] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light
					|| matrix[row+1][col-1] == Gamefield.light;
			
		} else {
			
			if (row > 23) return true;
			
			return matrix[row-1][col-1] == Gamefield.light
					|| matrix[row][col-1] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row][col+1] == Gamefield.light;
			
		}
		
	}

	private boolean checkBrickL(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP) {

			if (row > 22) return true;
			
			return matrix[row-1][col] == Gamefield.light
			  		|| matrix[row][col] == Gamefield.light
			  		|| matrix[row+1][col] == Gamefield.light
			  		|| matrix[row+1][col+1] == Gamefield.light;
			
		} else if (brickOri == BrickOrientation.RIGHT) {
			
			if (row > 22) return true;
		
			return matrix[row][col-1] == Gamefield.light
					|| matrix[row+1][col-1] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row][col+1] == Gamefield.light;
			
		} else if (brickOri == BrickOrientation.DOWN) {

			if (row > 22) return true;
			
			return matrix[row-1][col-1] == Gamefield.light
					|| matrix[row-1][col] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light;
			
		} else {

			if (row > 23) return true;
			
			return matrix[row][col-1] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row][col+1] == Gamefield.light
					|| matrix[row-1][col+1] == Gamefield.light;
			
		}
		
	}

	private boolean checkBrickO(byte[][] matrix, BrickOrientation brickOri, int row, int col) {

		if (row > 22) return true;
		
		return matrix[row][col] == Gamefield.light
				|| matrix[row][col+1] == Gamefield.light
				|| matrix[row+1][col] == Gamefield.light
				|| matrix[row+1][col+1] == Gamefield.light;
		
	}

	private boolean checkBrickS(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {

			if (row > 22) return true;
			
			return matrix[row+1][col-1] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row][col+1] == Gamefield.light;
			
		} else {

			if (row > 22) return true;
			
			return matrix[row-1][col-1] == Gamefield.light
					|| matrix[row][col-1] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light;
			
		}
		
	}
	
	private boolean checkBrickT(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP) {
			
			if (row > 22) return true;
			
			return matrix[row][col-1] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light
					|| matrix[row][col+1] == Gamefield.light;
			
		} else if (brickOri == BrickOrientation.RIGHT) {

			if (row > 22) return true;
			
			return matrix[row][col-1] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light;
			
		} else if (brickOri == BrickOrientation.DOWN) {

			if (row > 23) return true;
			
			return matrix[row][col-1] == Gamefield.light
					||matrix[row][col] == Gamefield.light
					||matrix[row-1][col] == Gamefield.light
					|| matrix[row][col+1] == Gamefield.light;
			
		} else {

			if (row > 22) return true;
			
			return matrix[row][col+1] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light;
			
		}
		
	}

	private boolean checkBrickZ(byte[][] matrix, BrickOrientation brickOri, int row, int col) {
		
		if (brickOri == BrickOrientation.UP || brickOri == BrickOrientation.DOWN) {

			if (row > 22) return true;
			
			return matrix[row][col-1] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row+1][col] == Gamefield.light
					|| matrix[row+1][col+1] == Gamefield.light;
			
		} else {

			if (row > 22) return true;
			
			return matrix[row-1][col] == Gamefield.light
					|| matrix[row][col] == Gamefield.light
					|| matrix[row][col-1] == Gamefield.light
					|| matrix[row+1][col-1] == Gamefield.light;
			
		}
		
	}

	public void setNextRow() {
		this.row++;
	}
	
	public int getBlockID() {
		return blockID;
	}

	public BrickOrientation getBrickOri() {
		return brickOri;
	}

	public void setBrickOri(BrickOrientation orientation) {
		brickOri = orientation;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	
}
