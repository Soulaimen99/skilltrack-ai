// src/components/LogsPage.jsx
import React, { useEffect, useState, useCallback } from 'react';

const baseUrl = 'http://localhost:8080';

const LogsPage = ({ username, onLogout }) => {
    const [logs, setLogs] = useState([]);
    const [newLogContent, setNewLogContent] = useState('');
    const [newLogTags, setNewLogTags] = useState('');
    const [summary, setSummary] = useState('');
    const storedAuth = localStorage.getItem('auth');

    const fetchLogs = useCallback(async () => {
        try {
            const response = await fetch(`${baseUrl}/logs`, {
                method: 'GET',
                headers: {
                    Authorization: `Basic ${storedAuth}`,
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });

            if (!response.ok) throw new Error('Failed to fetch logs');
            const data = await response.json();
            setLogs(data);
        } catch (error) {
            console.error(error);
        }
    }, [storedAuth]);

    useEffect(() => {
        fetchLogs();
    }, [fetchLogs]);

    const addLog = async () => {
        if (!newLogContent.trim()) return;

        try {
            const response = await fetch(`${baseUrl}/logs`, {
                method: 'POST',
                headers: {
                    Authorization: `Basic ${storedAuth}`,
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({ content: newLogContent, tags: newLogTags }),
            });

            if (!response.ok) throw new Error('Failed to add log');
            setNewLogContent('');
            setNewLogTags('');
            fetchLogs();
        } catch (error) {
            console.error(error);
        }
    };

    const removeLog = async (id) => {
        try {
            const response = await fetch(`${baseUrl}/logs/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Basic ${storedAuth}`,
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });

            if (!response.ok) throw new Error('Failed to delete log');
            fetchLogs();
        } catch (error) {
            console.error(error);
        }
    };

    const summarizeLogs = async () => {
        try {
            const logsInput = logs.map(log => ({ content: log.content, tags: log.tags || '' }));
            const response = await fetch(`${baseUrl}/logs/summarize`, {
                method: 'POST',
                headers: {
                    Authorization: `Basic ${storedAuth}`,
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(logsInput),
            });

            if (!response.ok) throw new Error('Failed to summarize');
            const data = await response.json();
            setSummary(data.summary);
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="container">
            <h2>Welcome, {username}!</h2>
            <button onClick={onLogout}>Logout</button>

            <h3>Your Logs:</h3>
            <ul>
                {logs.map(log => (
                    <li key={log.id}>
                        <strong>Content:</strong> {log.content}<br />
                        <strong>Tags:</strong> {log.tags}<br />
                        <button onClick={() => removeLog(log.id)}>Remove</button>
                    </li>
                ))}
            </ul>

            <h3>Add New Log</h3>
            <input type="text" value={newLogContent} onChange={(e) => setNewLogContent(e.target.value)} placeholder="Content" />
            <input type="text" value={newLogTags} onChange={(e) => setNewLogTags(e.target.value)} placeholder="Tags (optional)" />
            <button onClick={addLog}>Add</button>

            <h3>Summarize Logs</h3>
            <button onClick={summarizeLogs}>Generate Summary</button>
            {summary && <div className="summary"><h4>Summary:</h4><p>{summary}</p></div>}
        </div>
    );
};

export default LogsPage;
