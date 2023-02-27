/**
 * Assignment 1 - ConnectFour
 * @Author: Emilio Cruz
 * @Date: 1/28/23
 */
package edu.quinnipiac.ser210.fourinarowapp

import android.R.color
import android.os.Parcel
import android.os.Parcelable


class FourInARow() : IGame {
    // game board in 2D array initialized to zeros
    var board = Array(GameConstants.ROWS) { IntArray(GameConstants.COLS){0} }

    override fun clearBoard()
    {
        for (row in 0 until GameConstants.ROWS)
        {
            for (col in 0 until GameConstants.COLS)
            {
                board[row][col] = GameConstants.EMPTY
            }
        }
    }

    override fun setMove(player: Int, location: Int)
    {
        val r = location / GameConstants.ROWS
        val c = location % GameConstants.COLS

        board[r][c] = player

        if(player == GameConstants.BLUE)
            println("PLAYER MOVE SET")
        else
            println("AI MOVE SET")

        printBoard()
    }

    override val computerMove: Int
        get()
        {
            var move : Int = (0..35).random()
            var countH : Int = 1
            var countV : Int = 1

            // if the player gets three tiles in a row, the computer will try to block the win
            for (i in 0..34)
            {
                // if the current cell is not empty, compare it to the next cell and verify it's in the same row
                if (getCell(i) == GameConstants.BLUE && getCell(i) == getCell((i+1)) && (i+1) % 6 != 0)
                    countH++
                else
                    countH = 1

                if (countH == 3) {
                    // trying to block win on the right
                    if ((i + 2) <= 35 && getCell((i + 2)) == GameConstants.EMPTY && (i + 2) % 6 != 0)
                    {
                        move = i + 2
                        break
                    }

                    // otherwise, try to block win on left
                    else if (i > 1 && getCell((i - 2)) == GameConstants.EMPTY && (i - 2) % 6 != 5)
                    {
                        move = i - 2
                        break
                    }

                    // win has already been blocked
                    else
                        countH = 1
                }
            }

            // if the player gets three tiles in a column, the computer will try to block the win
            for (i in 0..5)
            {
                // checking for win down each column
                for(j in (i)..(i + 24) step 6)
                {
                    // if current cell is not empty, compare it to cell below
                    if (getCell(j) != GameConstants.EMPTY && getCell(j) == getCell((j+6)))
                        countV++
                    else
                        countV = 1

                    if(countV == 3)
                    {
                        // trying to block column win underneath
                        if ((j + 12) <= 35 && getCell((j + 12)) == GameConstants.EMPTY)
                        {
                            move = j + 12
                            break
                        }

                        // otherwise, try to block win above
                        else if (j > 11 && getCell((j - 12)) == GameConstants.EMPTY)
                        {
                            move = j - 12
                            break
                        }

                        // win has already been blocked
                        else
                            countV = 1
                    }
                }
            }

            // if no wins to block or random move is already taken
            if (getCell(move) != GameConstants.EMPTY)
            {
                do
                {
                    move = (0..35).random()
                } while (getCell(move) != GameConstants.EMPTY)
                // Generates random move until an available tile is found
            }

            return move
        }

    override fun checkForWinner(): Int
    {
        // running counter for horizontal matches
        var countH : Int = 1

        // running counter for vertical matches
        var countV : Int = 1

        // running counter for diagonal matches
        var countD : Int = 1

        if(checkBoardFull())
            return GameConstants.TIE

        // checking for horizontal win
        for (i in 0..34)
        {
            // if the current cell is not empty, compare it to the next cell and verify it's in the same row
            if (getCell(i) != GameConstants.EMPTY && getCell(i) == getCell((i+1)) && (i+1) % 6 != 0)
                countH++
            else
                countH = 1

            if (countH == 4)
            {
                println("Row Win")
                if(getCell(i) == GameConstants.BLUE)
                    return GameConstants.BLUE_WON
                else
                    return GameConstants.RED_WON
            }
        }

        // checking for vertical win
        for (i in 0..5)
        {
            countV = 1

            // checking for win down each column
            for(j in (i)..(i + 24) step 6)
            {
                println("j = " + j)
                // if current cell is not empty, compare it to cell below
                if (getCell(j) != GameConstants.EMPTY && getCell(j) == getCell((j+6)))
                    countV++
                else
                    countV = 1

                println("Count: " + countV)

                if(countV == 4)
                {
                    val x = j+6
                    println("last cell: " + x)
                    println("Column Win")
                    if(getCell(j) == GameConstants.BLUE)
                        return GameConstants.BLUE_WON
                    else
                        return GameConstants.RED_WON
                }
            }
        }


        // checking for diagonal win
        for(i in 0..17)
        {
            // i is the index of the first element and ii moves down the diagonal
            if(getCell(i) != GameConstants.EMPTY)
            {
                var ii : Int = i

                while(ii < 35 && countD != 4)
                {
                    if(i % 6 <= 2 && getCell(ii) == getCell((ii+7)))
                    {
                        countD++
                        ii += 7
                    }
                    else if(i % 6 > 2 && getCell(ii) == getCell((ii+5)))
                    {
                        countD++
                        ii += 5
                    }
                    else
                    {
                        countD = 1
                        break
                    }
                }
            }
            else
                countD = 1

            if(countD == 4)
            {
                println("Diagonal Win")
                if(getCell(i) == GameConstants.BLUE)
                    return GameConstants.BLUE_WON
                else
                    return GameConstants.RED_WON
            }
        }


        return GameConstants.PLAYING
    }

    /**
     * Print the game board
     */
    fun printBoard() {
        for (row in 0 until GameConstants.ROWS) {
            for (col in 0 until GameConstants.COLS) {
                printCell(board[row][col]) // print each of the cells
                if (col != GameConstants.COLS - 1) {
                    print("|") // print vertical partition
                }
            }
            println()
            if (row != GameConstants.ROWS - 1) {
                println("----------------------") // print horizontal partition
            }
        }
        println()
    }

    /**
     * Print a cell with the specified "content"
     * @param content either BLUE, RED or EMPTY
     */
    fun printCell(content: Int) {
        when (content) {
            GameConstants.EMPTY -> print("   ")
            GameConstants.BLUE -> print(" B ")
            GameConstants.RED -> print(" R ")
        }
    }

    fun getCell(location: Int): Int
    {
        val r = location / GameConstants.ROWS
        val c = location % GameConstants.COLS

        return board[r][c]
    }

    fun checkBoardFull(): Boolean
    {
        for (row in 0 until GameConstants.ROWS)
        {
            for (col in 0 until GameConstants.COLS)
            {
                //returns false if an empty tile is found
                if(board[row][col] == GameConstants.EMPTY)
                    return false

            }
        }

        return true
    }
}

