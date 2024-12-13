import React, { useState } from 'react';

const AppFolderFiles = () => {
    const [files, setFiles] = useState([]);
    const [error, setError] = useState(null);

    const fetchFiles = async () => {
        try {
            const response = await fetch('http://localhost:8080/yandex/appfiles');
            if (response.ok) {
                const data = await response.json();
                setFiles(data._embedded.items || []); // Если список файлов возвращается в items
            } else {
                setError('Ошибка при получении файлов: ' + response.statusText);
            }
        } catch (err) {
            setError('Произошла ошибка: ' + err.message);
        }
    };

    return (
        <div>
            <button onClick={fetchFiles}>Получить файлы из папки приложения</button>

            {error && <div className="error">{error}</div>}

            <div className="file-list">
                {files.length > 0 ? (
                    files.map((file, index) => (
                        <div key={index} className="file-item">
                            <p><strong>Имя файла:</strong> {file.name}</p>
                            <p><strong>Путь:</strong> {file.path}</p>
                            <p><strong>Размер:</strong> {file.size} байт</p>
                            <p><strong>Тип:</strong> {file.mime_type}</p>
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
