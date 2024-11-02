import React, { useEffect, useState } from 'react';

function App() {
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetch('/api/hello')
            .then(response => response.text())
            .then(data => setMessage(data));
    }, []);

    return (
        <div>
            <h1>React Frontend</h1>
            <button onclick="alert('Hello, World!')">Click Me!</button>  
            <p>{message}</p>
        </div>
    );
}

export default App;
