JFileUploader
=============

#### Brief:####
This project provides a client app to upload any folder or file to another local folder of the same PC or to another lan-PC wherever the respective server is running.

#### Project Purpose: ####
This project is done as an assignment for the "Networking sessional" (course CSE-322) of the CSE department of BUET.  <br>Whatever, I'll also use this project to transfer file between my laptop & PC lanned together via a wifi-router :D .
  
#### App Logic ####

- Each of the server & client GUIs are in separate classes, whose instants are used inside the `Server.java` & `Client.java`.
- The server continues to listen for an incoming request to the selected port inside an infinite loop running inside a thread.
- If a single file is selected to upload, then:
    - open a socket to the specified server address & port number;
    - get `ObjectOutputStream` from the opened socket;
    - send the file parent, file name & file size;
    - finally send the file by 8192 bytes each time;
    - receive the requested objects sequentially at the server-side.
- If a directory is selected to upload, then:
    - open a socket to the specified server address & port number;
    - get `ObjectOutputStream` from the opened socket;
    - send a `KEY_DIRECTORY` to tell the server-side that a directory is to be downloaded;
    - send the parent of the directory as the "client head", which will help to identify the sub-directoried save-location of the nested-folders & files at the server-side;
    - list all files inside the directory as well as inside its sub-directories iteratively;
    - send each file as the single-file-upload-process described above.
    - send `KEY_DIR_STOPPER` to stop accepting files at the server-end when all files have been transferred.

#### Development Environment: ####
The project is developed in Eclipse IDE.

#### How to RUN? ####
**Eclipse:**

1. Import the project in eclipse (`File>Import..`). If the client & server PCs are different, then import the project in both PCs' Eclipse IDE.
2. In the server(target) PC, Run the `Server.java` class (inside `com.touhiDroid.JFileUploader.Server` package) by `right click > "Run As" > "Java Application"`, or selecting it & then pressing `Ctrl+F11`
3. Provide the target save-location & click "Set Path". Similarly, set a port (in between 1025 & 65535).
4. In a similar fashion, run the `Client.java` class (inside `com.touhiDroid.JFileUploader.Client` package) in the client PC.
5. If the client & server PC is the same, then write `localhost` in the "Server URL" field, otherwise write the IP address of the server PC (as `192.168.0.100`)
6. Specify the `Server Port` as the same as specified in the server app.
7. Select a file/folder in the file chooser & click "Upload" button.

**Command Prompt:**

1. Go to the `JFileUploader>src>com>touhiDroid>JFileUploader>Server` in the server PC & run `Server.java` class.
2. Specify target save-location & server-port clicking on the set buttons.
3. Go to the `JFileUploader>src>com>touhiDroid>JFileUploader>Client` in the client PC & run `Client.java` class.
4. Specify server IP / destination folder & port no. same as the server-port.
5. Choose the file to upload & click the "Upload" button in the client app.

<br><br><br>
#### Bugs to Fix: ####

1. Progress bars doesn't show proper progresses, though the console helps to understand the current progress.
2. Sometimes the uploaded files uploads successfully but it shows size 0KB, though it gets fixed when it is opened by any text-editor or some app. like this.
3. While uploading a big-size folder (esp. containing nested folders) , some files gets corrupted (though transferred with full size) & can't be opened.
4. Files bigger than 240MB causes Java heap-size overflow exception.
