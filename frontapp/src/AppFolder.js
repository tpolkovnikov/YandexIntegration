import React, { useState } from "react";

const AppFolderFiles = () => {
  const [files, setFiles] = useState([]);
  const [error, setError] = useState(null);

  const fetchFiles = async () => {
    try {
      const response = await fetch("http://localhost:8080/yandex/appfiles");
      if (response.ok) {
        const data = await response.json();
        setFiles(data._embedded.items || []); // Если список файлов возвращается в items
      } else {
        setError("Ошибка при получении файлов: " + response.statusText);
      }
    } catch (err) {
      setError("Произошла ошибка: " + err.message);
    }
  };

  const handleFileDownload = async (fileName) => {
    const filePath = `GreenData/${fileName}`; 
    try {
      const response = await fetch(`http://localhost:8080/yandex/download?filePath=${encodeURIComponent(filePath)}`, {
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
      <button className="button-main" onClick={fetchFiles}>Посмотреть файлы на диске</button>

      {error && <div className="error">{error}</div>}

      <div className="file-list">
        {files.length > 0 ? (
          files.map((file, index) => (
            <div
              key={index}
              className="file-item"
              style={{ display: "flex", alignItems: "center", marginBottom: "10px" }}
            >
              <span style={{ marginRight: "10px" }}>{file.name}</span>
              <button className="download-button" onClick={() => handleFileDownload(file.name)}>Загрузить</button>
            </div>
          ))
        ) : (
          <p>Файлы отсутствуют</p>
        )}
      </div>
    </div>
  );
};

export default AppFolderFiles;
