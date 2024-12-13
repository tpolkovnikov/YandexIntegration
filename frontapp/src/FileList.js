import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './FileList.css'; // Подключаем файл стилей

function FileList() {
  const [files, setFiles] = useState([]);

  useEffect(() => {
    // Получаем список файлов с сервера
    axios.get('http://localhost:8080/api/files')
      .then(response => {
        setFiles(response.data);
      })
      .catch(error => {
        console.error('Error fetching files:', error);
      });
  }, []);

  // Обработчики для кнопок (пока пустые)
  const handleDelete = (fileName) => {
    // Логика удаления файла
    console.log(`Delete file: ${fileName}`);
  };

  const handleDownload = (fileName) => {
    // Логика загрузки файла на диск
    console.log(`Download file: ${fileName}`);
  };

  return (
    <div>
      <h2>Файлы в приложении:</h2>
      <ul>
        {files.map((file, index) => (
          <li key={index} className="file-item">
            <span>{file}</span>
            <div className="buttons-container">
              <button onClick={() => handleDownload(file)}>Download</button>
              <button onClick={() => handleDelete(file)}>Delete</button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default FileList;
