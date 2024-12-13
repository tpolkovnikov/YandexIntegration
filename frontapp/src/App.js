import React from 'react';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import YandexAuth from "./YandexAuth";
import AuthCallback from "./AuthCallback";
import FileUpload from "./FileUpload";
import YandexDisk from "./YandexDisk";
import './App.css';
import AppFolderFiles from './AppFolder';
import FileList from './FileList';


function App() {


  return (
    <div className="App">
      <div className="app-container">

        <Router>
          <Routes>
            <Route path="/" element={<YandexAuth />} />
            <Route path="/auth/callback" element={<AuthCallback />} />
          </Routes>
        </Router>
        <YandexDisk />
        <AppFolderFiles/>
      </div>


      
      <div className="main-container">
        <FileUpload />
        <FileList/>
      </div>
      
    </div>
  );
}


export default App;
