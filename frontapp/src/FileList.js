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

  const handleLoading = async (fileName) => {
    const filePath = `${fileName}`; 
    try {
      const response = await fetch(`http://localhost:8080/yandex/upload?filePath=${encodeURIComponent(filePath)}`, {
        method: "POST",
      });
      const result = await response.text();
      alert(result);
    } catch (error) {
      alert("Ошибка при загрузке файла: " + error.message);
    }
  };

  return (
    <div>
      <h2>Файлы в приложении:</h2>
      <ul>
        {files.map((file, index) => (
          <li key={index} className="file-item">
            <span>{file}</span>
            <div className="buttons-container">
              <button onClick={() => handleLoading(file)}>Загрузить на яндекс диск</button>
              <button onClick={() => handleDelete(file)}>Удалить</button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default FileList;
