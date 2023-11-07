# README

**Android Studio version:** Android Studio Giraffe | 2022.3.1 Patch 1

**Emulator choice:** Pixel 2 API 24 (same as project 1)

### Map View (Landing Page)
After open the project directory in Android Studio, run the app using the settings specified above. 
- You should see a map as the landing page. You can zoom in or out the map to some certain extend. 
- The circle button on the middle of buttom (but above the navigation bar) controls the clicking mode which can switch between info-displaying or reservation-action mode. 
- In info-displaying mode, you can click on the markers
- In reserve-action mode, you can try to reserve a building if logged in
- Any attemp to view profile page or reserve spots will prompt you to login if you are not

### Login & Signup
- **A valid credential to log in: id = 0000000001, password = 12345678**. (Dummy inputs of reservation and reservation history have been put into this account)
- Signup check: whether email is in valid form; whether id is 10-digit; whether password is at least 8 digits; whether affiliation is exactly "Faculty" or "Student", cap matters.

### Profile page
- Rightmost button in navigation bar guides you to profile page.
- You can see personal info, current activated reservation, and reservation history.
- Manage button can prompt you to a window to change your current time to available time slots from original building and original indoor or outdoor mode.
- Cancel button can cancel current reservation and put it in history.

### Building page
- Middle button in navigation bar guides you to the default building page (DML)
- You should see building information (building name,  abbreviation, and location) and booking information.
- You can select time slots on the booking information scroll menu.
- Book button can be clicked to make reservations (then redirect user back to the map view if successful, prompt error message if not).