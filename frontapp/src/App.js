import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import YandexAuth from "./YandexAuth";
import AuthCallback from "./AuthCallback";
import FileUpload from "./FileUpload";
import YandexDisk from "./YandexDisk";
import './App.css';
import FileFetcher from './FileFetcher';
import AppFolderFiles from './AppFolder';
import CreateFolder from './CreateFolder';


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
    
        <YandexDisk />

        <FileFetcher />
        <AppFolderFiles/>



      </div>
    </div>
  );
}


export default App;
