# Tagged Revision

This Android app shows revision questions associated with a location when an according NFC tag is detected.
For example, say an appropriately formatted (see below) NFC tag is attached to the coffee machine. When a device running the app registers this NFC tag, it retrieves all questions labeled "coffee machine" from its database and shows them one after the other.

## How to set up

### Writing NFC Tags
The `TagWriterActivity` can be used for preparing the NFC tags with the appropriate NDEF data. To run this activity, tap the "add location" entry in the action bar at the top of the main view. Then, touch a new NFC tag. The device should vibrate when it detects the tag and show a text entry field. Enter the desired location description. Make sure to leave the device close to the tag, so the connection can be upheld and the information can be written.

### Adding questions
To add questions, tap the "add question" entry in the action bar menu. Enter a question and answer and select a location. Only locations that have already been added are displayed


### Populating the database with sample data
Sample data can be found in the method `populateDatabaseWithSampleData` in `MainActivity`. Adjust the data such that the `location` parameter matches the locations defined for your NFC tags. This has to be an exact match, otherwise the questions will not be found later on. Uncomment the call of this method in `onCreate` before you first run the app (and remove it again for subsequent launches)