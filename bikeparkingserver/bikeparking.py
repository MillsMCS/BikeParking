import sqlite3

sqlite_file = 'bikeparking.sqlite'    # name of the sqlite database file

# Connecting to the database file
conn = sqlite3.connect(sqlite_file)
c = conn.cursor()

c.execute('CREATE TABLE USER (\
	_id INTEGER PRIMARY KEY AUTOINCREMENT,\
	NAME TEXT,\
	EMAIL TEXT,\
	AUTH TEXT,\
	STRIKES INTEGER\
);\
')

# Creating a new SQLite table with 1 column
c.execute('CREATE TABLE BIKE_RACK (\
    _id INTEGER PRIMARY KEY AUTOINCREMENT,\
    NAME TEXT,\
    LATITUDE REAL,\
    LONGITUDE REAL,\
	PHOTO INTEGER,\
	ADDED_BY INTEGER,\
	NOTES NUMERIC,\
	FOREIGN KEY (photo) REFERENCES PHOTO(_id),\
	FOREIGN KEY (added_by) REFERENCES USER(_id)\
);\
')

c.execute('CREATE TABLE NOTES (\
	_id INTEGER PRIMARY KEY AUTOINCREMENT,\
	RACK_ID INTEGER,\
	USER_ID INTEGER,\
	TIME NUMERIC,\
	NOTE TEXT,\
	FOREIGN KEY (rack_id) REFERENCES BIKE_RACK(_id),\
	FOREIGN KEY (user_id) REFERENCES USER(_id)\
);\
')

c.execute('CREATE TABLE CHECKIN (\
	_id INTEGER PRIMARY KEY AUTOINCREMENT,\
	RACK_ID INTEGER,\
	USER_ID INTEGER,\
	TIME NUMERIC,\
	SPACE_REMAINING INTEGER,\
	FOREIGN KEY (rack_id) REFERENCES BIKE_RACK(_id),\
	FOREIGN KEY (user_id) REFERENCES USER(_id)\
);\
')

c.execute('CREATE TABLE PHOTO (\
	_id INTEGER PRIMARY KEY AUTOINCREMENT,\
	RACK_ID INTEGER,\
	USER_ID INTEGER,\
	TIME NUMERIC,\
	PHOTO_URL TEXT,\
	RATING INTEGER,\
	FOREIGN KEY (rack_id) REFERENCES BIKE_RACK(_id),\
	FOREIGN KEY (user_id) REFERENCES USER(_id)\
);\
')

c.execute("INSERT INTO bike_rack VALUES(null, 'NSB', \
		37.780305, -122.180977, 'example.jpg', '1', '0' )")
c.execute("INSERT INTO bike_rack VALUES(null, 'Stevenson 1', \
		37.7814257, -122.1863674, 'example.jpg', '1', '0' )")

c.execute("INSERT INTO bike_rack VALUES(null, 'Stevenson 2', \
		37.781292, -122.186266, 'example.jpg', '1', '0' )")

c.execute("INSERT INTO bike_rack VALUES(null, 'Warren Olney', \
		37.782181, -122.182206, 'example.jpg', '1', '0' )")

c.execute("INSERT INTO bike_rack VALUES(null, 'Mills', \
		37.781004, -122.182827, 'example.jpg', '1', '0' )")


# Committing changes and closing the connection to the database file
conn.commit()
conn.close()