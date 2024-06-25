import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class Sudoku
{
    public static class SudokuGenerator
    {
        public int[][] grid;
        public int gridSideLength;
        public double exactSquareRootOfGridSideLength;
        public int squareRootOfGridSideLength;
        public int numberOfMissingDigits;

        SudokuGenerator(int gridSideLength, int numberOfMissingDigits)
        {
            this.gridSideLength = gridSideLength;
            this.numberOfMissingDigits = numberOfMissingDigits;
            this.exactSquareRootOfGridSideLength = Math.sqrt(gridSideLength);
            this.squareRootOfGridSideLength = (int) this.exactSquareRootOfGridSideLength;
            this.grid = new int[gridSideLength][gridSideLength];
        }

        public boolean doesNotExistInRow(int i, int num)
        {
            for (int j = 0; j < this.gridSideLength; j++)
            {
                if (this.grid[i][j] == num) return false;
            }
            return true;
        }

        public boolean doesNotExistInColumn(int j, int num)
        {
            for (int i = 0; i < this.gridSideLength; i++)
            {
                if (this.grid[i][j] == num) return false;
            }
            return true;
        }

        public boolean doesNotExistInSubGrid(int rowStart, int columnStart, int num)
        {
            for (int i = 0; i < this.squareRootOfGridSideLength; i++)
            {
                for (int j = 0; j < this.squareRootOfGridSideLength; j++)
                {
                    if (this.grid[rowStart + i][columnStart + j] == num) return false;
                }
            }
            return true;
        }

        public boolean isValidlyPlaced(int i, int j, int num)
        {
            return doesNotExistInRow(i, num) &&
                    doesNotExistInColumn(j, num) &&
                    doesNotExistInSubGrid(i - i % this.squareRootOfGridSideLength, j - j % this.squareRootOfGridSideLength, num);
        }

        public int fillNumber(int num) {
            return (int) (Math.random() * num + 1);
        }

        public void fillSubGrid(int row, int column)
        {
            int num;
            for (int i = 0; i < this.squareRootOfGridSideLength; i++)
            {
                for (int j = 0; j < this.squareRootOfGridSideLength; j++)
                {
                    do
                    {
                        num = this.fillNumber(this.gridSideLength);
                    } while (!doesNotExistInSubGrid(row, column, num));
                    this.grid[row + i][column + j] = num;
                }
            }
        }

        public void fillDiagonal()
        {
            for (int i = 0; i < this.gridSideLength; i += squareRootOfGridSideLength) this.fillSubGrid(i, i);
        }

        public boolean fillRemaining(int i, int j)
        {
            if (j >= this.gridSideLength && i < this.gridSideLength - 1)
            {
                i++;
                j = 0;
            }
            if (i >= this.gridSideLength && j >= this.gridSideLength) return true;
            if (i < this.squareRootOfGridSideLength)
            {
                if (j < this.squareRootOfGridSideLength) j = this.squareRootOfGridSideLength;
            }
            else if (i < this.gridSideLength - this.squareRootOfGridSideLength)
            {
                if (j == (i / this.squareRootOfGridSideLength) * this.squareRootOfGridSideLength) j += squareRootOfGridSideLength;
            }
            else
            {
                if (j == this.gridSideLength - this.squareRootOfGridSideLength)
                {
                    i++;
                    j = 0;
                    if (i >= this.gridSideLength) return true;
                }
            }

            for (int num = 1; num <= this.gridSideLength; num++)
            {
                if (this.isValidlyPlaced(i, j, num))
                {
                    grid[i][j] = num;
                    if (fillRemaining(i, j + 1)) return true;
                    grid[i][j] = 0;
                }
            }

            return false;
        }

        public void fillNumberOfMissingDigits()
        {
            int count = numberOfMissingDigits;
            while (count != 0)
            {
                int cellId = fillNumber(this.gridSideLength * this.gridSideLength) - 1;
                int i = cellId / this.gridSideLength;
                int j = cellId % this.gridSideLength;
                if (grid[i][j] != 0)
                {
                    grid[i][j] = 0;
                    count--;
                }
            }
        }

        public void fillGrid()
        {
            this.fillDiagonal();
            this.fillRemaining(0, this.squareRootOfGridSideLength);
            this.fillNumberOfMissingDigits();
        }
    }

    public static class SudokuSolver
    {
        public static int [][] grid;
        public int gridSize;

        public SudokuSolver(int [] @NotNull [] grid)
        {
            SudokuSolver.grid = grid;
            this.gridSize = grid.length;
        }

        public boolean numberExistsInColumn(int num, int column)
        {
            for(int i = 0; i < this.gridSize; i++)
            {
                if(grid[i][column] == num) return true;
            }
            return false;
        }

        public boolean numberExistsInRow(int num, int row)
        {
            for(int j = 0; j < this.gridSize; j++)
            {
                if(grid[row][j] == num) return true;
            }
            return false;
        }

        public boolean numberExistsInSubGrid(int num, int column, int row)
        {
            int localSubGridRow = row - row % 3;
            int localSubGridColumn = column - column % 3;
            for(int i = localSubGridRow; i < localSubGridRow + 3; i++)
            {
                for(int j = localSubGridColumn; j < localSubGridColumn + 3; j++)
                {
                    if(grid[i][j] == num) return true;
                }
            }
            return false;
        }

        public boolean numberPlacementValidity(int num, int column, int row)
        {
            return !numberExistsInColumn(num, column) && !numberExistsInRow(num, row) && !numberExistsInSubGrid(num, column, row);
        }

        public boolean solveGrid()
        {
            for(int row = 0; row < gridSize; row++)
            {
                for(int column = 0; column < gridSize; column++)
                {
                    if(grid[row][column] == 0)
                    {
                        for(int numCandidate = 1; numCandidate <= gridSize; numCandidate++)
                        {
                            if(numberPlacementValidity(numCandidate, column, row))
                            {
                                grid[row][column] = numCandidate;
                                if(solveGrid()) return true;
                                else grid[row][column] = 0;
                            }
                        }
                        return false;
                    }
                }
            }
            return true;
        }

    }

    public static void main(String[] args)
    {
        int n = 9;
        int m = n * n - 50;

        SudokuGenerator sudokuGenerator = new SudokuGenerator(n, m);
        sudokuGenerator.fillGrid();
        System.out.println("Generated Sudoku Puzzle: ");
        for(int [] column : sudokuGenerator.grid) System.out.println(Arrays.toString(column));
        System.out.println();

        SudokuSolver sudokuSolver = new SudokuSolver(sudokuGenerator.grid);
        if(sudokuSolver.solveGrid()) System.out.println("Sudoku Puzzle solved successfully!");
        else System.out.println("Unsolved... invalid puzzle.");
        for(int [] column : SudokuSolver.grid) System.out.println(Arrays.toString(column));
    }
}
