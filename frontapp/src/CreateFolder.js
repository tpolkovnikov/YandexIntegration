import React, { useState } from 'react';

// пусть пока будет на будущее, нигде не используем пока
const CreateFolder = () => {
    const [folderName, setFolderName] = useState('');
    const [responseMessage, setResponseMessage] = useState('');
    const [error, setError] = useState(null);
    setFolderName("GreenData")

    const createFolder = async () => {
        try {
            const response = await fetch(`http://localhost:8080/yandex/folder/create?folderName=${encodeURIComponent(folderName)}`, {
                method: 'POST'
            });

            if (response.ok) {
                const data = await response.json();
                setResponseMessage('Папка успешно создана!');
            } else {
                const errorText = await response.text();
                setError(`Ошибка при создании папки: ${errorText}`);
            }
        } catch (err) {
            setError(`Произошла ошибка: ${err.message}`);
        }
    };

    return (
        <div>
            <h3>Создать папку</h3>
            <input
                type="text"
                value={folderName}
                onChange={(e) => setFolderName(e.target.value)}
                placeholder="Введите название папки"
            />
            <button onClick={createFolder}>Создать папку GreenData (обя)</button>

            {responseMessage && <div className="success">{responseMessage}</div>}
            {error && <div className="error">{error}</div>}
        </div>
    );
};

export default CreateFolder;
