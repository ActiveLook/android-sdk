Compiled and validated with Android studio 3.1.3 for Windows 64-bit
Using (automatically installed):
- Gradle 4.4
- Android SDK Platform 21
- Build Tools revision 27.0.3


This code is licensed under the MIT Licence. You may obtain a copy of the License at

https://opensource.org/licenses/MIT

MDMBLE application embeds a SQLite database to store internal settings
The unique MessageBLE(int id, String message, String alias) table stores the commands:
- id: incremental number
- message: command to be sent
- alias: text displayed on the interface

Caution: the database is cleared only when uninstalling the application.
