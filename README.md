
# CS5303GroupProject
## Project Description
Hotel reservation management + billing software. Capabilities include keeping track of user accounts (guests, hotel clerks, and admins), managing reservations, and issuing billing.

All code is contained in the GitawayHotel folder, while the rest of the home directory stores project files.

## Installation Guide
First download the source code from GitHub:
```bash
git clone https://github.com/woonex/CS5303GroupProject
```
If using default *users.json* and *rooms.json*:
```bash
mkdir ./CS5303GroupProject/GitawayHotel/target/
cp  ./CS5303GroupProject/GitawayHotel/defaultUsers.json ./CS5303GroupProject/GitawayHotel/target/users.json
cp  ./CS5303GroupProject/GitawayHotel/defaultRooms.json ./CS5303GroupProject/GitawayHotel/target/rooms.json
```
Otherwise users and rooms may be entered in a json format in the target folder, or they may be added after an administrator user is created. The *defaultUsers.json* and *defaultRooms.json* may be used for formatting.
For *users.json*:

```json
[
  {
    "username" : "Guest"
    "password" : "password"
    "userType" : "guest" (or "clerk", "admin")
  },
]
```
For *rooms.json*:
```json
[
  {
    "room": 101,
    "bedQty": 2,
    "bedType": "king" (or "queen", "full", "twin"},
    "noSmoking": true
  },
]
```
Maven is used to manage dependencies and build the application. The following commands may be used to build and test the application.
```bash
cd ~/CS5303GroupProject/GitawayHotel/
mvn verify
```


