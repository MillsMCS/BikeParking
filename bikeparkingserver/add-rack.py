#!/usr/bin/python3

# Import modules for CGI handling
import cgi
# noinspection PyUnresolvedReferences
import os.path
import time
# noinspection PyUnresolvedReferences
from sqlite3 import conn

# Create instance of FieldStorage
form = cgi.FieldStorage()

print ("Content-Type: text/plain\n");
print ("\n");

name = form.getvalue('name')
latitude = form.getvalue('lat')
longitude = form.getvalue('long')
user = form.getvalue('added-by')
notes_text = form.getvalue('notes')  # The actual text of the note, not just true/false
# this makes querying simpler

if notes_text == None:
    notes_status = 0
if notes_text != None:
    notes_status = 1

conn = conn('bikeparking.sqlite')
c = conn.cursor()

# find the rack ID out of the existing ones

c.execute("SELECT _id FROM bike_rack ORDER BY _id DESC LIMIT 1;")
results = c.fetchall()
# noinspection PyUnresolvedReferences,PyUnresolvedReferences
rack_id = str(1 + int(results[0][0]))

# find the photo ID (a separate script handles uploading the photo data)
# CALL THIS SCRIPT BEFORE UPLOADING THE PHOTO.
c.execute("SELECT _id FROM photo ORDER BY _id DESC LIMIT 1;")
results = c.fetchall()
# noinspection PyUnresolvedReferences,PyUnresolvedReferences
photo_id = str(1 + int(results[0][0]))

# Insert the bike rack
c.execute("INSERT INTO BIKE_RACK VALUES (null, ?, ?, ?, ?, ?, ?);", \
          (name, latitude, longitude, photo_id, user, notes_status))

# Get the timestamp
curr_time = time.strftime("%a, %b %d, %Y, %I:%M %p", time.gmtime())

# Insert the notes, if any
if notes_status:
    c.execute("INSERT INTO NOTES VALUES (null, ?, ?, ?, ?);", \
              (rack_id, user, curr_time, notes_text))

conn.commit()

conn.close()
