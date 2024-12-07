import React, { useRef } from 'react';
import axios from 'axios';

function FileUpload() {
  const fileInputRef = useRef(null); // Ссылка на элемент input

  const handleClick = () => {
    fileInputRef.current.click(); // Открываем окно выбора файла при нажатии на кнопку
  };

  const handleFileChange = async (event) => {
    const file = event.target.files[0];
    if (!file) {
      alert('Пожалуйста, выберите файл для загрузки');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
        console.log("Отправка запроса...");
      const response = await axios.post('http://localhost:8080/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      console.log('Файл успешно загружен:', response.data);
    } catch (error) {
      console.error('Ошибка при загрузке файла:', error);
    }
  };

  return (
    <div>
      <button onClick={handleClick}>Выбрать файл для загрузки</button>
      <input
        type="file"
        ref={fileInputRef}
        style={{ display: 'none' }} // Скрываем input
        onChange={handleFileChange}
      />
    </div>
  );
}

export default FileUpload;
