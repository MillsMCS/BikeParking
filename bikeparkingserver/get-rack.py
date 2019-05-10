#!/usr/bin/python3

# Import modules for CGI handling
import cgi
import json
from sqlite3 import connect

# Create instance of FieldStorage 
form = cgi.FieldStorage()

print ("Content-Type: text/plain\n");

latitude = form.getvalue('lat')
longitude  = form.getvalue('long')

conn = connect('bikeparking.sqlite')
c = conn.cursor()
c.execute("SELECT NAME, latitude, longitude, photo, added_by, notes FROM BIKE_RACK")

bike_racks = {}

for name, lati, longi, photo, added_by, notes in c.fetchall():
    if latitude == None or longitude == None:
        bike_racks[str(lati) + ", " + str(longi)] = {"name": name, 
                "latitude": lati,
                "longitude": longi,
                "photo_url": photo,
                "added_by": added_by,
                "notes": notes}
    elif str(lati) == str(latitude) and str(longi) == str(longitude):
<<<<<<< HEAD
        bike_racks[str(lati) + ", " + str(longi)] = {"name": name,
=======
        bike_racks[name] = {"name": name,
>>>>>>> Implement upload photo button with image buttons
                                    "latitude": lati,
                                    "longitude": longi,
                                    "photo_url": photo,
                                    "added_by": added_by,
                                                     "notes": notes}  #
        

print (json.dumps(bike_racks, sort_keys=True, indent=4))
conn.close()