import { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';

export default function Dashboard() {
    const [logs, setLogs] = useState([]);
    const [content, setContent] = useState('');
    const [tags, setTags] = useState('');
    const [summary, setSummary] = useState('');
    const [loadingLogs, setLoadingLogs] = useState(false);
    const [loadingSummary, setLoadingSummary] = useState(false);
    const { keycloak } = useKeycloak();
    const [activeTag, setActiveTag] = useState(null);

    const allTags = Array.from(new Set(
        logs.flatMap(log => log.tags?.split(',').map(t => t.trim()) || [])
    )).filter(tag => tag);

    useEffect(() => {
        if (keycloak.authenticated) {
            fetchLogs();
        }
    }, [keycloak]);

    const fetchLogs = async () => {
        setLoadingLogs(true);
        try {
            const res = await fetch('/logs', {
                headers: { Authorization: `Bearer ${keycloak.token}` },
            });

            if (!res.ok) throw new Error(await res.text());
            const data = await res.json();
            setLogs(data);
        }
        catch (err) {
            console.error('Failed to fetch logs:', err);
        }
        finally {
            setLoadingLogs(false);
        }
    };

    const handleAddLog = async (e) => {
        e.preventDefault();
        try {
            const res = await fetch('/logs', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${keycloak.token}`,
                },
                body: JSON.stringify({ content, tags }),
            });

            if (!res.ok) throw new Error(await res.text());
            const newLog = await res.json();
            setLogs((prev) => [...prev, newLog]);
            setContent('');
            setTags('');
        }
        catch (err) {
            console.error('Error adding log:', err);
        }
    };

    const handleDelete = async (id) => {
        try {
            const res = await fetch(`/logs/${id}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${keycloak.token}`,
                },
            });

            if (res.ok) {
                setLogs(prev => prev.filter(log => log.id !== id));
            }
            else {
                console.error('Failed to delete log:', await res.text());
            }
        }
        catch (err) {
            console.error('Error deleting log:', err);
        }
    };

    const handleSummarize = async () => {
        setLoadingSummary(true);
        try {
            const res = await fetch('/logs/summarize', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${keycloak.token}`,
                },
                body: JSON.stringify(logs),
            });

            if (!res.ok) throw new Error(await res.text());
            const data = await res.json();
            setSummary(data.summary);
        }
        catch (err) {
            console.error('Error generating summary:', err);
        }
        finally {
            setLoadingSummary(false);
        }
    };

    return (
        <div className="container">
            <div>
                <div className="tag-filter">
                    <button onClick={() => setActiveTag(null)} className={!activeTag ? 'active' : ''}>All</button>
                    {allTags.map(tag => (
                        <button
                            key={tag}
                            onClick={() => setActiveTag(tag)}
                            className={activeTag === tag ? 'active' : ''}
                        >
                            {tag}
                        </button>
                    ))}
                </div>
                <h2>Learning Logs</h2>
                {loadingLogs ? (
                    <p>Loading logs...</p>
                ) : (
                    <ul>
                        {logs
                            .filter(log => !activeTag || log.tags?.includes(activeTag))
                            .map(log => (
                                <li key={log.id}>
                                    <div className="log-content">
                                        <div className="log-main">Content: {log.content}</div>
                                        <div className="log-tags">Tags: {log.tags}</div>
                                        <div className="log-footer">
                                            <div className="log-date">{new Date(log.date).toLocaleString()}</div>
                                        </div>
                                    </div>
                                    <button onClick={() => handleDelete(log.id)} title="Delete log">🗑️</button>
                                </li>
                            ))}
                    </ul>
                )}

                <h3>Add New Log</h3>
                <form onSubmit={handleAddLog}>
                    <textarea
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        placeholder="What did you learn?"
                        rows="4"
                        required
                    />
                    <input
                        type="text"
                        value={tags}
                        onChange={(e) => setTags(e.target.value)}
                        placeholder="Tags (comma separated)"
                    />
                    <button type="submit">Add Log</button>
                </form>
            </div>
            <div className="spacer"></div>
            <div>
                <h3>Summary</h3>
                <button onClick={handleSummarize} disabled={loadingSummary}>
                    {loadingSummary ? 'Summarizing...' : 'Generate Summary'}
                </button>

                {summary && (
                    <div className="summary">
                        <pre>{summary}</pre>
                    </div>
                )}
            </div>
        </div>
    );
}
