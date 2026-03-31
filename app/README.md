# Currency Converter App

This is a simple Android application developed for my Mobile Application Development (MAD) assignment.

The app allows users to convert currency values between INR, USD, EUR, and JPY using predefined exchange rates.

---

## Features

* Convert between INR, USD, EUR, JPY
* Clean and simple user interface
* Real-time conversion result
* Settings screen with Dark Mode option
* Smooth button animation
* Professional UI design

---

## Screenshots

### Home Screen

![UI](screenshots/ui.png)

### Conversion Output

![Result](screenshots/result.png)

---

## Technologies Used

* Java (Android)
* XML (UI Design)
* Android Studio
* Git & GitHub

---

##  How the App Works

1. User enters amount
2. Selects "From Currency"
3. Selects "To Currency"
4. Clicks Convert button
5. Result is displayed instantly

---

## Problems Faced & Solutions

### 1. App not running (MainActivity error)

**Error:**
Activity class does not exist

**Reason:**
MainActivity was not properly detected in AndroidManifest

**Solution:**

* Fixed manifest activity entry
* Rebuilt project using:
  Build → Rebuild Project

---

### 2. Spinner (Currency dropdown) not showing values

**Reason:**
Spinner adapter was not set

**Solution:**
Added ArrayAdapter in MainActivity:

```java
ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
android.R.layout.simple_spinner_dropdown_item, currencies);
fromCurrency.setAdapter(adapter);
toCurrency.setAdapter(adapter);
```

---

### 3. Currency conversion giving long decimal value

**Example:**
1.2048192771084338

**Solution:**
Formatted output:

```java
String.format("%.2f", output)
```

---

### 4. Text not visible (white on white issue)

**Reason:**
Default text color was not visible

**Solution:**
Added:

```xml
android:textColor="#000000"
android:textColorHint="#888888"
```

---

### 5. Screenshots not showing on GitHub

**Reason:**
Images were not added to Git

**Solution:**

```bash
git add .
git commit -m "Added screenshots"
git push
```

---

### 6. UI looked basic (not professional)

**Solution:**

* Added custom button background
* Added rounded corners
* Improved colors and spacing
* Added result box styling

---

## Project Structure

```
currencyConverter/
│
├── app/
│   ├── src/
│   ├── res/
│   └── screenshots/
│       ├── ui.png
│       └── result.png
│
├── README.md
└── build.gradle
```

---

##  Conclusion

This project helped me understand:

* Android UI design using XML
* Handling user input
* Using Spinner and Buttons
* Basic app navigation
* Debugging errors
* GitHub project management

---

##  Author

Roushan Kumar Singh
BTech CSE


