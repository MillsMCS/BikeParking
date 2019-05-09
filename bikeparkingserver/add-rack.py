#!/usr/bin/python3

# Import modules for CGI handling
import cgi
import cgitb
# noinspection PyUnresolvedReferences
import os.path
import time
# noinspection PyUnresolvedReferences
from sqlite3 import conn

cgitb.enable()

# Create instance of FieldStorage
form = cgi.FieldStorage()

print ("Content-Type: text/html\n");
print ("\n");


# noinspection PyUnresolvedReferences,SpellCheckingInspection
def save_uploaded_pic(form_value, upload_dir):
    """This saves a file uploaded by an HTML form.
       The form_field is the name of the file input field from the form.
       For example, the following form_field would be "file_1":
           <input name="file_1" type="file">
       The upload_dir is the directory where the file will be written.
       If no file was uploaded or if the field does not exist then
       this does nothing.
       Number: the number of photo currently
    """  # https://stackoverflow.com/questions/12166158/upload-a-file-with-python
    if not form_value: return
    if not form_value.file: return
    proposed_url = os.path.join(upload_dir, form_value.filename)
    # noinspection PyUnresolvedReferences
    fout = open(proposed_url, 'wb')
    while 1:
        chunk = form_value.file.read(100000)
        if not chunk: break
        fout.write(chunk)
    fout.close()
    return proposed_url


name = form.getvalue('name')
latitude = form.getvalue('lat')
longitude = form.getvalue('long')
photo = form.getvalue('photo')  # yes, in byte array/hex
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

# Upload the photo and get the url
photo_url = save_uploaded_pic(photo, "/racks/" + rack_id + "/")

# Insert the bike rack
c.execute("INSERT INTO BIKE_RACK VALUES (null, ?, ?, ?, ?, ?, ?);", \
          (name, latitude, longitude, photo_url, user, notes_status))

# Get the timestamp
curr_time = time.strftime("%a, %b %d, %Y, %I:%M %p", time.gmtime())

# Insert the photo data
c.execute("INSERT INTO PHOTO VALUES (null, ?, ?, ?, ?, ?);", \
          (rack_id, user, curr_time, photo_url, 0))

# Insert the notes, if any
if notes_status:
    c.execute("INSERT INTO NOTES VALUES (null, ?, ?, ?, ?);", \
              (rack_id, user, curr_time, notes_text))

conn.commit()

conn.close()
