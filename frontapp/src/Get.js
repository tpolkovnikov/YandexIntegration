import axios from "axios";

const getUploadLink = async () => {
  try {
    // Укажите ваш OAuth токен
    const token = "y0_AgAAAAA5mlD5AAzxkQAAAAEb_P7NAABdd0aXeppGLryqkEDNhKFs-TWNlA";

    // Укажите путь и параметры запроса
    const params = {
      path: "/Загрузки/.test.txt", // Путь, куда загрузить файл
      overwrite: true,                 // Признак перезаписи
      fields: "href"                   // Только нужные свойства в ответе
    };

    // Выполните GET-запрос
    const response = await axios.get("https://cloud-api.yandex.net/v1/disk/resources/upload", {
      headers: {
        Authorization: `OAuth ${token}`, // Добавьте токен в заголовок
      },
      params, // Передача параметров
    });

    console.log("Ссылка для загрузки файла:", response.data.href);
    return response.data.href; // Возвращает ссылку для загрузки файла
  } catch (error) {
    console.error("Ошибка при получении ссылки для загрузки:", error.response?.data || error.message);
  }
};

// Вызов функции
getUploadLink();
