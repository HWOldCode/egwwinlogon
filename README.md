# egwwinlogon
PGina EGroupware Plugin

* Project End 2018
* For EGroupware version 16
* Parts:
  * [Plugin for pGina](https://github.com/MutonUfoAI/pgina) 
  * [Module for EGroupware](https://github.com/EGroupware/egroupware)
  
Info: The project was discontinued due to lack of funds.

## Description

With pGina the login can be taken over in Windows. With the plugin for EGroupware, the users from the EGroupware can be used as Windows login.
The user does not have to exist on the system, then it will be created. The user list is kept as offline on the Windows after the successful 
login from EGroupware. So that another user can log in at any time. Commands can be executed before login and after login. After logging in, 
a tray program starts, with which the browser can be started with EGroupware and the user is immediately logged in to EGroupware (session transfer).  

In the EGroupware, the commands can be set according to machine (PC) relationship.

If a user is deactivated in EGroupware, this is also deactivated in Windows.

In addition, Synology NAS can be controlled via API. Users and shares are matched from the EGroupware. 
In Windows, a Windows share can be automatically mounted for the user.

In addition to the EWorkflow EGroupware Module, the EGroupware Module offers control entry blocks for a workflow for automation.

## Installation
[In Germany](docu/egwwinlogon.html) 

## License

I am posting the source code for inspiration to others. I only give consent to the use or further development of the project/code or parts of the code on request.

