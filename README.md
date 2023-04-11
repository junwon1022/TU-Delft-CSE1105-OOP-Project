## Description of project
This is the description of all the functionalities of our "TimeWise" application. 
The description closely follows both the logic of our application and the official backlog.

CONNECT TO SERVER

When first running the application, you are prompted with the
'Connect to  a TimeWise Server' screen. 
You can use the 'Connect' button to connect to another server other than the default, or 'Connect to default' to implicitly connect to your own server.

ADMIN PAGE

To connect as an admin, you need to put in the 'http://localhost:8080' in the server address, you put the password 'admin' in the password field,
and then connect to the application as an admin.
As an admin, you have access to all aspects of our application.
You have access to the server and can 'Create', 'Delete' and 'Rename'
boards on it and those changes will be seen everywhere else in the app.
As an admin, you can 'Add', 'Remove' and 'Edit' protected board passwords. 
Unlike a user, as an admin you can see a list of all boards on a certain server.
Admins can also change or remove board passwords from the main admin screen by clicking on the lock icon. 
When a board has no password the lock icon won't be visible. 
Admins can also delete a locked board in the normal way 
by simply clicking the delete button.

MULTIBOARD OVERVIEW

In your recent boards, for each board you can rename, join, leave it or copy its key.
When creating a new board.Clicking the "Join" button joins
a specific board, giving it a write access,
Clicking the "Delete" board deletes the board from the server,
clicking the Pen button renames the board in all areas and the 
'Copy' button lets you copy the key in order to access the board more easily. 
Under the list, there are 
2 buttons to create a new Board and disconnect from the server.
When adding a board, you can add a title and optionally a password and colors.
Once joined a board, you can see "My boards" and switch between them.
When you exit the current board, you will be returned to the main screen
In the board overview, you could see all the lists and cards at one sight.

BOARD OVERVIEW

When you click the button named 'Add List' on the bottom of the page, a pop up will show up, asking you to give the title of the list.
If you try to give it an empty title, it will show an error message that says 'Lists require a title'.
When you enter a valid title which is not null and click the 'Ok' button, it will add a list to the board overview with the given title.

When you click the 'pencil' symbol on the top left side of the list, a pop up will show up that requires a new title.
It also won't work when given a null title.
When we press 'Ok', the new title will be shown on the list.

Clicking the 'X' button on the top right side of the list, it will delete the list and all the cards within the list.

Clicking the 'Add Card' button on the bottom of the list, it will show a text field where you could enter the name of the card.
If you try to give it an empty title, it will show an error message that says 'Cards require a title'.
When you enter a valid title which is not null and click the 'Ok' button, it will add a card to the list with the given title.

When you click the 'pencil' symbol on the top left side of the card, a pop-up will show up that requires a new title.
When you press the 'Ok' button, the new title will be shown on the card.

Clicking the 'X' button on the top right side of the card, it will delete the card from the corresponding list.

Our application is synchronized using both websockets and long polling. 
We use websockets for synchronization of all the functionality inside the board overview
and long polling for synchronization of changes made in the multi-board overview. 
As a user, you can move cards within the same list and their order will be changed accordingly. Also, you can drag and drop them between different lists.
All these functionality is done by the user using the mouse to move cards in order to rearrange them on the board.


To create a tag, you need to expand the accordion in the board overview and then click on the plus button.
From there a pop-up will appear that prompts the user to enter a title and optionally choose colors.
To add a tag to a card, the user needs to open card details, again expand the corresponding accordion 
and double-click on a tag to apply it. To remove it, the user needs to click on the x (close) button.

CARD DETAILS

If you hover over a card and double-click it,
it will show the details overview screen, 
with the title of the card you opened it for in the top left.
Inside the details window, there are various functionalities you can choose from.

By clicking on the box right below where the card title shows,
you can enter text to add a description 
and save it by either clicking on the save description icon 
or using Ctrl+S or Cmdn+S depending on if you are a Mac user or a Windows user.
After this, a description icon will appear in the board overview indicating 
the card has a description.
If the description were to be deleted, the icon would disappear.

You can also add checklists to a card, 
on the box below the description box, 
there is a plus icon that, when clicked, 
shows a small box where you can enter the text 
and add or cancel the operation of adding the checklist. 
If a null title is added, it won’t let you proceed with the addition. 
If the checklist is added correctly; 
the checklist appears in the box above where it can be removed by clicking on the “x”
button on the right portion of the checklist,
and it can be renamed by clicking on the pencil icon, 
which will prompt you with a rename window. 
Checklists also have the option to be checked/unchecked
by clicking on the box to the left of the checklist name.
When a card has checklists, a progress text of how many are done compared
to the total will be shown on the card and updated instantly if there are changes made.
For example, if there are 2 checked checklists and a total of 4,
the text will show “2/4”. 
Whenever a checklist is removed/added or checked/unchecked, 
the text will show these changes.

