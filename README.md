Sandy H. 

Testing out mobile app development utilizing Andriod studio and Kotlin as a beginner to both. 
    I have done tutorials with the android studio website

Goal : Create a Calendar based app
    That should be multipurpose to replace several seperate apps that I currently use. 

Features desired:
- Notifications
* Appointments through user inputs
* Period Tracking
* To-do List
* Habit Tracker
* The ability to have the habits be customizable to the frequency
* Notifications that are for each above feature. 

Environment: Android Studio with Kotlin 

Current Objectives:
- Replace the Textview for the summaries panel to be Recyclerview to adapt with lists for a scrollable summary
- Add a remove event, not replace
- Debating whether it is better for certain events like appointments, habits, and to-dos whether there needs to be a monthly view of all of them in the summary or to keep the summary based soley on the day. 
    - This is due to an issue of using a tab to specify looking at certain events, fishing around for an event is tedious

Future Objectives:
- Complete the hybrid model of storage for events
- Implement a database like SQlite to ensure the app functions correctly
- Hopefully something that is intuitive to use

Feature Descriptions (In their own tab and overall)
    Main Overview:
    - Supposidly a calendar that shows every single possible event of a specific month
    - Restrictions, there is an issue of overlapping color indicators and possible flooding of events on a specific date
    - Which is why more specific information of events go to their own special tab
    
    Appointments: 
    - To keep track of the appointments in one's life
    - There needs to be able to have multiple appointments a day
    - Appointments require a title, and a time of the appointment 
        + Implemented the textview that recieves information for the user
    - Optional notes place for the appointments
    
    Periods:
    - A replacement period tracker 
        + current tracker has features I like however the notification system isn't great as it's easily ignored
        + Such as during dates completely unrelated to a period a notification to use the app appears
        + causing the desire to ignore the app
        + There is also a desire for a month default perspective due to it being a counter that is interactable and a simple touch changes the counter with the selected date shown more prevalent than the current date
    - Requires special UI as periods highlight the day with a specific flow 
        + Implemented UI
    - Require a period cycle expectance which is based off, the length of a cycle, the consistency of a cycle appearing, and the latest cycle
    - Track the cycles, special tracking for a latest cycle and an expected cycle
    - Possible tracking of the ovulatino cycle 
    - Possible tracking of mood and body condition
    
    Habits:
    - A replacement habit tracker
        + current habit tracker only shows 1 week for the free version 
        + causing an issue where I cannot check if I am on time or my current progress overall in a month
    - There needs to be a User provided habit and the frequency that it should occur
        + in the event no frequency is provided assume every day
    - Desired widget 
    - Simple click to confirm or deny a habit was done
    
    To-Do:
    - This was the reason I downloaded an app for the habit tracker for the notifications 
        + However I never used it despite downloading an entire app for it
        + The app I was using felt off to me that I started using the notes app and forgetting that I had, leading to using physical notes and trying to place them in a location I can see but those are forgotten too
    - Desire to plan certain things that need to be done ahead of time and being notified of it
    - For the list look perhaps a strike when something is labeled completed
    - Put down what needs to be done, when specifically it should be done, and optional notes as to why
    

Utilizing now the Kizitonwose Calendar API
https://github.com/kizitonwose/Calendar
