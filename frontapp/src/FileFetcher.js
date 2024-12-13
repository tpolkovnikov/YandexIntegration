import React, { useState } from 'react';

const FileFetcher = () => {
    const [files, setFiles] = useState([]);
    const [error, setError] = useState(null);

    const fetchFiles = async () => {
        const queryParams = new URLSearchParams({
            limit: 1, // Задайте параметры для вызова
            mediaType: 'image',
            offset: 0,
            fields: 'name,created',
            previewSize: 'M',
            previewCrop: true,
        });

        try {
            const response = await fetch(`http://localhost:8080/yandex/files?${queryParams.toString()}`);
            if (response.ok) {
                const data = await response.json(); 
                setFiles(data.items || []); // Предполагается, что данные API возвращают `items`
            } else {
                setError('Ошибка при получении файлов: ' + response.statusText);
            }
        } catch (err) {
            setError('Произошла ошибка: ' + err.message);
        }
    };

    return (
        <div>
            <button onClick={fetchFiles}>Получить файлы</button>

            {error && <div className="error">{error}</div>}

            <div className="file-list">
                {files.length > 0 ? (
                    files.map((file, index) => (
                        <div key={index} className="file-item">
                            <img
                                src={file.preview || 'https://via.placeholder.com/150'}
                                alt={file.name || 'Превью'}
                                style={{ width: '150px', height: '150px' }}
                            />
                            <p><strong>Имя:</strong> {file.name}</p>
                            <p><strong>Дата создания:</strong> {file.created}</p>
                        </div>
                    ))
                ) : (
                    <p>Файлы отсутствуют</p>
                )}
            </div>
        </div>
    );
};

export default FileFetcher;
