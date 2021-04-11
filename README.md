# ***Stalker notes***
>*this app is a must have for every survivor in the near future (both human and zombie)*
## **Description**
When starting the application, the main menu is displayed, which consists of the choice:
1. **DANGER**
2. **DATABASE**
3. **OBSERVE**

The application allows:
1. **DANGER**. View the characteristics of the hazards that you will have to face, namely:
   * Radioactive radiation: https://en.wikipedia.org/wiki/Radiation
   * Biodefense: https://en.wikipedia.org/wiki/Biodefense
   * Chemical hazard: https://en.wikipedia.org/wiki/Chemical_hazard
   * Laser safety: https://en.wikipedia.org/wiki/Laser_safety
   * Electromagnetic radiation: https://en.wikipedia.org/wiki/Electromagnetic_radiationn
   * Radio wave: https://en.wikipedia.org/wiki/Radio_wave
     
2. **DATABASE**. Create your own base of dangers in the surrounding world.
At the danger point, the user writes the hazard type and description into the database. Latitude and longitude are automatically recorded. 
   + The *BroadcastReceiver* + *LocationManager* is used to determine the current location
   + *ContentProvider* is used to store danger points in the database
3. **OBSERVE**. In observation mode, the distance from the current position to all danger points is calculated.
   + this mode uses *BoundService*
   
## **Activity**
The application has 6 activities

   *MainActivity*
   + *Danger*
   + *DataBase*
       - *Position*
           - *GoogleMap*
   +  *Observe*
         - *GoogleMap*


The scheme of transition between activities is shown in the UML diagram.

For the transition from MainActivity to Activity Danger, DataBAse, Observe, used an explicit intent passed to startActivity().

For the transition from Activity DataBAse to Activity Position used an explicit intent passed to startActivityForResult(intent, REQUEST), with different Requests:
  - REQUEST_INSERT_POSITION - to insert a new position
  - REQUEST_UPDATE_POSITION - to update the current position

To launch google maps used implicit `intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));`

## **Broadcast Receiver**
The application uses a dynamically registered Broadcast Receiver with the ACTION_POSITION_RECEIVER filter, which determines the current position and returns it to Position Activity.

## **Content Provider**
To save the danger points that are defined in Position Activity, use Content Provider. 

To ensure the work of the Content Provider, the following classes were used:
  + *DBContract* - Contract containing labels that are used to access the table and its entries of the location storage database
  + *DataBaseHelper extends SQLiteOpenHelper* - The database helper used by the DangerProvider to create and manage its underlying SQLite database.
  +  *DangerProvider* extends ContentProvider - provides the necessary operations with the database:
     - insert();
     - query();
     - delete();
     - update().
 
 ## **Reciever**
In Activity Observe there is real-time observation from the current location to the points of danger recorded in the database. Information is updated when you click on the button. 

For these purposes, a Bound Service is used and additional classes are used:
  + inner class *ReplyHandler extends Handler* that is built in class *Observe*
  + class *PositionBindService extends Service*
  + class *PositionRequestHandler extends Handler*

The interactions between classes are specified in the UML diagram.

## **TESTS**
  + Unit test:
    - UtilsTest.class
  + Espresso tests:
    - EspressoMainActivityTest
    - EspressoDangerTest
    - EspressoDataBaseActivityTest


## **Specification**

After installing the application, you need to enable the **permission:** *location* for this application.
For Emulator Android Studio: *setting* -> *Apps & notifications* -> *Stalker Notes* -> *Permissions*.

When you start the application, the main_activity appears with three buttons:
1. **DANGER**
2. **DATABASE**
3. **OBSERVE**


**DANGER**

When you click on this button, the Activity_danger appears.
At the top of the window there are six buttons that call up the corresponding resource on Wikipedia.
The main part of the activity is occupied by the widget webView, which displays information about various dangers

**DATABASE**

When you click on this button, the Activity_data_base appears.

The database of danger entered by the user is displayed in the center of the window (*ListView*), namely:
  + danger icon
  + description

There are three buttons at the top of the window:
1. *REFRESH* - Updates the display database (not used now, because after changes the database is updated automatically)
2. *ADD* - go to Activity_position. In this window, write down the current position to the database:
  + Select the danger icon in the spinner, which is displayed at the bottom of the screen
  + We write down a description of the danger; if you do not enter anything in the record field, the latitude and longitude of the current position will be automatically recorded in this field
  + the MAP button will display the current position in the google map in a new window (while the latitude and longitude is not defined, the button is not active)
  + the OK button writes information to the database and returns toActivity_data_base (while the latitude and longitude is not defined, the button is not active)
  
  As a result, it is written to the database using the content provider:
   + _ID
   + danger icon
   + description
   + latitude
   + longitude
  
  Latitude and longitude are recorded automatically (not manually by the user), therefore, to get latitude and longitude, you must visit the danger point yourself (necessarily in a respirator).
  
4. *DELETE ALL* - delete all information from the database. When you click on the button, a dialog is displayed so as not to accidentally delete all entries.

Long press on a database item displays a context menu:
  + change - go to Activity_position and displays all data about the selected item. You can view the position by clicking on the MAP button. You can change the icon and description of danger. By clicking on the OK button, go to Activity_data_base and the record is updated.
  + delete - deletes item by ID



**OBSERVE**

This window is used to monitor the danger points that are entered in the database. In the middle of the window, a database is displayed, where the danger icon, description and distance in meters are indicated. To start surveillance, you must press the button at the top of the screen (terrible eye)
Information is updated only by clicking on the button (terrible eye). When you click on an item in the *ListView*, a window with a google map is launched, where the location of the danger is shown.




https://github.com/AlexYach1973/Capstone
