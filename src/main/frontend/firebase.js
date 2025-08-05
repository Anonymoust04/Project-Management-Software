// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyCdltjFDE9kiEEC8qZ0Q9oSDmKXjpcH210",
  authDomain: "agile-avengers-435801.firebaseapp.com",
  projectId: "agile-avengers-435801",
  storageBucket: "agile-avengers-435801.appspot.com",
  messagingSenderId: "1092972924355",
  appId: "1:1092972924355:web:1b0151df9b30cdfe8ac176",
  measurementId: "G-1231Z1XH9L"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);