Inside the card details,
there is also an option for adding tags to a card by clicking 
in the accordion to the accordion to the right of the description box. 
It will show you a list of all the available tags inside the board (if there are any). 
You then have the option to double-click on one of them to add it. 
This will do two things: It will show the added tag in the box right above 
the accordion, and it will also make an indicator appear 
in the card overview with the color of the added tag. 
Tags can be removed by clicking on the “x” button when they are added to the tag box 
(this will let you add them again if you desire).

Finally, a user can exit the card details by clicking on the x icon in the window itself. 
The card details will also close automatically if the card is removed by yourself,
or one of the other users.
Additionally, all the above functionalities are synchronized with web-sockets 
and reply to any changes automatically without the need of refreshing.

CUSTOMIZATION

If you want to customize your board, you press the customization button. A customization window
is opened. You get the option to change the board background and font color, 
the lists' background and font color by pressing the coloured box and selecting a color.
For both the board and list colors you have two 'reset' buttons,
that reset the colours to the default ones. To highlight your tasks,
you can create different presets. At the creation of each preset, 
you need to provide a title, choose 2 colours and optionally set the preset as default.
If a card is set as default, either at creation or from the preset list, all cards which will be added to
the board will have that preset, current ones preserving their preset.
You can also remove a preset, by that any cards currently using that preset will be assigned the current default preset.

Within the card details, you can choose another preset, from all the board's presets, for the specific card, to highlight it.

KEYBOARD SHORTCUTS

In our application, there are various keyboard shortcuts that can be used
so some actions can be performed easier. Anywhere inside a board, 
if you enter a question mark on the keyboard (Shift + ? key)
A help screen with all the shortcuts will show.

By hovering over a card, it is highlighted
(shown by some lines that appear bordering the card) 
so you can activate the shortcuts. 
You can move the highlight with the arrow keys and also move 
the highlighted card by pressing Shift + any of the arrow keys to move 
it in the desired direction. The highlighted card can also be deleted by pressing 
Del key and renaming it by pressing E. By pressing Enter the card details window
will open and it can be closed by pressing Esc. In the board overview itself
if C is pressed the customization window will be prompted and if you press T 
the window to add a tag will show.

PASSWORD PROTECTION

To add a password, you need to click on the unlock icon in the top of the board overview.
Passwords can also be added when a board is being created in the main screen. 
After that, clicking the unlock button will open a password management
view that allows the user to change or remove the password.

When the user enters a locked board, 
they need to click the lock icon and enter a password to gain write access.



## Group members

| Profile Picture                                                                                         | Name        | Email                           |
|---------------------------------------------------------------------------------------------------------|-------------|---------------------------------|
| ![](https://s.gravatar.com/avatar/5f0677b48e487028ad4cee6eaae56bb1?s=50)                 | Luis Cabo Villagomez | L.CaboVillagomez@student.tudelft.nl |
| ![](https://secure.gravatar.com/avatar/72174c80be62867d6d1d53f862395668?s=50&d=identicon)               | Aleksandra Savova | A.Savova@student.tudelft.nl     |
| ![](https://secure.gravatar.com/avatar/30a36653a184a68ea685e2b38add7270?s=50&d=identicon)               | Liviu Moanta | I.L.Moanta-1@student.tudelft.nl |
| ![](https://en.gravatar.com/userimage/232097172/77a0298a72fbd15858329a2cb48763c9.png)              | Marina Mădăraș | m.madara@student.tudelft.nl     |
| ![](https://secure.gravatar.com/avatar/9c9c804ce7bad17cfebc82b726a11558?s=50&d=identicon)               | Junwon Yoon | junwonyoon@student.tudelft.nl   |
| ![](https://secure.gravatar.com/avatar/9c9c804ce7bad17cfebc82b726a11558?s=50&d=identicon)               | David Dobin | ddobin@student.tudelft.nl   |
## How to run it
To run our application, you can run the 'main' in Client. 

To connect to a server, you can run the 'main' in Server.
## How to contribute to it
As our project is not open source, to contribute to it,
you have to be a team member and join a closed GitLab Repository. There, you can create 
issues which will guide further code development. If any code is written, you create a merge
request, if it passes the pipeline, at least 2 reviews need to be done for your
code to be accepted and merged into the main branch.
## Copyright / License (opt.)
©2023, Team 41, OOPP, TU Delft