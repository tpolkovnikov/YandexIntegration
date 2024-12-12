import React from "react";

const YandexAuth = () => {
  const clientId = "ef3109f56ec54efbad504621c335a23b"; // Вставьте ваш client_id
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
