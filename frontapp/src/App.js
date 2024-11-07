import React, { useState } from 'react';
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
    const uploadedFiles = Array.from(event.target.files).map(file => ({
      name: file.name,
      date: new Date().toLocaleDateString(),
      file,
    }));
    setFiles([...files, ...uploadedFiles]);
  };

  const handlePermissionChange = (e) => {
    setPermissions({ ...permissions, [e.target.name]: e.target.checked });
  };

  const downloadFile = (fileName) => {
    // Implement download logic here
    console.log(`Download ${fileName} to local`);
  };

  const uploadToYandex = (fileName) => {
    if (permissions.saveYandex) {
      console.log(`Save ${fileName} to Yandex Disk`);
    }
  };

  const uploadToGoogle = (fileName) => {
    if (permissions.saveGoogle) {
      console.log(`Save ${fileName} to Google Drive`);
    }
  };

  const deleteFile = (fileName) => {
    setFiles(files.filter(file => file.name !== fileName));
  };

  return (
    <div className="App">
      <div className="main-container">
        {/* File Upload and Display Section */}
        <div className="file-upload-section">
          <input type="file" multiple onChange={handleFileUpload} />
          <div className="file-list">
            {files.map((file, index) => (
              <div key={index} className="file-item">
                <span className="file-name">{file.name}</span>
                <span className="file-date">{file.date}</span>
                <button onClick={() => downloadFile(file.name)}>⬇️</button>
                <button onClick={() => uploadToYandex(file.name)}>📁   Yandex</button>
                <button onClick={() => uploadToGoogle(file.name)}>📁 Google</button>
                <h4></h4>
                <button onClick={() => deleteFile(file.name)}>🗑️</button>
              </div>
            ))}
          </div>
        </div>

        {/* Permissions Section */}
        <div className="permissions">
          <div className="group">
            <h4></h4>
            <label className="green-checkbox">
              <input
                type="checkbox"
                name="attachYandex"
                checked={permissions.attachYandex}
                onChange={handlePermissionChange}
              />
              Разрешить прикрепление файлов с Yandex диска
            </label>
            <label className="green-checkbox">
              <input
                type="checkbox"
                name="attachGoogle"
                checked={permissions.attachGoogle}
                onChange={handlePermissionChange}
              />
              Разрешить прикрепление файлов с Google диска
            </label>
          </div>

          <div className="group">
            <h4></h4>
            <label className="green-checkbox">
              <input
                type="checkbox"
                name="saveYandex"
                checked={permissions.saveYandex}
                onChange={handlePermissionChange}
              />
              Разрешить сохранение на Yandex диск
            </label>
            <label className="green-checkbox">
              <input
                type="checkbox"
                name="saveGoogle"
                checked={permissions.saveGoogle}
                onChange={handlePermissionChange}
              />
              Разрешить сохранение на Google диск
            </label>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
