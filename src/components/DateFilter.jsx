import React from 'react';
import { getTodayRange, getThisWeekRange, getThisMonthRange } from '../utils/dateUtils';

export default function DateFilter({ dateRange, setDateRange, presetRange, setPresetRange }) {
    const setToday = () => {
        setDateRange(getTodayRange());
        setPresetRange('today');
    }
    const setThisWeek = () => {
        setDateRange(getThisWeekRange());
        setPresetRange('week');
    }
    const setThisMonth = () => {
        setDateRange(getThisMonthRange());
        setPresetRange('month');
    }

    return (
        <div className="date-filter">
            <div style={{ marginBottom: '0.5rem' }}>
                <button onClick={setToday} className={presetRange === 'today' ? 'active' : ''}>Today</button>
                <button onClick={setThisWeek} className={presetRange === 'week' ? 'active' : ''}>This Week</button>
                <button onClick={setThisMonth} className={presetRange === 'month' ? 'active' : ''}>This Month</button>
            </div>

            <label>
                <strong>From:</strong>
                <input
                    type="date"
                    value={dateRange.from || ''}
                    onChange={e => setDateRange(prev => ({ ...prev, from: e.target.value }))}
                    max={dateRange.to || undefined}
                />
            </label>
            <label>
                <strong>To:</strong>
                <input
                    type="date"
                    value={dateRange.to || ''}
                    onChange={e => setDateRange(prev => ({ ...prev, to: e.target.value }))}
                    min={dateRange.from || undefined}
                />
            </label>
        </div>
    );
}
