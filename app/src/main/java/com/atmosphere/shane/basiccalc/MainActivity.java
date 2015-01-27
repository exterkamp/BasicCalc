package com.atmosphere.shane.basiccalc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

public class MainActivity extends ActionBarActivity {

    //the string of infix characters
    ArrayList<String> mEquStack = new ArrayList<String>();
    //the infix string made to keep track of operations
    //String mInfixOutput = "";
    //precedence of operations
    String mPrecedence = "*/+-";
    //the current number stored as string
    String mCurrentNumber;
    //the output, old variable kept for posterity
    TextView mOutputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mCurrentNumber = "0";
        mCurrentNumber = "0";
        //get the reference for the textView
        mOutputText = (TextView) findViewById(R.id.outputText);
        updateOutput();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void button_handler(View view){
        Button b = (Button)view;
        String buttonText = "ERROR";
        //b.getId();
        //use based on ID to prevent ANY language or string mix-ups
        switch (b.getId())
        {
            case R.id.key1:
                buttonText = "1";
                break;
            case R.id.key2:
                buttonText = "2";
                break;
            case R.id.key3:
                buttonText = "3";
                break;
            case R.id.key4:
                buttonText = "4";
                break;
            case R.id.key5:
                buttonText = "5";
                break;
            case R.id.key6:
                buttonText = "6";
                break;
            case R.id.key7:
                buttonText = "7";
                break;
            case R.id.key8:
                buttonText = "8";
                break;
            case R.id.key9:
                buttonText = "9";
                break;
            case R.id.key0:
                buttonText = "0";
                break;
            case R.id.keyDiv:
                buttonText = "/";
                break;
            case R.id.keyMult:
                buttonText = "*";
                break;
            case R.id.keySub:
                buttonText = "-";
                break;
            case R.id.keyAdd:
                buttonText = "+";
                break;
            case R.id.keyClr:
                buttonText = "C";
                break;
            case R.id.keyEqu:
                buttonText = "=";
                break;
            case R.id.keyBack:
                buttonText = "<";
                break;
        }

        //String outputTextString = "";
        //Toast.makeText(getBaseContext(), buttonText, Toast.LENGTH_SHORT).show();
        switch (buttonText){
            case "*":
                mEquStack.add(mCurrentNumber);
                mCurrentNumber = "0";
                mEquStack.add(buttonText);
                updateOutput();
                if ((buildInfix().length() - buildInfix().replaceAll("\\*", "").length()) > 2)
                {
                    Toast.makeText(getBaseContext(), "Max multiplications reached", Toast.LENGTH_SHORT).show();
                    ((Button) findViewById(R.id.keyMult)).setEnabled(false);
                    break;
                }
                break;
            case "+":
            case "-":
            case "/":
                mEquStack.add(mCurrentNumber);
                mCurrentNumber = "0";
                mEquStack.add(buttonText);
                updateOutput();
                break;
            case "=":
                mEquStack.add(mCurrentNumber);
                mCurrentNumber = "";

                //call function to make the postfix
                ArrayList<String> output = makePostfix(mEquStack);

                //call function to make the solved postfix
                String example = evaluatePostfix(output);

                //update display with solved equation
                updateOutput(example);

                //reset the array and infix and mult button
                if (!(((Button) findViewById(R.id.keyMult)).isEnabled()) && example.length() <= 5)
                {
                    ((Button) findViewById(R.id.keyMult)).setEnabled(true);
                }
                if (example.length() > 5)
                {
                    ((Button) findViewById(R.id.keyMult)).setEnabled(false);
                }

                mEquStack = new ArrayList<String>();
                mCurrentNumber = example;
                break;
            case "C":
                mCurrentNumber = "0";
                if (!(((Button) findViewById(R.id.keyMult)).isEnabled()))
                {
                    ((Button) findViewById(R.id.keyMult)).setEnabled(true);
                }
                //reset the array and infix
                mEquStack = new ArrayList<String>();
                //mInfixOutput = "";
                //outputTextString = mCurrentNumber;
                updateOutput();
                break;
            case "<":
                //if there is a mCurrentNumber delete an element from it
                if (mCurrentNumber.length() > 1){ mCurrentNumber = mCurrentNumber.substring(0,mCurrentNumber.length()-1);}
                //if it is the last mCurrentNumber delete it and then make the next number the mCurrent Number
                else if (mCurrentNumber.length() == 1) { mCurrentNumber = "";}
                else if (mCurrentNumber.length() == 0){
                    //get new mEquStack
                    if (mEquStack.size() >= 1) {
                        //test on the new stack
                        String s = mEquStack.get(mEquStack.size()-1);
                        //if it is an operator, remove it
                        if ((s.contains("*") || s.contains("/") || s.contains("-") || s.contains("+"))){
                            mEquStack.remove(mEquStack.size()-1);
                            String s2 = mEquStack.get(mEquStack.size()-1);
                            mCurrentNumber = s2;
                            mEquStack.remove(mEquStack.size()-1);
                        }
                        //this should not happen, numbers shouldn't be next to numbers
                        else
                        {
                            Toast.makeText(getBaseContext(), "Backspace Error", Toast.LENGTH_SHORT).show();
                            mCurrentNumber = s;
                            mEquStack.remove(s);
                        }
                    }
                }
                if (mCurrentNumber.length() == 0 && mEquStack.size() == 0)
                {
                    mCurrentNumber = "0";
                }

                //update the mult button
                if (!(((Button) findViewById(R.id.keyMult)).isEnabled()) && mCurrentNumber.length() <= 5)
                {
                    ((Button) findViewById(R.id.keyMult)).setEnabled(true);
                }
                if (mCurrentNumber.length() > 5)
                {
                    ((Button) findViewById(R.id.keyMult)).setEnabled(false);
                }
                if ((buildInfix().length() - buildInfix().replaceAll("\\*", "").length()) > 2)
                {
                    ((Button) findViewById(R.id.keyMult)).setEnabled(false);
                }
                else
                {
                    ((Button) findViewById(R.id.keyMult)).setEnabled(true);
                }

                updateOutput();
                break;
            default:
                //delete the stlye 0
                boolean canEnter = true;
                if (mCurrentNumber.equals("0")) {mCurrentNumber = "";}
                if (mCurrentNumber.length() < 10) {
                    //String infix = buildInfix();
                    if (mCurrentNumber.length() > 4)
                    {
                        //if there are 3 *'s then done
                        ((Button) findViewById(R.id.keyMult)).setEnabled(false);

                        if (buildInfix().contains("*"))
                        {
                            canEnter = false;
                            Toast.makeText(getBaseContext(), "Maximum number of characters to multiply is 5", Toast.LENGTH_SHORT).show();
                        }
                    }


                    if (canEnter) {
                        mCurrentNumber = mCurrentNumber + buttonText;
                        //mInfixOutput = mInfixOutput + buttonText;
                        updateOutput();
                    }
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Maximum number of characters reached (10)", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private String buildInfix() {
        String builtInfix = "";
        for (String s : mEquStack)
        {
            builtInfix = builtInfix + s;
        }
        builtInfix = builtInfix + mCurrentNumber;
        return builtInfix;
    }

    private void updateOutput(){
        mOutputText.setText(buildInfix());
    }

    private void updateOutput(String s){
        mOutputText.setText(s);
    }

    private String evaluatePostfix(ArrayList<String> output){
        String example = "";
        Stack<Long> evaluteStack = new Stack<Long>();
        //analyse the ArrayList's values
        for (String s: output)
        {
            Long operand1 = new Long(0);
            Long operand2 = new Long(0);
            switch (s)
            {
                case "+":
                    operand2 = evaluteStack.pop();
                    operand1 = evaluteStack.pop();
                    evaluteStack.push((operand1 + operand2));
                    break;
                case "-":
                    operand2 = evaluteStack.pop();
                    operand1 = evaluteStack.pop();
                    evaluteStack.push((operand1 - operand2));
                    break;
                case "*":
                    operand2 = evaluteStack.pop();
                    operand1 = evaluteStack.pop();
                    try {
                        evaluteStack.push((operand1 * operand2));
                    } catch (ArithmeticException e){
                        Toast.makeText(getBaseContext(), "Exception", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    break;
                case "/":
                    operand2 = evaluteStack.pop();
                    operand1 = evaluteStack.pop();
                    if (operand2 == 0) {operand2 = new Long(1);}
                    evaluteStack.push((operand1 / operand2));
                    break;
                default:
                    evaluteStack.push(Long.parseLong(s));
                    break;
            }

        }
        example = "" + evaluteStack.pop();
        return example;
    }

    private ArrayList<String> makePostfix(ArrayList<String> inputList){

        Stack<String> operators = new Stack<String>();
        ArrayList<String> output = new ArrayList<String>();
        //int whileCounter = 0;
        int outputCounter = 0;
        for (String value: inputList)
        {
            //String value = mEquStack.get(whileCounter);
            switch (value)
            {
                case "+":
                case "-":
                case "*":
                case "/":
                    //check if stack empty
                    if (operators.empty()){operators.push(value);}
                    else {
                        //check precedence
                        int oldPrec = mPrecedence.indexOf(operators.peek());
                        int newPrec = mPrecedence.indexOf(value);

                        while (newPrec >= oldPrec)//the new one is of lower precedence
                        {
                            //pop off the oldPrec from the stack and add it to the output
                            output.add(operators.pop());
                            outputCounter++;

                            //get new precedences if not empty
                            if (!(operators.empty())) {
                                oldPrec = mPrecedence.indexOf(operators.peek());
                            }
                            else
                            {
                                oldPrec = 1000;//if empty make the while exit
                            }
                        }
                        //put new operator on stack
                        operators.push(value);
                    }
                    break;
                default:
                    output.add(value);
                    outputCounter++;
                    break;
            }
        }
        //scan through the operators, make sure we got them all
        while (!operators.empty())
        {
            output.add(operators.pop());
            outputCounter++;
        }

        return output;
    }

}