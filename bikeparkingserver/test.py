#!/usr/bin/python3

# Import modules for CGI handling
import cgi
import cgitb

cgitb.enable()

# Create instance of FieldStorage
form = cgi.FieldStorage()

print ("Content-Type: text/html\n");
print ("\n");
print ("<h1>This displays the result of what was posted.</h1>")
print(form);
