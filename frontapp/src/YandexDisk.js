import React, { useState } from "react";

const YandexDisk = () => {
  const [diskData, setDiskData] = useState(null);
  const [error, setError] = useState(null);

  const handleGetDiskData = () => {
    fetch("http://localhost:8080/YandexDisk", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Ошибка: ${response.status} ${response.statusText}`);
        }
        return response.json();
      })
      .then((data) => {
        setDiskData(data); // Сохраняем данные о диске
        setError(null); // Сбрасываем ошибки
      })
      .catch((err) => {
        setError(err.message); // Сохраняем сообщение об ошибке
        setDiskData(null); // Сбрасываем данные
      });
  };

  return (
    <div>
      <button onClick={handleGetDiskData}>Получить данные о диске</button>

      {diskData && (
        <div>
          <h3>Данные о Яндекс.Диске:</h3>
          <p>Общее пространство: {diskData.total_space} байт</p>
          <p>Использованное пространство: {diskData.used_space} байт</p>
          <p>Корзина: {diskData.trash_size} байт</p>
          <h4>Системные папки:</h4>
          <p>Приложения: {diskData.system_folders.applications}</p>
          <p>Загрузки: {diskData.system_folders.downloads}</p>
        </div>
      )}

      {error && (
        <div style={{ color: "red" }}>
          <h3>Ошибка:</h3>
          <p>{error}</p>
        </div>
      )}
    </div>
  );
};

export default YandexDisk;
