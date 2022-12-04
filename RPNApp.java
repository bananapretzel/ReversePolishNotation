import java.util.Scanner;
import java.util.ArrayList;
import java.util.Stack;

/**
 * COSC241 Assignment
 * A calculator app which uses the reverse polish notation method and utilizes
 * a stack to calculate the result.
 * Apart from standard operators, this calculator has extra functionality where
 * using an exclamation mark after a basic operator will repeat the operator
 * until there is one item left in the stack.
 * Typing in "d" duplicates the top item of the current stack.
 * "o" will output the top item of the stack (without removing it).
 * "c" will take the top element of the stack (x) and push the second element
 * from the top (y), x times.
 * "r" will take the top element of the stack (x) and shifts the second element
 * from the top (y), down the stack x times.
 * May 2021
 *
 * @author Group 23 - Andy, Casey, and Lauren
 */
public class RPNApp {

    /**
     * The stack used for this application.
     */
    private static final Stack<Long> stack = new Stack<>();
    /**
     * Container for non-numeric inputs.
     */
    private static String nonNumeric;
    /**
     * Holds the basic operators used in the application.
     */
    private static final String[] basicOperators = {"+", "-", "*", "/", "%"};
    /**
     * Holds the repeating operators used in the application.
     */
    private static final String[] repeatOperators =
            {"+!", "*!", "-!", "/!", "%!"};
    /**
     * Holds the special function operators used in the application.
     */
    private static final String[] specialOperators = {"d", "o", "c", "r"};
    /**
     * Holds all operators used in the application.
     * Used to check for valid non-numeric inputs.
     */
    private static final String[][] operators =
            {basicOperators, repeatOperators, specialOperators};
    /**
     * Container for operator inputs.
     */
    private static String operator;
    /**
     * Used to categorize whether the current operand is basic,
     * repeat or special.
     */
    private static String operatorType;
    /**
     * Used to contain error codes.
     */
    private static int error;
    /**
     * Stores the first numeric operand.
     */
    private static Long x;
    /**
     * Stores the second numeric operand.
     */
    private static Long y;
    /**
     * Container for the result of a calculation.
     */
    private static Long output;

    /**
     * Error code. No errors detected
     */
    private static final int NO_ERROR = 0;
    /**
     * Error code. Bad token detected.
     */
    private static final int BAD_TOKEN_ERROR = 1;
    /**
     * Error code. Too few operands detected.
     */
    private static final int TOO_FEW_OPERANDS_ERROR = 2;
    /**
     * Error code. Division by zero detected.
     */
    private static final int DIVISION_BY_ZERO_ERROR = 3;
    /**
     * Error code. Remainder by zero detected.
     */
    private static final int REMAINDER_BY_ZERO_ERROR = 4;
    /**
     * Error code. Negative copy number detected.
     */
    private static final int NEGATIVE_COPY_ERROR = 5;
    /**
     * Error code. Negative roll number detected.
     */
    private static final int NEGATIVE_ROLL_ERROR = 6;
    /**
     * Error code. Unmatched parentheses detected.
     */
    private static final int UNMATCHED_PARENTHESES_ERROR = 7;

