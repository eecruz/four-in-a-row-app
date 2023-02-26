/** Assignment: Assignment 2
 *  @author: Emilio Cruz
 *  @date:
 */
package edu.quinnipiac.ser210.fourinarowapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.findNavController

class WelcomeFragment : Fragment()
{
    lateinit var playerName: EditText
    lateinit var start: Button

    private val textWatcher: TextWatcher = object : TextWatcher
    {
        // checks if user has entered name and enables start button accordingly
        override fun onTextChanged(a: CharSequence, b: Int, c: Int, d: Int)
        {
            val name = playerName!!.text.toString().trim()
            start.isEnabled = (!name.isEmpty())
        }

        // unused functions
        override fun beforeTextChanged(a: CharSequence, b: Int, c: Int, d: Int) {}
        override fun afterTextChanged(a: Editable) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        // late initialization of instance variables
        playerName = view.findViewById<EditText>(R.id.personName)
        start = view.findViewById<Button>(R.id.start)

        start.isEnabled = false
        playerName.addTextChangedListener(textWatcher)

        start.setOnClickListener()
        {
            val name = playerName.text.toString().trim()
            val action = WelcomeFragmentDirections.actionWelcomeFragmentToGameFragment(name)
            view.findNavController().navigate(action)
        }

        return view
    }
}