export function isDateInRange(dateStr, from, to) {
    const date = new Date(dateStr);
    const fromDate = from ? new Date(from + 'T00:00:00') : null;
    const toDate = to ? new Date(to + 'T23:59:59') : null;
    return (!fromDate || date >= fromDate) && (!toDate || date <= toDate);
}


function formatDate(date) {
    return date.toISOString().split('T')[0]; // yyyy-mm-dd
}

export function getTodayRange() {
    const today = new Date();
    const formatted = formatDate(today);
    return { from: formatted, to: formatted };
}

export function getThisWeekRange() {
    const today = new Date();
    const day = today.getDay(); // 0 (Sun) - 6 (Sat)
    const diffToMonday = (day + 6) % 7;
    const monday = new Date(today);
    monday.setDate(today.getDate() - diffToMonday);
    const sunday = new Date(monday);
    sunday.setDate(monday.getDate() + 6);
    return { from: formatDate(monday), to: formatDate(sunday) };
}

export function getThisMonthRange() {
    const now = new Date();
    const first = new Date(now.getFullYear(), now.getMonth(), 1);
    const last = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    return { from: formatDate(first), to: formatDate(last) };
}
