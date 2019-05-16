#!/usr/bin/python3

# Import modules for CGI handling
import cgi
import cgitb

cgitb.enable()

# Create instance of FieldStorage
form = cgi.FieldStorage()

print ("Content-Type: text/plain\n");
print ("\n");
print ("This displays the result of what was posted.")
print(form);
