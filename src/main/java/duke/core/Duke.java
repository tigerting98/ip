package duke.core;

import duke.command.Command;
import java.io.File;

/**
 * The Duke programme implements an application that works as task tracker productivity tool.
 * It can track things to do, deadlines and events as well as their status - done or not yet done.
 * Users input text commands, and the programme will parse it and generate an appropriate response.
 * The programme terminates after the Ui object within is deactivated
 * @see Ui
 */
public class Duke {

    private Ui ui;
    private Storage storage;
    private TaskList taskList;
    private Parser parser;

    static final String PROJECT_ROOT = System.getProperty("user.dir");
    static final String DEFAULT_FILE_PATH = PROJECT_ROOT + File.separator + "data" + File.separator + "saveData.txt";
    static final String DEFAULT_DIRECTORY = PROJECT_ROOT + File.separator + "data";

    public static void main(String[] args) {
        new Duke(DEFAULT_FILE_PATH, DEFAULT_DIRECTORY).run();
    }

    private void run() {

        while (ui.isActive()) {
            String input = ui.receiveInput();
            Command command = parser.parse(input);
            command.execute(taskList,storage,ui);
        }

        storage.saveData(taskList.exportTaskList());
    }

    private Duke(String filePath, String dirPath) {
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Howdy pardner!! I'm\n" + logo);

        System.out.println("What can I get yer for?");

        ui = new Ui();
        storage = new Storage(filePath, dirPath);
        taskList = new TaskList();
        taskList.loadTasks(storage.loadData());
        parser = new Parser();
    }

}
