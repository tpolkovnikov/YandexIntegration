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
      <button onClick={fetchFiles}>Получить файлы из папки приложения</button>

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
              <button onClick={() => handleFileDownload(file.name)}>Загрузить</button>
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

/*
мое
если %
https://cloud-api.yandex.net/v1/disk/resources/download?path=GreenData%25Zombatar_1.jpg
Если /
https://cloud-api.yandex.net/v1/disk/resources/download?path=GreenData%252FZombatar_1.jpg
полигон
https://cloud-api.yandex.net/v1/disk/resources/download?path=GreenData%2FZombatar_1.jpg


моё
https://cloud-api.yandex.net/v1/disk/resources/download?path=GreenData%25%D0%94%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82.docx
полигон
https://cloud-api.yandex.net/v1/disk/resources/download?path=GreenData%2F%D0%94%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82.docx




полигон

https://downloader.disk.yandex.ru/disk/948c9112e129b088f37816bf415247f9c52883eb08466568937bf4202cd00c55/675c6628/kufd3_Ln_-HyXTxxR475TiBXrmj-ftyGeVSFAlNDsUoeRc2F5M3RENUAvVuzSGwXZ8x32NeDODIQBtrBtZDgvg%3D%3D?uid=966414585&filename=Zombatar_1.jpg&disposition=attachment&hash=&limit=0&content_type=image%2Fjpeg&owner_uid=966414585&fsize=9989&hid=b595284e6388bb5b4d2a43523b3d0fe9&media_type=image&tknv=v2&etag=a0c02a58358bb4a15aadf1b158e1bd88
https://downloader.disk.yandex.ru/disk/027ad12121c377e20932003b2c9ac022e680bc024f1a0a65b23fd0823c7ac24c/675c7782/kufd3_Ln_-HyXTxxR475TiBXrmj-ftyGeVSFAlNDsUoeRc2F5M3RENUAvVuzSGwXZ8x32NeDODIQBtrBtZDgvg%253D%253D
*/
