# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## 0.1.0
- Initial version

## 0.2.0
- Added localisation support (autotranslated) for all StS base game languages

## 0.2.1
- Fixed issue with hitbox size/locations for resizable panel
- Fixed issue with cursor overrides changing when the mouse was being held when they shouldn't

## 0.3.0
- Configuration menu now has multiple tabs for easier navigation/more options in future
- Added 'Characters' tab where characters (and Continuous/Rotating) can be toggled on/off individually 
- Added 'suppress save notification' option as it can look bad over the top of certain panel positions
- Added 'coloured streak counts' option that colours each streak number with a colour associated with the character

# 0.3.1
- Updated date formats for all languages to be same as StS run history screen's localised formats
- Added considerably more exception handling and error logging for streak calculation logic
- Possibly fixed issues with UI on config screen not scaling properly
- Added 'Help' section that has a 'Copy Error Log' button that copies the error log to clipboard for error reporting

# 0.3.2
- Fixed win-rate being calculated incorrectly, is now a percentage of wins out of total games played
- Fixed support for multiple profiles, it will now load runs dependent on profile and recalculate streak data when switching profile

# 0.3.3
- Fixed issue where winning with a single character multiple times in a row would increase the rotating streak if you didn't cause the full data to be recalculated by changing an option