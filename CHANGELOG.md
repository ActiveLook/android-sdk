# Changelog

## Version 4.0.0

### Add

- Check firmware version (closes #12)
- Upload configuration function (closes #14)
- Multi-application configuration commands (closes #5)
- Gauge commands (closes #3)
- Page commands (closes #4)
- Delete all commands (closes #2)
- Display flow control returned value (closes #8)

### Update

- Image commands (closes #1)
- Readme documentation (closes #11)
- Each command activity is self-sufficient (cloes #7)

### Fixes

- BLE MTU
- Glasses deconnection (closes #10)
- Various bugs (closes #9)
    - shift was not saved
    - serial number is an unsigned int
    - dim(0) is removed
    - add a lower bound for als and gesture periods (250ms)
    - polyline was KO (because of MTU)
    - rotation constants
    - bitmap commands was KO (because of MTU)
    - remove duplicate entries
    - layout example commands
    - gauge commands values