    /**
     * Main method implementing RPN calculator function.
     * Reads scanner input, calculates according to RPN method.
     * Prints calculated output.
     *
     * @param args command line arguments are not used.
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            /* expand brackets. */
            String inputExpanded = expandInput(input.nextLine());
            Scanner line = new Scanner(inputExpanded);
            if (line.hasNext()) {
                while (line.hasNext()) {
                    if (line.hasNextLong()) { //if next token is long
                        stack.push(Long.parseLong(line.next()));
                    } else { //if next token is non-numeric
                        nonNumeric = line.next();
                        if (isBadToken(nonNumeric)) {
                            break;
                        }
                        setOperator(nonNumeric);
                        setOperands();
                        evaluate();
                        resetOperands();
                    }
                }
                printOutput();
            }
            resetStack();
            resetError();
        }
    }

    /**
     * Takes the scanner input and expands parentheses content appropriately
     * if there is any.
     *
     * @param input String of input
     * @return inputExpanded, a String of the inputs with expanded parentheses
     * as appropriate.
     */
    public static String expandInput(String input) {
        ArrayList<String> tempArray = new ArrayList<>();
        Stack<String> tempStack = new Stack<>();
        Scanner scan = new Scanner(input);
        StringBuilder inputExpanded = new StringBuilder();
        String topElement;
        int countL = 0; //left bracket counter
        int countR = 0; //right bracket counter
        int k; //multiplier
        while (scan.hasNext()) {
            tempStack.push(scan.next()); //push next token onto stack
            if (tempStack.peek().equals("(")) { //if top of stack is (
                countL++; //increment left bracket counter
            } else if (tempStack.peek().equals(")")) { //if top of stack is )
                countR++; //increment right bracket counter
                tempStack.pop(); //pop off )
                while (!tempStack.isEmpty()) { //while stack is not empty
                    /* pop off top of stack and set to topElement */
                    topElement = tempStack.pop();
                    if (topElement.equals("(")) { //if top of stack is (
                        break; //break out of this while-loop
                    } else { //if top of stack is not (
                        /* add topElement to array */
                        tempArray.add(0, topElement);
                    }
                }
                try {
                    topElement = tempStack.pop(); //topElement preceding (
                    k = Integer.parseInt(topElement); //parse topElement as int
                    if (k < 0) { //if k is negative
                        k = 0; //same effect as k = 0
                    }
                } catch (Exception e) { //no valid number proceeds (
                    setError(TOO_FEW_OPERANDS_ERROR); //too few operands error
                    printOutput(); //print error
                    return ""; //return empty string
                }
                for (int i = 0; i < k; i++) {
                    /* repeatedly add tempArray to tempStack k times */
                    tempStack.addAll(tempArray);
                }
            }
            tempArray.clear(); //clear tempArray for next iteration
        }
        /* if left bracket and right bracket counts not equal */
        if (countL != countR) {
            setError(UNMATCHED_PARENTHESES_ERROR); //unmatched parentheses error
            printOutput(); //print output
            return ""; //return empty string
        }
        for (int i = 0; i < tempStack.size(); i++) { //turn stack into string
            inputExpanded.append(tempStack.elementAt(i)).append(" ");
        }
        /* if brackets used */
        if (inputExpanded.toString().equals("") && (countL + countR) > 0) {
            printOutput(); //output empty stack
        }
        /* return bracket expanded string of input */
        return inputExpanded.toString();
    }

    /**
     * Sets the error code.
     *
     * @param err apply one of the error code constants.
     */
    public static void setError(int err) {
        error = err; //set data field
    }

    /**
     * Takes string input token and sets that as operator.
     *
     * @param token String
     */
    public static void setOperator(String token) {
        operator = token;
        setOperatorType(operator);
    }

    /**
     * Takes operator, checks which type it belongs to and sets operatorType
     * data field. Compares operator to those in basic, repeat, and special
     * operator arrays.
     *
     * @param operator a String
     */
    public static void setOperatorType(String operator) {
        for (String op : basicOperators) {
            if (operator.equals(op)) {
                operatorType = "basic";
                return;
            }
        }
        for (String op : repeatOperators) {
            if (operator.equals(op)) {
                operatorType = "repeat";
                return;
            }
        }
        for (String op : specialOperators) {
            if (operator.equals(op)) {
                operatorType = "special";
                return;
            }
        }
    }

    /**
     * Takes top two operands in the stack (if there are two) and sets as y & x
     * operands.
     */
    public static void setOperands() {
        if (!stack.empty()) {
            y = stack.peek();
            stack.pop();
        }
        if (!stack.empty()) {
            x = stack.peek();
            stack.pop();
        }
    }

    /**
     * Evaluates the calculation using the operands and the operator.
     * Less than two operators results in an error.
     * The type of evaluation method performed depends on the operator string.
     */
    public static void evaluate() {
        if (operatorType.equals("basic")) { //error checks for basic operators
            if (tooFewOperands() || isSingular()) {
                return;
            }
        }
        switch (operator) {
            case "+":
                add();
                break;
            case "*":
                mul();
                break;
            case "-":
                sub();
                break;
            case "/":
                div();
                break;
            case "%":
                mod();
                break;
            case "+!":
                addRepeat();
                break;
            case "*!":
                mulRepeat();
                break;
            case "-!":
                subRepeat();
                break;
            case "/!":
                divRepeat();
                break;
            case "%!":
                modRepeat();
                break;
            case "d":
                duplicateTop();
                break;
            case "o":
                outputTop();
                break;
            case "c":
                copy();
                break;
            case "r":
                rotate();
                break;
        }
    }

    /**
     * Prints the stack, or if error, prints text describing error type.
     */
    public static void printOutput() {
        switch (error) {
            case NO_ERROR: //no error
                System.out.println(stack);
                break;
            case BAD_TOKEN_ERROR: //bad token error
                System.out.println("Error: bad token '" + nonNumeric + "'");
                break;
            case TOO_FEW_OPERANDS_ERROR: //too few operands error
                System.out.println("Error: too few operands");
                break;
            case DIVISION_BY_ZERO_ERROR: //division by zero error
                System.out.println("Error: division by 0");
                break;
            case REMAINDER_BY_ZERO_ERROR: //remainder by zero error
                System.out.println("Error: remainder by 0");
                break;
            case NEGATIVE_COPY_ERROR: //negative copy error
                System.out.println("Error: negative copy");
                break;
            case NEGATIVE_ROLL_ERROR: //negative roll error
                System.out.println("Error: negative roll");
                break;
            case UNMATCHED_PARENTHESES_ERROR:
                System.out.println("Error: unmatched parentheses");
        }
    }

    /**
     * Checks that the non-numeric token is known to the calculator.
     * Compares the token to all operators in basic, repeat and special
     * operator arrays. If the token is not found in the arrays, error is set to
     * bad token error and returns true.
     *
     * @param token a String.
     * @return boolean, if token is bad.
     */
    public static boolean isBadToken(String token) {

        for (String[] type : operators) {
            for (String op : type) {

                if (token.equals(op)) {
                    return false;
                }
            }
        }
        setError(BAD_TOKEN_ERROR);
        return true;
    }

    /**
     * Checks that there are at least two operands for a calculation.
     * If "nonNull" is not returned from the checkOperands method this means
     * either x or y is null and results in a too few Operands error.
     *
     * @return boolean, if there are too few operands.
     */
    public static boolean tooFewOperands() {
        if (!checkOperands().equals("nonNULL")) {
            setError(TOO_FEW_OPERANDS_ERROR);
            return true;
        }
        return false;
    }

    /**
     * Checks if calculation will divide by zero or do modulo zero.
     * If true, returns true and sets appropriate error.
     *
     * @return boolean, is singular if divides or modulus zero.
     */
    public static boolean isSingular() {
        if (y == 0 && operator.equals("/")) {
            setError(DIVISION_BY_ZERO_ERROR);
            return true;
        } else if (y == 0 && operator.equals("%")) {
            setError(REMAINDER_BY_ZERO_ERROR);
            return true;
        }
        return false;
    }

    /**
     * Checks that y and x operands are both not null.
     *
     * @return String describing operand null states.
     */
    public static String checkOperands() {
        if (y == null && x == null) {
            return "yxNULL";
        } else if (y == null) {
            return "yNULL";
        } else if (x == null) {
            return "xNULL";
        } else {
            return "nonNULL";
        }
    }


    /**
     * Resets x and y operand data fields to null.
     */
    public static void resetOperands() {
        x = null;
        y = null;
    }

    /**
     * Resets error data field to zero.
     */
    public static void resetError() {
        setError(NO_ERROR);
    }


    /**
     * Resets stack to an empty stack.
     */
    public static void resetStack() {
        stack.clear();
    }

    /**
     * A method which first, checks if the null status of the "x" and "y"
     * operands. If they are both null, the method does nothing. If one or the
     * other (or both) are not null, x and/or y will be pushed to the stack.
     */
    public static void restack() {
        switch (checkOperands()) {
            case "yxNULL":
                break;
            case "yNULL":
                stack.push(x);
                break;
            case "xNULL":
                stack.push(y);
                break;
            case "nonNULL":
                stack.push(x);
                stack.push(y);
        }
    }

    /*
     * ARITHMETIC OPERATIONS
     * Performs calculations with operands and push output onto the stack.
     */

    /**
     * Addition calculation method.
     */
    public static void add() {
        output = x + y;
        stack.push(output);
    }

    /**
     * Subtraction calculation method.
     */
    public static void sub() {
        output = x - y;
        stack.push(output);
    }

    /**
     * Multiplication calculation method.
     */
    public static void mul() {
        output = x * y;
        stack.push(output);
    }

    /**
     * Division calculation method.
     */
    public static void div() {
        output = x / y;
        stack.push(output);
    }

    /**
     * Modulus calculation method.
     */
    public static void mod() {
        output = x % y;
        stack.push(output);
    }

    /*
     * REPEAT OPERATORS
     * Repeats the arithmetic operation on operands until there is 1 item left
     * on the stack.
     */

    /**
     * Repeatedly add operands until there is 1 item left on the stack.
     */
    public static void addRepeat() {
        operator = "+";
        if (checkOperands().equals("yxNULL")) {
            setError(TOO_FEW_OPERANDS_ERROR);
            return;
        }
        if (checkOperands().equals("xNULL")) {
            stack.push(y); //put y back onto the stack
            resetError(); //since error = 2 from main
            return;
        }
        while (x != null) {
            add();
            resetOperands();
            setOperands();
        }
        stack.push(output);
    }

    /**
     * Repeatedly multiplies operands until there is 1 item on the stack.
     */
    public static void mulRepeat() {
        operator = "*";
        if (checkOperands().equals("yxNULL")) {
            setError(TOO_FEW_OPERANDS_ERROR);
            return;
        }
        if (checkOperands().equals("xNULL")) {
            stack.push(y); //put it back onto the stack
            resetError(); //since error = 2
            return;
        }
        while (x != null) {
            mul();
            resetOperands();
            setOperands();
        }
        stack.push(output);
    }

    /**
     * Performs subtraction on operands until there is 1 item left in the stack.
     */
    public static void subRepeat() {
        operator = "-";
        if (checkOperands().equals("yxNULL")) {
            setError(TOO_FEW_OPERANDS_ERROR);
            return;
        }
        if (checkOperands().equals("xNULL")) {
            stack.push(y); //put it back onto the stack
            resetError(); //since error = 2
            return;
        }
        while (x != null) {
            sub();
            resetOperands();
            setOperands();
        }
        stack.push(output);
    }

    /**
     * Performs divisions on the operands until there is 1 item left
     * in the stack.
     */
    public static void divRepeat() {
        operator = "/";
        if (checkOperands().equals("yxNULL")) {
            setError(TOO_FEW_OPERANDS_ERROR);
            return;
        }
        if (checkOperands().equals("xNULL")) {
            stack.push(y); //put it back onto the stack
            resetError(); //since error = 2
            return;
        }
        if (stack.empty()) {
            setError(TOO_FEW_OPERANDS_ERROR);
            return;
        }
        while (x != null) {
            if (isSingular()) {
                break;
            }
            div();
            resetOperands();
            setOperands();
        }
        stack.push(output);
    }

    /**
     * Performs modulo division on the operands until there is 1 item
     * left in the stack.
     */
    public static void modRepeat() {
        operator = "%";
        if (checkOperands().equals("yxNULL")) {
            setError(TOO_FEW_OPERANDS_ERROR);
            return;
        }
        if (checkOperands().equals("xNULL")) {
            stack.push(y); //put it back onto the stack
            resetError(); //since error = 2
            return;
        }
        if (stack.empty()) {
            setError(TOO_FEW_OPERANDS_ERROR);
            return;
        }
        while (x != null) {
            if (isSingular()) {
                break;
            }
            mod();
            resetOperands();
            setOperands();
        }
        stack.push(output);
    }

    // SPECIAL OPERATIONS
    // Perform special operations with operands and push output onto the stack.

    /**
     * Duplicates value on top of stack.
     * Checks stack is not empty,
     * Peeks at top item on stack and pushes on a duplicate copy.
     */
    public static void duplicateTop() {
        restack();
        if (stack.empty()) {
            setError(TOO_FEW_OPERANDS_ERROR);
        } else {
            stack.push(stack.peek());
        }
    }

    /**
     * Prints the item on top of the stack.
     */
    public static void outputTop() {
        restack();
        if (stack.empty()) {
            setError(TOO_FEW_OPERANDS_ERROR);
        } else {
            System.out.print(stack.peek() + " ");
        }
    }

    /**
     * Copies x operand y times and pushes onto the stack.
     * Checks two operands, and that y is zero or greater.
     */
    public static void copy() {
        if (tooFewOperands()) {
            return;
        }
        if (y < 0) {
            setError(NEGATIVE_COPY_ERROR); //negative copy error
            return;
        }
        for (int i = 1; i <= y; i++) {
            stack.push(x);
        }
    }


    /**
     * Rotates item 2nd top of the stack down k steps,
     * where k is the value of the item on top of the stack.
     */
    public static void rotate() {
        if (tooFewOperands()) {
            setError(TOO_FEW_OPERANDS_ERROR);
            return;
        }
        if (y < 0) {
            setError(NEGATIVE_ROLL_ERROR);
            return;
        }
        int k = y.intValue(); //top element
        if (k == 0) { //rolling zero items is allowed, same effect as k = 1
            k = 1;
        }
        int newPosition = stack.size() - (k - 1); //rotate down k-1 positions
        stack.add(newPosition, x); //insert into new position
    }
}
