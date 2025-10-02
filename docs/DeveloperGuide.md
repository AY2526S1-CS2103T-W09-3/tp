---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* {list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Tuition centre administrators and managers
* has a need to manage a significant number of contacts (students, tutors, parents)
* needs to track relationships between different contact types
* manages class schedules and student-tutor assignments
* handles administrative tasks like attendance tracking and fee management
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps
* values efficiency and comprehensive information access

**Value proposition**: Streamline tuition centre management by providing a centralized system to manage contacts, track relationships between students, tutors and parents, and access all critical information at a glance - all through an efficient CLI interface that's faster than traditional GUI applications


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                     | So that I can…​                                                        |
| -------- | ------------------------------------------ | ------------------------------ | ---------------------------------------------------------------------- |
| `* * *`  | first-time user                            | have clear list of commands   | learn the features quickly                                            |
| `* * *`  | first-time user                            | have demo/sample data preloaded | explore features without starting from scratch                       |
| `* * *`  | admin                                      | add and delete contacts (students, tutors, parents) | maintain an up-to-date contact database              |
| `* * *`  | admin                                      | prevent duplicate student records when adding | keep the data accurate                               |
| `* * *`  | admin                                      | update teachers' contact information | always have their latest details                                    |
| `* * *`  | admin                                      | view all contacts              | have easy access to all information                                   |
| `* * *`  | admin                                      | search for parents by their child's name | quickly locate linked contacts                         |
| `* * *`  | admin                                      | filter tutors and students by subjects | plan and allocate classes                                    |
| `* * *`  | admin                                      | see contacts of students and tutors for each class | track the status of each class                    |
| `* * *`  | admin                                      | see all assigned students under teachers' profiles | manage their classes easily                       |
| `* *`    | admin                                      | link parents to multiple children | only need one contact profile to manage all tuition-related information |
| `* *`    | admin                                      | assign tutors to multiple subjects | reflect real-world teaching responsibilities                      |
| `* *`    | admin                                      | record attendance for each student | don't need a separate sheet                                       |
| `* *`    | admin                                      | add performance remarks to students' profiles | monitor academic progress over time                     |
| `* *`    | admin                                      | tag contacts with labels      | inform tutors to plan follow-ups effectively                         |
| `* *`    | admin                                      | generate lists of students by academic year | plan exam preparation sessions                          |
| `* *`    | admin                                      | view tuition schedule          | easily keep track of lesson dates                                     |
| `*`      | admin                                      | import existing contact lists from Excel/CSV | quickly set up the system without retyping data         |
| `*`      | admin                                      | export the entire address book in JSON format | back up the data safely                              |
| `*`      | admin                                      | merge duplicate contacts       | maintain a clean and accurate address book                           |
| `*`      | admin                                      | track outstanding fees by parents | follow up with reminders                                          |
| `*`      | admin                                      | view monthly payout summaries  | have transparency on how payments are calculated                      |
| `*`      | admin                                      | be warned if any classes clash | schedule classes without discrepancies                               |

### Use cases

(For all use cases below, the **System** is the `TutBook` and the **Actor** is the `admin`, unless specified otherwise)

**Use case: UC01 - Add a new student**

**MSS**

1.  Admin enters command to add a new student with name, role, phone, and email
2.  TutBook validates the input data
3.  TutBook checks for duplicate contacts
4.  TutBook adds the student to the database
5.  TutBook displays success message with student details

    Use case ends.

**Extensions**

* 2a. Invalid input format (e.g., invalid phone number or email).
    * 2a1. TutBook shows an error message with the specific validation failure.

      Use case ends.

* 3a. Duplicate contact detected (same name and phone number).
    * 3a1. TutBook shows error message "Contact already exists. Cannot add duplicate."

      Use case ends.

**Use case: UC02 - Link parent to student**

**MSS**

1.  Admin searches for the student by name
2.  TutBook displays the student's profile
3.  Admin searches for the parent by name
4.  TutBook displays the parent's profile
5.  Admin enters command to link parent to student
6.  TutBook creates the relationship link
7.  TutBook displays success message

    Use case ends.

**Extensions**

* 2a. Student not found.
    * 2a1. TutBook shows error message "Student not found."

      Use case ends.

* 4a. Parent not found.
    * 4a1. TutBook shows error message "Parent not found."

      Use case ends.

* 6a. Link already exists.
    * 6a1. TutBook shows message "This relationship already exists."

      Use case ends.

**Use case: UC03 - Search for tutor by subject**

**MSS**

1.  Admin enters filter command with subject parameter
2.  TutBook searches for all tutors teaching the specified subject
3.  TutBook displays a list of matching tutors with their details
4.  Admin views the filtered results

    Use case ends.

**Extensions**

* 2a. No tutors found for the specified subject.
    * 2a1. TutBook shows message "No tutors found teaching [subject]."

      Use case ends.

* 1a. Invalid subject format.
    * 1a1. TutBook shows error message with valid subject format.

      Use case ends.

**Use case: UC04 - Record student attendance**

**MSS**

1.  Admin searches for the student by name
2.  TutBook displays the student's profile
3.  Admin enters attendance command with date and status (present/absent)
4.  TutBook validates the date format
5.  TutBook records the attendance
6.  TutBook displays confirmation message

    Use case ends.

**Extensions**

* 2a. Student not found.
    * 2a1. TutBook shows error message "Student not found."

      Use case ends.

* 4a. Invalid date format.
    * 4a1. TutBook shows error message "Date must be in YYYY-MM-DD format."

      Use case resumes at step 3.

* 5a. Attendance already recorded for this date.
    * 5a1. TutBook asks for confirmation to overwrite.
    * 5a2. Admin confirms or cancels.

      Use case ends.

**Use case: UC05 - Delete a contact**

**MSS**

1.  Admin requests to list all contacts
2.  TutBook shows a list of contacts
3.  Admin requests to delete a specific contact by index or by name and phone
4.  TutBook requests confirmation
5.  Admin confirms deletion
6.  TutBook deletes the contact

    Use case ends.

**Extensions**

* 2a. The list is empty.

      Use case ends.

* 3a. The given index is invalid.
    * 3a1. TutBook shows error message "Invalid index. Please use a visible contact index."

      Use case resumes at step 2.

* 3b. Multiple contacts match the given name.
    * 3b1. TutBook shows error message "Multiple contacts match this name. Please specify phone number or use index."

      Use case resumes at step 2.

**Use case: UC06 - View all students assigned to a tutor**

**MSS**

1.  Admin enters command to view tutor with tutor's name
2.  TutBook searches for the tutor
3.  TutBook retrieves all students assigned to this tutor
4.  TutBook displays the tutor's profile with list of assigned students
5.  Admin views the student list with their subjects and schedules

    Use case ends.

**Extensions**

* 2a. Tutor not found.
    * 2a1. TutBook shows error message "Tutor not found."

      Use case ends.

* 3a. No students assigned to this tutor.
    * 3a1. TutBook displays tutor profile with message "No students currently assigned."

      Use case ends.

### Non-Functional Requirements

#### Performance Requirements
1. The system should respond to any user command within 2 seconds under normal load conditions
2. Should be able to hold up to 1000 contacts (combination of students, tutors, and parents) without noticeable sluggishness in performance for typical usage
3. Search operations should return results within 1 second for databases with up to 1000 contacts
4. The application should start up within 5 seconds on standard hardware

#### Usability Requirements
5. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse
6. New users should be able to learn basic operations (add, delete, search, view) within 15 minutes with the help documentation
7. Error messages should clearly indicate what went wrong and how to fix it
8. Command syntax should be consistent across all features

#### Compatibility Requirements
9. Should work on any _mainstream OS_ (Windows 10/11, macOS 10.14+, Ubuntu 20.04+) as long as it has Java `17` or above installed
10. Data files should be portable across different operating systems
11. The application should work on systems with minimum 4GB RAM and 100MB free disk space

#### Reliability Requirements
12. The system should have 99.5% uptime during operational hours
13. Data should be automatically saved after each successful command execution
14. The system should be able to recover from crashes without data loss (persistent storage)
15. Backup functionality should be available to prevent complete data loss

#### Security Requirements
16. Contact information should be stored locally only (no cloud sync for privacy)
17. The application should not require administrative privileges to run
18. Data files should be stored in a user-accessible format (JSON) but with data validation on load

#### Scalability Requirements
19. The architecture should support future addition of new contact types without major restructuring
20. The system should support batch operations for importing/exporting contacts (future enhancement)
21. Database design should allow for easy addition of new fields to existing contact types

#### Maintainability Requirements
22. Code should follow standard Java coding conventions
23. All public methods should have comprehensive JavaDoc documentation
24. Test coverage should be at least 70% for critical components
25. The application should use standard design patterns for extensibility

#### Portability Requirements
26. The application should be distributed as a single JAR file
27. No installation process should be required beyond having Java installed
28. User data should be stored in a platform-independent format (JSON)

#### Accessibility Requirements
29. The application should support keyboard-only navigation
30. Font size in the UI should be readable (minimum 11pt)
31. Color schemes should have sufficient contrast for readability

### Glossary

* **Admin**: A tuition centre administrator or manager who uses TutBook to manage contacts and operations
* **Academic Year**: The year level of a student (e.g., Primary 1, Secondary 3, JC 1)
* **Class**: A scheduled tuition session with assigned tutor(s) and student(s) for a specific subject
* **Contact**: A person entry in TutBook, which can be a Student, Tutor, or Parent
* **Demo Data**: Pre-populated sample data provided for new users to explore TutBook features
* **Duplicate Contact**: A contact entry that has the same name AND phone number as an existing contact
* **Enrolled Subjects**: The subjects that a student is taking tuition for
* **Filter**: A search operation that shows only contacts matching specific criteria (e.g., role, subject)
* **Link**: A relationship connection between different contact types (e.g., parent-student, tutor-student)
* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Parent**: A contact type representing the parent or guardian of a student
* **Performance Remarks**: Notes about a student's academic progress and areas needing improvement
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Role**: The type of contact - Student, Tutor, or Parent
* **Student**: A contact type representing someone receiving tuition
* **Subject**: An academic subject taught at the tuition centre (e.g., Mathematics, English, Physics)
* **Tag**: A label attached to contacts for categorization (e.g., "Exam Prep", "Needs Support")
* **Tuition Centre**: An educational institution providing supplementary academic instruction
* **Tutor**: A contact type representing a teacher at the tuition centre
* **TutBook**: The address book application specifically designed for tuition centre management
* **Validation**: The process of checking if user input meets the required format and constraints

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
