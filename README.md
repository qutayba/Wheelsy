# Wheelsy

This project is meant to resit the course Mobile Infrastructure & Securtiy. The project is an Android app that collects data from the users and sends it to the backend (Firebase). The users can view the history of their trips and visited locations in the app.

<table border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td><img src="https://iili.io/2zlm2p.png" width="200px" style="display:inline-block" /></td>
    <td><img src="https://iili.io/2zltEv.png" width="200px" style="display:inline-block" /></td>
    <td><img src="https://iili.io/2zlLQa.png" width="200px" style="display:inline-block" /></td>
    <td><img src="https://iili.io/2zlD4R.png" width="200px" style="display:inline-block" /></td>
  </tr>
</table

---


## Assignment

The scope of this assignment is to make an Android application that uses two native sensors of the device. The application must collect some sort of data and push it to the backend to be stored for further use.

## Requirements

### Authentication

- [x] User login and logout
- [x] User registration
- [x] User login session check
- [ ] Forget password option

### Trips registration

- [x] Creating trips based on the user's location
- [x] Using camera to take photos and include them with the trip information
- [x] Handling the user's request to start, pause, stop or reset the location tracker
- [x] Showing the distance that has been made by the user

### Trips list

- [x] Showing the stored trips in a list view
- [x] Styling the list to show the basic information of the trips
- [x] Filter options for the list
- [ ] Sorting option for the list

### Trip details

- [x] Showing the trip route in Google Maps view
- [x] Showing the trip's details
- [x] Option to share the trip on social media
- [x] Option to delete a trip
- [x] Showing the trip's photos

## Technical info

## Backend

- Firebase realtime database: To store the trips information in document-based database
- Firebase auth: To handel the authentication of the app (login, users...etc)
- Firebase storage: To store the images of th trips

## App

- The complete application was with Java made
- The complete UI was made based on the Material Design guidelines
- External libraries:
  - **zgallery:** Simple photos gallery
  - **gson:** to handle JSON parsing and serializing
  - **faboptions:** Nice package to make a FAB menu (in the home view)
  - **prettytime:** To format date object to make time human readable
  - **Google maps** To show the trip's route on a map view

<br/>
<br/>
<br/>
Developed by: <br/>
Qutayba Ali - 500710634
