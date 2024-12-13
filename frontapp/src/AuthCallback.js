import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const AuthCallback = () => {
  // Название папки приложения - которое будет 
  const [folderName, setFolderName] = useState("GreenData");
  const [responseMessage, setResponseMessage] = useState("");
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  const createFolder = async (token) => {
    try {
      const response = await fetch(
        `http://localhost:8080/yandex/folder/create?folderName=${encodeURIComponent(folderName)}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`, // Если нужен токен
          },
        }
      );

      if (response.ok) {
        const data = await response.json();
        setResponseMessage("Папка успешно создана!");
        console.log("Ответ:", data);
      } else {
        const errorText = await response.text();
        setError(`Ошибка при создании папки: ${errorText}`);
        console.error(errorText);
      }
    } catch (err) {
      setError(`Произошла ошибка: ${err.message}`);
      console.error(err);
    }
  };

  useEffect(() => {
    const hash = window.location.hash.substring(1); // Убираем # из URL
    const params = new URLSearchParams(hash);
    const token = params.get("access_token");

    if (token) {
      console.log("Ваш токен:", token);

      // Отправляем токен на сервер
      fetch("http://localhost:8080/api/save-token", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token }),
      })
        .then(() => createFolder(token)) // Создаём папку после сохранения токена
        .then(() => navigate("/")); // Перенаправляем пользователя
    }
  }, [navigate]);

  return (
    <div>
      <p>Авторизация...</p>
      {responseMessage && <div className="success">{responseMessage}</div>}
      {error && <div className="error">{error}</div>}
    </div>
  );
};

export default AuthCallback;
