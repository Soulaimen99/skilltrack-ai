import React from 'react';
import { getTodayRange, getThisWeekRange, getThisMonthRange } from '../utils/dateUtils';

export default function DateFilter({ dateRange, setDateRange }) {
    const setToday = () => setDateRange(getTodayRange());
    const setThisWeek = () => setDateRange(getThisWeekRange());
    const setThisMonth = () => setDateRange(getThisMonthRange());

    return (
        <div className="date-filter">
            <div style={{ marginBottom: '0.5rem' }}>
                <button onClick={setToday}>Today</button>
                <button onClick={setThisWeek}>This Week</button>
                <button onClick={setThisMonth}>This Month</button>
            </div>

            <label>
                <strong>From:</strong>
                <input
                    type="date"
                    value={dateRange.from || ''}
                    onChange={e => setDateRange(prev => ({ ...prev, from: e.target.value }))}
                />
            </label>
            <label>
                <strong>To:</strong>
                <input
                    type="date"
                    value={dateRange.to || ''}
                    onChange={e => setDateRange(prev => ({ ...prev, to: e.target.value }))}
                />
            </label>
        </div>
    );
}
