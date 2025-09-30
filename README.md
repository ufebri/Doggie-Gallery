[![Coverage Status](https://coveralls.io/repos/github/ufebri/Doggie-Gallery/badge.svg?branch=master)](https://coveralls.io/github/ufebri/Doggie-Gallery?branch=master)[![Join Firebase Testing](https://img.shields.io/badge/Firebase-Join%20Testing-orange?logo=firebase)](https://appdistribution.firebase.dev/i/cc70d0566a3b66d3)

<img src="./assets/cover.png" width="1280" height="640" alt="cover" />

# Doggy Gallery
Doggie-Gallery is an Android application designed to showcase a delightful gallery of dog images. It allows users to browse through pictures of various dog breeds and save their favorite ones.

## Key Features

*   Displays a list of dog images.
*   Allows users to mark and save their favorite images.

## Core Technologies

*   **Programming Language:** Java
*   **Architecture:** Implements the MVVM (Model-View-ViewModel) pattern to separate UI logic from business logic, enhancing testability and maintainability.
*   **Networking:** Utilizes Retrofit for efficient communication with external APIs to fetch data.
*   **Dependency Management:** Employs a custom setup for managing dependencies within the project.

## Modules

*   `:app` ( `Doggie-Gallery.app` ): The main application module containing the core functionality.

## Setup

1.  Clone this repository.
2.  Open the project using Android Studio.
3. Copy `config.sample.properties` to `config.properties` and fill in:
    - Release signing keystore location, alias, and passwords.
    - Your AdMob app ID and banner ad unit IDs (production & optional debug).
4. Run the application on an Android emulator or a physical device.

## API Integration

The application integrates with an external API to fetch and display dog images, providing a dynamic content experience.

---

*This README provides an overview of the Doggie-Gallery project.*

Screenshoot
-----------
<img src="./assets/image1.jpeg" height="350" alt="image 1" /> <img src="./assets/image2.jpeg" height="350" alt="image 2" /> <img src="./assets/image3.jpeg" height="350" alt="image 3" /> <img src="./assets/image4.jpeg" height="350" alt="image 4" /> <img src="./assets/image5.jpeg" height="350" alt="image 5" />

## License

Doggie Gallery is distributed under the **Doggie Gallery Commercial License (DGCL)**. See
the [LICENSE](LICENSE) file for the exact grant of rights and restrictions. The project also depends
on several third-party libraries (AndroidX, Retrofit, Glide, Google Mobile Ads, Firebase, etc.);
please make sure you comply with their respective licenses when redistributing your end product.

### Third-Party Notices

A list of bundled third-party libraries and their licenses is available under [
`licenses/THIRD_PARTY_NOTICES.md`](licenses/THIRD_PARTY_NOTICES.md).
