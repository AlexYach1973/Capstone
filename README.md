# ***Stalker notes***
>*this app is a must have for every survivor in the near future (both human and zombie)*
## **Description**
The application allows:
1. see the characteristics of the dangers that will be encountered using the world wide web
(in the future it will be called differently) namely:
   * Radioactive radiation: https://en.wikipedia.org/wiki/Radiation
   * Biodefense: https://en.wikipedia.org/wiki/Biodefense
   * Chemical hazard: https://en.wikipedia.org/wiki/Chemical_hazard
   * Laser safety: https://en.wikipedia.org/wiki/Laser_safety
   * Electromagnetic radiation: https://en.wikipedia.org/wiki/Electromagnetic_radiationn
   * Radio wave: https://en.wikipedia.org/wiki/Radio_wave
     
2. create your own base of dangers in the surrounding world.
At the danger point, the user writes the hazard type and description into the database. Latitude and longitude are automatically recorded. 
 + The *BroadcastReceiver* is used to determine the current location
 + *ContentProvider* is used to store danger points in the database
3. In observation mode, the distance from the current position to all danger points is calculated.
+ this mode uses *BoundSevice*
