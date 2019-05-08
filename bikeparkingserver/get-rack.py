#!/usr/bin/python3

# Import modules for CGI handling 
import cgi, cgitb 
from sqlite3 import connect
import json

# Create instance of FieldStorage 
form = cgi.FieldStorage()

print ("Content-Type: text/html\n");
print ("\n");

latitude = form.getvalue('lat')
longitude  = form.getvalue('long')

conn = connect('bikeparking.sqlite')
c = conn.cursor()
c.execute("SELECT NAME, latitude, longitude, photo, added_by, notes FROM BIKE_RACK")

for name, lati, longi, photo, added_by, notes in c.fetchall():
    data = {"name": name, 
                "latitude": lati,
                "longitude": longi,
                "photo_url": photo,
                "added_by": added_by,
                "notes": notes}
    if latitude == None or longitude == None:
        print (json.dumps(data, sort_keys=True, indent=4))
    else:
        print (json.dumps(data, sort_keys=True, indent=4))
        break

conn.close()