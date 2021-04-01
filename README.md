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
   + *MainActivity*
   + *Danger*
   + *DataBase*
       - *Position*
           - *GoogleMap*
   +  *Observe*
