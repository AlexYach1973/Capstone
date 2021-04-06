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

## **Specification**
I will fill in the next milestones...


https://github.com/AlexYach1973/Capstone
