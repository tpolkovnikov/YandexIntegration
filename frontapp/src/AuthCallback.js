import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const AuthCallback = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const hash = window.location.hash.substring(1); // Убираем # из URL
    const params = new URLSearchParams(hash);
    const token = params.get("access_token");

    if (token) {
      console.log("Ваш токен:", token);
      // Передайте токен на сервер
      fetch("/api/save-token", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token }),
      }).then(() => navigate("/")); // Перенаправить пользователя
    }
  }, [navigate]);

  return <div>Авторизация...</div>;
};

export default AuthCallback;
