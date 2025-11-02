package application;

/**
 * <p> Title: FSM-translated PasswordRecognizer. </p>
 * 
 * <p> Description: A demonstration of the mechanical translation of Finite State Machine 
 * diagram into an executable Java program using the Password Recognizer. The code 
 * detailed design is based on a while loop with a select list, following the same
 * style as UserNameRecognizer</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2022 </p>
 * 
 * @author Lynn Robert Carter
 * @version 0.00		2018-02-22	Initial baseline
 *
 * 
 */
public class PasswordRecognizer {

	/**********************************************************************************************
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	public static String passwordRecognizerErrorMessage = "";	// The error message text
	public static String passwordRecognizerInput = "";			// The input being processed
	public static int passwordRecognizerIndexofError = -1;		// The index of error location
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static boolean finalState = false;			// Is this state a final state?
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;					// The flag that specifies if the FSM is running
	private static int passwordSize = 0;				// The password character count
	
	// Boolean flags for password requirements (from the FSM diagram)
	public static boolean foundUpperCase = false;		// Flag for uppercase letter
	public static boolean foundLowerCase = false;		// Flag for lowercase letter
	public static boolean foundNumericDigit = false;	// Flag for numeric digit
	public static boolean foundSpecialChar = false;	// Flag for special character
	public static boolean foundLongEnough = false;		// Flag for minimum length

	// Private method to display debugging data
	private static void displayDebuggingInfo() {
		// Display the current state of the FSM as part of an execution trace
		if (currentCharNdx >= inputLine.length())
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
					((finalState) ? "       F   " : "           ") + "None" + 
					"     Size: " + passwordSize);
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state + 
				((finalState) ? "       F   " : "           ") + "  " + currentChar + " " + 
				((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + 
				nextState + "     Size: " + passwordSize + 
				" U:" + foundUpperCase + " L:" + foundLowerCase + 
				" N:" + foundNumericDigit + " S:" + foundSpecialChar);
	}
	
	// Private method to move to the next character within the limits of the input line
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method. The Password FSM has a single state (State 0) with self-loops for different
	 * character types, implementing the diagram's semantic actions.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if everything is okay or it is a String
	 * 						with a helpful description of the error
	 */
	public static String checkForValidPassword(String input) {
		// Check to ensure that there is input to process
		if(input.length() <= 0) {
			passwordRecognizerIndexofError = 0;	// Error at first character
			return "\n*** ERROR *** The password is empty";
		}
		
		// The local variables used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from above indexed position

		passwordRecognizerInput = input;	// Save a copy of the input
		running = true;						// Start the loop
		nextState = 0;						// Stay in state 0 (self-loops)
		
		System.out.println("\nCurrent Final Input  Next  Password\nState   State Char  State  Info");
		
		// Semantic Action [0]: Initialize all flags and counter
		passwordSize = 0;					// Initialize the password size
		foundUpperCase = false;				// Reset all Boolean flags
		foundLowerCase = false;
		foundNumericDigit = false;
		foundSpecialChar = false;
		foundLongEnough = false;

		// The Finite State Machine continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition
		while (running) {
			// The FSM has only State 0 with self-loops for each character type
			// This implements transitions A, B, C, D, and E from my diagram
			switch (state) {
			case 0: 
				// State 0 processes all character types with self-loops
				// Each character type sets its corresponding flag
				
				// Transition A: A-Z (Uppercase) -> State 0 [Semantic Action 1]
				if (currentChar >= 'A' && currentChar <= 'Z') {
					nextState = 0;
					foundUpperCase = true;		// Set uppercase flag
					passwordSize++;				// Increment counter
				}
				// Transition B: a-z (Lowercase) -> State 0 [Semantic Action 2]
				else if (currentChar >= 'a' && currentChar <= 'z') {
					nextState = 0;
					foundLowerCase = true;		// Set lowercase flag
					passwordSize++;				// Increment counter
				}
				// Transition C: 0-9 (Numeric) -> State 0 [Semantic Action 3]
				else if (currentChar >= '0' && currentChar <= '9') {
					nextState = 0;
					foundNumericDigit = true;	// Set numeric flag
					passwordSize++;				// Increment counter
				}
				// Transition D: Special characters -> State 0 [Semantic Action 4]
				else if ("~`!@#$%^&*()_-+{}[]|:,.?/".indexOf(currentChar) >= 0) {
					nextState = 0;
					foundSpecialChar = true;	// Set special character flag
					passwordSize++;				// Increment counter
				}
				// Transition E: Other/Invalid character [Semantic Action 5]
				else {
					// Invalid character found - FSM halts
					running = false;
				}
				
				// Semantic Action [6]: Check if length >= 8
				if (passwordSize >= 8) {
					foundLongEnough = true;		// Set length flag
				}
				
				// The execution of this state is finished
				break;
			
			default:
				// This should not happen as we only have State 0
				running = false;
				break;
			}
			
			if (running) {
				displayDebuggingInfo();
				// Move to the next character
				moveToNextCharacter();

				// Move to the next state (always stays at 0 for this FSM)
				state = nextState;
				
				// State 0 is a final state if all requirements are met
				finalState = (foundUpperCase && foundLowerCase && 
							 foundNumericDigit && foundSpecialChar && 
							 foundLongEnough);

				// Ensure that one of the cases sets this to a valid value
				nextState = 0;
			}
		}
		
		displayDebuggingInfo();
		System.out.println("The loop has ended.");
		
		// When the FSM halts, determine if the situation is an error or not
		passwordRecognizerIndexofError = currentCharNdx;	// Set index of possible error
		passwordRecognizerErrorMessage = "";
		
		// Check why the FSM stopped
		if (currentCharNdx < inputLine.length() && 
			"~`!@#$%^&*()_-+{}[]|:,.?/".indexOf(currentChar) < 0 &&
			!(currentChar >= 'A' && currentChar <= 'Z') &&
			!(currentChar >= 'a' && currentChar <= 'z') &&
			!(currentChar >= '0' && currentChar <= '9')) {
			// Invalid character found
			passwordRecognizerErrorMessage += "Invalid character '" + currentChar + 
				"' found at position " + (currentCharNdx + 1) + ".\n";
			return passwordRecognizerErrorMessage;
		}
		
		// Transition F: Input fully consumed - check if all requirements are met
		String missingRequirements = "";
		
		if (!foundUpperCase)
			missingRequirements += "uppercase letter, ";
		
		if (!foundLowerCase)
			missingRequirements += "lowercase letter, ";
		
		if (!foundNumericDigit)
			missingRequirements += "numeric digit, ";
			
		if (!foundSpecialChar)
			missingRequirements += "special character, ";
			
		if (!foundLongEnough)
			missingRequirements += "minimum 8 characters (only " + passwordSize + " found) ";
		
		if (missingRequirements.isEmpty()) {
			// Password is valid - all requirements met
			passwordRecognizerIndexofError = -1;
			passwordRecognizerErrorMessage = "";
			return "";	// Empty string indicates success
		}
		else {
			// Password is missing requirements
			passwordRecognizerErrorMessage += "Password missing: " + missingRequirements;
			return passwordRecognizerErrorMessage;
		}
	}
}