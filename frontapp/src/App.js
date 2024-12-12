import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import YandexAuth from "./YandexAuth";
import AuthCallback from "./AuthCallback";
import FileUpload from "./FileUpload";
import './App.css';

function App() {

  return (
    <div className="App">
      <div className="main-container">

        <Router>
          <Routes>
            <Route path="/" element={<YandexAuth />} />
            <Route path="/auth/callback" element={<AuthCallback />} />
          </Routes>
        </Router>

        <FileUpload />


      </div>
    </div>
  );
}

export default App;
