# Tagged Revision

This Android app shows revision questions associated with a location when an according NFC tag is detected.
For example, say an appropriately formatted (see below) NFC tag is attached to the coffee machine. When a device running the app registers this NFC tag, it retrieves all questions labeled "coffee machine" from its database and shows them one after the other.

## How to set up


### Writing NFC Tags
The project contains a second Activity called `TagWriterActivity` which can be used for preparing the NFC tags with the appropriate NDEF data. To run this activity, change the Android manifest by uncommenting the commented code and by moving the Launcher intent inside this activity instead of MainActivity. Then, in the activity code, define your location in `writeMessage`. Start the app on a device and touch a new NFC tag. It should vibrate when it detects the tag and then write the information.

### Populating the database
Sample data can be found in the method `populateDatabaseWithSampleData` in `MainActivity`. Adjust the data such that the `location` parameter matches the locations defined for your NFC tags. This has to be an exact match, otherwise the questions will not be found later on. Uncomment the call of this method in `onCreate` before you first run the app (and remove it again for subsequent launches)