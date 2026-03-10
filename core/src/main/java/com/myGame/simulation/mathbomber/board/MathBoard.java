package com.myGame.simulation.mathbomber.board;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class MathBoard {
    public static final class BlastCell {
        private final int row;
        private final int col;

        public BlastCell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    public interface TilePredicate {
        boolean test(int row, int col);
    }

    public static final class TileBounds {
        private final int minRow;
        private final int maxRow;
        private final int minCol;
        private final int maxCol;

        public TileBounds(int minRow, int maxRow, int minCol, int maxCol) {
            if (minRow > maxRow) {
                throw new IllegalArgumentException("minRow cannot be greater than maxRow");
            }
            if (minCol > maxCol) {
                throw new IllegalArgumentException("minCol cannot be greater than maxCol");
            }
            this.minRow = minRow;
            this.maxRow = maxRow;
            this.minCol = minCol;
            this.maxCol = maxCol;
        }

        public int getMinRow() {
            return minRow;
        }

        public int getMaxRow() {
            return maxRow;
        }

        public int getMinCol() {
            return minCol;
        }

        public int getMaxCol() {
            return maxCol;
        }
    }

    private final int rows;
    private final int cols;
    private final float tileSize;
    private final float boardX;
    private final float boardY;
    private final boolean[][] hardWalls;

    public MathBoard(int rows, int cols, float tileSize, float screenWidth, float screenHeight) {
        this.rows = rows;
        this.cols = cols;
        this.tileSize = tileSize;
        this.boardX = Math.max(0f, (screenWidth - cols * tileSize) * 0.5f);
        this.boardY = 0f;
        this.hardWalls = new boolean[rows][cols];
        buildDefaultWalls();
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public float getTileSize() {
        return tileSize;
    }

    public float getBoardX() {
        return boardX;
    }

    public float getBoardY() {
        return boardY;
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean isWall(int row, int col) {
        return hardWalls[row][col];
    }

    public TileBounds circleBoundsAt(float worldX, float worldY, float radius) {
        float centerX = worldX;
        float centerY = worldY;

        float left = centerX - radius;
        float right = centerX + radius;
        float bottom = centerY - radius;
        float top = centerY + radius;

        int minCol = (int) Math.floor((left - boardX) / tileSize);
        int maxCol = (int) Math.floor((right - boardX) / tileSize);
        int minRow = (int) Math.floor((bottom - boardY) / tileSize);
        int maxRow = (int) Math.floor((top - boardY) / tileSize);

        return new TileBounds(minRow, maxRow, minCol, maxCol);
    }

    public boolean tileBoundsContain(TileBounds bounds, int row, int col) {
        return row >= bounds.minRow
                && row <= bounds.maxRow
                && col >= bounds.minCol
                && col <= bounds.maxCol;
    }

    public boolean allTilesPass(TileBounds bounds, TilePredicate predicate) {
        for (int row = bounds.minRow; row <= bounds.maxRow; row++) {
            for (int col = bounds.minCol; col <= bounds.maxCol; col++) {
                if (!predicate.test(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void draw(ShapeRenderer shape, float screenWidth, float screenHeight) {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(new Color(0.12f, 0.13f, 0.16f, 1f));
        shape.rect(0f, 0f, screenWidth, screenHeight);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float x = boardX + c * tileSize;
                float y = boardY + r * tileSize;
                if (hardWalls[r][c]) {
                    shape.setColor(new Color(0.20f, 0.40f, 0.88f, 1f));
                } else {
                    shape.setColor(new Color(0.80f, 0.82f, 0.87f, 1f));
                }
                shape.rect(x, y, tileSize - 1f, tileSize - 1f);
            }
        }
        shape.end();
    }

    public List<BlastCell> computeBlastCells(int centerRow, int centerCol, int range) {
        List<BlastCell> result = new ArrayList<>();
        result.add(new BlastCell(centerRow, centerCol));

        addRayCells(result, centerRow, centerCol, 1, 0, range);
        addRayCells(result, centerRow, centerCol, -1, 0, range);
        addRayCells(result, centerRow, centerCol, 0, 1, range);
        addRayCells(result, centerRow, centerCol, 0, -1, range);
        return List.copyOf(result);
    }

    private void addRayCells(List<BlastCell> cells, int row, int col, int dRow, int dCol, int range) {
        for (int i = 1; i <= range; i++) {
            int nr = row + dRow * i;
            int nc = col + dCol * i;
            if (!isInside(nr, nc) || hardWalls[nr][nc]) {
                break;
            }
            cells.add(new BlastCell(nr, nc));
        }
    }

    private void buildDefaultWalls() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean border = r == 0 || c == 0 || r == rows - 1 || c == cols - 1;
                boolean evenEvenPillar = (r % 2 == 0) && (c % 2 == 0);
                hardWalls[r][c] = border || evenEvenPillar;
            }
        }
    }
}
