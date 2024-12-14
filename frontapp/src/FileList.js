import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './FileList.css'; // Подключаем файл стилей

function FileList({ permissions }) {
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

  const update = () => {
    // Логика удаления файла
    axios.get('http://localhost:8080/api/files')
      .then(response => {
        setFiles(response.data);
      })
      .catch(error => {
        console.error('Error fetching files:', error);
      });
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

  const handleDelete = (fileName) => {
    // Отправляем запрос на удаление файла
    axios.delete(`http://localhost:8080/api/files/${fileName}`)
      .then(response => {
        // Если файл удален успешно, обновляем список файлов
        alert("Файл успешно удален");
        update();  // Обновляем список файлов после удаления
      })
      .catch(error => {
        console.error('Ошибка при удалении файла:', error);
        alert("Ошибка при удалении файла");
      });
      update();
  };

  return (
    <div>
      <div className="fileUpdate-container">

      <h3 className="h3">Файлы:</h3>
      <button className = "button-main" onClick={() => update()}>Обновить</button>
      
      </div>
      <ul>
        {files.map((file, index) => (
          <li key={index} className="file-item">
            <span>{file}</span>
            <div className="buttons-container">
              {/* Отображение кнопки загрузки на Yandex диск, если разрешение активировано */}
              {permissions.saveYandex && (
                <button onClick={() => handleLoading(file)}>Загрузить на яндекс диск</button>
              )}
              <button onClick={() => handleDelete(file)}>Удалить</button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default FileList;
