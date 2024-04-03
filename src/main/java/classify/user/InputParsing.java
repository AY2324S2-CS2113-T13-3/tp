package classify.user;

import classify.commands.AddStudent;
import classify.commands.ArchiveCommands;
import classify.commands.DeleteCommands;
import classify.commands.EditStudent;
import classify.commands.ListStudentsCommand;
import classify.commands.StudentSorter;
import classify.commands.ViewStudent;
import classify.data.DataHandler;
import classify.student.Student;
import classify.ui.UI;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputParsing {
    public static final Logger LOGGER = Logger.getLogger(InputParsing.class.getName());
    private static final String EARLIER_POSSIBLE_DATE = "1970-01-01";
    private static final String DEFAULT_STRING_VALUE = "Unknown";
    private static final String BYE = "bye";
    private static final String LIST = "list";
    private static final String ADD = "add";
    private static final String VIEW = "view";
    private static final String DELETE = "delete";
    private static final String UNDO = "undo";
    private static final String RESTORE = "restore";
    private static final String EDIT = "edit";
    private static final String HELP = "help";
    private static final String SORT = "sort";
    private static final String ARCHIVE = "archive";
    private static final String UNARCHIVE = "unarchive";
    private static final String VIEW_SUBJECT = "view_subject";
    private static final String ENTER_THE_SUBJECT_NAME_TYPE_EXIT_TO_GO_BACK = 
            "Enter the subject name (type 'exit' to go back):";
    private static final String EXIT = "exit";
    private static final String EXITED_THE_COMMAND = "Exited the command.";
    private static final String LIST_SORTED = "Sort complete!";
    private static final String SORT_BY_CHOOSE_INDEX = "Sort by: (Choose index)";
    private static final String NAME_A_TO_Z = "1. Name (A to Z)";
    private static final String TOTAL_NUMBER_OF_CLASSES_ATTENDED = "2. Total number of classes attended:";
    private static final String LAST_PAID_DATE = "3. Date of last fee payment: ";
    private static final int NUMBER_TOO_SMALL = 80000000;
    private static final int NUMBER_TOO_BIG = 1000_000_00;
    private static final String CLASSES_ATTENDED_MUST_BE_MORE = "Classes attended must be 0 or more.";

    public static void parseUserCommand(String[] userCommand, ArrayList<Student> masterStudentList,
            ArrayList<Student> recentlyDeletedList,
            ArrayList<Student> archiveList, Scanner in) {
        // @@author blackmirag3
        if (masterStudentList == null) {
            System.out.println("Student list is null.");
        }
        // @@author tayponghee
        switch (userCommand[0]) {
        case ADD:
            AddStudent.addStudent(masterStudentList, in, userCommand[1]);
            // @@author ParthGandhiNUS
            assert masterStudentList != null;
            break;

        case VIEW:
            ViewStudent.viewStudent(masterStudentList, in, userCommand[1]);
            UI.printDivider();
            break;

        // @@author alalal47
        case DELETE:
            DeleteCommands.deleteStudent(masterStudentList, recentlyDeletedList, in, userCommand[1]);
            break;

        case RESTORE:
            DeleteCommands.restoreStudent(masterStudentList, recentlyDeletedList, in, userCommand[1]);
            break;

        case UNDO:
            DeleteCommands.undoDelete(masterStudentList, recentlyDeletedList);
            break;

        case HELP:
            UI.printHelp();
            UI.printDivider();
            break;

        // @@author ParthGandhiNUS
        case BYE:
            UI.printEndConversation();
            DataHandler.writeStudentInfo(masterStudentList);
            break;

        case LIST:
            if (userCommand[1] != null) {
                UI.printInvalidListCommand();
            } else {
                parseListCommand(masterStudentList, in);
            }
            break;

        // @@ author tayponghee
        case SORT:
            sortStudents(masterStudentList, in, userCommand[1]);
            break;

        case VIEW_SUBJECT:
            handleViewSubjectCommand(masterStudentList, in, userCommand[1]);
            break;

        // @@author blackmirag3
        case EDIT:
            EditStudent.editStudent(masterStudentList, in, userCommand[1]);
            break;

        case ARCHIVE:
            ArchiveCommands.archiveStudent(masterStudentList, archiveList, userCommand[1], in);
            break;

        case UNARCHIVE:
            ArchiveCommands.unarchiveStudent(masterStudentList, archiveList, userCommand[1], in);
            break;

        default:
            UI.printWrongInput();
            break;
        }
    }


    /**
     * 
     * Function which leads to the chooseListType Function
     * 
     * @param masterStudentList List of the students used in the chooseListType function
     * @param scanner Scanner used for the chooseListType function
     */
    //@@author tayponghee
    public static void parseListCommand(ArrayList<Student> masterStudentList, Scanner scanner) {
        UI.printListCommandStart();
        String subject = scanner.nextLine().trim();
        ListStudentsCommand.chooseListType(masterStudentList, scanner, subject);
    }

    /**
     * Sorts the list of students based on user input.
     *
     * @param masterStudentList The list of students to be sorted.
     * @param in                The Scanner object to read user input.
     */
    private static void sortStudents(ArrayList<Student> masterStudentList, Scanner in, String sortType) {
        String input;
        while (true) {
            //@@author alalal47
            if (sortType == null) {
                UI.println(SORT_BY_CHOOSE_INDEX);
                UI.println(NAME_A_TO_Z);
                UI.println(TOTAL_NUMBER_OF_CLASSES_ATTENDED);
                UI.println(LAST_PAID_DATE);
                input = in.nextLine().trim();
            } else {
                input = sortType.trim().toLowerCase();
            }
            //@@author tayponghee
            // input = in.nextLine().trim();

            if (input.equalsIgnoreCase(EXIT)) {
                System.out.println(EXITED_THE_COMMAND);
                UI.printDivider();
                return;
            }

            if (StudentSorter.isValidChoice(input)) {
                StudentSorter.sortByChoice(masterStudentList, input, in);
                UI.println(LIST_SORTED);
                break;
            } else {
                sortType = null;
                UI.println(StudentSorter.INVALID_CHOICE);
            }
        }
    }

    /**
     * Lets the user check view a list of students with that corresponding subject.
     * If the user types view_subject [subject], it will only generate the list of
     * students with that subject,
     * then exit.
     * If the user types view_subject, the user can continuously view all students
     * that
     * have that subject, until they exit the command.
     *
     * @param masterStudentList The list of all students.
     * @param in                The user's input.
     */
    private static void handleViewSubjectCommand(ArrayList<Student> masterStudentList, Scanner in, String subject) {
        if (subject != null && !subject.isEmpty()) {
            ListStudentsCommand.listStudentsBySubject(masterStudentList, subject);
        } else {
            findStudentsWithSubject(masterStudentList, in);
        }
    }

    /**
     * Finds students with the specified subject and displays them to the user.
     * Continuously prompts the user to enter a subject name until they choose to
     * exit.
     *
     * @param masterStudentList The list of all students.
     * @param in                The scanner object to read user input.
     */
    private static void findStudentsWithSubject(ArrayList<Student> masterStudentList, Scanner in) {
        while (true) {
            System.out.println(ENTER_THE_SUBJECT_NAME_TYPE_EXIT_TO_GO_BACK);
            String input = in.nextLine().trim();

            if (input.equalsIgnoreCase(EXIT)) {
                System.out.println(EXITED_THE_COMMAND);
                UI.printDivider();
                return;
            }

            if (!input.isEmpty()) {
                ListStudentsCommand.listStudentsBySubject(masterStudentList, input);
                return;
            } else {
                System.out.println("Please enter a valid subject name.");
            }
        }
    }


    //@@author blackmirag3
    /**
     * Prompts for grade from user input and checks format
     * Prompts the user to enter a valid double within the range [0, 100] until one
     * is provided.
     *
     * @param in The scanner object to read user input.
     * @return The valid grade.
     */
    public static double promptForGrade(Scanner in) {
        while (true) {
            UI.printStudentGradesPrompt();
            String gradeInput = in.nextLine();

            if (gradeInput.isBlank()) {
                return -1;
            }

            //@@author ParthGandhiNUS
            double grade;
            try {
                grade = Double.parseDouble(gradeInput);
            } catch (NumberFormatException e) {
                System.out.println("Wrong number format! Please try again! e.g. 75 ");
                UI.printDivider();
                grade = promptForGrade(in);
            }

            if (isValidGrade(grade)) {
                return grade;
            }
        }
    }

    /**
     * Finds a student in the list by their name.
     *
     * @param masterStudentList The list of all students.
     * @param name              The name of the student to search for.
     * @return A list of students with names matching the input.
     */
    public static Student findStudentByName(ArrayList<Student> masterStudentList, String name, Scanner in) {

        //@@author Cryolian
        ArrayList<Student> studentsWithMatchingNames = new ArrayList<Student>();
        for (Student student : masterStudentList) {
            if (student.getName().equalsIgnoreCase(name)) {
                studentsWithMatchingNames.add(student);
            }
        }

        if (studentsWithMatchingNames.size() == 0) {
            return null;
        }

        if (studentsWithMatchingNames.size() == 1) {
            return studentsWithMatchingNames.get(0);
        }

        UI.printSameNameError();
        return findStudentByNumber(studentsWithMatchingNames, in);

    }

    /**
     * Function looks through a list of students with the same name,
     * prints them out, and expects an Integer input to specify a student.
     * 
     * @param studentList List of students to look for.
     * @param in          Scanner to parse in the user input.
     * @return            Student with rigth name and number pair.
     */
    private static Student findStudentByNumber(ArrayList<Student> studentList, Scanner in) {
        assert studentList.size() > 1 : "List should contain more than one Student";

        for (Student student : studentList) {
            student.printStringNumber();
        }
        int number;

        try {

            number = promptForPhoneNumber(in);

            for (Student student : studentList) {

                if (student.getPhoneNumber() == number) {
                    return student;
                }
            }
            UI.println("No student found with a matching number. Please try again.");

        } catch (NumberFormatException e) {
            UI.printValidNumberError();
            return null;
        }

        return null;
    }

    //@@author ParthGandhiNUS
    /**
     * 
     * Returns a true or false depending on user input for number of lines
     * 
     * @param classesAttended   Takes in the integer input by the user
     * @return True if number of classes attended is greater than 0 and false otherwise.
     * 
     */
    //@@author blackmirag3
    public static boolean isValidClassesAttended(int classesAttended) {

        if (classesAttended < 0) {
            //@@author ParthGandhiNUS
            UI.println(CLASSES_ATTENDED_MUST_BE_MORE);
            //@@author blackmirag3
            UI.printDivider();
            return false;
        }
        return true;
    }
    //@@author ParthGandhiNUS
    /**
     * 
     * Returns a true or false depending on user input for grade input
     * 
     * @param grade   Takes in the double input by the user for the subject grade
     * @return False if number of subject grade is less than 0 or more than 100
     *         Returns true otherwise
     */
    //@@author blackmirag3
    private static boolean isValidGrade(double grade) {

        if (grade < 0 || grade > 100) {
            System.out.println("Grade must be between 0 and 100. Please enter a valid number.");
            UI.printDivider();
            return false;
        }

        return true;
    }

    // @@author Cryolian
    /**
     * Creates a looping prompt asking for a phone number.
     * Only exits the loop when either an exception is thrown,
     * or when the number is either 8 or 10 digits to account for
     * the length of a Singaporean number, with or without
     * the country code.
     * 
     * @param in The scanner class to read inputs from.
     * @return -1 if an exception was thrown. An 8
     *         or 10-digit number if not.
     * @throws NumberFormatException thrown if invalid number is give
     */
    public static int promptForPhoneNumber(Scanner in) throws NumberFormatException {

        int number = 0;

        do {
            UI.printPhoneNumberPrompt();
            number = readInPhoneNumber(in);
        } while (!checkNumberValidity(number));

        LOGGER.log(Level.INFO, "Storing number: " + number);
        return number;
    }

    //@@author ParthGandhiNUS
    /**
     * 
     * Uses the scanner to read the all the characters input by the user
     * 
     * @param in Scanner takes in the next line input by user
     * @return  integer value of the characters input by the user
     * @throws NumberFormatException in the event that the characters input by the user are not numbers
     */
    //@@author Cryolian
    private static int readInPhoneNumber(Scanner in) throws NumberFormatException {

        String input = in.nextLine().trim();

        return Integer.parseInt(input);
    }

    //@@author ParthGandhiNUS
    /**
     * 
     * Returns true or false depending on whether the number is a valid Singapore phone number
     * 
     * @param number Integer number input by user
     * @return True if the number is a valid Singapore Number, false otherwise
     */
    //@@author Cryolian
    private static boolean checkNumberValidity(int number) {
        return number > NUMBER_TOO_SMALL && number < NUMBER_TOO_BIG;
    }

    //@@author ParthGandhiNUS
    /**
     * A prompting input to scan in a string from the user input.
     * 
     * @param in The scanner class to scan inputs from.
     * @return "Unknown" if blank was inputted, or the
     *         trimmed string inputted by the user.
     */
    //@@author Cryolian
    public static String readInString(Scanner in) {

        String string = in.nextLine();
        if (string.isBlank()) {
            return DEFAULT_STRING_VALUE;
        }
        return string.trim();

    }

    
    //@@author Cryolian
    public static LocalDate readInDate(Scanner in) {

        String userInput;
        LocalDate paymentDate;
        UI.promptForLastPaymentDate();
        do {

            userInput = in.nextLine();
            paymentDate = parseDateFromString(userInput);

        } while (!isDateValid(paymentDate));

        return paymentDate;
    }

    
    //@@author Cryolian
    protected static LocalDate parseDateFromString(String string) {

        if (string.isBlank()) {
            LOGGER.log(Level.INFO, "Storing today as the last payment date." + '\n');
            return LocalDate.now();
        }

        LocalDate paymentDate;

        try {

            paymentDate = LocalDate.parse(string);

        } catch (DateTimeParseException e) {
            return printInvalidDateMessage();
        }

        return paymentDate;
    }
    //@@author Cryolian
    private static LocalDate printInvalidDateMessage() {
        LOGGER.log(Level.WARNING, "Invalid date format entered." + '\n');
        UI.printInvalidDateFormatError();
        return LocalDate.now().plusDays(2);
    }
    //@@author Cryolian
    private static boolean isDateValid(LocalDate paymentDate) {

        if (paymentDate.isAfter(LocalDate.now().plusDays(1)) ||
                paymentDate.isBefore(LocalDate.parse(EARLIER_POSSIBLE_DATE))) {

            UI.printInvalidDateRangeError();
            return false;
        }

        return true;

    }

}
