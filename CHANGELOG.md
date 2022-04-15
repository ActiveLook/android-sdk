# CHANGELOG

## Version 4.2.3

### New features
- add a failure callback for updating errors
- add an up to date state for updates
- Allow cancelling connection
- Add a serialized glasses object allowing reconnecting without scanning

### Changes
- make update progress a double
- update dependencies
- On connection lost, directly trigger the disconnected glasses callback

## Version 4.2.2

### New features
- Include firmware update
    - get latest firmware from repository
- Include configuration update
    - get latest configuration from repository

### Fixes
- fix bad types.
- fix pages command id & byte typing
- fix version check for compatibility
- fix blocking api unavailability
- fix config update progress
- fix suota synchronization
- fix writting on characteristics twice
- fix BLE scan permission
- fix command unstacking
- fix flow control after 1000ms
- fix disconnect on disconnected

## Version 4.0.0

### Add

- Initial import of the SDK compatible with the firmware 4.0.0
