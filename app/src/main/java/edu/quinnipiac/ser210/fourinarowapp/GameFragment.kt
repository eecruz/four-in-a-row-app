/**
 * Assignment 2 - ConnectFourApp
 * @Author: Emilio Cruz
 * @Date: 2/26/23
 */
package edu.quinnipiac.ser210.fourinarowapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class GameFragment : Fragment(), View.OnClickListener
{
    // used to represent the game in an array
    var FIR_board = FourInARow()

    // instance variables
    var currentState: Int = GameConstants.PLAYING
    var boardButtons = Array<ImageButton?>(36){null}
    lateinit var userName: String

    // used to save and load instances of board on screen rotation
    var buttonStates = BooleanArray(36){true}
    var buttonColors = IntArray(36){0}

    // view representations
    lateinit var buttonReset: Button
    lateinit var playerTurnText: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        // populate views with ids
        playerTurnText = view.findViewById<TextView>(R.id.playerTurnText)
        buttonReset = view.findViewById<Button>(R.id.button_reset)

        // update view text with username argument
        userName = GameFragmentArgs.fromBundle(requireArguments()).username
        playerTurnText.text = "Make your move, $userName!"

        // retrieves button views and populates array with correct ids
        for (i in boardButtons.indices)
        {
            // iterates id
            val buttonID: String = "button_" + (i)
            val resID = resources.getIdentifier(buttonID, "id", "edu.quinnipiac.ser210.fourinarowapp")

            // assigns button in array with corresponding id and adds listener
            boardButtons[i] = view.findViewById<ImageButton>(resID)
            boardButtons[i]!!.setOnClickListener(this)
        }

        buttonReset.setOnClickListener(this)

        return view
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        // saves important variables on screen rotation
        super.onSaveInstanceState(outState)
        outState.putSerializable("board", FIR_board.board)
        outState.putBooleanArray("buttonStates", buttonStates)
        outState.putIntArray("buttonColors", buttonColors)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)

        if(savedInstanceState != null)
        {
            // loads in array values from savedInstanceState
            FIR_board.board = savedInstanceState!!.getSerializable("board") as (Array<IntArray>)
            buttonStates = savedInstanceState.getBooleanArray("buttonStates") as BooleanArray
            buttonColors = savedInstanceState.getIntArray("buttonColors") as IntArray

            // restores board state according to loaded arrays
            for(i in boardButtons.indices)
            {
                boardButtons[i]!!.isEnabled = buttonStates[i]

                when(buttonColors[i])
                {
                    GameConstants.BLUE ->
                        boardButtons[i]!!.setImageResource(R.drawable.blue_circle)

                    GameConstants.RED ->
                        boardButtons[i]!!.setImageResource(R.drawable.red_circle)
                }
            }

            // restores playerTurnText
            updateTurnText()
        }
    }

    private fun updateBoard(button: ImageButton?)
    {
        // make player's move
        val move: Int = button!!.tag.toString().toInt()
        FIR_board.setMove(GameConstants.BLUE, move)

        // update appearance of button and disables it
        boardButtons[move]!!.setImageResource(R.drawable.blue_circle)
        buttonStates[move] = false
        boardButtons[move]!!.isEnabled = buttonStates[move]
        buttonColors[move] = GameConstants.BLUE

        // checks if player won
        if (FIR_board.checkForWinner() == GameConstants.BLUE_WON)
        {
            playerTurnText.text = "$userName Wins!"
            onGameFinish()
            return
        }

        // make AI move
        val aiMove = FIR_board.computerMove
        FIR_board.setMove(GameConstants.RED, aiMove)

        // update appearance of button and disable it
        boardButtons[aiMove]!!.setImageResource(R.drawable.red_circle)
        buttonStates[aiMove] = false
        boardButtons[aiMove]!!.isEnabled = buttonStates[aiMove]
        buttonColors[aiMove] = GameConstants.RED

        // checks if AI won or there's a tie
        updateTurnText()
    }

    private fun updateTurnText()
    {
        // updates playerTurnText based on situation
        when (FIR_board.checkForWinner())
        {
            GameConstants.BLUE_WON ->
            {
                playerTurnText.text = "$userName Wins!"
                onGameFinish()
            }

            GameConstants.RED_WON ->
            {
                playerTurnText.text = "You Lose, $userName! Better Luck Next Time!"
                onGameFinish()
            }

            GameConstants.TIE ->
            {
                playerTurnText.text = getString(R.string.gameTie)
                onGameFinish()
            }

            GameConstants.PLAYING ->
            {
                playerTurnText.text = "Make your move, $userName!"
            }
        }
    }

    private fun onGameFinish()
    {
        // disables board buttons and enables reset button
        buttonReset.isEnabled = true

        for (i in boardButtons.indices)
        {
            buttonStates[i] = false
            boardButtons[i]!!.isEnabled = buttonStates[i]
        }
    }

    override fun onClick(button: View?)
    {
        when (button!!.id)
        {
            // if button clicked is reset button
            R.id.button_reset ->
            {
                // enables and resets all board buttons
                for (i in boardButtons.indices)
                {
                    buttonStates[i] = true
                    boardButtons[i]!!.isEnabled = buttonStates[i]
                    buttonColors[i] = 0
                    boardButtons[i]!!.setImageResource(buttonColors[i])
                }

                // resets instance variables
                currentState = GameConstants.PLAYING
                FIR_board = FourInARow()
                playerTurnText.text = "Make your move, $userName!"

                Toast.makeText(requireActivity(), "New Game!", Toast.LENGTH_SHORT).show()

                // disables reset button until end of next game
                buttonReset.isEnabled = false
            }

            // if button clicked is a board button
            else ->
            {
                val b = boardButtons[button.tag.toString().toInt()]
                updateBoard(b)
            }
        }
    }
}