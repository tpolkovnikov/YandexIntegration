import React from "react";

const YandexAuth = () => {
  const clientId = "62b07b7a758d421eab2f23e88712d270"; // Вставьте ваш client_id
  const redirectUri = "http://localhost:3000/auth/callback";

  const handleLogin = () => {
    const authUrl = `https://oauth.yandex.ru/authorize?response_type=token&client_id=${clientId}&redirect_uri=${redirectUri}`;
    window.location.href = authUrl;
  };
  
  return (
    <div>
      <button onClick={handleLogin}>Войти через Яндекс</button>
    </div>
  );
};

export default YandexAuth;
