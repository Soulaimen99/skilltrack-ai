import { useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import DateFilter from './components/DateFilter';
import TagFilter from './components/TagFilter';
import LogList from './components/LogList';
import SummaryBox from './components/SummaryBox';
import { getTodayRange, isDateInRange } from './utils/dateUtils';

export default function Dashboard() {
    const { keycloak } = useKeycloak();
    const [allLogs, setAllLogs] = useState([]);
    const [logs, setLogs] = useState([]);
    const [content, setContent] = useState('');
    const [tags, setTags] = useState('');
    const [summary, setSummary] = useState('');
    const [loadingLogs, setLoadingLogs] = useState(false);
    const [loadingSummary, setLoadingSummary] = useState(false);
    const [activeTag, setActiveTag] = useState(null);
    const [presetRange, setPresetRange] = useState('today');
    const [dateRange, setDateRange] = useState(getTodayRange());

    const allTags = Array.from(new Set(
        allLogs.flatMap(log => log.tags?.split(',').map(t => t.trim()) || [])
    )).filter(tag => tag);

    useEffect(() => {
        if (keycloak.authenticated) fetchLogs();
    }, [keycloak]);

    useEffect(() => {
        const filtered = allLogs.filter(log => {
            const matchesTag = !activeTag || (log.tags || '').split(',').map(t => t.trim()).includes(activeTag);
            const inDateRange = isDateInRange(log.date, dateRange.from, dateRange.to);
            return matchesTag && inDateRange;
        });
        setLogs(filtered);
    }, [activeTag, dateRange, allLogs]);


    const fetchLogs = async () => {
        setLoadingLogs(true);
        try {
            const params = new URLSearchParams();
            if (dateRange.from) params.append('from', dateRange.from);
            if (dateRange.to) params.append('to', dateRange.to);
            const res = await fetch(`/logs?${params.toString()}`, {
                headers: { Authorization: `Bearer ${keycloak.token}` },
            });
            if (!res.ok) throw new Error(await res.text());
            const data = await res.json();
            setAllLogs(data);
        } catch (err) {
            console.error('Failed to fetch logs:', err);
        } finally {
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
            setAllLogs(prev => [...prev, newLog]);
            setContent('');
            setTags('');
        } catch (err) {
            console.error('Error adding log:', err);
        }
    };

    const handleDelete = async (id) => {
        try {
            const res = await fetch(`/logs/${id}`, {
                method: 'DELETE',
                headers: { Authorization: `Bearer ${keycloak.token}` },
            });
            if (res.ok) {
                setAllLogs(prev => prev.filter(log => log.id !== id));
            } else {
                console.error('Failed to delete log:', await res.text());
            }
        } catch (err) {
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
        } catch (err) {
            console.error('Error generating summary:', err);
        } finally {
            setLoadingSummary(false);
        }
    };

    return (
        <div className="container">
            <TagFilter allTags={allTags} activeTag={activeTag} setActiveTag={setActiveTag} />
            <DateFilter dateRange={dateRange} setDateRange={setDateRange} presetRange={presetRange} setPresetRange={setPresetRange} />

            <h2>Learning Logs</h2>
            {loadingLogs ? <p>Loading logs...</p> : <LogList logs={logs} activeTag={activeTag} handleDelete={handleDelete} />}

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
                <button type="submit" disabled={content.length === 0}>Add Log</button>
            </form>

            <SummaryBox summary={summary} loadingSummary={loadingSummary} handleSummarize={handleSummarize} logs={logs} dateRange={dateRange} />
        </div>
    );
}