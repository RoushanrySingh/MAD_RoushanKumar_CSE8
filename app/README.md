#  Currency Converter App

This is a simple Android application developed for my Mobile Application Development (MAD) assignment.

The app allows users to convert currency values between INR, USD, EUR, and JPY using predefined exchange rates.

---

##  Features

* Convert between INR, USD, EUR, JPY
* Clean and simple user interface
* Real-time conversion result
* Settings screen with Dark Mode option
* Smooth button animation
* Professional UI design

---

##  Screenshots

### Home Screen (Dropdown Working)
![Home](screenshots/result1.png)

### Conversion Output (Final Result)
![Output](screenshots/result.png)

### Settings Screen (Dark Mode)
![Screen3](screenshots/result3.png)

### Improved Home UI
![Screen4](screenshots/result2.png)

---

##  Technologies Used

* Java (Android)
* XML (UI Design)
* Android Studio
* Git & GitHub

---

## ️ How the App Works

1. User enters amount
2. Selects "From Currency"
3. Selects "To Currency"
4. Clicks Convert button
5. Result is displayed instantly

---

##  Problems Faced & Solutions

###  App not running (MainActivity error)

Fixed manifest and rebuilt project.

---

###  Spinner not showing currencies

Solved using ArrayAdapter.

---

###  Long decimal values

Formatted output using:
`String.format("%.2f", output)`

---

### Text visibility issue

Added proper textColor and background.

---

###  Screenshots not visible on GitHub

Fixed by adding files using Git and correcting image paths.

---

###  UI looked basic

Improved with custom drawable, colors, and spacing.

---

##  Project Structure

currencyConverter/
│
├── app/
│   ├── src/
│   ├── res/
│   └── screenshots/
│       ├── result.png
│       ├── result1.png
│       ├── result2.png
│       ├── result3.png
│       └── result4.png
│
├── README.md

---

## Conclusion

This project helped me understand Android development basics, UI design, user input handling, debugging, and GitHub project management.

---

## Author

Roushan Kumar Singh  
BTech CSE





