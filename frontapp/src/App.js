import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import YandexAuth from "./YandexAuth";
import AuthCallback from "./AuthCallback";
import './App.css';

function App() {
  const [files, setFiles] = useState([]);
  const [permissions, setPermissions] = useState({
    attachYandex: true,
    attachGoogle: true,
    saveYandex: false,
    saveGoogle: false,
  });

  const handleFileUpload = (event) => {
    const uploadedFiles = Array.from(event.target.files).map((file) => ({
      name: file.name,
      date: new Date().toLocaleDateString(),
      file,
    }));
    setFiles([...files, ...uploadedFiles]);
  };

  const handlePermissionChange = (e) => {
    setPermissions({ ...permissions, [e.target.name]: e.target.checked });
  };

  // нажатие на яндекс
  const attachToYandex = (fileName) => {
    fetch('http://localhost:8080/api/toYandex')
    

    console.log(`Attach ${fileName} to Yandex Disk`);
  };

  const attachToGoogle = (fileName) => {
    console.log(`Attach ${fileName} to Google Drive`);
  };

  const deleteFile = (fileName) => {
    setFiles(files.filter((file) => file.name !== fileName));
  };



  return (
    <div className="App">
      <div className="main-container">

        <Router>
          <Routes>
            <Route path="/" element={<YandexAuth />} />
            <Route path="/auth/callback" element={<AuthCallback />} />
          </Routes>
        </Router>

        {/* File Upload and Display Section */}
        <div className="file-upload-section">
          <input type="file" multiple onChange={handleFileUpload} />
          <div className="file-list">
            {files.map((file, index) => (
              <div key={index} className="file-item">
                <span className="file-name">{file.name}</span>
                <span className="file-date">{file.date}</span>
                {/* Conditional buttons */}
                {permissions.attachYandex && (
                  <button onClick={() => attachToYandex(file.name)}>📁 Yandex</button>
                )}
                {permissions.attachGoogle && (
                  <button onClick={() => attachToGoogle(file.name)}>📁 Google</button>
                )}
                <button onClick={() => deleteFile(file.name)}>🗑</button>
              </div>
            ))}
          </div>
        </div>

        {/* Permissions Section */}
        <div className="permissions">
          <div className="group">
            <h4>Разрешить прикрепление файлов</h4>
            <label className="green-checkbox">
              <input
                type="checkbox"
                name="attachYandex"
                checked={permissions.attachYandex}
                onChange={handlePermissionChange}
              />
              Yandex Диск
            </label>
            <label className="green-checkbox">
              <input
                type="checkbox"
                name="attachGoogle"
                checked={permissions.attachGoogle}
                onChange={handlePermissionChange}
              />
              Google Диск
            </label>
          </div>
          <div className="group">
            <h4>Разрешить сохранение файлов</h4>
            <label className="green-checkbox">
              <input
                type="checkbox"
                name="saveYandex"
                checked={permissions.saveYandex}
                onChange={handlePermissionChange}
              />
              Yandex Диск
            </label>
            <label className="green-checkbox">
              <input
                type="checkbox"
                name="saveGoogle"
                checked={permissions.saveGoogle}
                onChange={handlePermissionChange}
              />
              Google Диск
            </label>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;