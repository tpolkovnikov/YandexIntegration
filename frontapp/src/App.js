import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import YandexAuth from "./YandexAuth";
import AuthCallback from "./AuthCallback";
import FileUpload from "./FileUpload";
import './App.css';
import AppFolderFiles from './AppFolder';
import FileList from './FileList';

function App() {
  // При первом рендере читаем значения из localStorage или используем значения по умолчанию
  const [permissions, setPermissions] = useState(() => {
    const savedPermissions = localStorage.getItem('permissions');
    return savedPermissions ? JSON.parse(savedPermissions) : { attachYandex: false, saveYandex: false };
  });

  // Функция для обновления состояния и сохранения в localStorage
  const updatePermissions = (newPermissions) => {
    setPermissions(newPermissions);
    localStorage.setItem('permissions', JSON.stringify(newPermissions)); // Сохраняем состояние в localStorage
  };

  const handleAttachChange = (event) => {
    updatePermissions({ ...permissions, attachYandex: event.target.checked });
  };

  const handleSaveChange = (event) => {
    updatePermissions({ ...permissions, saveYandex: event.target.checked });
  };

  return (
    <div className="App">
      <div className="app-container">
        {/* Блок с флажками и заголовком */}
        <div className="permissions">
          <h4>Разрешения</h4>
          <label>
            <input
              type="checkbox"
              checked={permissions.attachYandex}
              onChange={handleAttachChange}
            />
            Разрешить прикрепление файлов с Yandex диска
          </label>

          <label>
            <input
              type="checkbox"
              checked={permissions.saveYandex}
              onChange={handleSaveChange}
            />
            Разрешить сохранение файлов на Yandex диск
          </label>
        </div>

        {/* Отображение Router и Auth по условиям флажков */}
        {(permissions.attachYandex || permissions.saveYandex) && (
          <Router>
            <Routes>
              <Route path="/" element={<YandexAuth />} />
              <Route path="/auth/callback" element={<AuthCallback />} />
            </Routes>
          </Router>
        )}

        {permissions.attachYandex && <AppFolderFiles />}
      </div>

      <div className="main-container">
        <FileList permissions={permissions} />
        <FileUpload />
      </div>
    </div>
  );
}

export default App;
