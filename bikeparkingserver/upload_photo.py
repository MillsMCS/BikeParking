#!/usr/bin/python3

# Import modules for CGI handling
import cgi
import cgitb
import time

cgitb.enable();

# noinspection PyUnresolvedReferences
from sqlite3 import conn

# Create instance of FieldStorage
form = cgi.FieldStorage()

print ("Content-Type: text/plain\n");
print ("\n");

data = form.getvalue('data');
user_id = form.getvalue("userid")
rack_id = form.getvalue('rackid')

conn = conn('bikeparking.sqlite')
c = conn.cursor()

# Get the timestamp
curr_time = time.strftime("%a, %b %d, %Y, %I:%M %p", time.gmtime())

# Insert into the photo table
c.execute("INSERT INTO PHOTO VALUES (null, ?, ?, ?, ?, 0);", \
          (rack_id, user_id, curr_time, data))

conn.commit()

conn.close()
