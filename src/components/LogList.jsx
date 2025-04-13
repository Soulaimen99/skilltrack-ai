import React from 'react';

export default function LogList({ logs, activeTag, handleDelete }) {
    return (
        <ul>
            {logs
                .filter(log => !activeTag || (log.tags || '').split(',').map(t => t.trim()).includes(activeTag))
                .map(log => (
                    <li key={log.id}>
                        <div className="log-content">
                            <div className="log-main">
                                <span className="label">Content:</span>{log.content}
                            </div>
                            <div className="log-tags">
                                <span className="label">Tags:</span>{log.tags}
                            </div>
                            <div className="log-footer">
                                <span>{new Date(log.date).toLocaleString()}</span>
                            </div>
                        </div>
                        <button onClick={() => handleDelete(log.id)} title="Delete log">🗑️</button>
                    </li>
                ))}
        </ul>
    );
}