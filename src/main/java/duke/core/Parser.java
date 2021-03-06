package duke.core;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import duke.command.AddCommand;
import duke.command.Command;
import duke.command.DeleteCommand;
import duke.command.DoneCommand;
import duke.command.EmptyCommand;
import duke.command.EndCommand;
import duke.command.FindCommand;
import duke.command.ListCommand;
import duke.command.TagCommand;
import duke.exception.DukeException;
import duke.task.Tag;

/**
 * An object used to parse user input given during the usage of the Duke programme.
 */
public class Parser {

    /**
     * Returns an appropriate Command object that corresponds to the textToParse.
     * If the format of the textToParse is invalid, an appropriate error message will be printed and the user can
     * try again.
     * @param textToParse a String that represents the user input that is given by the Ui object
     * @return an appropriate command based on the first word of the textToParse
     * @see Ui
     * @see Command
     */
    public Command parse(String textToParse) throws DukeException {

        String[] inputArray = textToParse.split(" ");
        String firstWord = inputArray[0];
        Command command = new EmptyCommand();

        if (firstWord.toLowerCase().equals("bye")) { // for termination
            command = new EndCommand();
        } else if (firstWord.toLowerCase().equals("list")) { // to display list of tasks
            command = new ListCommand();
        } else if (firstWord.toLowerCase().equals("done")) {
            command = parseDone(inputArray);
        } else if (firstWord.toLowerCase().equals("delete")) {
            command = parseDelete(inputArray);
        } else if (firstWord.toLowerCase().equals("find")) {
            command = parseFind(inputArray);
        } else if (firstWord.toLowerCase().equals("tag")) {
            command = parseAddTag(inputArray);
        } else {
            switch (firstWord.toLowerCase()) {
            case "todo":
                command = parseAddTodo(inputArray);
                break;
            case "event":
                String[] eventSplit = textToParse.split("/at");
                command = parseAddEvent(inputArray, eventSplit);
                break;
            case "deadline":
                String[] deadlineSplit = textToParse.split("/by");
                command = parseAddDeadline(inputArray, deadlineSplit);
                break;
            default:
                throw new DukeException("Sorry, I didn't quite catch that!");
            }
        }
        assert command != null;
        return command;
    }

    private Command parseAddTag(String[] inputArray) throws DukeException {
        if (inputArray.length <= 1) {
            throw new DukeException("Which task do you want tagged?");
        }
        if (inputArray.length == 2) {
            throw new DukeException("You're missing either the description for your tag or the task number!");
        }
        try {
            int index = Integer.parseInt(inputArray[1]);
            Tag tag = new Tag(inputArray[2]);
            return new TagCommand(index - 1, tag);
        } catch (NumberFormatException e) {
            throw new DukeException("You gotta give me the task index before the tag description!");
        }
    }

    private Command parseDelete(String[] inputArray) throws DukeException {
        if (inputArray.length <= 1) {
            throw new DukeException("Sorry, which item did you want me to strike off again?");
        }
        try {
            int index = Integer.parseInt(inputArray[1]);
            return new DeleteCommand(index - 1);
        } catch (NumberFormatException e) {
            throw new DukeException("I need a number not a word in this case. "
                                            + "Could ya pass that by me one more time?");
        }
    }

    private Command parseFind(String[] inputArray) throws DukeException {
        if (inputArray.length <= 1) {
            throw new DukeException("Sorry, " + "didn't quite catch what you wanted to find!");
        }
        String searchTerm = stringCombiner(inputArray, 1, inputArray.length - 1).trim();
        return new FindCommand(searchTerm);
    }

    private Command parseDone(String[] inputArray) throws DukeException {
        if (inputArray.length <= 1) {
            throw new DukeException("Sorry, which item did you want me to mark as done again?");
        }
        int index = Integer.parseInt(inputArray[1]);
        return new DoneCommand(index - 1);
    }

    private Command parseAddTodo(String[] inputArray) throws DukeException {
        if (inputArray.length <= 1) {
            throw new DukeException("Sorry, but I can't do anything "
                                            + "if you don't give me the description of your todo!");
        }
        String desc = stringCombiner(inputArray, 1, inputArray.length - 1);
        return AddCommand.addTodo(desc.trim());
    }

    private Command parseAddEvent(String[] inputArray, String[] eventSplit) throws DukeException {
        int dateKeywordIndex = indexFinder(inputArray, "/at");
        if (dateKeywordIndex == 0) {
            throw new DukeException("Think you forgot the /at keyword, pardner!");
        }
        if (dateKeywordIndex == 1) {
            throw new DukeException("I'm gonna need a description for this here event!");
        }
        if (eventSplit.length == 1) {
            throw new DukeException("I'm gonna need a date or time for this!");
        }
        try {
            LocalDate date = LocalDate.parse(eventSplit[1].trim());
            return AddCommand.addEvent(stringCombiner(inputArray, 1, dateKeywordIndex - 1).trim(), date);
        } catch (DateTimeParseException e) {
            throw new DukeException("Can't seem to make out this date over here");
        }
    }

    private Command parseAddDeadline(String[] inputArray, String[] deadlineSplit) throws DukeException {
        int dateKeywordIndex = indexFinder(inputArray, "/by");
        if (dateKeywordIndex == 0) {
            throw new DukeException("Think you forgot the /by keyword, pardner!");
        }
        if (dateKeywordIndex == 1) {
            throw new DukeException("I'm gonna need a description for this here deadline!");
        }
        if (deadlineSplit.length == 1) {
            throw new DukeException("I'm gonna need a date or time for this!");
        }
        try {
            LocalDate date = LocalDate.parse(deadlineSplit[1].trim());
            return AddCommand.addDeadline(stringCombiner(inputArray, 1, dateKeywordIndex - 1).trim(), date);
        } catch (DateTimeParseException e) {
            throw new DukeException("Can't seem to make out this date over here");
        }
    }

    private static String stringCombiner(String[] arr, int start, int end) {
        StringBuffer str = new StringBuffer();
        for (int i = start; i <= end; i++) {
            str.append(arr[i] + " ");
        }
        return str.toString();
    }

    private static int indexFinder(String[] arr, String exp) {
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(exp)) {
                assert i >= index;
                index = i;
            }
        }
        return index;
    }

}
