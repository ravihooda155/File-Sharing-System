# File-Sharing-System


## Code Files
```
- Server.java
- Client.java
```

## Execution
```
=> Change server ip,port on corresponding machine.
=> Compile both server and client code by running commands.
=> javac Client.java
=> javac Server.java
=> Run Server code on the server machine.
=> Run client code on the client machine.
=> Command for running
=> java Client
=> java Server
=> Commands will be run as per the assignment document.
=> when client want to exit then command to be run is "exit".
```

## Implementation Details
```
=> After running both server and client,server will handle the commands that were fired by client.
=> Server will fired new thread for habdling the command activities of the specific client.
=> Multiple thread at server end will map to the corresponding client activities.
=> Message will be displayed at both server and client end after the execution of command.
=> Server will trace the client activities by storing the metadata in hashmap which can be extended
to dumping metadata and can be read.
=> Mapping is done between client to a group.
=> Active clients details were stored.
=> Current directory specific to user is stored at server end.
```
## Implementation specific to commands is as follows:

## Account Creation 

### cmd: create_user `username`
On execution of this command,folder structure is created at server end for that group.=> cmd: upload `filename`
On execution of command,file is uploaded to corresponding client folder at server.File is
send in chunks.

## File Management

### cmd : upload_udp `filename`
On execution of command,file is uploaded to corresponding client folder at server.File is
send in chunks.Udp port is created at client
for execution.
### cmd: create_folder `foldername`
On execution folder is created at server at corresponding client directory.
### cmd: move_file `source_path` `dest_path`
On excecution file is transfered at client folder at server by the mentioed path.

## Group Management 

###  cmd: create_group `groupname`
On excution a group is created,folder structure is created at server for handling group
specific activity.
### cmd: list_groups
On execution list of groups were listed that were created.
### cmd: join_group `groupname`
On execution user is joined to specific group ,also a folder is created at server in that group
folder for handling group specific
activity.
###  cmd: leave_group `groupname`
On execution user is removed from specific group ,also a folder is emoved at server in that
group folder .
### cmd: list_detail `groupname`
On execution details were displayed by scanning directory structure of corresponding
groups.
### cmd: share_msg `message text`
On execution message were shared in a group.As we have stored connection details,server
will transfer message to specific user in a group.
### cmd: get_file `groupname/username/file_path`
On execution files from specific group can be downloaded.File transfer is in form of chunks.
